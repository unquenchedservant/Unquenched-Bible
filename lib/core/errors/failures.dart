import 'package:equatable/equatable.dart';

abstract class Failure extends Equatable {
    final String message;
    const Failure(this.message);

    @override
    List<Object> get props => [message];
  }

class ServerFailure extends Failure {
    const ServerFailure([super.message = 'Server Error']);
  }

class CacheFailure extends Failure {
  const CacheFailure([super.message = 'Cache error']);
}

class AuthFailure extends Failure {
    const AuthFailure([super.message = 'Authentication error']);
  }

class ValidationFailure extends Failure {
  const ValidationFailure([super.message = 'Validation error']);
}
