import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:nexustasks_mobile/core/providers/core_providers.dart';

import '../../data/repositories/task_repository.dart';
import '../../domain/models/task.dart';
import '../../domain/models/task_filters.dart';

final taskRepositoryProvider = Provider<TaskRepository>(
  (ref) => TaskRepository(ref.watch(dioProvider)),
);

class TasksState {
  const TasksState({
    required this.tasks,
    required this.page,
    required this.hasMore,
    required this.filters,
    required this.search,
    this.isLoadingMore = false,
  });

  final List<Task> tasks;
  final int page;
  final bool hasMore;
  final bool isLoadingMore;
  final TaskFilters filters;
  final String search;

  TasksState copyWith({
    List<Task>? tasks,
    int? page,
    bool? hasMore,
    bool? isLoadingMore,
    TaskFilters? filters,
    String? search,
  }) {
    return TasksState(
      tasks: tasks ?? this.tasks,
      page: page ?? this.page,
      hasMore: hasMore ?? this.hasMore,
      isLoadingMore: isLoadingMore ?? this.isLoadingMore,
      filters: filters ?? this.filters,
      search: search ?? this.search,
    );
  }
}

class TasksNotifier extends AsyncNotifier<TasksState> {
  Timer? _searchDebounce;

  @override
  Future<TasksState> build() async {
    ref.onDispose(() => _searchDebounce?.cancel());
    return _loadPage(page: 0, filters: const TaskFilters(), search: '');
  }

  Future<TasksState> _loadPage({
    required int page,
    required TaskFilters filters,
    required String search,
    List<Task> previous = const [],
  }) async {
    final repo = ref.read(taskRepositoryProvider);
    final result = await repo.getTasks(
      page: page,
      size: 20,
      filters: filters,
      search: search.isEmpty ? null : search,
    );
    return TasksState(
      tasks: [...previous, ...result.tasks],
      page: page,
      hasMore: !result.last,
      filters: filters,
      search: search,
    );
  }

  Future<void> refresh() async {
    final current = state.value;
    final filters = current?.filters ?? const TaskFilters();
    final search = current?.search ?? '';
    state = await AsyncValue.guard<TasksState>(
      () => _loadPage(page: 0, filters: filters, search: search),
    );
  }

  Future<void> loadMore() async {
    final current = state.value;
    if (current == null || current.isLoadingMore || !current.hasMore) return;

    state = AsyncData<TasksState>(current.copyWith(isLoadingMore: true));
    try {
      final next = await _loadPage(
        page: current.page + 1,
        filters: current.filters,
        search: current.search,
        previous: current.tasks,
      );
      state = AsyncData<TasksState>(next);
    } catch (_) {
      state = AsyncData<TasksState>(current.copyWith(isLoadingMore: false));
    }
  }

  Future<void> setFilters(TaskFilters filters) async {
    final search = state.value?.search ?? '';
    state = AsyncLoading<TasksState>();
    state = await AsyncValue.guard<TasksState>(
      () => _loadPage(page: 0, filters: filters, search: search),
    );
  }

  void setSearch(String query) {
    _searchDebounce?.cancel();
    _searchDebounce = Timer(const Duration(milliseconds: 400), () {
      _applySearch(query);
    });
  }

  Future<void> _applySearch(String query) async {
    final filters = state.value?.filters ?? const TaskFilters();
    state = AsyncLoading<TasksState>();
    state = await AsyncValue.guard<TasksState>(
      () => _loadPage(page: 0, filters: filters, search: query),
    );
  }

  Future<void> createTask(CreateTaskRequest request) async {
    await ref.read(taskRepositoryProvider).createTask(request);
    await refresh();
  }

  Future<void> updateTask(String publicId, CreateTaskRequest request) async {
    await ref.read(taskRepositoryProvider).updateTask(publicId, request);
    await refresh();
  }

  Future<void> setArchived(String publicId, bool archived) async {
    final repo = ref.read(taskRepositoryProvider);
    if (archived) {
      await repo.archiveTask(publicId);
    } else {
      await repo.restoreTask(publicId);
    }
    await refresh();
  }

  Future<void> deleteTask(String publicId) async {
    await ref.read(taskRepositoryProvider).deleteTask(publicId);
    await refresh();
  }
}

final tasksProvider = AsyncNotifierProvider<TasksNotifier, TasksState>(
  TasksNotifier.new,
);
