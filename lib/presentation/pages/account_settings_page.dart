import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/auth_providers.dart';
import '../providers/data_providers.dart';

class AccountSettingsPage extends ConsumerWidget {
  const AccountSettingsPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authStateProvider);
    final user = authState.value;

    // Determine if user is signed in with email/password or Google
    // Firebase provider IDs: 'password' for email/password, 'google.com' for Google
    final firebaseAuth = ref.read(firebaseAuthProvider);
    final currentUser = firebaseAuth.currentUser;
    final isEmailPasswordUser = currentUser?.providerData.any(
      (provider) => provider.providerId == 'password'
    ) ?? false;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Account Settings'),
      ),
      body: ListView(
        children: [
          if (user != null) ...[
            ListTile(
              title: const Text('Email'),
              subtitle: Text(user.email),
              leading: const Icon(Icons.email),
            ),
            const Divider(),

            if (isEmailPasswordUser) ...[
              ListTile(
                title: const Text('Change Email'),
                leading: const Icon(Icons.email_outlined),
                trailing: const Icon(Icons.chevron_right),
                onTap: () => _showChangeEmailDialog(context, ref),
              ),
              ListTile(
                title: const Text('Change Password'),
                leading: const Icon(Icons.lock_outline),
                trailing: const Icon(Icons.chevron_right),
                onTap: () => _showChangePasswordDialog(context, ref),
              ),
              const Divider(),
            ],

            ListTile(
              title: const Text('Delete Account'),
              textColor: Colors.red,
              leading: const Icon(Icons.delete_forever, color: Colors.red),
              trailing: const Icon(Icons.chevron_right, color: Colors.red),
              onTap: () => _showDeleteAccountDialog(context, ref),
            ),
          ],
        ],
      ),
      bottomNavigationBar: BottomAppBar(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          child: ElevatedButton.icon(
            onPressed: () => context.go('/settings'),
            icon: const Icon(Icons.arrow_back),
            label: const Text('Back to Settings'),
            style: ElevatedButton.styleFrom(
              padding: const EdgeInsets.symmetric(vertical: 12),
            ),
          ),
        ),
      ),
    );
  }

  void _showChangeEmailDialog(BuildContext context, WidgetRef ref) {
    final emailController = TextEditingController();
    final passwordController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Change Email'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text('Enter your new email address and current password to confirm.'),
            const SizedBox(height: 16),
            TextField(
              controller: emailController,
              decoration: const InputDecoration(
                labelText: 'New Email',
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.emailAddress,
            ),
            const SizedBox(height: 8),
            TextField(
              controller: passwordController,
              decoration: const InputDecoration(
                labelText: 'Current Password',
                border: OutlineInputBorder(),
              ),
              obscureText: true,
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () async {
              final newEmail = emailController.text.trim();
              final password = passwordController.text;

              if (newEmail.isEmpty || password.isEmpty) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Please fill in all fields')),
                );
                return;
              }

              if (!context.mounted) return;
              Navigator.of(context).pop();

              // Implement email change
              final authRepo = ref.read(authRepositoryProvider);
              final result = await authRepo.updateEmail(newEmail, password);

              if (context.mounted) {
                result.fold(
                  (failure) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('Failed to update email: ${failure.message}')),
                    );
                  },
                  (_) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Email updated successfully! Please verify your new email.')),
                    );
                  },
                );
              }
            },
            child: const Text('Change Email'),
          ),
        ],
      ),
    );
  }

  void _showChangePasswordDialog(BuildContext context, WidgetRef ref) {
    final currentPasswordController = TextEditingController();
    final newPasswordController = TextEditingController();
    final confirmPasswordController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Change Password'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: currentPasswordController,
              decoration: const InputDecoration(
                labelText: 'Current Password',
                border: OutlineInputBorder(),
              ),
              obscureText: true,
            ),
            const SizedBox(height: 8),
            TextField(
              controller: newPasswordController,
              decoration: const InputDecoration(
                labelText: 'New Password',
                border: OutlineInputBorder(),
              ),
              obscureText: true,
            ),
            const SizedBox(height: 8),
            TextField(
              controller: confirmPasswordController,
              decoration: const InputDecoration(
                labelText: 'Confirm New Password',
                border: OutlineInputBorder(),
              ),
              obscureText: true,
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () async {
              final currentPassword = currentPasswordController.text;
              final newPassword = newPasswordController.text;
              final confirmPassword = confirmPasswordController.text;

              if (currentPassword.isEmpty || newPassword.isEmpty || confirmPassword.isEmpty) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Please fill in all fields')),
                );
                return;
              }

              if (newPassword != confirmPassword) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('New passwords do not match')),
                );
                return;
              }

              if (newPassword.length < 6) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Password must be at least 6 characters')),
                );
                return;
              }

              if (!context.mounted) return;
              Navigator.of(context).pop();

              // Implement password change
              final authRepo = ref.read(authRepositoryProvider);
              final result = await authRepo.updatePassword(newPassword, currentPassword);

              if (context.mounted) {
                result.fold(
                  (failure) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('Failed to update password: ${failure.message}')),
                    );
                  },
                  (_) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Password updated successfully!')),
                    );
                  },
                );
              }
            },
            child: const Text('Change Password'),
          ),
        ],
      ),
    );
  }

  void _showDeleteAccountDialog(BuildContext context, WidgetRef ref) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Account'),
        content: const Text(
          'Are you sure you want to delete your account? This action cannot be undone. All your reading progress will be permanently deleted.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () async {
              if (!context.mounted) return;
              Navigator.of(context).pop();

              // Get user ID before deletion
              final authState = ref.read(authStateProvider);
              final userId = authState.value?.id;

              if (userId == null) {
                if (context.mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Error: User not found')),
                  );
                }
                return;
              }

              // Delete user data from Firestore first
              final readingPlanRepo = ref.read(readingPlanRepositoryProvider);
              final deleteDataResult = await readingPlanRepo.deleteUserData(userId);

              // Then delete the Firebase Auth user
              final authRepo = ref.read(authRepositoryProvider);
              final deleteAuthResult = await authRepo.deleteAccount();

              if (context.mounted) {
                // Check if both operations succeeded
                deleteDataResult.fold(
                  (failure) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('Failed to delete user data: ${failure.message}')),
                    );
                  },
                  (_) {
                    deleteAuthResult.fold(
                      (failure) {
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(content: Text('Failed to delete account: ${failure.message}')),
                        );
                      },
                      (_) {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('Account deleted successfully')),
                        );
                        // Account is deleted, user is signed out, navigate to sign-in page
                        if (context.mounted) {
                          context.go('/signin');
                        }
                      },
                    );
                  },
                );
              }
            },
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: const Text('Delete Account'),
          ),
        ],
      ),
    );
  }
}
