import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:url_launcher/url_launcher.dart';
import '../providers/auth_providers.dart';
import '../providers/data_providers.dart';

class OnboardingPage extends ConsumerStatefulWidget {
  const OnboardingPage({super.key});

  @override
  ConsumerState<OnboardingPage> createState() => _OnboardingPageState();
}

class _OnboardingPageState extends ConsumerState<OnboardingPage> {
  int _currentStep = 0;
  String _selectedPlanSystem = 'horner';
  String _selectedPlanType = 'standard';
  bool _fivePsalmsMode = false;
  bool _requireAllListsForStreak = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Welcome to Unquenched Bible'),
      ),
      body: Stepper(
        currentStep: _currentStep,
        onStepContinue: _onStepContinue,
        onStepCancel: _currentStep > 0 ? () => setState(() => _currentStep--) : null,
        controlsBuilder: (context, details) {
          return Padding(
            padding: const EdgeInsets.only(top: 16.0),
            child: Row(
              children: [
                FilledButton(
                  onPressed: details.onStepContinue,
                  child: Text(_currentStep == 4 ? 'Get Started' : 'Continue'),
                ),
                if (_currentStep > 0) ...[
                  const SizedBox(width: 8),
                  TextButton(
                    onPressed: details.onStepCancel,
                    child: const Text('Back'),
                  ),
                ],
              ],
            ),
          );
        },
        steps: [
          Step(
            title: const Text('Account'),
            content: _buildAuthStep(),
            isActive: _currentStep >= 0,
            state: _currentStep > 0 ? StepState.complete : StepState.indexed,
          ),
          Step(
            title: const Text('Choose Reading Plan'),
            content: _buildPlanSystemStep(),
            isActive: _currentStep >= 1,
            state: _currentStep > 1 ? StepState.complete : StepState.indexed,
          ),
          Step(
            title: const Text('Reading Mode'),
            content: _buildPlanTypeStep(),
            isActive: _currentStep >= 2,
            state: _currentStep > 2 ? StepState.complete : StepState.indexed,
          ),
          Step(
            title: const Text('5 Psalms A Day'),
            content: _buildFivePsalmsStep(),
            isActive: _currentStep >= 3,
            state: _currentStep > 3 ? StepState.complete : StepState.indexed,
          ),
          Step(
            title: const Text('Streak Requirements'),
            content: _buildStreakRequirementsStep(),
            isActive: _currentStep >= 4,
            state: _currentStep > 4 ? StepState.complete : StepState.indexed,
          ),
        ],
      ),
    );
  }

  Widget _buildAuthStep() {
    final authState = ref.watch(authStateProvider);
    final isSignedIn = authState.value != null;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (isSignedIn) ...[
          const Text('You are signed in!'),
          const SizedBox(height: 8),
          Text(
            'Your progress will be synced across all your devices.',
            style: Theme.of(context).textTheme.bodyMedium,
          ),
        ] else ...[
          const Text(
            'Create an account to sync your reading plan.',
          ),
          const SizedBox(height: 8),
          Text(
            'An account is only needed to sync your reading progress across all your devices. Your data is securely stored and always available.',
            style: Theme.of(context).textTheme.bodyMedium,
          ),
          const SizedBox(height: 24),
          FilledButton.icon(
            onPressed: () => context.push('/signup'),
            icon: const Icon(Icons.person_add),
            label: const Text('Sign Up'),
          ),
          const SizedBox(height: 12),
          OutlinedButton.icon(
            onPressed: () => context.push('/signin'),
            icon: const Icon(Icons.login),
            label: const Text('Sign In'),
          ),
          const SizedBox(height: 16),
          TextButton.icon(
            onPressed: () async {
              final Uri privacyPolicyUri = Uri.parse('${Uri.base.origin}/privacy_policy.html');
              try {
                await launchUrl(privacyPolicyUri, mode: LaunchMode.inAppBrowserView);
              } catch (e) {
                if (context.mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Could not open Privacy Policy: $e')),
                  );
                }
              }
            },
            icon: const Icon(Icons.privacy_tip_outlined, size: 18),
            label: const Text('Privacy Policy'),
          ),
        ],
      ],
    );
  }

  Widget _buildPlanSystemStep() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Choose your Bible reading plan:'),
        const SizedBox(height: 16),
        RadioListTile<String>(
          title: const Text('Grant Horner\'s Plan'),
          subtitle: const Text('Read 10 chapters daily from different parts of the Bible'),
          value: 'horner',
          groupValue: _selectedPlanSystem,
          onChanged: (value) => setState(() => _selectedPlanSystem = value!),
        ),
        RadioListTile<String>(
          title: const Text('M\'Cheyne Reading Plan'),
          subtitle: const Text('Read through the Bible in one year with 4 readings daily'),
          value: 'mcheyne',
          groupValue: _selectedPlanSystem,
          onChanged: (value) => setState(() => _selectedPlanSystem = value!),
        ),
      ],
    );
  }

  Widget _buildPlanTypeStep() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('How would you like to track your progress?'),
        const SizedBox(height: 16),
        RadioListTile<String>(
          title: const Text('Standard'),
          subtitle: const Text('Each list tracks its own progress independently'),
          value: 'standard',
          groupValue: _selectedPlanType,
          onChanged: (value) => setState(() => _selectedPlanType = value!),
        ),
        RadioListTile<String>(
          title: const Text('Index'),
          subtitle: const Text('All lists share the same reading index (must complete all to advance)'),
          value: 'index',
          groupValue: _selectedPlanType,
          onChanged: (value) => setState(() => _selectedPlanType = value!),
        ),
        RadioListTile<String>(
          title: const Text('Calendar'),
          subtitle: const Text('Readings follow the day of the year (1-365)'),
          value: 'calendar',
          groupValue: _selectedPlanType,
          onChanged: (value) => setState(() => _selectedPlanType = value!),
        ),
      ],
    );
  }

  Widget _buildFivePsalmsStep() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Would you like to enable 5 Psalms A Day?'),
        const SizedBox(height: 8),
        Text(
          _selectedPlanSystem == 'horner'
              ? 'For Horner\'s Plan: This replaces the Psalms list with 5 psalms based on the day of the month.'
              : 'For M\'Cheyne Plan: This adds an additional reading card that shows 5 psalms based on the day of the month.',
          style: Theme.of(context).textTheme.bodyMedium,
        ),
        const SizedBox(height: 16),
        SwitchListTile(
          title: const Text('Enable 5 Psalms A Day'),
          value: _fivePsalmsMode,
          onChanged: (value) => setState(() => _fivePsalmsMode = value),
        ),
      ],
    );
  }

  Widget _buildStreakRequirementsStep() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('How should streak days be counted?'),
        const SizedBox(height: 16),
        RadioListTile<bool>(
          title: const Text('Any list completed'),
          subtitle: const Text('Your streak increases when you complete any reading'),
          value: false,
          groupValue: _requireAllListsForStreak,
          onChanged: (value) => setState(() => _requireAllListsForStreak = value!),
        ),
        RadioListTile<bool>(
          title: const Text('All lists completed'),
          subtitle: const Text('Your streak increases only when you complete all readings for the day'),
          value: true,
          groupValue: _requireAllListsForStreak,
          onChanged: (value) => setState(() => _requireAllListsForStreak = value!),
        ),
      ],
    );
  }

  void _onStepContinue() async {
    final authState = ref.watch(authStateProvider);
    final isSignedIn = authState.value != null;

    // Step 0: Auth step - advance if signed in or skip
    if (_currentStep == 0) {
      if (isSignedIn) {
        setState(() => _currentStep++);
      }
      // If not signed in, user must explicitly click "Continue without account"
      return;
    }

    // Steps 1-3: Just advance
    if (_currentStep < 4) {
      setState(() => _currentStep++);
      return;
    }

    // Step 4: Final step - create the reading plan
    if (_currentStep == 4) {
      await _completeOnboarding();
    }
  }

  Future<void> _completeOnboarding() async {
    final authState = ref.watch(authStateProvider);
    final user = authState.value;

    // Authentication is required
    if (user == null) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Please sign in to continue')),
        );
      }
      return;
    }

    try {
      final repository = ref.read(readingPlanRepositoryProvider);

      // Create the reading plan with selected settings
      await repository.updatePlan(user.id, _selectedPlanSystem, _selectedPlanType);

      // Apply the reading mode
      if (_selectedPlanSystem == 'horner') {
        await repository.setReadingMode(user.id, _selectedPlanType);
        await repository.toggleFivePsalmsMode(user.id, _fivePsalmsMode);
        await repository.toggleRequireAllListsForStreak(user.id, _requireAllListsForStreak);
      } else {
        await repository.setMcheyneReadingMode(user.id, _selectedPlanType);
        await repository.toggleMcheyneFivePsalmsMode(user.id, _fivePsalmsMode);
        await repository.toggleMcheyneRequireAllListsForStreak(user.id, _requireAllListsForStreak);
      }

      if (mounted) {
        // Navigate to home page
        context.go('/home');
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error setting up plan: $e')),
        );
      }
    }
  }
}
