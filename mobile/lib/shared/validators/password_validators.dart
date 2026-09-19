import 'package:reactive_forms/reactive_forms.dart';

/// Politique identique au backend et au web.
class PasswordPolicyValidator extends Validator<dynamic> {
  static const String validationKey = 'passwordPolicy';

  static final RegExp _policy = RegExp(
    r'^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$',
  );

  @override
  Map<String, dynamic>? validate(AbstractControl<dynamic> control) {
    final value = control.value as String?;
    if (value == null || value.isEmpty) return null;
    return _policy.hasMatch(value) ? null : {validationKey: true};
  }
}

/// Validateur de niveau FormGroup : compare newPassword et confirmPassword.
class PasswordMatchValidator extends Validator<dynamic> {
  static const String validationKey = 'passwordMismatch';

  @override
  Map<String, dynamic>? validate(AbstractControl<dynamic> control) {
    if (control is! FormGroup) return null;
    final newPassword = control.control('newPassword').value as String?;
    final confirmPassword = control.control('confirmPassword').value as String?;
    if (newPassword == null ||
        confirmPassword == null ||
        confirmPassword.isEmpty) {
      return null;
    }
    return newPassword == confirmPassword ? null : {validationKey: true};
  }
}
