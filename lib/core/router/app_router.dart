import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../presentation/pages/home_page.dart';
import '../../presentation/pages/splash_page.dart';
import '../../presentation/pages/sign_in_page.dart';
import '../../presentation/pages/sign_up_page.dart';
import '../../presentation/pages/statistics_page.dart';
import '../../presentation/pages/settings_page.dart';
import '../../presentation/pages/plan_selection_page.dart';
import '../../presentation/pages/account_settings_page.dart';
import '../../presentation/pages/manual_override_page.dart';
import '../../presentation/pages/onboarding_page.dart';

class AppRouter {
  static GoRouter router(WidgetRef ref) {
  return GoRouter(
    initialLocation: '/',
    redirect: (context, state) {
      // No authentication required - users can use the app without an account
      // Onboarding check will be handled by the splash page
      return null;
    },
    routes: [
      GoRoute(
        path: '/',
        builder: (context, state) => const SplashPage(),
      ),
      GoRoute(
        path: '/onboarding',
        builder: (context, state) => const OnboardingPage(),
      ),
      GoRoute(
        path: '/signin',
        builder: (context, state) => const SignInPage(),
      ),
      GoRoute(
        path: '/login',
        builder: (context, state) => const SignInPage(),
      ),
      GoRoute(
        path: '/signup',
        builder: (context, state) => const SignUpPage(),
      ),
      GoRoute(
        path: '/home',
        builder: (context, state) => const HomePage(),
      ),
      GoRoute(
        path: '/stats',
        builder: (context, state) => const StatisticsPage(),
      ),
      GoRoute(
        path: '/settings',
        builder: (context, state) => const SettingsPage(),
      ),
      GoRoute(
        path: '/plan-selection',
        builder: (context, state) => const PlanSelectionPage(),
      ),
      GoRoute(
        path: '/account-settings',
        builder: (context, state) => const AccountSettingsPage(),
      ),
      GoRoute(
        path: '/manual-override',
        builder: (context, state) => const ManualOverridePage(),
      ),
    ],
  );
  }
}
