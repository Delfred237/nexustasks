import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:reactive_forms/reactive_forms.dart';

import '../../../../core/auth/auth_notifier.dart';
import '../../../../core/utils/error_utils.dart';

/// Même politique de mot de passe que le backend et le web.
/// Même politique de mot de passe que le backend et le web.
///
/// reactive_forms 18.x exige des instances de `Validator<T>` (classe abstraite),
/// pas des fonctions nues : la liste `validators` est typée `List<Validator<dynamic>>`.
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

class RegisterScreen extends ConsumerStatefulWidget {
  const RegisterScreen({super.key});

  @override
  ConsumerState<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends ConsumerState<RegisterScreen> {
  late final FormGroup form;
  bool _submitting = false;

  @override
  void initState() {
    super.initState();
    form = FormGroup({
      'firstName': FormControl<String>(validators: [Validators.required]),
      'lastName': FormControl<String>(validators: [Validators.required]),
      'email': FormControl<String>(
        validators: [Validators.required, Validators.email],
      ),
      'password': FormControl<String>(
        validators: [Validators.required, PasswordPolicyValidator()],
      ),
    });
  }

  @override
  void dispose() {
    form.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!form.valid) {
      form.markAllAsTouched();
      return;
    }

    setState(() => _submitting = true);
    try {
      final email = form.control('email').value as String;
      await ref
          .read(authProvider.notifier)
          .register(
            firstName: form.control('firstName').value as String,
            lastName: form.control('lastName').value as String,
            email: email,
            password: form.control('password').value as String,
          );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Account created! Check your email for the code.'),
          ),
        );
        context.go('/verify-email', extra: email);
      }
    } catch (error) {
      if (mounted) {
        ScaffoldMessenger.of(context)
            .showSnackBar(SnackBar(content: Text(describeError(error))));
      }
    } finally {
      if (mounted) setState(() => _submitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Create account')),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: ReactiveForm(
            formGroup: form,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                ReactiveTextField(
                  formControlName: 'firstName',
                  textInputAction: TextInputAction.next,
                  decoration: const InputDecoration(
                    labelText: 'First Name',
                    prefixIcon: Icon(Icons.person_outline),
                  ),
                  validationMessages: {
                    ValidationMessage.required: (_) => 'First name is required',
                  },
                ),
                const SizedBox(height: 16),
                ReactiveTextField(
                  formControlName: 'lastName',
                  textInputAction: TextInputAction.next,
                  decoration: const InputDecoration(
                    labelText: 'Last Name',
                    prefixIcon: Icon(Icons.badge_outlined),
                  ),
                  validationMessages: {
                    ValidationMessage.required: (_) => 'Last name is required',
                  },
                ),
                const SizedBox(height: 16),
                ReactiveTextField(
                  formControlName: 'email',
                  keyboardType: TextInputType.emailAddress,
                  textInputAction: TextInputAction.next,
                  decoration: const InputDecoration(
                    labelText: 'Email',
                    prefixIcon: Icon(Icons.email_outlined),
                  ),
                  validationMessages: {
                    ValidationMessage.required: (_) => 'Email is required',
                    ValidationMessage.email: (_) =>
                        'Enter a valid email address',
                  },
                ),
                const SizedBox(height: 16),
                ReactiveTextField(
                  formControlName: 'password',
                  obscureText: true,
                  decoration: const InputDecoration(
                    labelText: 'Password',
                    prefixIcon: Icon(Icons.lock_outline),
                  ),
                  validationMessages: {
                    ValidationMessage.required: (_) => 'Password is required',
                    'passwordPolicy': (_) =>
                        'Min 8 chars with upper, lower, digit and special char',
                  },
                ),
                const SizedBox(height: 24),
                FilledButton(
                  onPressed: _submitting ? null : _submit,
                  child: _submitting
                      ? const SizedBox(
                          height: 20,
                          width: 20,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        )
                      : const Text('Create Account'),
                ),
                const SizedBox(height: 16),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      'Already have an account?',
                      style: Theme.of(context).textTheme.bodyMedium,
                    ),
                    TextButton(
                      onPressed: () => context.go('/login'),
                      child: const Text('Sign in'),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
