import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:unquenched_bible/data/models/reading_plan_hive_model.dart';
import '../../data/repositories/reading_plan_repository_impl.dart';
import '../../data/sources/reading_plan_local_data_source.dart';
import '../../data/sources/reading_plan_remote_data_source.dart';
import '../../domain/repositories/reading_plan_repository.dart';
import 'providers.dart' hide authStateProvider;
import 'auth_providers.dart';

final hiveInitProvider = FutureProvider<void>((ref) async {
  await Hive.initFlutter();
  Hive.registerAdapter(ReadingPlanHiveModelAdapter());
});

final readingPlanLocalDataSourceProvider = Provider<ReadingPlanLocalDataSource>((ref) {
  return ReadingPlanLocalDataSourceImpl();
});

final readingPlanRemoteDataSourceProvider = Provider<ReadingPlanRemoteDataSource>((ref) {
  final firestore = ref.read(firestoreProvider);
  return ReadingPlanRemoteDataSourceImpl(firestore: firestore);
});

final readingPlanRepositoryProvider = Provider<ReadingPlanRepository>((ref) {
  return ReadingPlanRepositoryImpl(
    remoteDataSource: ref.read(readingPlanRemoteDataSourceProvider),
    localDataSource: ref.read(readingPlanLocalDataSourceProvider),
  );
});

/// Provider to check if the user needs onboarding
/// Returns true if:
/// - User is not authenticated OR user doesn't have a reading plan
/// Returns false if:
/// - User is authenticated AND has a reading plan
final needsOnboardingProvider = FutureProvider<bool>((ref) async {
  try {
    final repository = ref.read(readingPlanRepositoryProvider);
    final authRepository = ref.read(authRepositoryProvider);

    // Get current user synchronously instead of waiting for stream
    final currentUserResult = await authRepository.getCurrentUser();
    final authState = currentUserResult.fold(
      (failure) => null,
      (user) => user,
    );

    // If user is not authenticated, they need onboarding
    if (authState == null) {
      return true;
    }

    // User is authenticated - check if they have a reading plan
    final result = await repository.getReadingPlan(authState.id);

    return result.fold(
      (failure) {
        // No plan found = needs onboarding
        return true;
      },
      (plan) {
        // Plan exists = skip onboarding
        return false;
      },
    );
  } catch (e) {
    // If anything goes wrong, show onboarding to be safe
    return true;
  }
});
