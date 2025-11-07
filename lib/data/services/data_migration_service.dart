import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/reading_plan_model.dart';

class DataMigrationService {
  final FirebaseFirestore firestore;

  DataMigrationService({required this.firestore});
  Future<void> migrateUserData(String userId) async {
    try {
      final oldDoc = await firestore.collection('main').doc(userId).get();

      if(!oldDoc.exists) return;

      final data = oldDoc.data();
      if(data == null) return;

      final newDoc = await firestore.collection('reading_plans').doc(userId).get();

      if(newDoc.exists) return;

      final migratedPlan = ReadingPlanModel.fromLegacyFirestore(oldDoc);

      await firestore.collection('reading_plans').doc(userId).set(migratedPlan.toFirestore());

      print('Migration complete for user: $userId');
    } catch (e) {
      print('Migration failed for user $userId: $e');
      rethrow;
    }
  }
}
