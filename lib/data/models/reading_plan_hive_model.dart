import 'package:hive/hive.dart';
import '../../domain/entities/reading_plan_entity.dart';

part 'reading_plan_hive_model.g.dart';

@HiveType(typeId: 0)
class ReadingPlanHiveModel extends HiveObject {
    @HiveField(0)
    final String userId;

    @HiveField(1)
    final String planSystem;

    @HiveField(2)
    final String planType;

    @HiveField(3)
    final Map<String, int> progress;

    @HiveField(4)
    final DateTime lastUpdated;

    @HiveField(5)
    final Map<String, DateTime> completedToday;

    @HiveField(6)
    final int currentStreak;

    @HiveField(7)
    final int longestStreak;

    @HiveField(8)
    final DateTime? lastCompletionDate;

    @HiveField(9)
    final Map<String, bool> listCompletionStatus;

    @HiveField(10)
    final bool fivePsalmsMode;

    @HiveField(11)
    final bool requireAllListsForStreak;

    @HiveField(12)
    final String readingMode;

    @HiveField(13)
    final int indexProgress;

    // McCheyne-specific fields
    @HiveField(14)
    final Map<String, int> mcheyneProgress;

    @HiveField(15)
    final int mcheyneIndexProgress;

    @HiveField(16)
    final String mcheyneReadingMode;

    @HiveField(17)
    final Map<String, DateTime> mcheyneCompletedToday;

    @HiveField(18)
    final Map<String, bool> mcheyneListCompletionStatus;

    @HiveField(19)
    final bool mcheyneFivePsalmsMode;

    @HiveField(20)
    final bool mcheyneRequireAllListsForStreak;

    ReadingPlanHiveModel({
        required this.userId,
        required this.planSystem,
        required this.planType,
        this.readingMode = 'standard',
        required this.progress,
        this.indexProgress = 1,
        required this.lastUpdated,
        this.completedToday = const {},
        this.listCompletionStatus = const {},
        this.currentStreak = 0,
        this.longestStreak = 0,
        this.lastCompletionDate,
        this.fivePsalmsMode = false,
        this.requireAllListsForStreak = false,
        // McCheyne-specific fields
        this.mcheyneProgress = const {},
        this.mcheyneIndexProgress = 1,
        this.mcheyneReadingMode = 'standard',
        this.mcheyneCompletedToday = const {},
        this.mcheyneListCompletionStatus = const {},
        this.mcheyneFivePsalmsMode = false,
        this.mcheyneRequireAllListsForStreak = false,
    });

    factory ReadingPlanHiveModel.fromEntity(ReadingPlanEntity entity){
      return ReadingPlanHiveModel(
        userId: entity.userId,
        planSystem: entity.planSystem,
        planType: entity.planType,
        readingMode: entity.readingMode,
        progress: Map<String, int>.from(entity.progress),
        indexProgress: entity.indexProgress,
        lastUpdated: entity.lastUpdated,
        completedToday: Map<String, DateTime>.from(entity.completedToday),
        listCompletionStatus: Map<String, bool>.from(entity.listCompletionStatus),
        currentStreak: entity.currentStreak,
        longestStreak: entity.longestStreak,
        lastCompletionDate: entity.lastCompletionDate,
        fivePsalmsMode: entity.fivePsalmsMode,
        requireAllListsForStreak: entity.requireAllListsForStreak,
        // McCheyne-specific fields
        mcheyneProgress: Map<String, int>.from(entity.mcheyneProgress),
        mcheyneIndexProgress: entity.mcheyneIndexProgress,
        mcheyneReadingMode: entity.mcheyneReadingMode,
        mcheyneCompletedToday: Map<String, DateTime>.from(entity.mcheyneCompletedToday),
        mcheyneListCompletionStatus: Map<String, bool>.from(entity.mcheyneListCompletionStatus),
        mcheyneFivePsalmsMode: entity.mcheyneFivePsalmsMode,
        mcheyneRequireAllListsForStreak: entity.mcheyneRequireAllListsForStreak,
      );
    }

    ReadingPlanEntity toEntity() {
      return ReadingPlanEntity(
        userId: userId,
        planSystem: planSystem,
        planType: planType,
        readingMode: readingMode,
        progress: Map<String, int>.from(progress),
        indexProgress: indexProgress,
        lastUpdated: lastUpdated,
        completedToday: Map<String, DateTime>.from(completedToday),
        listCompletionStatus: Map<String, bool>.from(listCompletionStatus),
        currentStreak: currentStreak,
        longestStreak: longestStreak,
        lastCompletionDate: lastCompletionDate,
        fivePsalmsMode: fivePsalmsMode,
        requireAllListsForStreak: requireAllListsForStreak,
        // McCheyne-specific fields
        mcheyneProgress: Map<String, int>.from(mcheyneProgress),
        mcheyneIndexProgress: mcheyneIndexProgress,
        mcheyneReadingMode: mcheyneReadingMode,
        mcheyneCompletedToday: Map<String, DateTime>.from(mcheyneCompletedToday),
        mcheyneListCompletionStatus: Map<String, bool>.from(mcheyneListCompletionStatus),
        mcheyneFivePsalmsMode: mcheyneFivePsalmsMode,
        mcheyneRequireAllListsForStreak: mcheyneRequireAllListsForStreak,
      );
    }
}
