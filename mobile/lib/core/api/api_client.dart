import 'package:dio/dio.dart';
import 'package:talker_flutter/talker_flutter.dart';

class ApiClient {
  late final Dio dio;
  final Talker _talker;
  
  ApiClient({
    required this._talker,
    required String baseUrl,
    required String Function()? getAccessToken,
    required String Function()? getRefreshToken,
    required Future<void> Function(String newToken) onTokenRefreshed,
    required void Function() onLogout,
  }) {
    dio = Dio(BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 30),
    ));

    // Request Interceptor : Ajouter Bearer token
    dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) {
        final token = getAccessToken!();
        options.headers['Authorization'] = 'Bearer $token';
        return handler.next(options);
      },
      onError: (error, handler) async {
        if (error.response?.statusCode == 401) {
          // Tenter de refresh le token
          final refreshToken = getRefreshToken!();
          try {
            final response = await dio.post(
              '/api/auth/refresh',
              data: {'refreshToken': refreshToken},
            );
            
            final newToken = response.data['accessToken'] as String;
            await onTokenRefreshed(newToken);
            
            // Retry la requête originale
            final options = error.requestOptions;
            options.headers['Authorization'] = 'Bearer $newToken';
            
            final retryResponse = await dio.fetch(options);
            return handler.resolve(retryResponse);
          } catch (e) {
            _talker.error('Token refresh failed', e);
            onLogout();
          }
        }
        return handler.next(error);
      },
    ));

    // Logging Interceptor
    dio.interceptors.add(LogInterceptor(
      requestBody: true,
      responseBody: true,
      logPrint: (obj) => _talker.debug(obj.toString()),
    ));
  }
}