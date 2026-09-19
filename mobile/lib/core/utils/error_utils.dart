import 'package:dio/dio.dart';

/// Convertit une erreur réseau en message lisible.
/// Comprend le format Problem Details (RFC 7807) du backend :
/// { detail, title, errorCode, errors: [{ field, message }] }
///
/// Fonction pure : aucun effet de bord (pas de log ici).
/// Le logging éventuel est fait par les appelants (intercepteurs, écrans).
String describeError(Object error) {
  if (error is DioException) {
    final data = error.response?.data;

    if (data is Map<String, dynamic>) {
      // Erreurs de validation Bean Validation
      final errors = data['errors'];
      if (errors is List && errors.isNotEmpty) {
        return errors
            .map(
              (e) => (e is Map && e['message'] is String) ? e['message'] : '',
            )
            .where((m) => m.isNotEmpty)
            .join(' • ');
      }
      // Erreur métier standard
      final detail = data['detail'];
      if (detail is String && detail.isNotEmpty) return detail;
      final title = data['title'];
      if (title is String && title.isNotEmpty) return title;
    }

    switch (error.type) {
      case DioExceptionType.connectionError:
      case DioExceptionType.connectionTimeout:
        return 'Cannot reach the server. Check your connection.';
      case DioExceptionType.receiveTimeout:
        return 'The server took too long to respond.';
      default:
        break;
    }
  }
  return 'An unexpected error occurred.';
}
