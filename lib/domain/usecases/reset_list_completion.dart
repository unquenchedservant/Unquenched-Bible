import 'package:dartz/dartz.dart';
import '../../core/errors/failures.dart';
import '../repositories/reading_plan_repository.dart';

class ResetListCompletion {
  final ReadingPlanRepository repository;

  ResetListCompletion(this.repository);

  Future<Either<Failure, void>> call(String userId, String listId) async {
    return await repository.resetListCompletion(userId, listId);
  }
}
