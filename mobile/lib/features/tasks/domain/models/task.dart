class TaskCategory {
  const TaskCategory({
    required this.publicId,
    required this.name,
    required this.color,
  });

  final String publicId;
  final String name;
  final String color;

  factory TaskCategory.fromJson(Map<String, dynamic> json) => TaskCategory(
    publicId: json['publicId'] as String,
    name: json['name'] as String,
    color: (json['color'] as String?) ?? '#6b7280',
  );
}

class Task {
  const Task({
    required this.publicId,
    required this.title,
    required this.status,
    required this.priority,
    this.description,
    this.dueDate,
    this.completedAt,
    required this.archived,
    required this.createdAt,
    required this.updatedAt,
    this.category,
  });

  final String publicId;
  final String title;
  final String status;
  final String priority;
  final String? description;
  final DateTime? dueDate;
  final DateTime? completedAt;
  final bool archived;
  final DateTime createdAt;
  final DateTime updatedAt;
  final TaskCategory? category;

  factory Task.fromJson(Map<String, dynamic> json) => Task(
    publicId: json['publicId'] as String,
    title: json['title'] as String,
    status: json['status'] as String,
    priority: json['priority'] as String,
    description: json['description'] as String?,
    dueDate: json['dueDate'] != null
        ? DateTime.parse(json['dueDate'] as String)
        : null,
    completedAt: json['completedAt'] != null
        ? DateTime.parse(json['completedAt'] as String)
        : null,
    archived: (json['archived'] as bool?) ?? false,
    createdAt: DateTime.parse(json['createdAt'] as String),
    updatedAt: DateTime.parse(json['updatedAt'] as String),
    category: json['category'] != null
        ? TaskCategory.fromJson(json['category'] as Map<String, dynamic>)
        : null,
  );
}
