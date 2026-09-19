import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:nexustasks_mobile/core/providers/core_providers.dart';
import 'package:nexustasks_mobile/features/tasks/domain/models/task.dart';

class DashboardStats {
  const DashboardStats({
    required this.totalTasks,
    required this.completedTasks,
    required this.inProgressTasks,
    required this.todoTasks,
    required this.dueSoonCount,
  });

  final int totalTasks;
  final int completedTasks;
  final int inProgressTasks;
  final int todoTasks;
  final int dueSoonCount;
}

/// Récupère les tâches non archivées et agrège les statistiques.
/// Réplique exacte de la logique `useDashboardStats` du frontend React.
final dashboardProvider = FutureProvider<DashboardStats>((ref) async {
  final dio = ref.watch(dioProvider);
  // final repo = AuthRepository(dio);

  // Réutiliser me() pour valider la session, mais les stats viennent des tasks
  // On récupère jusqu'à 1000 tâches non archivées
  final response = await dio.get(
    '/tasks',
    queryParameters: {
      'page': 0,
      'size': 1000,
      'includeArchived': false,
    },
  );

  final data = response.data as Map<String, dynamic>;
  final content = (data['content'] as List)
      .map((e) => Task.fromJson(e as Map<String, dynamic>))
      .toList();

  final now = DateTime.now();
  final threeDaysLater = now.add(const Duration(days: 3));

  int byStatus(String status) =>
      content.where((t) => t.status == status).length;

  final dueSoon = content.where((t) {
    if (t.dueDate == null || t.status == 'COMPLETED') return false;
    final due = t.dueDate!;
    return due.isAfter(now.subtract(const Duration(seconds: 1))) &&
        due.isBefore(threeDaysLater);
  }).length;

  return DashboardStats(
    totalTasks: (data['totalElements'] as int?) ?? content.length,
    completedTasks: byStatus('COMPLETED'),
    inProgressTasks: byStatus('IN_PROGRESS'),
    todoTasks: byStatus('TODO'),
    dueSoonCount: dueSoon,
  );
});