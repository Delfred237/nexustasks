import 'package:flutter/material.dart';

/// Design tokens partagés avec le frontend React.
/// Ces couleurs correspondent aux variables CSS du web
/// (--primary, --destructive, etc.) pour une cohérence visuelle
/// entre les plateformes.
class AppColors {
  AppColors._();

  /// Couleur primaire (indigo) — correspond à --primary du web
  static const Color primary = Color(0xFF4F46E5);

  /// Accent secondaire (violet) — gradients du web
  static const Color accent = Color(0xFF7C3AED);

  /// Erreur / destructif — correspond à --destructive du web
  static const Color destructive = Color(0xFFEF4444);

  /// Succès (vert) — badges "Completed"
  static const Color success = Color(0xFF10B981);

  /// Warning (ambre) — badges "In Progress" / "Due soon"
  static const Color warning = Color(0xFFF59E0B);

  /// Priorités (miroir des badges web)
  static const Color priorityLow = Color(0xFF6B7280);
  static const Color priorityMedium = Color(0xFF3B82F6);
  static const Color priorityHigh = Color(0xFFF97316);
  static const Color priorityUrgent = Color(0xFFEF4444);
}
