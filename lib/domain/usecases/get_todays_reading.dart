import 'package:dartz/dartz.dart';
import '../../core/constants/reading_lists.dart';
import '../../core/errors/failures.dart';
import '../entities/reading_list_entity.dart';
import '../repositories/reading_plan_repository.dart';

class GetTodaysReadings {
  final ReadingPlanRepository repository;

  GetTodaysReadings(this.repository);

  Future<Either<Failure, List<ReadingListEntity>>> call(String userId) async {
    final planResult = await repository.getReadingPlan(userId);

    return planResult.fold(
      (failure) => Left(failure),
      (plan) {
        List<ReadingListEntity> readings = [];

        if (plan.planSystem == 'horner') {
          // Define the correct order for Grant Horner lists
          final orderedListIds = [
            'list1',  // Gospels
            'list2',  // Pentateuch
            'list3',  // Epistles I
            'list4',  // Epistles II
            'list5',  // Poetry
            'list6',  // Psalms
            'list7',  // Proverbs
            'list8',  // History
            'list9',  // Prophets
            'list10', // Acts
          ];

          // Iterate in the specified order
          for (var listId in orderedListIds) {
            final currentChapter = plan.progress[listId];
            if (currentChapter == null) continue; // Skip if not in progress map

            final totalChapters = ReadingLists.getTotalChapters(listId, plan.planSystem);
            final isCompleted = plan.isCompletedToday(listId);

            // Determine which chapter to show based on reading mode
            int displayChapter;
            switch (plan.readingMode) {
              case 'index':
                // In index mode, all lists show the shared indexProgress
                displayChapter = plan.indexProgress;
                break;
              case 'calendar':
                // In calendar mode, everyone reads based on day of year (1-365/366)
                final now = DateTime.now();
                final dayOfYear = now.difference(DateTime(now.year, 1, 1)).inDays + 1;
                // Horner plan wraps around naturally, so day 366 is fine
                displayChapter = ((dayOfYear - 1) % totalChapters) + 1;
                break;
              default:
                // Standard mode: each list has its own progress
                displayChapter = currentChapter;
            }

            readings.add(ReadingListEntity(
              id: listId,
              name: _getListName(listId, plan.planSystem),
              books: _getListBooks(listId, plan.planSystem),
              totalChapters: totalChapters,
              currentChapter: displayChapter,
              isCompletedToday: isCompleted,
              fivePsalmsMode: plan.fivePsalmsMode,
              requireAllListsForStreak: plan.requireAllListsForStreak,
              readingMode: plan.readingMode,
              indexProgress: plan.indexProgress,
            ));
          }
        } else if (plan.planSystem == 'mcheyne') {
          // McCheyne plan has 4 lists
          final mcheyneListIds = [
            'mcheynelist1',  // Family I
            'mcheynelist2',  // Family II
            'mcheynelist3',  // Secret I
            'mcheynelist4',  // Secret II
          ];

          for (var listId in mcheyneListIds) {
            final currentIndex = plan.mcheyneProgress[listId];
            if (currentIndex == null) continue;

            final totalReadings = ReadingLists.getTotalChapters(listId, plan.planSystem);
            final isCompleted = plan.mcheyneListCompletionStatus[listId] ?? false;

            // Determine which reading to show based on reading mode
            int displayIndex;
            switch (plan.mcheyneReadingMode) {
              case 'index':
                displayIndex = plan.mcheyneIndexProgress;
                break;
              case 'calendar':
                final now = DateTime.now();
                final dayOfYear = now.difference(DateTime(now.year, 1, 1)).inDays + 1;
                // On leap years (day 366), give a day off by setting to reading 1
                // Regular years max out at 365, leap years at 366
                if (dayOfYear == 366) {
                  displayIndex = 1; // Day off - show first reading but don't require completion
                } else {
                  displayIndex = ((dayOfYear - 1) % totalReadings) + 1;
                }
                break;
              default:
                displayIndex = currentIndex;
            }

            readings.add(ReadingListEntity(
              id: listId,
              name: _getListName(listId, plan.planSystem),
              books: [], // McCheyne lists don't have book arrays
              totalChapters: totalReadings,
              currentChapter: displayIndex,
              isCompletedToday: isCompleted,
              fivePsalmsMode: plan.mcheyneFivePsalmsMode,
              requireAllListsForStreak: plan.mcheyneRequireAllListsForStreak,
              readingMode: plan.mcheyneReadingMode,
              indexProgress: plan.mcheyneIndexProgress,
            ));
          }

          // Add 5th card for 5 Psalms A Day if enabled
          if (plan.mcheyneFivePsalmsMode) {
            readings.add(ReadingListEntity(
              id: 'mcheynepsalms',
              name: '5 Psalms A Day',
              books: ['Psalms'],
              totalChapters: 30, // Days in a month (excluding day 31)
              currentChapter: DateTime.now().day,
              isCompletedToday: false, // 5 Psalms mode doesn't track completion
              fivePsalmsMode: true,
              requireAllListsForStreak: plan.mcheyneRequireAllListsForStreak,
              readingMode: plan.mcheyneReadingMode,
              indexProgress: plan.mcheyneIndexProgress,
            ));
          }
        }

        return Right(readings);
      },
    );
  }

  String _getListName(String listId, String system) {
    if (system == 'horner'){
      return ReadingLists.hornerLists[listId]?['name'] as String? ?? listId;
    } else if (system == 'mcheyne'){
      return ReadingLists.mcheynePlan[listId]?['name'] as String? ?? listId;
    }
    return listId;
  }

  List<String> _getListBooks(String listId, String system) {
    if (system == 'horner'){
      final list = ReadingLists.hornerLists[listId];
      if (list == null) return [];
      return List<String>.from(list['books'] as List);
    }
    // McCheyne lists don't have individual books, just readings
    return [];
  }
}
