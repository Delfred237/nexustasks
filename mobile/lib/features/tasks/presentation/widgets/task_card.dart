import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';

import '../../../../core/theme/app_colors.dart';
import '../../../../core/utils/color_utils.dart';
import '../../../../core/utils/error_utils.dart';
import '../../domain/models/task.dart';
import '../../domain/providers/tasks_provider.dart';

const Map<String, String> _statusLabels = {
  'TODO': 'To Do',
  'IN_PROGRESS': 'In Progress',
  'COMPLETED': 'Completed',
  'ARCHIVED': 'Archived',
};

const Map<String, Color> _statusColors = {
  'TODO': AppColors.priorityLow,
  'IN_PROGRESS': AppColors.warning,
  'COMPLETED': AppColors.success,
  'ARCHIVED': AppColors.accent,
};

const Map<String, String> _priorityLabels = {
  'LOW': 'Low',
  'MEDIUM': 'Medium',
  'HIGH': 'High',
  'URGENT': 'Urgent',
};

const Map<String, Color> _priorityColors = {
  'LOW': AppColors.priorityLow,
  'MEDIUM': AppColors.priorityMedium,
  'HIGH': AppColors.priorityHigh,
  'URGENT': AppColors.priorityUrgent,
};

class TaskCard extends ConsumerWidget {
  const TaskCard({super.key, required this.task, required this.onEdit});

  final Task task;
  final ValueChanged<Task> onEdit;

  Future<void> _onSelected(
    BuildContext context,
    WidgetRef ref,
    String value,
  ) async {
    final notifier = ref.read(tasksProvider.notifier);
    final messenger = ScaffoldMessenger.of(context);

    try {
      switch (value) {
        case 'edit':
          onEdit(task);
        case 'archive':
          await notifier.setArchived(task.publicId, !task.archived);
        case 'delete':
          final confirmed = await showDialog<bool>(
            context: context,
            builder: (ctx) => AlertDialog(
              title: const Text('Delete task?'),
              content: Text('"${task.title}" will be permanently removed.'),
              actions: [
                TextButton(
                  onPressed: () => Navigator.of(ctx).pop(false),
                  child: const Text('Cancel'),
                ),
                FilledButton.tonal(
                  onPressed: () => Navigator.of(ctx).pop(true),
                  child: const Text('Delete'),
                ),
              ],
            ),
          );
          if (confirmed == true) {
            await notifier.deleteTask(task.publicId);
          }
      }
    } catch (error) {
      messenger.showSnackBar(SnackBar(content: Text(describeError(error))));
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final statusColor = _statusColors[task.status] ?? AppColors.priorityLow;
    final priorityColor =
        _priorityColors[task.priority] ?? AppColors.priorityLow;

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  child: Text(
                    task.title,
                    style: Theme.of(context).textTheme.titleMedium
                        ?.copyWith(fontWeight: FontWeight.w600),
                  ),
                ),
                PopupMenuButton<String>(
                  onSelected: (value) => _onSelected(context, ref, value),
                  itemBuilder: (_) => [
                    const PopupMenuItem(value: 'edit', child: Text('Edit')),
                    PopupMenuItem(
                      value: 'archive',
                      child: Text(task.archived ? 'Restore' : 'Archive'),
                    ),
                    const PopupMenuItem(value: 'delete', child: Text('Delete')),
                  ],
                ),
              ],
            ),
            if (task.description != null && task.description!.isNotEmpty) ...[
              const SizedBox(height: 4),
              Text(
                task.description!,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: Theme.of(context).colorScheme.onSurfaceVariant,
                ),
              ),
            ],
            const SizedBox(height: 12),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: [
                _Badge(
                  label: _statusLabels[task.status] ?? task.status,
                  color: statusColor,
                ),
                _Badge(
                  label: _priorityLabels[task.priority] ?? task.priority,
                  color: priorityColor,
                ),
                if (task.category != null)
                  _Badge(
                    label: task.category!.name,
                    color: colorFromHex(task.category!.color),
                  ),
                if (task.dueDate != null)
                  Padding(
                    padding: const EdgeInsets.symmetric(vertical: 4),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(
                          Icons.event_outlined,
                          size: 14,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          DateFormat('MMM d, yyyy').format(task.dueDate!),
                          style: Theme.of(context).textTheme.bodySmall
                              ?.copyWith(
                                color: Theme.of(context)
                                    .colorScheme
                                    .onSurfaceVariant,
                              ),
                        ),
                      ],
                    ),
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _Badge extends StatelessWidget {
  const _Badge({required this.label, required this.color});

  final String label;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.15),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Text(
        label,
        style: TextStyle(
          color: color,
          fontSize: 11,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }
}
