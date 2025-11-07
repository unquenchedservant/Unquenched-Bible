import 'package:equatable/equatable.dart';

class ReadingListEntity extends Equatable {
  final String id;
  final String name;
  final List<String> books;
  final int totalChapters;
  final int currentChapter;
  final bool isCompletedToday;
  final bool fivePsalmsMode; // For list6: indicates 5 psalms per day mode
  final bool requireAllListsForStreak; // Require all lists complete for streak (default: false, any list counts)
  final String readingMode; // standard, index, or calendar
  final int indexProgress; // Shared progress for index mode

  const ReadingListEntity({
    required this.id,
    required this.name,
    required this.books,
    required this.totalChapters,
    required this.currentChapter,
    this.isCompletedToday = false,
    this.fivePsalmsMode = false,
    this.requireAllListsForStreak = false,
    this.readingMode = 'standard',
    this.indexProgress = 1,
  });

  @override
  List<Object> get props => [id, name, books, totalChapters, currentChapter, isCompletedToday, fivePsalmsMode, requireAllListsForStreak, readingMode, indexProgress];
}
