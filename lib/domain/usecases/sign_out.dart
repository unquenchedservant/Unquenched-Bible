import 'package:dartz/dartz.dart';
import '../../core/errors/failures.dart';
import '../entities/user_entity.dart';
import '../repositories/auth_repository.dart';

class SignOut {
    final AuthRepository repository;

    SignOut(this.repository);

    Future<Either<Failure, void>> call(){
        return repository.signOut();
      }
  }
