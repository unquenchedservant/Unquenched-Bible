import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:google_sign_in/google_sign_in.dart';
import '../../core/errors/exceptions.dart';

abstract class AuthRemoteDataSource {
    Future<User> signInWithEmail(String email, String password);
    Future<User> signInWithGoogle();
    Future<User> signUpWithEmail(String email, String password);
    Future<void> resetPassword(String email);
    Future<void> updateEmail(String newEmail, String currentPassword);
    Future<void> updatePassword(String newPassword, String currentPassword);
    Future<void> deleteAccount();
    Future<void> signOut();
    Future<User?> getCurrentUser();
    Stream<User?> authStateChanges();
}

class AuthRemoteDataSourceImpl implements AuthRemoteDataSource {
    final FirebaseAuth firebaseAuth;
    final GoogleSignIn googleSignIn;

    AuthRemoteDataSourceImpl({
        required this.firebaseAuth,
        required this.googleSignIn,
      });

    @override
    Future<User> signInWithEmail(String email, String password) async {
        try {
            final credential = await firebaseAuth.signInWithEmailAndPassword(
            email: email,
            password: password,
            );

            if (credential.user == null){
                throw AuthException('Sign in failed');
            }

            return credential.user!;
          } on FirebaseAuthException catch (e) {
              throw AuthException(e.message ?? 'Sign in failed');
            }
      }

      @override
      Future<User> signInWithGoogle() async {
          try {
              if (kIsWeb) {
                // Web: Use Firebase Google Auth Provider popup
                final GoogleAuthProvider googleProvider = GoogleAuthProvider();

                // Sign in with popup
                final userCredential = await firebaseAuth.signInWithPopup(googleProvider);

                if(userCredential.user == null){
                    throw AuthException('Google sign in failed');
                }
                return userCredential.user!;
              }

              // Mobile platforms: use GoogleSignIn package
              final GoogleSignInAccount googleUser = await googleSignIn.authenticate();

              if(googleUser == null){
                  throw AuthException('Google sign in cancelled');
                }

              final GoogleSignInAuthentication googleAuth = googleUser.authentication;

              final credential = GoogleAuthProvider.credential(
                idToken: googleAuth.idToken,
              );

              final userCredential = await firebaseAuth.signInWithCredential(credential);

              if(userCredential.user == null){
                  throw AuthException('Google sign in failed');
              }
              return userCredential.user!;
            } catch (e) {
                throw AuthException(e.toString());
            }
        }

        @override
        Future<User> signUpWithEmail(String email, String password) async {
            try {
                final credential = await firebaseAuth.createUserWithEmailAndPassword(
                  email: email, 
                  password: password,
                );

                if (credential.user == null){
                    throw AuthException('Sign up failed');
                }
                return credential.user!;
              } on FirebaseAuthException catch (e) {
                  throw AuthException(e.message ?? 'Sign up failed');
              }
        }

        @override
        Future<void> resetPassword(String email) async {
          try {
            await firebaseAuth.sendPasswordResetEmail(email: email);
          } on FirebaseAuthException catch (e) {
            throw AuthException(e.message ?? 'Password reset failed');
          }
        }

        @override
        Future<void> updateEmail(String newEmail, String currentPassword) async {
          try {
            final user = firebaseAuth.currentUser;
            if (user == null) throw AuthException('No user signed in');
            if (user.email == null) throw AuthException('User has no email');

            // Re-authenticate user
            final credential = EmailAuthProvider.credential(
              email: user.email!,
              password: currentPassword,
            );
            await user.reauthenticateWithCredential(credential);

            // Update email
            await user.verifyBeforeUpdateEmail(newEmail);
          } on FirebaseAuthException catch (e) {
            throw AuthException(e.message ?? 'Update email failed');
          }
        }

        @override
        Future<void> updatePassword(String newPassword, String currentPassword) async {
          try {
            final user = firebaseAuth.currentUser;
            if (user == null) throw AuthException('No user signed in');
            if (user.email == null) throw AuthException('User has no email');

            // Re-authenticate user
            final credential = EmailAuthProvider.credential(
              email: user.email!,
              password: currentPassword,
            );
            await user.reauthenticateWithCredential(credential);

            // Update password
            await user.updatePassword(newPassword);
          } on FirebaseAuthException catch (e) {
            throw AuthException(e.message ?? 'Update password failed');
          }
        }

        @override
        Future<void> deleteAccount() async {
          try {
            final user = firebaseAuth.currentUser;
            if (user == null) throw AuthException('No user signed in');
            await user.delete();
          } on FirebaseAuthException catch (e) {
            throw AuthException(e.message ?? 'Delete account failed');
          }
        }

        @override
        Future<void> signOut() async {
          try {
            await Future.wait([
              firebaseAuth.signOut(),
              googleSignIn.signOut(),
            ]);
          } catch (e) {
            throw AuthException('Sign out failed');
          }
        }

        @override
        Future<User?> getCurrentUser() async {
          return firebaseAuth.currentUser;
        }

        @override
        Stream<User?> authStateChanges() {
          return firebaseAuth.authStateChanges();
        }
  }
