import 'package:dartz/dartz.dart';
import '../../core/errors/failures.dart';
import '../repositories/reading_plan_repository.dart';

class MarkReadingComplete {
  final ReadingPlanRepository repository;

  MarkReadingComplete(this.repository);

  Future<Either<Failure, void>> call(String userId, String listId) async {
    final planResult = await repository.getReadingPlan(userId);
    return await planResult.fold(
      (failure) async => Left(failure),
      (plan) async {
        // Check if already completed today
        if (plan.isCompletedToday(listId)) {
          return Left(const ValidationFailure('This list has already been completed today'));
        }

        final currentChapter = plan.progress[listId] ?? 1;

        // Mark as completed WITHOUT incrementing the chapter
        // Chapter only increments when reset button is clicked or new day starts
        return await repository.updateProgress(userId, listId, currentChapter, markCompleted: true);
      },
    );
  }
}
