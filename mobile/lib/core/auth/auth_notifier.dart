import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:nexustasks_mobile/core/providers/core_providers.dart';
import 'package:nexustasks_mobile/features/auth/models/user.dart';

import 'auth_repository.dart';
import 'secure_storage_service.dart';

enum AuthStatus { unknown, authenticated, unauthenticated }

class AuthState {
  const AuthState({this.status = AuthStatus.unknown, this.user});

  final AuthStatus status;
  final User? user;
}

/// Gère l'état de session global de l'app.
class AuthNotifier extends Notifier<AuthState> {
  @override
  AuthState build() => const AuthState();

  SecureStorageService get _storage => ref.read(secureStorageProvider);
  AuthRepository get _repo => AuthRepository(ref.read(dioProvider));

  /// Vérifie la session au démarrage de l'app.
  Future<void> checkSession() async {
    final accessToken = await _storage.getAccessToken();

    if (accessToken == null) {
      state = const AuthState(status: AuthStatus.unauthenticated);
      return;
    }

    try {
      final user = await _repo.me();
      state = AuthState(status: AuthStatus.authenticated, user: user);
    } catch (_) {
      // Token invalide/expiré et refresh impossible → session morte
      await _storage.clearTokens();
      state = const AuthState(status: AuthStatus.unauthenticated);
    }
  }

  /// Appelé après un login/register réussi.
  Future<void> setSession({
    required String accessToken,
    required String refreshToken,
    required User user,
  }) async {
    await _storage.setAccessToken(accessToken);
    await _storage.setRefreshToken(refreshToken);
    state = AuthState(status: AuthStatus.authenticated, user: user);
  }

  Future<void> logout() async {
    await _repo.logout();
    await _storage.clearTokens();
    state = const AuthState(status: AuthStatus.unauthenticated);
  }
}

final authProvider = NotifierProvider<AuthNotifier, AuthState>(
  AuthNotifier.new,
);