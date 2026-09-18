import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../auth/auth_notifier.dart';
import '../../features/auth/presentation/screens/login_screen.dart';
import '../../features/auth/presentation/screens/register_screen.dart';
import '../../features/auth/presentation/screens/verify_email_screen.dart';
import '../../features/auth/presentation/screens/splash_screen.dart';
import '../../features/tasks/presentation/screens/dashboard_screen.dart';

final routerProvider = Provider<GoRouter>((ref) {
  final router = GoRouter(
    initialLocation: '/splash',
    redirect: (context, state) {
      final auth = ref.read(authProvider);
      final loc = state.matchedLocation;

      final isAuthRoute =
          loc == '/login' || loc == '/register' || loc == '/verify-email';
      final isSplash = loc == '/splash';

      switch (auth.status) {
        case AuthStatus.unknown:
          // Pendant le check de session : seul /splash est autorisé
          return isSplash ? null : '/splash';

        case AuthStatus.unauthenticated:
          // GuestGuard : routes auth ouvertes, tout le reste → login
          return isAuthRoute ? null : '/login';

        case AuthStatus.authenticated:
          // AuthGuard inverse : connecté → hors des routes auth
          return (isAuthRoute || isSplash) ? '/dashboard' : null;
      }
    },
    routes: [
      GoRoute(
        path: '/splash',
        builder: (context, state) => const SplashScreen(),
      ),
      GoRoute(path: '/login', builder: (context, state) => const LoginScreen()),
      GoRoute(
        path: '/register',
        builder: (context, state) => const RegisterScreen(),
      ),
      GoRoute(
        path: '/verify-email',
        builder: (context, state) => VerifyEmailScreen(
          initialEmail: state.extra is String ? state.extra as String : null,
        ),
      ),
      GoRoute(
        path: '/dashboard',
        builder: (context, state) => const DashboardScreen(),
      ),
    ],
  );

  // Re-évalue les redirects à chaque changement d'état auth
  ref.listen(authProvider, (_, __) => router.refresh());

  return router;
});
