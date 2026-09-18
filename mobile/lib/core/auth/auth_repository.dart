import 'package:dio/dio.dart';
import 'package:nexustasks_mobile/features/auth/models/user.dart';

/// Accès réseau pour l'authentification.
class AuthRepository {
  const AuthRepository(this._dio);

  final Dio _dio;

  /// Récupère l'utilisateur courant (valide la session).
  Future<User> me() async {
    final response = await _dio.get('/users/me');
    return User.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> logout() async {
    try {
      await _dio.post('/auth/logout');
    } on DioException {
      // Le logout réseau peut échouer (token expiré) :
      // ce n'est pas bloquant, les tokens locaux seront nettoyés.
    }
  }
}