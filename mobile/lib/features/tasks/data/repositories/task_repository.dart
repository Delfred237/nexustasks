import 'package:dio/dio.dart';

import '../../domain/models/task.dart';
import '../../domain/models/task_filters.dart';

class CreateTaskRequest {
  const CreateTaskRequest({
    required this.title,
    this.description,
    required this.status,
    required this.priority,
    this.dueDate,
    this.categoryPublicId,
  });

  final String title;
  final String? description;
  final String status;
  final String priority;
  final DateTime? dueDate;
  final String? categoryPublicId;

  Map<String, dynamic> toJson() => {
    'title': title,
    'description': description,
    'status': status,
    'priority': priority,
    'dueDate': dueDate?.toUtc().toIso8601String(),
    'categoryPublicId': categoryPublicId,
  };
}

class TaskPageResult {
  const TaskPageResult({
    required this.tasks,
    required this.last,
    required this.totalElements,
  });

  final List<Task> tasks;
  final bool last;
  final int totalElements;
}

class TaskRepository {
  const TaskRepository(this._dio);

  final Dio _dio;

  Future<TaskPageResult> getTasks({
    required int page,
    required int size,
    required TaskFilters filters,
    String? search,
  }) async {
    final response = await _dio.get(
      '/tasks',
      queryParameters: {
        'page': page,
        'size': size,
        'sort': 'createdAt,desc',
        'includeArchived': filters.includeArchived,
        if (filters.status != null) 'status': filters.status,
        if (filters.priority != null) 'priority': filters.priority,
        if (filters.category != null) 'category': filters.category,
        if (search != null && search.isNotEmpty) 'search': search,
      },
    );

    final data = response.data as Map<String, dynamic>;
    final content = (data['content'] as List? ?? [])
        .map((e) => Task.fromJson(e as Map<String, dynamic>))
        .toList();

    return TaskPageResult(
      tasks: content,
      last: (data['last'] as bool?) ?? true,
      totalElements: (data['totalElements'] as int?) ?? content.length,
    );
  }

  Future<Task> createTask(CreateTaskRequest request) async {
    final response = await _dio.post('/tasks', data: request.toJson());
    return Task.fromJson(response.data as Map<String, dynamic>);
  }

  Future<Task> updateTask(String publicId, CreateTaskRequest request) async {
    final response = await _dio.put('/tasks/$publicId', data: request.toJson());
    return Task.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> archiveTask(String publicId) async {
    await _dio.post('/tasks/$publicId/archive');
  }

  Future<void> restoreTask(String publicId) async {
    await _dio.post('/tasks/$publicId/restore');
  }

  Future<void> deleteTask(String publicId) async {
    await _dio.delete('/tasks/$publicId');
  }
}
