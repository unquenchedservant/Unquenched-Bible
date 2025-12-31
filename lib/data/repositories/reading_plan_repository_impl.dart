import 'package:dartz/dartz.dart';
import '../../core/constants/reading_lists.dart';
import '../../core/errors/exceptions.dart';
import '../../core/errors/failures.dart';
import '../../domain/entities/reading_plan_entity.dart';
import '../../domain/repositories/reading_plan_repository.dart';
import '../models/reading_plan_model.dart';
import '../models/reading_plan_hive_model.dart';
import '../sources/reading_plan_local_data_source.dart';
import '../sources/reading_plan_remote_data_source.dart';

class ReadingPlanRepositoryImpl implements ReadingPlanRepository {
  final ReadingPlanRemoteDataSource remoteDataSource;
  final ReadingPlanLocalDataSource localDataSource;

  ReadingPlanRepositoryImpl({
    required this.remoteDataSource,
    required this.localDataSource,
  });

  /// Helper method to save plan to local and remote
  Future<void> _savePlan(ReadingPlanModel plan) async {
    final hiveModel = ReadingPlanHiveModel.fromEntity(plan);
    await localDataSource.saveReadingPlan(hiveModel);
    await remoteDataSource.saveReadingPlan(plan);
  }

  @override
  Future<Either<Failure, ReadingPlanEntity>> getReadingPlan(String userId) async {
    try {
      final localPlan = await localDataSource.getReadingPlan(userId);
      if (localPlan!=null){
        final entity = localPlan.toEntity();

        // Sync in background - await to prevent race conditions
        await _syncInBackground(userId);

        return Right(entity);
      }

      // No local plan - fetch from remote
      final remotePlan = await remoteDataSource.getReadingPlan(userId);
      final hiveModel = ReadingPlanHiveModel.fromEntity(remotePlan);
      await localDataSource.saveReadingPlan(hiveModel);
      return Right(remotePlan);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } on CacheException catch (e) {
      return Left(CacheFailure(e.message));
    } catch(e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  Future<void> _syncInBackground(String userId) async {
    try {
      final localPlan = await localDataSource.getReadingPlan(userId);
      final remotePlan = await remoteDataSource.getReadingPlan(userId);

      // Only overwrite local with remote if remote is newer
      if (localPlan == null || remotePlan.lastUpdated.isAfter(localPlan.lastUpdated)) {
        final hiveModel = ReadingPlanHiveModel.fromEntity(remotePlan);
        await localDataSource.saveReadingPlan(hiveModel);
      }
    } catch (_){
      // Silently fail background sync - local data is still available
    }
  }

  @override
  Future<Either<Failure, void>> updateProgress(
    String userId,
    String listId,
    int chapter,
    {bool markCompleted = false}
  ) async {
    try {
      final result = await getReadingPlan(userId);
      final plan = result.fold((failure)=>throw Exception(failure.message), (plan) => plan);

      // Determine if this is a McCheyne list
      final isMcheyneList = listId.startsWith('mcheynelist');

      // Initialize variables for both systems
      Map<String, int> updatedProgress;
      Map<String, DateTime> updatedCompletedToday;
      Map<String, bool> updatedListCompletionStatus;
      int newIndexProgress;
      String currentReadingMode;
      bool currentRequireAllLists;
      int totalListsRequired;

      Map<String, int> updatedMcheyneProgress;
      Map<String, DateTime> updatedMcheyneCompletedToday;
      Map<String, bool> updatedMcheyneListCompletionStatus;
      int newMcheyneIndexProgress;

      if (isMcheyneList) {
        // McCheyne list - update McCheyne progress
        updatedMcheyneProgress = Map<String, int>.from(plan.mcheyneProgress);
        updatedMcheyneCompletedToday = Map<String, DateTime>.from(plan.mcheyneCompletedToday);
        updatedMcheyneListCompletionStatus = Map<String, bool>.from(plan.mcheyneListCompletionStatus);
        newMcheyneIndexProgress = plan.mcheyneIndexProgress;
        currentReadingMode = plan.mcheyneReadingMode;
        currentRequireAllLists = plan.mcheyneRequireAllListsForStreak;
        totalListsRequired = 4; // McCheyne has 4 lists

        // Keep Horner progress unchanged
        updatedProgress = Map<String, int>.from(plan.progress);
        updatedCompletedToday = Map<String, DateTime>.from(plan.completedToday);
        updatedListCompletionStatus = Map<String, bool>.from(plan.listCompletionStatus);
        newIndexProgress = plan.indexProgress;
      } else {
        // Horner list - update Horner progress
        updatedProgress = Map<String, int>.from(plan.progress);
        updatedCompletedToday = Map<String, DateTime>.from(plan.completedToday);
        updatedListCompletionStatus = Map<String, bool>.from(plan.listCompletionStatus);
        newIndexProgress = plan.indexProgress;
        currentReadingMode = plan.readingMode;
        currentRequireAllLists = plan.requireAllListsForStreak;
        totalListsRequired = 10; // Horner has 10 lists

        // Keep McCheyne progress unchanged
        updatedMcheyneProgress = Map<String, int>.from(plan.mcheyneProgress);
        updatedMcheyneCompletedToday = Map<String, DateTime>.from(plan.mcheyneCompletedToday);
        updatedMcheyneListCompletionStatus = Map<String, bool>.from(plan.mcheyneListCompletionStatus);
        newMcheyneIndexProgress = plan.mcheyneIndexProgress;
      }

      // Handle progress updates based on reading mode
      if (markCompleted) {
        // When marking complete, don't increment progress
        // Progress will be incremented during daily reset for completed lists
        // This ensures the same reading is shown until the next day
        switch (currentReadingMode) {
          case 'standard':
            // Standard mode: keep progress unchanged
            // Daily reset will increment for completed lists
            break;
          case 'index':
            // Index mode: keep individual progress and indexProgress unchanged
            // Daily reset will increment indexProgress if all lists complete
            break;
          case 'calendar':
            // Calendar mode: progress is automatic, don't increment anything
            break;
          default:
            // Fallback: keep progress unchanged
            break;
        }
      } else {
        // Not marking complete, just update the chapter (for manual resets, etc.)
        if (isMcheyneList) {
          updatedMcheyneProgress[listId] = chapter;
        } else {
          updatedProgress[listId] = chapter;
        }
      }

      int newCurrentStreak = plan.currentStreak;
      int newLongestStreak = plan.longestStreak;
      DateTime? newLastCompletionDate = plan.lastCompletionDate;

      if (markCompleted) {
        final now = DateTime.now();

        if (isMcheyneList) {
          updatedMcheyneCompletedToday[listId] = now;
          updatedMcheyneListCompletionStatus[listId] = true;
          // Index mode: indexProgress will be incremented during daily reset
        } else {
          updatedCompletedToday[listId] = now;
          updatedListCompletionStatus[listId] = true;
          // Index mode: indexProgress will be incremented during daily reset
        }

        // Determine if streak should be updated (streak is global across both systems)
        bool shouldUpdateStreak = !currentRequireAllLists;

        if (currentRequireAllLists) {
          // Check if all lists in the current system are completed
          if (isMcheyneList) {
            shouldUpdateStreak = updatedMcheyneListCompletionStatus.length == totalListsRequired &&
                updatedMcheyneListCompletionStatus.values.every((completed) => completed == true);
          } else {
            shouldUpdateStreak = updatedListCompletionStatus.length == totalListsRequired &&
                updatedListCompletionStatus.values.every((completed) => completed == true);
          }
        }

        // Calculate streak updates only if conditions are met
        if (shouldUpdateStreak) {
          final streakUpdate = _calculateStreakUpdate(
            lastCompletionDate: plan.lastCompletionDate,
            currentStreak: plan.currentStreak,
            longestStreak: plan.longestStreak,
            completionDate: now,
          );

          newCurrentStreak = streakUpdate['currentStreak'] as int;
          newLongestStreak = streakUpdate['longestStreak'] as int;
          newLastCompletionDate = now;
        }
      }

      final updatedPlan = ReadingPlanModel(
        userId: userId,
        planSystem: plan.planSystem,
        planType: plan.planType,
        readingMode: plan.readingMode,
        progress: updatedProgress,
        indexProgress: newIndexProgress,
        completedToday: updatedCompletedToday,
        listCompletionStatus: updatedListCompletionStatus,
        currentStreak: newCurrentStreak,
        longestStreak: newLongestStreak,
        lastCompletionDate: newLastCompletionDate,
        lastResetDate: plan.lastResetDate,
        lastUpdated: DateTime.now(),
        fivePsalmsMode: plan.fivePsalmsMode,
        requireAllListsForStreak: plan.requireAllListsForStreak,
        // McCheyne-specific fields
        mcheyneProgress: updatedMcheyneProgress,
        mcheyneIndexProgress: newMcheyneIndexProgress,
        mcheyneReadingMode: plan.mcheyneReadingMode,
        mcheyneCompletedToday: updatedMcheyneCompletedToday,
        mcheyneListCompletionStatus: updatedMcheyneListCompletionStatus,
        mcheyneFivePsalmsMode: plan.mcheyneFivePsalmsMode,
        mcheyneRequireAllListsForStreak: plan.mcheyneRequireAllListsForStreak,
      );

      await _savePlan(updatedPlan);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  /// Calculate streak updates based on last completion date
  Map<String, int> _calculateStreakUpdate({
    required DateTime? lastCompletionDate,
    required int currentStreak,
    required int longestStreak,
    required DateTime completionDate,
  }) {
    int newCurrentStreak = currentStreak;
    int newLongestStreak = longestStreak;

    if (lastCompletionDate == null) {
      // First completion ever
      newCurrentStreak = 1;
    } else {
      final lastDate = DateTime(
        lastCompletionDate.year,
        lastCompletionDate.month,
        lastCompletionDate.day,
      );
      final todayDate = DateTime(
        completionDate.year,
        completionDate.month,
        completionDate.day,
      );

      // Check if this is the same day (shouldn't happen with proper checks, but defensive)
      if (lastDate == todayDate) {
        // Same day, keep current streak
        newCurrentStreak = currentStreak;
      } else {
        final daysDifference = todayDate.difference(lastDate).inDays;

        if (daysDifference == 1) {
          // Consecutive day - increment streak
          newCurrentStreak = currentStreak + 1;
        } else if (daysDifference > 1) {
          // Streak broken - reset to 1
          newCurrentStreak = 1;
        }
      }
    }

    // Update longest streak if current exceeds it
    if (newCurrentStreak > longestStreak) {
      newLongestStreak = newCurrentStreak;
    }

    return {
      'currentStreak': newCurrentStreak,
      'longestStreak': newLongestStreak,
    };
  }

  @override
  Future<Either<Failure, void>> resetListCompletion(String userId, String listId) async {
    try {
      final result = await getReadingPlan(userId);
      final plan = result.fold((failure)=>throw Exception(failure.message), (plan) => plan);

      // Determine if this is a McCheyne list
      final isMcheyneList = listId.startsWith('mcheynelist');

      Map<String, bool> updatedListCompletionStatus;
      Map<String, DateTime> updatedCompletedToday;
      Map<String, int> updatedProgress;
      Map<String, bool> updatedMcheyneListCompletionStatus;
      Map<String, DateTime> updatedMcheyneCompletedToday;
      Map<String, int> updatedMcheyneProgress;

      if (isMcheyneList) {
        // Reset McCheyne list
        updatedMcheyneListCompletionStatus = Map<String, bool>.from(plan.mcheyneListCompletionStatus);
        updatedMcheyneListCompletionStatus[listId] = false;

        updatedMcheyneCompletedToday = Map<String, DateTime>.from(plan.mcheyneCompletedToday);

        updatedMcheyneProgress = Map<String, int>.from(plan.mcheyneProgress);
        final currentChapter = plan.mcheyneProgress[listId] ?? 1;
        final totalChapters = _getTotalChapters(listId, plan.planSystem);
        final nextChapter = currentChapter >= totalChapters ? 1 : currentChapter + 1;
        updatedMcheyneProgress[listId] = nextChapter;

        // Keep Horner progress unchanged
        updatedListCompletionStatus = Map<String, bool>.from(plan.listCompletionStatus);
        updatedCompletedToday = Map<String, DateTime>.from(plan.completedToday);
        updatedProgress = Map<String, int>.from(plan.progress);
      } else {
        // Reset Horner list
        updatedListCompletionStatus = Map<String, bool>.from(plan.listCompletionStatus);
        updatedListCompletionStatus[listId] = false;

        updatedCompletedToday = Map<String, DateTime>.from(plan.completedToday);

        updatedProgress = Map<String, int>.from(plan.progress);
        final currentChapter = plan.progress[listId] ?? 1;
        final totalChapters = _getTotalChapters(listId, plan.planSystem);
        final nextChapter = currentChapter >= totalChapters ? 1 : currentChapter + 1;
        updatedProgress[listId] = nextChapter;

        // Keep McCheyne progress unchanged
        updatedMcheyneListCompletionStatus = Map<String, bool>.from(plan.mcheyneListCompletionStatus);
        updatedMcheyneCompletedToday = Map<String, DateTime>.from(plan.mcheyneCompletedToday);
        updatedMcheyneProgress = Map<String, int>.from(plan.mcheyneProgress);
      }

      final updatedPlan = ReadingPlanModel(
        userId: plan.userId,
        planSystem: plan.planSystem,
        planType: plan.planType,
        readingMode: plan.readingMode,
        progress: updatedProgress,
        indexProgress: plan.indexProgress,
        completedToday: updatedCompletedToday,
        listCompletionStatus: updatedListCompletionStatus,
        currentStreak: plan.currentStreak,
        longestStreak: plan.longestStreak,
        lastCompletionDate: plan.lastCompletionDate,
        lastResetDate: plan.lastResetDate,
        lastUpdated: DateTime.now(),
        fivePsalmsMode: plan.fivePsalmsMode,
        requireAllListsForStreak: plan.requireAllListsForStreak,
        // McCheyne-specific fields
        mcheyneProgress: updatedMcheyneProgress,
        mcheyneIndexProgress: plan.mcheyneIndexProgress,
        mcheyneReadingMode: plan.mcheyneReadingMode,
        mcheyneCompletedToday: updatedMcheyneCompletedToday,
        mcheyneListCompletionStatus: updatedMcheyneListCompletionStatus,
        mcheyneFivePsalmsMode: plan.mcheyneFivePsalmsMode,
        mcheyneRequireAllListsForStreak: plan.mcheyneRequireAllListsForStreak,
      );

      await _savePlan(updatedPlan);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  int _getTotalChapters(String listId, String planSystem) {
    return ReadingLists.getTotalChapters(listId, planSystem);
  }

  @override
  Future<Either<Failure, void>> toggleFivePsalmsMode(String userId, bool enabled) async {
    try {
      final result = await getReadingPlan(userId);
      final plan = result.fold((failure) => throw Exception(failure.message), (plan) => plan);

      // Preserve list6 progress when toggling mode
      final updatedPlan = ReadingPlanModel(
        userId: plan.userId,
        planSystem: plan.planSystem,
        planType: plan.planType,
        progress: plan.progress,
        completedToday: plan.completedToday,
        listCompletionStatus: plan.listCompletionStatus,
        currentStreak: plan.currentStreak,
        longestStreak: plan.longestStreak,
        lastCompletionDate: plan.lastCompletionDate,
        lastUpdated: DateTime.now(),
        fivePsalmsMode: enabled,
      );

      await _savePlan(updatedPlan);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<Either<Failure, void>> syncWithRemote(String userId) async {
    try {
      final remotePlan = await remoteDataSource.getReadingPlan(userId);
      final hiveModel = ReadingPlanHiveModel.fromEntity(remotePlan);
      await localDataSource.saveReadingPlan(hiveModel);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Stream<ReadingPlanEntity> watchReadingPlan(String userId) {
    return remoteDataSource.watchReadingPlan(userId);
  }

  @override
  Future<Either<Failure, void>> updatePlan(String userId, String planSystem, String planType) async {
    try{
      final result = await getReadingPlan(userId);

      // Create a default plan if one doesn't exist
      ReadingPlanEntity plan;
      if (result.isLeft()) {
        // No plan exists, create a default one
        plan = ReadingPlanModel(
          userId: userId,
          planSystem: planSystem,
          planType: planType,
          progress: {},
          lastUpdated: DateTime.now(),
        );
      } else {
        plan = result.fold(
          (failure) => throw Exception(failure.message),
          (p) => p,
        );
      }

      // Initialize progress for the new plan system if needed
      Map<String, int> progress = Map<String, int>.from(plan.progress);
      Map<String, int> mcheyneProgress = Map<String, int>.from(plan.mcheyneProgress);

      if (planSystem == 'horner' && progress.isEmpty) {
        // Initialize Horner progress
        for (int i = 1; i <= 10; i++) {
          progress['list$i'] = 1;
        }
      } else if (planSystem == 'mcheyne' && mcheyneProgress.isEmpty) {
        // Initialize McCheyne progress
        mcheyneProgress = {
          'mcheynelist1': 1,
          'mcheynelist2': 1,
          'mcheynelist3': 1,
          'mcheynelist4': 1,
        };
      }

      final updatedPlan = ReadingPlanModel(
        userId: userId,
        planSystem: planSystem,
        planType: planType,
        progress: progress,
        readingMode: plan.readingMode,
        indexProgress: plan.indexProgress,
        completedToday: plan.completedToday,
        listCompletionStatus: plan.listCompletionStatus,
        currentStreak: plan.currentStreak,
        longestStreak: plan.longestStreak,
        lastCompletionDate: plan.lastCompletionDate,
        lastUpdated: DateTime.now(),
        fivePsalmsMode: plan.fivePsalmsMode,
        requireAllListsForStreak: plan.requireAllListsForStreak,
        // McCheyne-specific fields
        mcheyneProgress: mcheyneProgress,
        mcheyneIndexProgress: plan.mcheyneIndexProgress,
        mcheyneReadingMode: plan.mcheyneReadingMode,
        mcheyneCompletedToday: plan.mcheyneCompletedToday,
        mcheyneListCompletionStatus: plan.mcheyneListCompletionStatus,
        mcheyneFivePsalmsMode: plan.mcheyneFivePsalmsMode,
        mcheyneRequireAllListsForStreak: plan.mcheyneRequireAllListsForStreak,
      );

      await _savePlan(updatedPlan);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<Either<Failure, bool>> performDailyResetIfNeeded(String userId) async {
    try {
      final result = await getReadingPlan(userId);
      final plan = result.fold((failure)=>throw Exception(failure.message), (plan) => plan);

      final now = DateTime.now();
      final lastReset = plan.lastResetDate ?? plan.lastUpdated; // Fallback to lastUpdated for existing users

      // Check if it's a new day based on last reset
      final isNewDay = !(now.year == lastReset.year &&
                         now.month == lastReset.month &&
                         now.day == lastReset.day);

      // Don't reset if it's not a new day
      if (!isNewDay) {
        return const Right(false); // No reset needed
      }

      // Don't reset if there are no completion statuses to reset
      if (plan.listCompletionStatus.isEmpty && plan.mcheyneListCompletionStatus.isEmpty) {
        return const Right(false); // No reset needed
      }

      // Perform daily reset for Horner lists
      final updatedProgress = Map<String, int>.from(plan.progress);
      final updatedListCompletionStatus = Map<String, bool>.from(plan.listCompletionStatus);

      for (var listId in plan.listCompletionStatus.keys) {
        // Only increment chapter for lists that were completed
        if (plan.listCompletionStatus[listId] == true) {
          final currentChapter = plan.progress[listId] ?? 1;
          final totalChapters = _getTotalChapters(listId, plan.planSystem);
          final nextChapter = currentChapter >= totalChapters ? 1 : currentChapter + 1;
          updatedProgress[listId] = nextChapter;
        }
        // Reset ALL lists to false (both completed and incomplete)
        updatedListCompletionStatus[listId] = false;
      }

      // Perform daily reset for McCheyne lists
      final updatedMcheyneProgress = Map<String, int>.from(plan.mcheyneProgress);
      final updatedMcheyneListCompletionStatus = Map<String, bool>.from(plan.mcheyneListCompletionStatus);

      for (var listId in plan.mcheyneListCompletionStatus.keys) {
        // Only increment chapter for lists that were completed
        if (plan.mcheyneListCompletionStatus[listId] == true) {
          final currentChapter = plan.mcheyneProgress[listId] ?? 1;
          final totalChapters = _getTotalChapters(listId, 'mcheyne');
          final nextChapter = currentChapter >= totalChapters ? 1 : currentChapter + 1;
          updatedMcheyneProgress[listId] = nextChapter;
        }
        // Reset ALL lists to false (both completed and incomplete)
        updatedMcheyneListCompletionStatus[listId] = false;
      }

      // Handle index mode: increment indexProgress if all lists were completed
      int updatedIndexProgress = plan.indexProgress;
      int updatedMcheyneIndexProgress = plan.mcheyneIndexProgress;

      // Horner index mode check
      if (plan.readingMode == 'index') {
        final allHornerCompleted = plan.listCompletionStatus.length == 10 &&
            plan.listCompletionStatus.values.every((completed) => completed == true);
        if (allHornerCompleted) {
          updatedIndexProgress = plan.indexProgress + 1;
        }
      }

      // McCheyne index mode check
      if (plan.mcheyneReadingMode == 'index') {
        final allMcheyneCompleted = plan.mcheyneListCompletionStatus.length == 4 &&
            plan.mcheyneListCompletionStatus.values.every((completed) => completed == true);
        if (allMcheyneCompleted) {
          updatedMcheyneIndexProgress = plan.mcheyneIndexProgress + 1;
        }
      }

      // Check if streak should be reset
      // Streak resets if more than 1 day has passed since last completion
      int updatedStreak = plan.currentStreak;
      if (plan.lastCompletionDate != null) {
        final lastCompletion = DateTime(
          plan.lastCompletionDate!.year,
          plan.lastCompletionDate!.month,
          plan.lastCompletionDate!.day,
        );
        final todayDate = DateTime(
          now.year,
          now.month,
          now.day,
        );
        final daysSinceCompletion = todayDate.difference(lastCompletion).inDays;

        // If more than 1 day has passed, reset the streak
        if (daysSinceCompletion > 1) {
          updatedStreak = 0;
        }
      }

      // Create updated plan with cleared completion status
      final updatedPlan = ReadingPlanModel(
        userId: plan.userId,
        planSystem: plan.planSystem,
        planType: plan.planType,
        readingMode: plan.readingMode,
        progress: updatedProgress,
        indexProgress: updatedIndexProgress,
        completedToday: plan.completedToday,
        listCompletionStatus: updatedListCompletionStatus,
        currentStreak: updatedStreak,
        longestStreak: plan.longestStreak,
        lastCompletionDate: plan.lastCompletionDate,
        lastResetDate: now, // Set the reset date to now
        lastUpdated: DateTime.now(),
        fivePsalmsMode: plan.fivePsalmsMode,
        requireAllListsForStreak: plan.requireAllListsForStreak,
        // McCheyne-specific fields
        mcheyneProgress: updatedMcheyneProgress,
        mcheyneIndexProgress: updatedMcheyneIndexProgress,
        mcheyneReadingMode: plan.mcheyneReadingMode,
        mcheyneCompletedToday: plan.mcheyneCompletedToday,
        mcheyneListCompletionStatus: updatedMcheyneListCompletionStatus,
        mcheyneFivePsalmsMode: plan.mcheyneFivePsalmsMode,
        mcheyneRequireAllListsForStreak: plan.mcheyneRequireAllListsForStreak,
      );

      await _savePlan(updatedPlan);

      return const Right(true); // Reset performed
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<Either<Failure, void>> toggleRequireAllListsForStreak(String userId, bool enabled) async {
    try {
      final result = await getReadingPlan(userId);
      final plan = result.fold((failure) => throw Exception(failure.message), (plan) => plan);

      final updatedPlan = ReadingPlanModel(
        userId: plan.userId,
        planSystem: plan.planSystem,
        planType: plan.planType,
        progress: plan.progress,
        completedToday: plan.completedToday,
        listCompletionStatus: plan.listCompletionStatus,
        currentStreak: plan.currentStreak,
        longestStreak: plan.longestStreak,
        lastCompletionDate: plan.lastCompletionDate,
        lastUpdated: DateTime.now(),
        fivePsalmsMode: plan.fivePsalmsMode,
        requireAllListsForStreak: enabled,
      );

      await _savePlan(updatedPlan);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<Either<Failure, void>> setReadingMode(String userId, String readingMode) async {
    try {
      final result = await getReadingPlan(userId);
      final plan = result.fold((failure) => throw Exception(failure.message), (plan) => plan);

      final updatedPlan = ReadingPlanModel(
        userId: plan.userId,
        planSystem: plan.planSystem,
        planType: plan.planType,
        readingMode: readingMode,
        progress: plan.progress,
        indexProgress: plan.indexProgress,
        lastUpdated: DateTime.now(),
        completedToday: plan.completedToday,
        listCompletionStatus: plan.listCompletionStatus,
        currentStreak: plan.currentStreak,
        longestStreak: plan.longestStreak,
        lastCompletionDate: plan.lastCompletionDate,
        fivePsalmsMode: plan.fivePsalmsMode,
        requireAllListsForStreak: plan.requireAllListsForStreak,
        // McCheyne-specific fields
        mcheyneProgress: plan.mcheyneProgress,
        mcheyneIndexProgress: plan.mcheyneIndexProgress,
        mcheyneReadingMode: plan.mcheyneReadingMode,
        mcheyneCompletedToday: plan.mcheyneCompletedToday,
        mcheyneListCompletionStatus: plan.mcheyneListCompletionStatus,
        mcheyneFivePsalmsMode: plan.mcheyneFivePsalmsMode,
        mcheyneRequireAllListsForStreak: plan.mcheyneRequireAllListsForStreak,
      );

      await _savePlan(updatedPlan);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<Either<Failure, void>> setIndexProgress(String userId, int indexProgress) async {
    try {
      final result = await getReadingPlan(userId);
      final plan = result.fold((failure) => throw Exception(failure.message), (plan) => plan);

      // Determine max index based on plan system
      int maxIndex;
      if (plan.planSystem == 'horner') {
        // For Horner, find the longest list (list9 - Prophets has 250 chapters)
        maxIndex = ReadingLists.getTotalChapters('list9', 'horner');
      } else {
        // For M'Cheyne, max is 365
        maxIndex = 365;
      }

      // Wrap the index to stay within bounds (1 to maxIndex)
      final wrappedIndex = indexProgress <= 0
          ? ((indexProgress % maxIndex) + maxIndex) % maxIndex + 1
          : ((indexProgress - 1) % maxIndex) + 1;

      final updatedPlan = ReadingPlanModel(
        userId: plan.userId,
        planSystem: plan.planSystem,
        planType: plan.planType,
        readingMode: plan.readingMode,
        progress: plan.progress,
        indexProgress: plan.planSystem == 'horner' ? wrappedIndex : plan.indexProgress,
        lastUpdated: DateTime.now(),
        completedToday: plan.completedToday,
        listCompletionStatus: plan.listCompletionStatus,
        currentStreak: plan.currentStreak,
        longestStreak: plan.longestStreak,
        lastCompletionDate: plan.lastCompletionDate,
        fivePsalmsMode: plan.fivePsalmsMode,
        requireAllListsForStreak: plan.requireAllListsForStreak,
        // McCheyne-specific fields
        mcheyneProgress: plan.mcheyneProgress,
        mcheyneIndexProgress: plan.planSystem == 'mcheyne' ? wrappedIndex : plan.mcheyneIndexProgress,
        mcheyneReadingMode: plan.mcheyneReadingMode,
        mcheyneCompletedToday: plan.mcheyneCompletedToday,
        mcheyneListCompletionStatus: plan.mcheyneListCompletionStatus,
        mcheyneFivePsalmsMode: plan.mcheyneFivePsalmsMode,
        mcheyneRequireAllListsForStreak: plan.mcheyneRequireAllListsForStreak,
      );

      await _savePlan(updatedPlan);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<Either<Failure, void>> deleteUserData(String userId) async {
    try {
      // Delete from both local and remote
      await localDataSource.deleteReadingPlan(userId);
      await remoteDataSource.deleteUserData(userId);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } on CacheException catch (e) {
      return Left(CacheFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  // McCheyne-specific settings methods
  @override
  Future<Either<Failure, void>> toggleMcheyneFivePsalmsMode(String userId, bool enabled) async {
    try {
      final result = await getReadingPlan(userId);
      final plan = result.fold((failure) => throw Exception(failure.message), (plan) => plan);

      final updatedPlan = ReadingPlanModel(
        userId: plan.userId,
        planSystem: plan.planSystem,
        planType: plan.planType,
        readingMode: plan.readingMode,
        progress: plan.progress,
        indexProgress: plan.indexProgress,
        completedToday: plan.completedToday,
        listCompletionStatus: plan.listCompletionStatus,
        currentStreak: plan.currentStreak,
        longestStreak: plan.longestStreak,
        lastCompletionDate: plan.lastCompletionDate,
        lastUpdated: DateTime.now(),
        fivePsalmsMode: plan.fivePsalmsMode,
        requireAllListsForStreak: plan.requireAllListsForStreak,
        // McCheyne-specific fields
        mcheyneProgress: plan.mcheyneProgress,
        mcheyneIndexProgress: plan.mcheyneIndexProgress,
        mcheyneReadingMode: plan.mcheyneReadingMode,
        mcheyneCompletedToday: plan.mcheyneCompletedToday,
        mcheyneListCompletionStatus: plan.mcheyneListCompletionStatus,
        mcheyneFivePsalmsMode: enabled, // Update this field
        mcheyneRequireAllListsForStreak: plan.mcheyneRequireAllListsForStreak,
      );

      await _savePlan(updatedPlan);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<Either<Failure, void>> toggleMcheyneRequireAllListsForStreak(String userId, bool enabled) async {
    try {
      final result = await getReadingPlan(userId);
      final plan = result.fold((failure) => throw Exception(failure.message), (plan) => plan);

      final updatedPlan = ReadingPlanModel(
        userId: plan.userId,
        planSystem: plan.planSystem,
        planType: plan.planType,
        readingMode: plan.readingMode,
        progress: plan.progress,
        indexProgress: plan.indexProgress,
        completedToday: plan.completedToday,
        listCompletionStatus: plan.listCompletionStatus,
        currentStreak: plan.currentStreak,
        longestStreak: plan.longestStreak,
        lastCompletionDate: plan.lastCompletionDate,
        lastUpdated: DateTime.now(),
        fivePsalmsMode: plan.fivePsalmsMode,
        requireAllListsForStreak: plan.requireAllListsForStreak,
        // McCheyne-specific fields
        mcheyneProgress: plan.mcheyneProgress,
        mcheyneIndexProgress: plan.mcheyneIndexProgress,
        mcheyneReadingMode: plan.mcheyneReadingMode,
        mcheyneCompletedToday: plan.mcheyneCompletedToday,
        mcheyneListCompletionStatus: plan.mcheyneListCompletionStatus,
        mcheyneFivePsalmsMode: plan.mcheyneFivePsalmsMode,
        mcheyneRequireAllListsForStreak: enabled, // Update this field
      );

      await _savePlan(updatedPlan);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<Either<Failure, void>> setMcheyneReadingMode(String userId, String readingMode) async {
    try {
      final result = await getReadingPlan(userId);
      final plan = result.fold((failure) => throw Exception(failure.message), (plan) => plan);

      final updatedPlan = ReadingPlanModel(
        userId: plan.userId,
        planSystem: plan.planSystem,
        planType: plan.planType,
        readingMode: plan.readingMode,
        progress: plan.progress,
        indexProgress: plan.indexProgress,
        completedToday: plan.completedToday,
        listCompletionStatus: plan.listCompletionStatus,
        currentStreak: plan.currentStreak,
        longestStreak: plan.longestStreak,
        lastCompletionDate: plan.lastCompletionDate,
        lastUpdated: DateTime.now(),
        fivePsalmsMode: plan.fivePsalmsMode,
        requireAllListsForStreak: plan.requireAllListsForStreak,
        // McCheyne-specific fields
        mcheyneProgress: plan.mcheyneProgress,
        mcheyneIndexProgress: plan.mcheyneIndexProgress,
        mcheyneReadingMode: readingMode, // Update this field
        mcheyneCompletedToday: plan.mcheyneCompletedToday,
        mcheyneListCompletionStatus: plan.mcheyneListCompletionStatus,
        mcheyneFivePsalmsMode: plan.mcheyneFivePsalmsMode,
        mcheyneRequireAllListsForStreak: plan.mcheyneRequireAllListsForStreak,
      );

      await _savePlan(updatedPlan);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<Either<Failure, void>> setMcheyneIndexProgress(String userId, int indexProgress) async {
    try {
      final result = await getReadingPlan(userId);
      final plan = result.fold((failure) => throw Exception(failure.message), (plan) => plan);

      // McCheyne has 365 readings total - wrap around if exceeds
      const int mcheyneTotal = 365;
      int wrappedIndex = indexProgress;
      if (indexProgress > mcheyneTotal) {
        // Wrap around: 366 -> 1, 367 -> 2, etc.
        wrappedIndex = ((indexProgress - 1) % mcheyneTotal) + 1;
      } else if (indexProgress < 1) {
        // Handle negative or zero values by wrapping from the end
        wrappedIndex = mcheyneTotal + (indexProgress % mcheyneTotal);
        if (wrappedIndex <= 0) wrappedIndex = mcheyneTotal;
      }

      final updatedPlan = ReadingPlanModel(
        userId: plan.userId,
        planSystem: plan.planSystem,
        planType: plan.planType,
        readingMode: plan.readingMode,
        progress: plan.progress,
        indexProgress: plan.indexProgress,
        completedToday: plan.completedToday,
        listCompletionStatus: plan.listCompletionStatus,
        currentStreak: plan.currentStreak,
        longestStreak: plan.longestStreak,
        lastCompletionDate: plan.lastCompletionDate,
        lastUpdated: DateTime.now(),
        fivePsalmsMode: plan.fivePsalmsMode,
        requireAllListsForStreak: plan.requireAllListsForStreak,
        // McCheyne-specific fields
        mcheyneProgress: plan.mcheyneProgress,
        mcheyneIndexProgress: wrappedIndex, // Update with wrapped value
        mcheyneReadingMode: plan.mcheyneReadingMode,
        mcheyneCompletedToday: plan.mcheyneCompletedToday,
        mcheyneListCompletionStatus: plan.mcheyneListCompletionStatus,
        mcheyneFivePsalmsMode: plan.mcheyneFivePsalmsMode,
        mcheyneRequireAllListsForStreak: plan.mcheyneRequireAllListsForStreak,
      );

      await _savePlan(updatedPlan);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }
}
