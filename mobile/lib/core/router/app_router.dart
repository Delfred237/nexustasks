import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:nexustasks_mobile/features/auth/presentation/screens/login_screen.dart';
import 'package:nexustasks_mobile/features/auth/presentation/screens/register_screen.dart';
import 'package:nexustasks_mobile/features/auth/presentation/screens/verify_email_screen.dart';
import 'package:nexustasks_mobile/features/auth/presentation/screens/splash_screen.dart';
import 'package:nexustasks_mobile/features/tasks/presentation/screens/dashboard_screen.dart';

final routerProvider = Provider<GoRouter>((ref) {
  return GoRouter(
    initialLocation: '/splash',
    routes: [
      GoRoute(
        path: '/splash',
        builder: (context, state) => const SplashScreen(),
      ),
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginScreen(),
      ),
      GoRoute(
        path: '/register',
        builder: (context, state) => const RegisterScreen(),
      ),
      GoRoute(
        path: '/verify-email',
        builder: (context, state) => const VerifyEmailScreen(),
      ),
      GoRoute(
        path: '/dashboard',
        builder: (context, state) => const DashboardScreen(),
      ),
    ],
  );
});