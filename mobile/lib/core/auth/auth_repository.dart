import 'package:dio/dio.dart';
import 'package:nexustasks_mobile/features/auth/models/user.dart';


/// Réponse de login/refresh du backend.
class AuthTokens {
  const AuthTokens({
    required this.accessToken,
    required this.refreshToken,
    required this.publicId,
    required this.role,
  });

  final String accessToken;
  final String refreshToken;
  final String publicId;
  final String role;

  factory AuthTokens.fromJson(Map<String, dynamic> json) => AuthTokens(
    accessToken: json['accessToken'] as String,
    refreshToken: json['refreshToken'] as String,
    publicId: json['publicId'] as String,
    role: json['role'] as String,
  );
}

/// Accès réseau pour l'authentification.
class AuthRepository {
  const AuthRepository(this._dio);

  final Dio _dio;

  Future<AuthTokens> login(String email, String password) async {
    final response = await _dio.post(
      '/auth/login',
      data: {'email': email, 'password': password},
    );
    return AuthTokens.fromJson(response.data as Map<String, dynamic>);
  }

  Future<User> register({
    required String firstName,
    required String lastName,
    required String email,
    required String password,
  }) async {
    final response = await _dio.post(
      '/auth/register',
      data: {
        'firstName': firstName,
        'lastName': lastName,
        'email': email,
        'password': password,
      },
    );
    return User.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> verifyEmail(String email, String code) async {
    await _dio.post('/auth/verify-email', data: {'email': email, 'code': code});
  }

  Future<void> resendVerification(String email) async {
    await _dio.post('/auth/resend-verification', data: {'email': email});
  }

  Future<User> me() async {
    final response = await _dio.get('/users/me');
    return User.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> logout() async {
    try {
      await _dio.post('/auth/logout');
    } on DioException {
      // Non bloquant : les tokens locaux seront nettoyés quoi qu'il arrive.
    }
  }
}
