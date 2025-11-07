import 'package:flutter/material.dart';
import '../../domain/entities/reading_list_entity.dart';
import '../../core/constants/reading_lists.dart';

class ReadingListCard extends StatelessWidget {
  final ReadingListEntity reading;
  final VoidCallback onMarkComplete;
  final VoidCallback? onReset;
  final VoidCallback? onViewScripture;
  final VoidCallback? onPressRead;

  const ReadingListCard({
    super.key,
    required this.reading,
    required this.onMarkComplete,
    this.onReset,
    this.onViewScripture,
    this.onPressRead,
  });

  @override
  Widget build(BuildContext context) {
    // Check if this is a McCheyne list
    final bool isMcheyneList = reading.id.startsWith('mcheynelist');

    // Check if this is the McCheyne 5 Psalms special card
    final bool isMcheynePsalms = reading.id == 'mcheynepsalms';

    // Special handling for Horner 5 Psalms mode - use current day of month
    final bool is5PsalmsMode = reading.id == 'list6' && reading.fivePsalmsMode;
    final int dayToUse = is5PsalmsMode ? DateTime.now().day : reading.currentChapter;

    String displayText;
    String progressText;

    if (isMcheynePsalms) {
      // McCheyne 5 Psalms A Day card
      final currentDay = DateTime.now().day;
      if (currentDay == 31) {
        displayText = 'Day Off';
      } else {
        // Calculate the 5 psalms based on the current day
        final psalms = [
          currentDay,
          currentDay + 30,
          currentDay + 60,
          currentDay + 90,
          currentDay + 120,
        ];
        displayText = 'Psalms ${psalms.join(', ')}';
      }
      progressText = 'Day $currentDay/30';
    } else if (isMcheyneList) {
      // McCheyne lists - check for leap year day off in calendar mode
      if (reading.readingMode == 'calendar') {
        final now = DateTime.now();
        final dayOfYear = now.difference(DateTime(now.year, 1, 1)).inDays + 1;

        if (dayOfYear == 366) {
          // Day 366 (Dec 31st in leap years) - show "Day Off"
          displayText = 'Day Off';
          progressText = 'Enjoy your rest day!';
        } else {
          // Normal calendar day
          displayText = ReadingLists.getMcheyneReading(reading.id, reading.currentChapter);
          progressText = 'Progress: ${reading.currentChapter}/${reading.totalChapters}';
        }
      } else {
        // Standard or index mode
        displayText = ReadingLists.getMcheyneReading(reading.id, reading.currentChapter);
        progressText = 'Progress: ${reading.currentChapter}/${reading.totalChapters}';
      }
    } else {
      // Horner lists - use book/chapter lookup
      final bookInfo = ReadingLists.getBookAndChapter(reading.id, dayToUse, fivePsalmsMode: reading.fivePsalmsMode);
      final book = bookInfo['book'] ?? '';
      final chapter = bookInfo['chapter'] ?? 0;

      final List<int> psalms = is5PsalmsMode ? (bookInfo['psalms'] as List<int>?) ?? [] : [];

      if (is5PsalmsMode) {
        if (dayToUse == 31) {
          displayText = 'Day Off';
        } else if (psalms.isNotEmpty) {
          displayText = 'Psalms ${psalms.join(', ')}';
        } else {
          displayText = book;
        }
      } else {
        displayText = '$book ${chapter > 0 ? chapter : ''}';
      }

      // Progress text for 5 Psalms mode shows "Day X/30" instead of chapter progress
      if (is5PsalmsMode) {
        progressText = 'Day $dayToUse/30';
      } else {
        progressText = 'Progress: ${reading.currentChapter}/${reading.totalChapters}';
      }
    }

    // Use MediaQuery to detect screen size
    final screenWidth = MediaQuery.of(context).size.width;
    final isCompact = screenWidth < 600; // Mobile/compact mode

    return Card(
      margin: EdgeInsets.only(bottom: isCompact ? 8 : 12),
      child: Padding(
        padding: EdgeInsets.all(isCompact ? 12 : 16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              reading.name,
              style: Theme.of(context).textTheme.titleSmall?.copyWith(
                fontSize: isCompact ? 12 : null,
              ),
            ),
            SizedBox(height: isCompact ? 4 : 8),

            Text(
              displayText,
              style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                fontSize: isCompact ? 20 : null,
              ),
            ),
            SizedBox(height: isCompact ? 4 : 8),

            if (!is5PsalmsMode && !isMcheynePsalms)
              LinearProgressIndicator(
                value: reading.currentChapter / reading.totalChapters,
              ),
            if (!is5PsalmsMode && !isMcheynePsalms)
              SizedBox(height: isCompact ? 2 : 4),
            Text(
              progressText,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                fontSize: isCompact ? 11 : null,
              ),
            ),
            SizedBox(height: isCompact ? 8 : 12),

            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                if (onPressRead != null && !isCompact)
                  TextButton.icon(
                    onPressed: onPressRead,
                    icon: const Icon(Icons.book, size: 18),
                    label: const Text('Read'),
                  ),
                if (onPressRead != null && isCompact)
                  TextButton(
                    onPressed: onPressRead,
                    style: TextButton.styleFrom(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      minimumSize: const Size(60, 32),
                    ),
                    child: const Text('Read', style: TextStyle(fontSize: 13)),
                  ),
                // Don't show Complete/Reset buttons for McCheyne 5 Psalms card
                if (!isMcheynePsalms)
                  SizedBox(width: isCompact ? 4 : 8),
                // Show Reset button if completed today, otherwise show Complete button
                if (!isMcheynePsalms && reading.isCompletedToday && !isCompact)
                  OutlinedButton.icon(
                    onPressed: onReset,
                    icon: const Icon(Icons.refresh, size: 18),
                    label: const Text('Reset'),
                  ),
                if (!isMcheynePsalms && reading.isCompletedToday && isCompact)
                  OutlinedButton(
                    onPressed: onReset,
                    style: OutlinedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                      minimumSize: const Size(70, 32),
                    ),
                    child: const Text('Reset', style: TextStyle(fontSize: 13)),
                  ),
                if (!isMcheynePsalms && !reading.isCompletedToday && !isCompact)
                  FilledButton.icon(
                    onPressed: onMarkComplete,
                    icon: const Icon(Icons.check, size: 18),
                    label: const Text('Complete'),
                  ),
                if (!isMcheynePsalms && !reading.isCompletedToday && isCompact)
                  FilledButton(
                    onPressed: onMarkComplete,
                    style: FilledButton.styleFrom(
                      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                      minimumSize: const Size(80, 32),
                    ),
                    child: const Text('Complete', style: TextStyle(fontSize: 13)),
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
