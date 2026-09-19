import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:nexustasks_mobile/features/categories/presentation/screens/categories_screen.dart';
import 'package:nexustasks_mobile/features/tasks/presentation/task_screen.dart';

import '../auth/auth_notifier.dart';
import '../../features/auth/presentation/screens/login_screen.dart';
import '../../features/auth/presentation/screens/register_screen.dart';
import '../../features/auth/presentation/screens/verify_email_screen.dart';
import '../../features/auth/presentation/screens/splash_screen.dart';
import '../../features/tasks/presentation/screens/dashboard_screen.dart';
import '../../features/notifications/presentation/screens/notifications_screen.dart';
import '../../features/profile/presentation/screens/profile_screen.dart';
import '../../shared/widgets/authenticated_shell.dart';

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
          return isSplash ? null : '/splash';
        case AuthStatus.unauthenticated:
          return isAuthRoute ? null : '/login';
        case AuthStatus.authenticated:
          return (isAuthRoute || isSplash) ? '/dashboard' : null;
      }
    },
    routes: [
      // Routes publiques
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

      // Routes authentifiées dans un ShellRoute partagé
      StatefulShellRoute.indexedStack(
        builder: (context, state, navigationShell) {
          return AuthenticatedShell(navigationShell: navigationShell);
        },
        branches: [
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/dashboard',
                builder: (context, state) => const DashboardScreen(),
              ),
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/tasks',
                builder: (context, state) => const TasksScreen(),
              ),
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/categories',
                builder: (context, state) => const CategoriesScreen(),
              ),
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/notifications',
                builder: (context, state) => const NotificationsScreen(),
              ),
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/profile',
                builder: (context, state) => const ProfileScreen(),
              ),
            ],
          ),
        ],
      ),
    ],
  );

  ref.listen(authProvider, (_, __) => router.refresh());

  return router;
});
