import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:image_picker/image_picker.dart';
import 'package:nexustasks_mobile/core/providers/core_providers.dart';
import 'package:reactive_forms/reactive_forms.dart';

import '../../../../core/auth/auth_notifier.dart';
import '../../../../core/auth/auth_repository.dart';
import '../../../../core/utils/error_utils.dart';
import '../../../../shared/validators/password_validators.dart';
import '../../../../shared/widgets/user_avatar.dart';
import '../../domain/providers/profile_provider.dart';

class ProfileScreen extends ConsumerStatefulWidget {
  const ProfileScreen({super.key});

  @override
  ConsumerState<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends ConsumerState<ProfileScreen> {
  late final FormGroup profileForm;
  late final FormGroup passwordForm;
  bool _savingProfile = false;
  bool _changingPassword = false;
  bool _uploadingAvatar = false;

  @override
  void initState() {
    super.initState();
    final user = ref.read(authProvider).user;
    profileForm = FormGroup({
      'firstName': FormControl<String>(
        value: user?.firstName,
        validators: [Validators.required],
      ),
      'lastName': FormControl<String>(
        value: user?.lastName,
        validators: [Validators.required],
      ),
    });
    passwordForm = FormGroup(
      {
        'currentPassword': FormControl<String>(
          validators: [Validators.required],
        ),
        'newPassword': FormControl<String>(
          validators: [Validators.required, PasswordPolicyValidator()],
        ),
        'confirmPassword': FormControl<String>(
          validators: [Validators.required],
        ),
      },
      validators: [PasswordMatchValidator()],
    );
  }

  @override
  void dispose() {
    profileForm.dispose();
    passwordForm.dispose();
    super.dispose();
  }

  void _snack(String message) {
    if (mounted) {
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text(message)));
    }
  }

  Future<void> _refreshUser() async {
    final fresh = await AuthRepository(ref.read(dioProvider)).me();
    ref.read(authProvider.notifier).setUser(fresh);
  }

  Future<void> _pickAndUploadAvatar() async {
    final picked = await ImagePicker().pickImage(
      source: ImageSource.gallery,
      maxWidth: 1024,
      maxHeight: 1024,
      imageQuality: 85,
    );
    if (picked == null) return;

    setState(() => _uploadingAvatar = true);
    try {
      await ref.read(profileRepositoryProvider).uploadAvatar(picked);
      await _refreshUser();
      _snack('Avatar updated');
    } catch (error) {
      _snack(describeError(error));
    } finally {
      if (mounted) setState(() => _uploadingAvatar = false);
    }
  }

  Future<void> _removeAvatar() async {
    setState(() => _uploadingAvatar = true);
    try {
      await ref.read(profileRepositoryProvider).deleteAvatar();
      await _refreshUser();
      _snack('Avatar removed');
    } catch (error) {
      _snack(describeError(error));
    } finally {
      if (mounted) setState(() => _uploadingAvatar = false);
    }
  }

  Future<void> _saveProfile() async {
    if (!profileForm.valid) {
      profileForm.markAllAsTouched();
      return;
    }
    setState(() => _savingProfile = true);
    try {
      final updated = await ref
          .read(profileRepositoryProvider)
          .updateProfile(
            firstName: profileForm.control('firstName').value as String,
            lastName: profileForm.control('lastName').value as String,
          );
      ref.read(authProvider.notifier).setUser(updated);
      _snack('Profile updated');
    } catch (error) {
      _snack(describeError(error));
    } finally {
      if (mounted) setState(() => _savingProfile = false);
    }
  }

  Future<void> _changePassword() async {
    if (!passwordForm.valid) {
      passwordForm.markAllAsTouched();
      return;
    }
    setState(() => _changingPassword = true);
    try {
      final response = await ref
          .read(profileRepositoryProvider)
          .changePassword(
            currentPassword:
                passwordForm.control('currentPassword').value as String,
            newPassword: passwordForm.control('newPassword').value as String,
          );
      passwordForm.reset();

      if (mounted) {
        await showDialog<void>(
          context: context,
          builder: (ctx) => AlertDialog(
            title: const Text('Password changed'),
            content: Text(
              response.otherSessionsRevoked
                  ? 'All other sessions were revoked. Please sign in again.'
                  : 'Please sign in again.',
            ),
            actions: [
              FilledButton(
                onPressed: () => Navigator.of(ctx).pop(),
                child: const Text('OK'),
              ),
            ],
          ),
        );
        await ref.read(authProvider.notifier).logout();
      }
    } catch (error) {
      _snack(describeError(error));
    } finally {
      if (mounted) setState(() => _changingPassword = false);
    }
  }

  Future<void> _confirmLogout() async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Sign out?'),
        content: const Text(
          'You will need to sign in again to access your tasks.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(ctx).pop(false),
            child: const Text('Cancel'),
          ),
          FilledButton.tonal(
            onPressed: () => Navigator.of(ctx).pop(true),
            child: const Text('Sign out'),
          ),
        ],
      ),
    );
    if (confirmed == true && mounted) {
      await ref.read(authProvider.notifier).logout();
    }
  }

  @override
  Widget build(BuildContext context) {
    final user = ref.watch(authProvider).user;
    if (user == null) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    final mismatch = passwordForm.errors.containsKey(
      PasswordMatchValidator.validationKey,
    );

    return Scaffold(
      appBar: AppBar(title: const Text('Profile')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // ---------- Avatar ----------
          Card(
            child: Padding(
              padding: const EdgeInsets.all(24),
              child: Column(
                children: [
                  UserAvatar(
                    url: user.avatarUrl,
                    firstName: user.firstName,
                    lastName: user.lastName,
                    size: 88,
                  ),
                  const SizedBox(height: 16),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Expanded(
                        child: OutlinedButton.icon(
                          onPressed: _uploadingAvatar
                              ? null
                              : _pickAndUploadAvatar,
                          icon: _uploadingAvatar
                              ? const SizedBox(
                                  width: 16,
                                  height: 16,
                                  child: CircularProgressIndicator(
                                    strokeWidth: 2,
                                  ),
                                )
                              : const Icon(Icons.photo_outlined, size: 18),
                          label: const Text('Change photo'),
                        ),
                      ),
                      if (user.avatarUrl != null) ...[
                        const SizedBox(width: 8),
                        IconButton.outlined(
                          onPressed: _uploadingAvatar ? null : _removeAvatar,
                          icon: const Icon(Icons.delete_outline),
                          tooltip: 'Remove photo',
                        ),
                      ],
                    ],
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // ---------- Personal info ----------
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: ReactiveForm(
                formGroup: profileForm,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Text(
                      'Personal information',
                      style: Theme.of(context).textTheme.titleMedium
                          ?.copyWith(fontWeight: FontWeight.bold),
                    ),
                    const Divider(height: 24),
                    ReactiveTextField(
                      formControlName: 'firstName',
                      decoration: const InputDecoration(
                        labelText: 'First Name',
                      ),
                      validationMessages: {
                        ValidationMessage.required: (_) =>
                            'First name is required',
                      },
                    ),
                    const SizedBox(height: 16),
                    ReactiveTextField(
                      formControlName: 'lastName',
                      decoration: const InputDecoration(labelText: 'Last Name'),
                      validationMessages: {
                        ValidationMessage.required: (_) =>
                            'Last name is required',
                      },
                    ),
                    const SizedBox(height: 16),
                    FilledButton(
                      onPressed: _savingProfile ? null : _saveProfile,
                      child: _savingProfile
                          ? const SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(strokeWidth: 2),
                            )
                          : const Text('Save changes'),
                    ),
                  ],
                ),
              ),
            ),
          ),
          const SizedBox(height: 16),

          // ---------- Security ----------
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: ReactiveForm(
                formGroup: passwordForm,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Text(
                      'Change password',
                      style: Theme.of(context).textTheme.titleMedium
                          ?.copyWith(fontWeight: FontWeight.bold),
                    ),
                    const Divider(height: 24),
                    ReactiveTextField(
                      formControlName: 'currentPassword',
                      obscureText: true,
                      decoration: const InputDecoration(
                        labelText: 'Current password',
                      ),
                      validationMessages: {
                        ValidationMessage.required: (_) =>
                            'Current password is required',
                      },
                    ),
                    const SizedBox(height: 16),
                    ReactiveTextField(
                      formControlName: 'newPassword',
                      obscureText: true,
                      decoration: const InputDecoration(
                        labelText: 'New password',
                      ),
                      validationMessages: {
                        ValidationMessage.required: (_) =>
                            'New password is required',
                        PasswordPolicyValidator.validationKey: (_) => 'Min 8 chars with upper, lower, digit and special char',
                      },
                    ),
                    const SizedBox(height: 16),
                    ReactiveTextField(
                      formControlName: 'confirmPassword',
                      obscureText: true,
                      decoration: const InputDecoration(
                        labelText: 'Confirm new password',
                      ),
                      validationMessages: {
                        ValidationMessage.required: (_) =>
                            'Confirmation is required',
                      },
                    ),
                    if (mismatch)
                      Padding(
                        padding: const EdgeInsets.only(top: 6),
                        child: Text(
                          'Passwords do not match',
                          style: TextStyle(
                            color: Theme.of(context).colorScheme.error,
                            fontSize: 12,
                          ),
                        ),
                      ),
                    const SizedBox(height: 16),
                    FilledButton(
                      onPressed: _changingPassword ? null : _changePassword,
                      child: _changingPassword
                          ? const SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(strokeWidth: 2),
                            )
                          : const Text('Change password'),
                    ),
                  ],
                ),
              ),
            ),
          ),
          const SizedBox(height: 16),

          // ---------- Account ----------
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Account',
                    style: Theme.of(context).textTheme.titleMedium
                        ?.copyWith(fontWeight: FontWeight.bold),
                  ),
                  const Divider(height: 24),
                  _InfoRow(label: 'Email', value: user.email),
                  const SizedBox(height: 12),
                  _InfoRow(label: 'Role', value: user.role),
                  const SizedBox(height: 12),
                  _InfoRow(
                    label: 'Email verified',
                    value: user.emailVerified ? '✓ Yes' : '✗ No',
                    valueColor: user.emailVerified
                        ? Colors.green
                        : Theme.of(context).colorScheme.error,
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          OutlinedButton.icon(
            onPressed: _confirmLogout,
            icon: const Icon(Icons.logout),
            label: const Text('Sign out'),
            style: OutlinedButton.styleFrom(
              foregroundColor: Theme.of(context).colorScheme.error,
              side: BorderSide(color: Theme.of(context).colorScheme.error),
              minimumSize: const Size.fromHeight(50),
            ),
          ),
          const SizedBox(height: 32),
          Center(
            child: Text(
              'NexusTasks v1.0.0',
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: Theme.of(context).colorScheme.onSurfaceVariant,
              ),
            ),
          ),
          const SizedBox(height: 16),
        ],
      ),
    );
  }
}

class _InfoRow extends StatelessWidget {
  const _InfoRow({required this.label, required this.value, this.valueColor});

  final String label;
  final String value;
  final Color? valueColor;

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          width: 120,
          child: Text(
            label,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: Theme.of(context).colorScheme.onSurfaceVariant,
            ),
          ),
        ),
        Expanded(
          child: Text(
            value,
            style: Theme.of(context).textTheme.bodyMedium
                ?.copyWith(color: valueColor, fontWeight: FontWeight.w500),
          ),
        ),
      ],
    );
  }
}
