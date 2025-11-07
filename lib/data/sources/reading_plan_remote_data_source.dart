import 'package:cloud_firestore/cloud_firestore.dart';
import '../../core/errors/exceptions.dart';
import '../models/reading_plan_model.dart';

abstract class ReadingPlanRemoteDataSource {
  Future<ReadingPlanModel> getReadingPlan(String userId);
  Future<void> saveReadingPlan(ReadingPlanModel plan);
  Stream<ReadingPlanModel> watchReadingPlan(String userId);
  Future<void> deleteUserData(String userId);
}

class ReadingPlanRemoteDataSourceImpl implements ReadingPlanRemoteDataSource {
  final FirebaseFirestore firestore;
  static const String collection = 'reading_plans';

  ReadingPlanRemoteDataSourceImpl({required this.firestore});

  @override
  Future<ReadingPlanModel> getReadingPlan(String userId) async {
      try {
        final doc = await firestore.collection(collection).doc(userId).get();
        if (!doc.exists){
          // Don't auto-create - let onboarding handle it
          throw ServerException('No reading plan found for user');
        }

        return ReadingPlanModel.fromFirestore(doc);
      } catch (e) {
        throw ServerException('Failed to fetch reading plan: $e');
      }
  }
  
  @override
  Future<void> saveReadingPlan(ReadingPlanModel plan) async {
    try {
      await firestore.collection(collection).doc(plan.userId).set(plan.toFirestore(), SetOptions(merge: true));
    } catch (e) {
      throw ServerException('Failed to save reading plan: $e');
    }
  }

  @override
  Stream<ReadingPlanModel> watchReadingPlan(String userId){
    return firestore.collection(collection).doc(userId).snapshots().map((doc) => ReadingPlanModel.fromFirestore(doc));
  }

  @override
  Future<void> deleteUserData(String userId) async {
    try {
      await firestore.collection(collection).doc(userId).delete();
    } catch (e) {
      throw ServerException('Failed to delete user data: $e');
    }
  }
}
