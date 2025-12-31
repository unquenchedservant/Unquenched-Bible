import 'package:dartz/dartz.dart';
import '../../core/errors/failures.dart';
import '../entities/user_entity.dart';
import '../repositories/auth_repository.dart';

class SignUpWithEmail {
    final AuthRepository repository;

    SignUpWithEmail(this.repository);

    Future<Either<Failure, UserEntity>> call(String email, String password) {
        return repository.signUpWithEmail(email, password);
      }
  }
