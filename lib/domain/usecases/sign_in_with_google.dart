import 'package:dartz/dartz.dart';
import '../../core/errors/failures.dart';
import '../entities/user_entity.dart';
import '../repositories/auth_repository.dart';

class SignInWithGoogle {
    final AuthRepository repository;

    SignInWithGoogle(this.repository);

    Future<Either<Failure, UserEntity>> call(){
        return repository.signInWithGoogle();
      }
  }
