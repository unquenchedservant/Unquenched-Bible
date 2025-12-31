import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../domain/entities/reading_list_entity.dart';
import '../../domain/usecases/get_todays_reading.dart';
import '../../domain/usecases/mark_reading_complete.dart';
import '../../domain/usecases/reset_list_completion.dart';
import '../../domain/usecases/check_daily_reset.dart';
import 'auth_providers.dart';
import 'data_providers.dart';

final getTodaysReadingsProvider = Provider<GetTodaysReadings>((ref) {
  final repository = ref.read(readingPlanRepositoryProvider);
  return GetTodaysReadings(repository);
});

final markReadingCompleteUseCaseProvider = Provider<MarkReadingComplete>((ref) {
  final repository = ref.read(readingPlanRepositoryProvider);
  return MarkReadingComplete(repository);
});

final resetListCompletionUseCaseProvider = Provider<ResetListCompletion>((ref) {
  final repository = ref.read(readingPlanRepositoryProvider);
  return ResetListCompletion(repository);
});

final checkDailyResetUseCaseProvider = Provider<CheckDailyReset>((ref) {
  final repository = ref.read(readingPlanRepositoryProvider);
  return CheckDailyReset(repository);
});

final todaysReadingsProvider = FutureProvider<TodaysReadingsResult>((ref) async {
  // Use authStateProvider instead of currentUserProvider for reactive updates
  final authState = ref.watch(authStateProvider);
  final user = authState.value;

  // Authentication is required
  if (user == null) {
    throw Exception('User must be authenticated');
  }

  // Check for daily reset before getting today's readings
  final checkDailyReset = ref.read(checkDailyResetUseCaseProvider);
  await checkDailyReset(user.id);

  final useCase = ref.read(getTodaysReadingsProvider);
  final result = await useCase(user.id);

  return result.fold(
    (failure) => throw Exception(failure.message),
    (readingsResult) => readingsResult,
  );
});

final markReadingCompleteProvider = Provider<MarkReadingCompleteAction>((ref){
  return MarkReadingCompleteAction(ref);
});

final resetListCompletionProvider = Provider<ResetListCompletionAction>((ref){
  return ResetListCompletionAction(ref);
});

final checkDailyResetProvider = Provider<CheckDailyResetAction>((ref){
  return CheckDailyResetAction(ref);
});

class MarkReadingCompleteAction {
  final Ref ref;

  MarkReadingCompleteAction(this.ref);

  Future<void> call(String listId) async {
    // Use authStateProvider for current user
    final authState = ref.read(authStateProvider);
    final user = authState.value;

    if (user == null) {
      throw Exception('User must be authenticated');
    }

    final useCase = ref.read(markReadingCompleteUseCaseProvider);
    await useCase(user.id, listId);

    ref.invalidate(todaysReadingsProvider);
  }

  // Bulk version that doesn't invalidate after each call
  Future<void> callWithoutInvalidate(String listId) async {
    final authState = ref.read(authStateProvider);
    final user = authState.value;

    if (user == null) {
      throw Exception('User must be authenticated');
    }

    final useCase = ref.read(markReadingCompleteUseCaseProvider);
    await useCase(user.id, listId);
  }
}

class ResetListCompletionAction {
  final Ref ref;

  ResetListCompletionAction(this.ref);

  Future<void> call(String listId) async {
    // Use authStateProvider for current user
    final authState = ref.read(authStateProvider);
    final user = authState.value;

    if (user == null) {
      throw Exception('User must be authenticated');
    }

    final useCase = ref.read(resetListCompletionUseCaseProvider);
    await useCase(user.id, listId);

    // Force a complete refresh
    ref.invalidate(todaysReadingsProvider);
  }

  // Bulk version that doesn't invalidate after each call
  Future<void> callWithoutInvalidate(String listId) async {
    final authState = ref.read(authStateProvider);
    final user = authState.value;

    if (user == null) {
      throw Exception('User must be authenticated');
    }

    final useCase = ref.read(resetListCompletionUseCaseProvider);
    await useCase(user.id, listId);
  }
}

class CheckDailyResetAction {
  final Ref ref;

  CheckDailyResetAction(this.ref);

  Future<bool> call() async {
    // Use authStateProvider for current user
    final authState = ref.read(authStateProvider);
    final user = authState.value;

    if (user == null) {
      throw Exception('User must be authenticated');
    }

    final useCase = ref.read(checkDailyResetUseCaseProvider);
    final result = await useCase(user.id);

    final wasReset = result.fold(
      (failure) => false,
      (resetPerformed) => resetPerformed,
    );

    if (wasReset) {
      // Force a complete refresh if reset was performed
      ref.invalidate(todaysReadingsProvider);
    }

    return wasReset;
  }
}
