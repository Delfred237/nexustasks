class TaskFilters {
  const TaskFilters({
    this.status,
    this.priority,
    this.category,
    this.includeArchived = false,
  });

  final String? status;
  final String? priority;
  final String? category;
  final bool includeArchived;

  int get activeCount =>
      (status != null ? 1 : 0) +
      (priority != null ? 1 : 0) +
      (category != null ? 1 : 0) +
      (includeArchived ? 1 : 0);

  bool get hasActiveFilters => activeCount > 0;

  TaskFilters copyWith({
    String? status,
    String? priority,
    String? category,
    bool? includeArchived,
    bool clearStatus = false,
    bool clearPriority = false,
    bool clearCategory = false,
  }) {
    return TaskFilters(
      status: clearStatus ? null : (status ?? this.status),
      priority: clearPriority ? null : (priority ?? this.priority),
      category: clearCategory ? null : (category ?? this.category),
      includeArchived: includeArchived ?? this.includeArchived,
    );
  }
}
