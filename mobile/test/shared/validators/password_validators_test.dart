import 'package:flutter_test/flutter_test.dart';
import 'package:reactive_forms/reactive_forms.dart';
import 'package:nexustasks_mobile/shared/validators/password_validators.dart';

void main() {
  group('PasswordPolicyValidator', () {
    FormControl<String> control(String? value) => FormControl<String>(
      value: value,
      validators: [PasswordPolicyValidator()],
    );

    test('accepte un mot de passe fort', () {
      expect(control('Password123!').valid, isTrue);
    });

    test('rejette les mots de passe faibles', () {
      expect(control('abc').valid, isFalse);
      expect(control('password').valid, isFalse);
      expect(control('PASSWORD').valid, isFalse);
      expect(control('Password').valid, isFalse);
      expect(control('Password123').valid, isFalse);
    });

    test('ne valide rien si vide (laissé à Validators.required)', () {
      expect(control(null).valid, isTrue);
      expect(control('').valid, isTrue);
    });
  });

  group('PasswordMatchValidator', () {
    FormGroup build(String newP, String confirm) => FormGroup(
      {
        'newPassword': FormControl<String>(value: newP),
        'confirmPassword': FormControl<String>(value: confirm),
      },
      validators: [PasswordMatchValidator()],
    );

    test('valide si identiques', () {
      expect(build('Password123!', 'Password123!').valid, isTrue);
    });

    test('invalide si différents avec la clé passwordMismatch', () {
      final form = build('Password123!', 'Other123!');
      expect(form.valid, isFalse);
      expect(
        form.errors.containsKey(PasswordMatchValidator.validationKey),
        isTrue,
      );
    });
  });
}
