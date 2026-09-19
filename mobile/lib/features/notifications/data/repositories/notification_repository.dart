import 'package:dio/dio.dart';

import '../../domain/models/app_notification.dart';

class NotificationRepository {
  const NotificationRepository(this._dio);

  final Dio _dio;

  Future<List<AppNotification>> getNotifications({
    int page = 0,
    int size = 50,
  }) async {
    final response = await _dio.get(
      '/notifications',
      queryParameters: {'page': page, 'size': size, 'sort': 'createdAt,desc'},
    );
    final data = response.data as Map<String, dynamic>;
    return (data['content'] as List? ?? [])
        .map((e) => AppNotification.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  /// Le backend peut renvoyer un int brut ou un objet {count}.
  Future<int> getUnreadCount() async {
    final response = await _dio.get('/notifications/unread-count');
    final data = response.data;
    if (data is int) return data;
    if (data is Map && data['count'] is int) return data['count'] as int;
    return 0;
  }

  Future<void> markAsRead(String publicId) async {
    await _dio.patch('/notifications/$publicId/read');
  }

  Future<void> markAllAsRead() async {
    await _dio.patch('/notifications/read-all');
  }
}
