import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../domain/entities/reading_plan_entity.dart';
import 'auth_providers.dart';
import 'data_providers.dart';

/// Provider for reading plan statistics
final statisticsProvider = FutureProvider<ReadingPlanEntity?>((ref) async {
  final authState = ref.watch(authStateProvider);
  final user = authState.value;

  if (user == null) return null;

  final repository = ref.read(readingPlanRepositoryProvider);
  final result = await repository.getReadingPlan(user.id);

  return result.fold(
    (failure) => null,
    (plan) => plan,
  );
});

/// Provider for current streak
final currentStreakProvider = Provider<int>((ref) {
  final statistics = ref.watch(statisticsProvider);
  return statistics.when(
    data: (plan) => plan?.currentStreak ?? 0,
    loading: () => 0,
    error: (_, _) => 0,
  );
});

/// Provider for longest streak
final longestStreakProvider = Provider<int>((ref) {
  final statistics = ref.watch(statisticsProvider);
  return statistics.when(
    data: (plan) => plan?.longestStreak ?? 0,
    loading: () => 0,
    error: (_, _) => 0,
  );
});
