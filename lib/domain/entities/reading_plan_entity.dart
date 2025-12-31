import 'package:equatable/equatable.dart';

class ReadingPlanEntity extends Equatable {
  final String userId;
  final String planSystem;
  final String planType;
  final String readingMode; // standard, index, or calendar
  final Map<String, int> progress;
  final int indexProgress; // Independent progress for Index mode (shared across all lists)
  final DateTime lastUpdated;
  final Map<String, DateTime> completedToday; // Tracks when lists were last completed (for streaks)
  final Map<String, bool> listCompletionStatus; // Tracks if list is marked as done (for UI)
  final int currentStreak; // Current consecutive days with at least one completion
  final int longestStreak; // Longest consecutive days with at least one completion
  final DateTime? lastCompletionDate; // Last date any list was completed
  final DateTime? lastResetDate; // Last date the daily reset was performed (independent of lastUpdated)
  final bool fivePsalmsMode; // For Horner plan: list6 shows 5 psalms per day based on day of month
  final bool requireAllListsForStreak; // Require all lists complete for streak (default: false, any list counts)

  // McCheyne-specific progress tracking (separate from Horner)
  final Map<String, int> mcheyneProgress;
  final int mcheyneIndexProgress;
  final String mcheyneReadingMode;
  final Map<String, DateTime> mcheyneCompletedToday;
  final Map<String, bool> mcheyneListCompletionStatus;
  final bool mcheyneFivePsalmsMode; // For McCheyne plan: separate 5 psalms setting
  final bool mcheyneRequireAllListsForStreak; // For McCheyne plan: separate require all lists setting

  const ReadingPlanEntity({
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
    this.lastResetDate,
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

  /// Check if a list is marked as complete (for UI button state)
  bool isCompletedToday(String listId) {
    return listCompletionStatus[listId] ?? false;
  }

  @override
  List<Object?> get props => [
    userId, planSystem, planType, readingMode, progress, indexProgress, lastUpdated,
    completedToday, listCompletionStatus, currentStreak, longestStreak, lastCompletionDate,
    lastResetDate, fivePsalmsMode, requireAllListsForStreak,
    mcheyneProgress, mcheyneIndexProgress, mcheyneReadingMode, mcheyneCompletedToday,
    mcheyneListCompletionStatus, mcheyneFivePsalmsMode, mcheyneRequireAllListsForStreak
  ];
}
