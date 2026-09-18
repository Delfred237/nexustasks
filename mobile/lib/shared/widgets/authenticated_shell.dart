import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

/// Shell persistant pour les routes authentifiées.
/// Le `ShellRoute` de GoRouter garde chaque onglet vivant,
/// donc le scroll, les filtres, etc. sont préservés quand on switch.
class AuthenticatedShell extends StatefulWidget {
  const AuthenticatedShell({super.key, required this.navigationShell});

  final StatefulNavigationShell navigationShell;

  @override
  State<AuthenticatedShell> createState() => _AuthenticatedShellState();
}

class _AuthenticatedShellState extends State<AuthenticatedShell> {
  static const _tabs = [
    _Tab(Icons.dashboard_outlined, Icons.dashboard, 'Dashboard'),
    _Tab(Icons.checklist_outlined, Icons.checklist, 'Tasks'),
    _Tab(Icons.notifications_outlined, Icons.notifications, 'Notifications'),
    _Tab(Icons.person_outline, Icons.person, 'Profile'),
  ];

  void _onTabTap(int index) {
    widget.navigationShell.goBranch(
      index,
      // Revenir au premier écran de l'onglet si déjà dessus
      initialLocation: index == widget.navigationShell.currentIndex,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: widget.navigationShell,
      bottomNavigationBar: NavigationBar(
        selectedIndex: widget.navigationShell.currentIndex,
        onDestinationSelected: _onTabTap,
        destinations: _tabs
            .map(
              (t) => NavigationDestination(
                icon: Icon(t.icon),
                selectedIcon: Icon(t.selectedIcon),
                label: t.label,
              ),
            )
            .toList(),
      ),
    );
  }
}

class _Tab {
  const _Tab(this.icon, this.selectedIcon, this.label);
  final IconData icon;
  final IconData selectedIcon;
  final String label;
}
