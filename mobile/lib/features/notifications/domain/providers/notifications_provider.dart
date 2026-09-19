import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:nexustasks_mobile/core/providers/core_providers.dart';

import '../../data/repositories/notification_repository.dart';
import '../models/app_notification.dart';

final notificationRepositoryProvider = Provider<NotificationRepository>(
  (ref) => NotificationRepository(ref.watch(dioProvider)),
);

final notificationsProvider = FutureProvider.autoDispose<List<AppNotification>>(
  (ref) async {
    return ref.watch(notificationRepositoryProvider).getNotifications();
  },
);

/// Compteur de non-lues, re-pollé toutes les 30 s tant qu'un widget l'écoute.
/// autoDispose : le Timer est annulé automatiquement au logout (plus de listener).
final unreadCountProvider = FutureProvider.autoDispose<int>((ref) async {
  final repo = ref.watch(notificationRepositoryProvider);
  final count = await repo.getUnreadCount();

  final timer = Timer.periodic(
    const Duration(seconds: 30),
    (_) => ref.invalidateSelf(),
  );
  ref.onDispose(timer.cancel);

  return count;
});
