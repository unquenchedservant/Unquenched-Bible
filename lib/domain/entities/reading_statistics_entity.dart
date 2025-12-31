import 'package:equatable/equatable.dart';

class ReadingStatisticsEntity extends Equatable {
  final String userId;
  final int totalChaptersRead;
  final int currentStreak;
  final int longestStreak;
  final DateTime lastReadDate;
  final Map<String, int> completionCounts;

  const ReadingStatisticsEntity({
    required this.userId,
    required this.totalChaptersRead,
    required this.currentStreak,
    required this.longestStreak,
    required this.lastReadDate,
    required this.completionCounts,
  });

  @override
  List<Object> get props => [
    userId,
    totalChaptersRead,
    currentStreak,
    longestStreak,
    lastReadDate,
    completionCounts,
  ];
}
