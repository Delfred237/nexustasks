import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../../domain/models/app_notification.dart';

const Map<String, IconData> _typeIcons = {
  'TASK_CREATED': Icons.add_task_outlined,
  'TASK_COMPLETED': Icons.task_alt_outlined,
  'TASK_ARCHIVED': Icons.archive_outlined,
  'TASK_RESTORED': Icons.restore_from_trash_outlined,
  'TASK_DELETED': Icons.delete_outline,
  'SECURITY_EVENT': Icons.shield_outlined,
};

String timeAgo(DateTime dateTime) {
  final diff = DateTime.now().difference(dateTime);
  if (diff.inMinutes < 1) return 'Just now';
  if (diff.inMinutes < 60) return '${diff.inMinutes}m ago';
  if (diff.inHours < 24) return '${diff.inHours}h ago';
  if (diff.inDays < 7) return '${diff.inDays}d ago';
  return DateFormat('MMM d').format(dateTime);
}

class NotificationTile extends StatelessWidget {
  const NotificationTile({
    super.key,
    required this.notification,
    required this.onTap,
  });

  final AppNotification notification;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;

    return Card(
      color: notification.read
          ? null
          : scheme.primaryContainer.withValues(alpha: 0.35),
      child: ListTile(
        onTap: onTap,
        leading: CircleAvatar(
          backgroundColor: scheme.primary.withValues(alpha: 0.15),
          child: Icon(
            _typeIcons[notification.type] ?? Icons.notifications_outlined,
            color: scheme.primary,
            size: 20,
          ),
        ),
        title: Text(
          notification.title,
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
          style: TextStyle(
            fontWeight: notification.read ? FontWeight.w400 : FontWeight.w600,
          ),
        ),
        subtitle: Text(
          notification.message ?? timeAgo(notification.createdAt),
          maxLines: 2,
          overflow: TextOverflow.ellipsis,
        ),
        trailing: notification.read
            ? Text(
                timeAgo(notification.createdAt),
                style: Theme.of(context).textTheme.bodySmall,
              )
            : Container(
                width: 10,
                height: 10,
                decoration: BoxDecoration(
                  color: scheme.primary,
                  shape: BoxShape.circle,
                ),
              ),
      ),
    );
  }
}
