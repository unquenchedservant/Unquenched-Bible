import 'package:dartz/dartz.dart';
import '../../core/errors/failures.dart';
import '../entities/user_entity.dart';

abstract class AuthRepository {
  Future<Either<Failure, UserEntity>> signInWithEmail(String email, String password);
  Future<Either<Failure, UserEntity>> signInWithGoogle();

  Future<Either<Failure, UserEntity>> signUpWithEmail(String email, String password);

  Future<Either<Failure, void>> resetPassword(String email);
  Future<Either<Failure, void>> updateEmail(String newEmail, String currentPassword);
  Future<Either<Failure, void>> updatePassword(String newPassword, String currentPassword);
  Future<Either<Failure, void>> deleteAccount();
  Future<Either<Failure, void>> signOut();

  Future<Either<Failure, UserEntity?>> getCurrentUser();
  Stream<UserEntity?> authStateChanges();
}
