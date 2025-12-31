import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:google_sign_in_web/web_only.dart' as web;
import '../providers/auth_providers.dart';
import '../providers/data_providers.dart';

class SignInPage extends ConsumerStatefulWidget {
  const SignInPage({super.key});

  @override
  ConsumerState<SignInPage> createState() => _SignInPageState();
}

class _SignInPageState extends ConsumerState<SignInPage> {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _isLoading = false;

  @override
  void dispose() {
      _emailController.dispose();
      _passwordController.dispose();
      super.dispose();
  }

  Future<void> _signInWithEmail() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    final signInUseCase = ref.read(signInWithEmailProvider);
    final result = await signInUseCase(
      _emailController.text.trim(),
      _passwordController.text,
    );

    if (mounted) {
      setState(() => _isLoading = false);
      result.fold(
        (failure) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(failure.message)),
          );
        },
        (user) async {
          // Check if user has a reading plan
          final repository = ref.read(readingPlanRepositoryProvider);
          final planResult = await repository.getReadingPlan(user.id);

          if (!mounted) return;

          planResult.fold(
            (failure) {
              // No plan exists - go to onboarding
              context.go('/onboarding');
            },
            (plan) {
              // Plan exists - go to home
              context.go('/home');
            },
          );
        },
      );
    }
  }

  Future<void> _signInWithGoogle() async {
      setState(() => _isLoading = true);

      final signInUseCase = ref.read(signInWithGoogleProvider);
      final result = await signInUseCase();

      if (mounted) {
        setState(() => _isLoading = false);

        result.fold(
          (failure) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(failure.message)),
            );
          },
          (user) async {
            // Check if user has a reading plan
            final repository = ref.read(readingPlanRepositoryProvider);
            final planResult = await repository.getReadingPlan(user.id);

            if (!mounted) return;

            planResult.fold(
              (failure) {
                // No plan exists - go to onboarding
                context.go('/onboarding');
              },
              (plan) {
                // Plan exists - go to home
                context.go('/home');
              },
            );
          },
        );
      }
    }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: Form(
              key: _formKey,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.book, size: 80),
                  const SizedBox(height: 16),
                  const Text(
                    'Unquenched Bible',
                    style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 48),

                  TextFormField(
                    controller: _emailController,
                    keyboardType: TextInputType.emailAddress,
                    decoration: const InputDecoration(
                      labelText: 'Email',
                      border: OutlineInputBorder(),
                    ),
                    validator: (value) {
                        if (value == null || value.isEmpty) {
                            return 'Please enter email';
                        }
                        if (!value.contains('@')) {
                            return 'Please enter valid email';
                        }
                        return null;
                      },
                  ),
                  const SizedBox(height: 16),

                  TextFormField(
                    controller: _passwordController,
                    obscureText: true,
                    decoration: const InputDecoration(
                      labelText: 'Password',
                      border: OutlineInputBorder(),
                    ),
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return 'Please enter password';
                      }
                      if (value.length < 6) {
                        return 'Password must be at least 6 characters';
                      }
                      return null;
                    },
                  ),
                  const SizedBox(height: 24),

                  SizedBox(
                    width: double.infinity,
                    child: FilledButton(
                      onPressed: _isLoading? null : _signInWithEmail,
                      child: _isLoading
                        ? const SizedBox(
                          height: 20,
                          width: 20,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        ) : const Text('Sign In'),
                    ),
                  ),
                  const SizedBox(height: 16),

                  // Platform-specific Google Sign-In button
                  if (kIsWeb)
                    // Web: Use Google's official renderButton
                    web.renderButton()
                  else
                    // Mobile: Use custom button
                    SizedBox(
                      width: double.infinity,
                      child: OutlinedButton.icon(
                        onPressed: _isLoading ? null : _signInWithGoogle,
                        icon: const Icon(Icons.login),
                        label: const Text('Sign in with Google'),
                      ),
                    ),
                  const SizedBox(height: 16),

                  TextButton(
                    onPressed: () => context.push('/signup'),
                    child: const Text('Don\'t have an account? Sign up'),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
