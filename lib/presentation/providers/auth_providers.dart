import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:firebase_auth/firebase_auth.dart' as firebase_auth;
import 'package:google_sign_in/google_sign_in.dart';
import '../../data/repositories/auth_repository_impl.dart';
import '../../data/sources/auth_remote_data_source.dart';
import '../../domain/usecases/sign_in_with_email.dart';
import '../../domain/repositories/auth_repository.dart';
import '../../domain/usecases/sign_in_with_google.dart';
import '../../domain/usecases/sign_up_with_email.dart';
import '../../domain/usecases/sign_out.dart';
import '../../domain/entities/user_entity.dart';

final firebaseAuthProvider = Provider<firebase_auth.FirebaseAuth>((ref){
  return firebase_auth.FirebaseAuth.instance;
});

final googleSignInProvider = Provider<GoogleSignIn>((ref) {
    return GoogleSignIn.instance;
});

final authRemoteDataSourceProvider = Provider<AuthRemoteDataSource>((ref) {
    return AuthRemoteDataSourceImpl(
      firebaseAuth: ref.read(firebaseAuthProvider),
      googleSignIn: ref.read(googleSignInProvider),
    );
});

final authRepositoryProvider = Provider<AuthRepository>((ref){
    return AuthRepositoryImpl(
      remoteDataSource: ref.read(authRemoteDataSourceProvider),
    );
});

final signInWithEmailProvider = Provider<SignInWithEmail>((ref) {
  return SignInWithEmail(ref.read(authRepositoryProvider));
});

final signInWithGoogleProvider = Provider<SignInWithGoogle>((ref){
    return SignInWithGoogle(ref.read(authRepositoryProvider));
});

final signUpWithEmailProvider = Provider<SignUpWithEmail>((ref){
    return SignUpWithEmail(ref.read(authRepositoryProvider));
});

final signOutProvider = Provider<SignOut>((ref) {
    return SignOut(ref.read(authRepositoryProvider));
});

final authStateProvider = StreamProvider<UserEntity?>((ref) {
    final repository = ref.watch(authRepositoryProvider);
    return repository.authStateChanges();
});

// Make the provider callback async so `await` can be used
final currentUserProvider = FutureProvider<UserEntity?>((ref) async {
    final repository = ref.read(authRepositoryProvider);
    final result = await repository.getCurrentUser();
    return result.fold(
      (failure) => null,
      (user) => user,
    );
});
