import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/auth/auth_notifier.dart';

/// Écran de démarrage : vérifie la session puis redirige.
/// - Session valide  → /dashboard
/// - Pas de session  → /login
class SplashScreen extends ConsumerWidget {
  const SplashScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authProvider);

    // Déclenche la vérification une seule fois
    if (authState.status == AuthStatus.unknown) {
      Future.microtask(() => ref.read(authProvider.notifier).checkSession());
    }

    // Réagit aux changements d'état pour naviguer
    ref.listen<AuthState>(authProvider, (previous, next) {
      switch (next.status) {
        case AuthStatus.authenticated:
          context.go('/dashboard');
        case AuthStatus.unauthenticated:
          context.go('/login');
        case AuthStatus.unknown:
          break;
      }
    });

    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.checklist_rounded,
              size: 72,
              color: Theme.of(context).colorScheme.primary,
            ),
            const SizedBox(height: 24),
            Text(
              'NexusTasks',
              style: Theme.of(context).textTheme.headlineMedium
                  ?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 32),
            const CircularProgressIndicator(),
          ],
        ),
      ),
    );
  }
}
