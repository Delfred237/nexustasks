import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:reactive_forms/reactive_forms.dart';

import '../../../../core/auth/auth_notifier.dart';
import '../../../../core/utils/error_utils.dart';

class VerifyEmailScreen extends ConsumerStatefulWidget {
  const VerifyEmailScreen({super.key, this.initialEmail});

  final String? initialEmail;

  @override
  ConsumerState<VerifyEmailScreen> createState() => _VerifyEmailScreenState();
}

class _VerifyEmailScreenState extends ConsumerState<VerifyEmailScreen> {
  late final FormGroup form;
  bool _submitting = false;
  bool _resending = false;

  @override
  void initState() {
    super.initState();
    form = FormGroup({
      'email': FormControl<String>(
        value: widget.initialEmail,
        validators: [Validators.required, Validators.email],
      ),
      'code': FormControl<String>(
        validators: [
          Validators.required,
          Validators.pattern(RegExp(r'^\d{6}$')),
        ],
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
      await ref.read(authProvider.notifier).verifyEmail(
            email: form.control('email').value as String,
            code: form.control('code').value as String,
          );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Email verified! You can now sign in.')),
        );
        context.go('/login');
      }
    } catch (error) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(describeError(error))),
        );
      }
    } finally {
      if (mounted) setState(() => _submitting = false);
    }
  }

  Future<void> _resend() async {
    final emailControl = form.control('email');
    if (emailControl.invalid) {
      emailControl.markAsTouched();
      return;
    }

    setState(() => _resending = true);
    try {
      await ref
          .read(authProvider.notifier)
          .resendVerification(emailControl.value as String);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('New code sent! Check your inbox.')),
        );
      }
    } catch (error) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(describeError(error))),
        );
      }
    } finally {
      if (mounted) setState(() => _resending = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Verify email')),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: ReactiveForm(
            formGroup: form,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Icon(
                  Icons.mark_email_read_outlined,
                  size: 64,
                  color: Theme.of(context).colorScheme.primary,
                ),
                const SizedBox(height: 16),
                Text(
                  'Check your inbox',
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                ),
                const SizedBox(height: 8),
                Text(
                  'We sent a 6-digit verification code to your email address.',
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: Theme.of(context).colorScheme.onSurfaceVariant,
                      ),
                ),
                const SizedBox(height: 32),
                ReactiveTextField(
                  formControlName: 'email',
                  keyboardType: TextInputType.emailAddress,
                  decoration: const InputDecoration(
                    labelText: 'Email',
                    prefixIcon: Icon(Icons.email_outlined),
                  ),
                  validationMessages: {
                    ValidationMessage.required: (_) => 'Email is required',
                    ValidationMessage.email: (_) => 'Enter a valid email address',
                  },
                ),
                const SizedBox(height: 16),
                ReactiveTextField(
                  formControlName: 'code',
                  keyboardType: TextInputType.number,
                  maxLength: 6,
                  textAlign: TextAlign.center,
                  style: const TextStyle(fontSize: 24, letterSpacing: 8),
                  decoration: const InputDecoration(
                    labelText: 'Verification code',
                    counterText: '',
                  ),
                  validationMessages: {
                    ValidationMessage.required: (_) => 'Code is required',
                    ValidationMessage.pattern: (_) => 'Code must be 6 digits',
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
                      : const Text('Verify Email'),
                ),
                const SizedBox(height: 8),
                OutlinedButton(
                  onPressed: _resending ? null : _resend,
                  child: Text(_resending ? 'Sending...' : 'Resend Code'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}