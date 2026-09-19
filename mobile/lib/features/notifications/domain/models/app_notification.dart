/// Nommé AppNotification pour éviter la collision avec
/// la classe `Notification` du framework Flutter.
class AppNotification {
  const AppNotification({
    required this.publicId,
    required this.type,
    required this.title,
    this.message,
    required this.read,
    this.readAt,
    this.resourcePublicId,
    required this.createdAt,
  });

  final String publicId;
  final String type;
  final String title;
  final String? message;
  final bool read;
  final DateTime? readAt;
  final String? resourcePublicId;
  final DateTime createdAt;

  factory AppNotification.fromJson(Map<String, dynamic> json) =>
      AppNotification(
        publicId: json['publicId'] as String,
        type: (json['type'] as String?) ?? 'TASK_CREATED',
        title: (json['title'] as String?) ?? '',
        message: json['message'] as String?,
        read: (json['read'] as bool?) ?? false,
        readAt: json['readAt'] != null
            ? DateTime.parse(json['readAt'] as String)
            : null,
        resourcePublicId: json['resourcePublicId'] as String?,
        createdAt: DateTime.parse(json['createdAt'] as String),
      );
}
