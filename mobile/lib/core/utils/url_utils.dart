import '../config/app_config.dart';

/// Convertit un avatarUrl backend en URL complète chargeable.
/// - null/vide      → null
/// - déjà absolu    → tel quel
/// - "/api/files/…" → préfixé par l'origine (pas par apiBaseUrl, sinon double /api)
String? resolveAvatarUrl(String? avatarUrl) {
  if (avatarUrl == null || avatarUrl.isEmpty) return null;
  if (avatarUrl.startsWith('http')) return avatarUrl;
  return '${AppConfig.apiOrigin}$avatarUrl';
}
