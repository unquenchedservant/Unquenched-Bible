import 'package:dartz/dartz.dart';
import '../../core/errors/failures.dart';
import '../repositories/auth_repository.dart';

class ResetPassword {
    final AuthRepository repository;

    ResetPassword(this.repository);

    Future<Either<Failure, void>> call(String email){
        return repository.resetPassword(email);
      }
  }
