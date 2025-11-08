import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/auth_providers.dart';
import '../providers/data_providers.dart';
import '../widgets/google_sign_in_button.dart';

class SignUpPage extends ConsumerStatefulWidget {
  const SignUpPage({super.key});

  @override
  ConsumerState<SignUpPage> createState() => _SignUpPageState();
}

class _SignUpPageState extends ConsumerState<SignUpPage> {
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

  Future<void> _signUpWithEmail() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    final signUpUseCase = ref.read(signUpWithEmailProvider);
    final result = await signUpUseCase(
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
        (user) {
          // New user - go to onboarding to set up their reading plan
          context.go('/onboarding');
        },
      );
    }
  }

  Future<void> _signUpWithGoogle() async {
    setState(() => _isLoading = true);

    final signInUseCase = ref.read(signInWithGoogleProvider);
    final result = await signInUseCase();

    if (!mounted) return;

    // Handle failure case
    if (result.isLeft()) {
      setState(() => _isLoading = false);
      result.fold(
        (failure) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(failure.message)),
          );
        },
        (_) {}, // Never called
      );
      return;
    }

    // Handle success case
    final user = result.fold(
      (_) => throw Exception('Unreachable'), // Never called
      (user) => user,
    );

    // Check if user has a reading plan
    final repository = ref.read(readingPlanRepositoryProvider);
    final planResult = await repository.getReadingPlan(user.id);

    if (!mounted) return;

    setState(() => _isLoading = false);

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
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 500),
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
                      onPressed: _isLoading? null : _signUpWithEmail,
                      child: _isLoading
                        ? const SizedBox(
                          height: 20,
                          width: 20,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        ) : const Text('Sign Up'),
                    ),
                  ),
                  const SizedBox(height: 16),

                  GoogleSignInButton(
                    onPressed: _signUpWithGoogle,
                    isLoading: _isLoading,
                    text: 'Sign up with Google',
                  ),
                  const SizedBox(height: 16),

                  TextButton(
                    onPressed: () => context.push('/signin'),
                    child: const Text('Already have an account? Sign in'),
                  ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
