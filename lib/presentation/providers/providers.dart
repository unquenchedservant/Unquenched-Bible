import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:cloud_firestore/cloud_firestore.dart';

final firebaseAuthProvider = Provider<FirebaseAuth>((ref) {
    return FirebaseAuth.instance;
});

final firestoreProvider = Provider<FirebaseFirestore>((ref) {
    return FirebaseFirestore.instance;
});

final authStateProvider = StreamProvider<User?>((ref) {
    final auth = ref.watch(firebaseAuthProvider);
    return auth.authStateChanges();
});
