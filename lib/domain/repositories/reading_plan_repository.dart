import 'package:dartz/dartz.dart';
import '../../core/errors/failures.dart';
import '../entities/reading_plan_entity.dart';

abstract class ReadingPlanRepository{
  Future<Either<Failure, ReadingPlanEntity>> getReadingPlan(String userId);

  Future<Either<Failure, void>> updateProgress(
    String userId,
    String listId,
    int chapter,
    {bool markCompleted = false}
  );

  Future<Either<Failure, void>> resetListCompletion(String userId, String listId);

  Future<Either<Failure, void>> updatePlan(String userId, String planSystem, String planType);

  Future<Either<Failure, void>> toggleFivePsalmsMode(String userId, bool enabled);

  Future<Either<Failure, void>> toggleRequireAllListsForStreak(String userId, bool enabled);

  Future<Either<Failure, void>> setReadingMode(String userId, String readingMode);

  Future<Either<Failure, void>> setIndexProgress(String userId, int indexProgress);

  // McCheyne-specific settings
  Future<Either<Failure, void>> toggleMcheyneFivePsalmsMode(String userId, bool enabled);

  Future<Either<Failure, void>> toggleMcheyneRequireAllListsForStreak(String userId, bool enabled);

  Future<Either<Failure, void>> setMcheyneReadingMode(String userId, String readingMode);

  Future<Either<Failure, void>> setMcheyneIndexProgress(String userId, int indexProgress);

  Future<Either<Failure, void>> syncWithRemote(String userId);

  Stream<ReadingPlanEntity> watchReadingPlan(String userId);

  Future<Either<Failure, bool>> performDailyResetIfNeeded(String userId);

  Future<Either<Failure, void>> deleteUserData(String userId);
}
