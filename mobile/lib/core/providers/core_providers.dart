import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:nexustasks_mobile/core/auth/secure_storage_service.dart';
import 'package:talker_flutter/talker_flutter.dart';
import 'package:nexustasks_mobile/core/config/app_config.dart';

final talkerProvider = Provider<Talker>((ref) => Talker());

final secureStorageProvider = Provider<SecureStorageService>(
  (ref) => SecureStorageService(),
);

/// Instance Dio globale avec :
/// - Injection du Bearer token à chaque requête
/// - Refresh automatique du token en cas de 401
/// - Logging via Talker
final dioProvider = Provider<Dio>((ref) {
  final storage = ref.watch(secureStorageProvider);
  final talker = ref.watch(talkerProvider);

  final dio = Dio(
    BaseOptions(
      baseUrl: AppConfig.apiBaseUrl,
      connectTimeout: const Duration(seconds: 15),
      receiveTimeout: const Duration(seconds: 15),
      headers: {'Content-Type': 'application/json'},
    ),
  );

  dio.interceptors.add(
    InterceptorsWrapper(
      onRequest: (options, handler) async {
        final token = await storage.getAccessToken();
        if (token != null) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        handler.next(options);
      },
      onError: (error, handler) async {
        final statusCode = error.response?.statusCode;
        final isRefreshCall = error.requestOptions.path.contains(
          '/auth/refresh',
        );

        // 401 sur une requête normale (pas le refresh lui-même) → tentative de refresh
        if (statusCode == 401 && !isRefreshCall) {
          final refreshToken = await storage.getRefreshToken();

          if (refreshToken != null) {
            try {
              final response = await Dio(
                BaseOptions(baseUrl: AppConfig.apiBaseUrl),
              ).post('/auth/refresh', data: {'refreshToken': refreshToken});

              final newAccess = response.data['accessToken'] as String;
              final newRefresh = response.data['refreshToken'] as String;

              await storage.setAccessToken(newAccess);
              await storage.setRefreshToken(newRefresh);

              // Retry de la requête d'origine avec le nouveau token
              final retryOptions = error.requestOptions
                ..headers['Authorization'] = 'Bearer $newAccess';

              final retryResponse = await dio.fetch(retryOptions);
              return handler.resolve(retryResponse);
            } catch (e) {
              talker.error('Token refresh failed → logout', e);
              await storage.clearTokens();
            }
          } else {
            await storage.clearTokens();
          }
        }
        handler.next(error);
      },
    ),
  );

  return dio;
});
