import 'package:dartz/dartz.dart';
import '../../core/errors/failures.dart';
import '../repositories/reading_plan_repository.dart';

/// Checks if a new day has started and performs automatic reset if needed
class CheckDailyReset {
  final ReadingPlanRepository repository;

  CheckDailyReset(this.repository);

  Future<Either<Failure, bool>> call(String userId) async {
    return await repository.performDailyResetIfNeeded(userId);
  }
}
