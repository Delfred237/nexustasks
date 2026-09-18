import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:nexustasks_mobile/core/router/app_router.dart';
import 'package:nexustasks_mobile/core/theme/app_theme.dart';

void main() {
  runApp(ProviderScope(child: const NexusTasksApp()));
}

class NexusTasksApp extends ConsumerWidget {
  const NexusTasksApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final router = ref.watch(routerProvider);

    return MaterialApp.router(
      title: 'NexusTasks',
      theme: AppTheme.lightTheme(),
      darkTheme: AppTheme.darkTheme(),
      themeMode: ThemeMode.system,
      routerConfig: router,
      debugShowCheckedModeBanner: false,
    );
  }
}
