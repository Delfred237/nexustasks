import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:nexustasks_mobile/core/utils/error_utils.dart';

void main() {
  RequestOptions opts() => RequestOptions(path: '/test');

  group('describeError', () {
    test('extrait le detail RFC 7807', () {
      final error = DioException(
        requestOptions: opts(),
        type: DioExceptionType.badResponse,
        response: Response(
          requestOptions: opts(),
          statusCode: 409,
          data: {'detail': 'Email already registered', 'status': 409},
        ),
      );
      expect(describeError(error), 'Email already registered');
    });

    test('joint les erreurs de validation Bean Validation', () {
      final error = DioException(
        requestOptions: opts(),
        type: DioExceptionType.badResponse,
        response: Response(
          requestOptions: opts(),
          statusCode: 400,
          data: {
            'errors': [
              {'field': 'email', 'message': 'must be a valid email'},
              {'field': 'password', 'message': 'too weak'},
            ],
          },
        ),
      );
      expect(describeError(error), 'must be a valid email • too weak');
    });

    test('message réseau pour connection error', () {
      final error = DioException(
        requestOptions: opts(),
        type: DioExceptionType.connectionError,
      );
      expect(describeError(error), contains('Cannot reach the server'));
    });

    test('fallback générique pour une exception inconnue', () {
      expect(describeError(Exception('boom')), 'An unexpected error occurred.');
    });
  });
}
