import 'package:flutter_test/flutter_test.dart';
import 'package:nexustasks_mobile/core/config/app_config.dart';
import 'package:nexustasks_mobile/core/utils/url_utils.dart';

void main() {
  group('resolveAvatarUrl', () {
    test('null ou vide retourne null', () {
      expect(resolveAvatarUrl(null), isNull);
      expect(resolveAvatarUrl(''), isNull);
    });

    test('une URL absolue est inchangée', () {
      expect(
        resolveAvatarUrl('https://cdn.example.com/a.png'),
        'https://cdn.example.com/a.png',
      );
    });

    test('un chemin backend est préfixé par l\'origine (pas double /api)', () {
      expect(
        resolveAvatarUrl('/api/files/avatars/a.png'),
        '${AppConfig.apiOrigin}/api/files/avatars/a.png',
      );
    });
  });
}
