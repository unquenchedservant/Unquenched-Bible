import 'package:hive/hive.dart';
import '../../core/errors/exceptions.dart';
import '../models/reading_plan_hive_model.dart';

abstract class ReadingPlanLocalDataSource {
  Future<ReadingPlanHiveModel?> getReadingPlan(String userId);
  Future<void> saveReadingPlan(ReadingPlanHiveModel plan);
  Future<void> deleteReadingPlan(String userId);
}

class ReadingPlanLocalDataSourceImpl implements ReadingPlanLocalDataSource {
  static const String boxName = 'reading_plans';

  Future<Box<ReadingPlanHiveModel>> _getBox() async {
    if (!Hive.isBoxOpen(boxName)){
      return await Hive.openBox<ReadingPlanHiveModel>(boxName);
    }
    return Hive.box<ReadingPlanHiveModel>(boxName);
  }

  @override
  Future<ReadingPlanHiveModel?> getReadingPlan(String userId) async {
    try {
      final box = await _getBox();
      return box.get(userId);
    } catch (e) {
      throw CacheException('Failed to get local reading plan: $e');
    }
  }

  @override
  Future<void> saveReadingPlan(ReadingPlanHiveModel plan) async {
    try {
      final box = await _getBox();
      await box.put(plan.userId, plan);
    } catch (e) {
      throw CacheException('Failed to save reading plan: $e');
    }     
  }

  @override
  Future<void> deleteReadingPlan(String userId) async {
    try {
      final box = await _getBox();
      await box.delete(userId);
    } catch (e){
      throw CacheException('Failed to delete reading plan: $e');
    }
  }
}
