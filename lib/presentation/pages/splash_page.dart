import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/data_providers.dart';

class SplashPage extends ConsumerStatefulWidget {
  const SplashPage({super.key});

  @override
  ConsumerState<SplashPage> createState() => _SplashPageState();
}

class _SplashPageState extends ConsumerState<SplashPage> {
  @override
  void initState() {
    super.initState();
    _navigate();
  }

  Future<void> _navigate() async {
    await Future.delayed(const Duration(seconds: 2));

    if (!mounted) return;

    try {
      // Check if onboarding is needed with a timeout
      final needsOnboarding = await ref.read(needsOnboardingProvider.future)
          .timeout(
            const Duration(seconds: 5),
            onTimeout: () => true, // Default to showing onboarding on timeout
          );

      if (!mounted) return;

      if (needsOnboarding) {
        context.go('/onboarding');
      } else {
        context.go('/home');
      }
    } catch (e) {
      // If there's an error, assume we need onboarding
      if (mounted) {
        context.go('/onboarding');
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.book, size: 80),
            SizedBox(height: 16),
            Text(
              'Unquenched Bible',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
          ],
        ),
      ),
    );
  }
}

