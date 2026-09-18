/// Configuration globale de l'application.
///
/// - Émulateur : 10.0.2.2 pointe vers le localhost de la machine hôte.
/// - Device physique USB/Wi-Fi : remplace par l'IP LAN de ton PC
///   (ex: http://192.168.1.20:8080).
///
/// Surchargeable au build :
///   flutter run --dart-define=API_BASE_URL=http://192.168.1.20:8080/api
class AppConfig {
  AppConfig._();

  static const String apiBaseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'http://10.0.2.2:8080/api',
  );
}