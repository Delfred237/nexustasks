# NexusTasks — Mobile Android

Application Flutter 3.47 (Android uniquement), Material 3, à parité fonctionnelle avec le web.

## Prérequis

- Flutter 3.47+ (`flutter --version`)
- Android SDK + émulateur ou device physique

## Démarrage

```bash
flutter pub get

# Émulateur : l'alias 10.0.2.2 pointe vers le localhost de la machine hôte
flutter run

# Device physique : utiliser l'IP LAN du PC qui héberge le backend
flutter run --dart-define=API_BASE_URL=http://192.168.1.x:8080/api
```

> Le backend doit tourner (`docker compose up -d` dans `../docker`).
> En debug, le HTTP cleartext est autorisé ; en release,
> `android:usesCleartextTraffic="true"` est nécessaire tant que l'API n'est pas en HTTPS.

## Commandes

| Commande | Rôle |
|---|---|
| `flutter analyze` | Analyse statique (fatal warnings) |
| `flutter test` | Tests unitaires (mocktail) + widget tests |
| `flutter build apk --release` | APK release signé (keystore debug par défaut) |
| `flutter install --release` | Installe l'APK sur le device branché |

## Structure (feature-first)

```
lib/
├── core/       # config, api (Dio + intercepteurs), auth (store, storage), router, theme, utils
├── features/   # auth, tasks, categories, notifications, profile (data / domain / presentation)
└── shared/     # widgets transverses (UserAvatar, shell), validateurs partagés
```

## Sécurité mobile

- Tokens stockés en **flutter_secure_storage** (Keystore Android)
- Refresh automatique sur 401 via intercepteur Dio
- Logout = révocation serveur + purge locale

## Branding

- Logo source : `assets/icon/app_icon.svg` (vectoriel)
- Icônes launcher générées via `flutter_launcher_icons`
- Splash natif via `flutter_native_splash`
