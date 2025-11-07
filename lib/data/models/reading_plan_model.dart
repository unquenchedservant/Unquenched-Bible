import 'package:cloud_firestore/cloud_firestore.dart';
import '../../domain/entities/reading_plan_entity.dart';

class ReadingPlanModel extends ReadingPlanEntity {
  const ReadingPlanModel({
    required super.userId,
    required super.planSystem,
    required super.planType,
    super.readingMode,
    required super.progress,
    super.indexProgress,
    required super.lastUpdated,
    super.completedToday,
    super.listCompletionStatus,
    super.currentStreak,
    super.longestStreak,
    super.lastCompletionDate,
    super.fivePsalmsMode,
    super.requireAllListsForStreak,
    // McCheyne-specific fields
    super.mcheyneProgress,
    super.mcheyneIndexProgress,
    super.mcheyneReadingMode,
    super.mcheyneCompletedToday,
    super.mcheyneListCompletionStatus,
    super.mcheyneFivePsalmsMode,
    super.mcheyneRequireAllListsForStreak,
  });

  factory ReadingPlanModel.fromFirestore(DocumentSnapshot doc){
    final data = doc.data() as Map<String, dynamic>;

    // Parse completedToday from Firestore
    Map<String, DateTime> completedToday = {};
    if (data['completedToday'] != null) {
      final completedData = data['completedToday'] as Map<String, dynamic>;
      completedData.forEach((key, value) {
        completedToday[key] = (value as Timestamp).toDate();
      });
    }

    // Parse listCompletionStatus from Firestore
    Map<String, bool> listCompletionStatus = {};
    if (data['listCompletionStatus'] != null) {
      listCompletionStatus = Map<String, bool>.from(data['listCompletionStatus'] as Map);
    }

    // Parse lastCompletionDate from Firestore
    DateTime? lastCompletionDate;
    if (data['lastCompletionDate'] != null) {
      lastCompletionDate = (data['lastCompletionDate'] as Timestamp).toDate();
    }

    // Parse McCheyne-specific completedToday from Firestore
    Map<String, DateTime> mcheyneCompletedToday = {};
    if (data['mcheyneCompletedToday'] != null) {
      final mcheyneCompletedData = data['mcheyneCompletedToday'] as Map<String, dynamic>;
      mcheyneCompletedData.forEach((key, value) {
        mcheyneCompletedToday[key] = (value as Timestamp).toDate();
      });
    }

    // Parse McCheyne listCompletionStatus from Firestore
    Map<String, bool> mcheyneListCompletionStatus = {};
    if (data['mcheyneListCompletionStatus'] != null) {
      mcheyneListCompletionStatus = Map<String, bool>.from(data['mcheyneListCompletionStatus'] as Map);
    }

    return ReadingPlanModel(
      userId: doc.id,
      planSystem: data['planSystem'] ?? 'horner',
      planType: data['planType'] ?? 'standard',
      readingMode: data['readingMode'] ?? 'standard',
      progress: Map<String, int>.from(data['progress'] ?? {}),
      indexProgress: data['indexProgress'] ?? 1,
      lastUpdated: (data['lastUpdated'] as Timestamp).toDate(),
      completedToday: completedToday,
      listCompletionStatus: listCompletionStatus,
      currentStreak: data['currentStreak'] ?? 0,
      longestStreak: data['longestStreak'] ?? 0,
      lastCompletionDate: lastCompletionDate,
      fivePsalmsMode: data['fivePsalmsMode'] ?? false,
      requireAllListsForStreak: data['requireAllListsForStreak'] ?? false,
      // McCheyne-specific fields
      mcheyneProgress: Map<String, int>.from(data['mcheyneProgress'] ?? {}),
      mcheyneIndexProgress: data['mcheyneIndexProgress'] ?? 1,
      mcheyneReadingMode: data['mcheyneReadingMode'] ?? 'standard',
      mcheyneCompletedToday: mcheyneCompletedToday,
      mcheyneListCompletionStatus: mcheyneListCompletionStatus,
      mcheyneFivePsalmsMode: data['mcheyneFivePsalmsMode'] ?? false,
      mcheyneRequireAllListsForStreak: data['mcheyneRequireAllListsForStreak'] ?? false,
    );
  }

  Map<String, dynamic> toFirestore(){
    // Convert completedToday to Firestore Timestamps
    final completedTodayData = <String, Timestamp>{};
    completedToday.forEach((key, value) {
      completedTodayData[key] = Timestamp.fromDate(value);
    });

    // Convert McCheyne completedToday to Firestore Timestamps
    final mcheyneCompletedTodayData = <String, Timestamp>{};
    mcheyneCompletedToday.forEach((key, value) {
      mcheyneCompletedTodayData[key] = Timestamp.fromDate(value);
    });

    return {
      'planSystem': planSystem,
      'planType': planType,
      'readingMode': readingMode,
      'progress': progress,
      'indexProgress': indexProgress,
      'lastUpdated': Timestamp.fromDate(lastUpdated),
      'completedToday': completedTodayData,
      'listCompletionStatus': listCompletionStatus,
      'currentStreak': currentStreak,
      'longestStreak': longestStreak,
      'lastCompletionDate': lastCompletionDate != null ? Timestamp.fromDate(lastCompletionDate!) : null,
      'fivePsalmsMode': fivePsalmsMode,
      'requireAllListsForStreak': requireAllListsForStreak,
      // McCheyne-specific fields
      'mcheyneProgress': mcheyneProgress,
      'mcheyneIndexProgress': mcheyneIndexProgress,
      'mcheyneReadingMode': mcheyneReadingMode,
      'mcheyneCompletedToday': mcheyneCompletedTodayData,
      'mcheyneListCompletionStatus': mcheyneListCompletionStatus,
      'mcheyneFivePsalmsMode': mcheyneFivePsalmsMode,
      'mcheyneRequireAllListsForStreak': mcheyneRequireAllListsForStreak,
    };
  }

  factory ReadingPlanModel.fromLegacyFirestore(DocumentSnapshot doc){
      final data = doc.data() as Map<String, dynamic>;

      Map<String, int> progress = {};
      for (int i = 1; i <= 10; i++){
        final key = 'list$i';
        if (data.containsKey(key)){
            progress[key] = data[key] as int;
        }
      }

      return ReadingPlanModel(
        userId: doc.id,
        planSystem: data['planSystem'] ?? 'horner',
        planType: 'standard',
        progress: progress,
        lastUpdated: DateTime.now(),
      );
  }
  ReadingPlanEntity toEntity() => this;
}
