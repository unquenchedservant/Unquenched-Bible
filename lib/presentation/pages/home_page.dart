import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../core/constants/reading_lists.dart';
import '../providers/reading_providers.dart';
import '../widgets/reading_list_card.dart';

class HomePage extends ConsumerWidget {
    const HomePage({super.key});

    @override
    Widget build(BuildContext context, WidgetRef ref) {
      final readingsAsync = ref.watch(todaysReadingsProvider);

      return Scaffold(
        appBar: AppBar(
          title: const Text('Today\'s Reading'),
          actions: [
            // Bulk action button that changes based on completion state
            readingsAsync.when(
              data: (readings) {
                if (readings.isEmpty) return const SizedBox.shrink();

                // Count completed readings (excluding McCheyne 5 Psalms card)
                final trackableReadings = readings.where((r) => r.id != 'mcheynepsalms').toList();
                final completedCount = trackableReadings.where((r) => r.isCompletedToday).length;
                final totalCount = trackableReadings.length;
                final allCompleted = completedCount == totalCount;
                final someCompleted = completedCount > 0;

                // Determine button state
                String buttonText;
                VoidCallback? buttonAction;

                if (allCompleted) {
                  buttonText = 'Reset All';
                  buttonAction = () async {
                    final resetAction = ref.read(resetListCompletionProvider);
                    for (var reading in readings) {
                      if ((reading.id == 'list6' && reading.fivePsalmsMode) || reading.id == 'mcheynepsalms') {
                        continue; // Skip 5 Psalms cards
                      }
                      await resetAction.callWithoutInvalidate(reading.id);
                    }
                    // Refresh once at the end
                    ref.invalidate(todaysReadingsProvider);
                  };
                } else if (someCompleted) {
                  buttonText = 'Mark Remaining';
                  buttonAction = () async {
                    final markAction = ref.read(markReadingCompleteProvider);
                    for (var reading in readings) {
                      if (!reading.isCompletedToday && reading.id != 'mcheynepsalms') {
                        await markAction.callWithoutInvalidate(reading.id);
                      }
                    }
                    // Refresh once at the end
                    ref.invalidate(todaysReadingsProvider);
                  };
                } else {
                  buttonText = 'Mark All';
                  buttonAction = () async {
                    final markAction = ref.read(markReadingCompleteProvider);
                    for (var reading in readings) {
                      if (reading.id != 'mcheynepsalms') {
                        await markAction.callWithoutInvalidate(reading.id);
                      }
                    }
                    // Refresh once at the end
                    ref.invalidate(todaysReadingsProvider);
                  };
                }

                return TextButton(
                  onPressed: buttonAction,
                  child: Text(buttonText),
                );
              },
              loading: () => const SizedBox.shrink(),
              error: (_, __) => const SizedBox.shrink(),
            ),
            IconButton(
              icon: const Icon(Icons.refresh),
              tooltip: 'Refresh',
              onPressed: () async {
                // Check for daily reset first
                await ref.read(checkDailyResetProvider).call();
                // Then refresh the readings
                ref.invalidate(todaysReadingsProvider);
              },
            ),
          ],
        ),
        body: readingsAsync.when(
          data: (readings) {
            // Check for McCheyne list validation errors
            final validationError = ReadingLists.validateMcheyneLists();

            if (readings.isEmpty) {
              return const Center(child: Text('No reading plan set'));
            }

            return Center(
              child: ConstrainedBox(
                constraints: const BoxConstraints(maxWidth: 900),
                child: LayoutBuilder(
                  builder: (context, constraints) {
                    // Calculate card width based on available width within the ConstrainedBox
                    final availableWidth = constraints.maxWidth;
                    final cardWidth = (availableWidth - 44) / 2; // 44 = padding(32) + spacing(12)

                    return SingleChildScrollView(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        children: [
                      // Show validation warning if there's an error
                      if (validationError != null)
                        Container(
                          width: double.infinity,
                          margin: const EdgeInsets.only(bottom: 16),
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: Colors.red.shade100,
                            border: Border.all(color: Colors.red.shade300),
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Row(
                            children: [
                              Icon(Icons.warning, color: Colors.red.shade700),
                              const SizedBox(width: 8),
                              Expanded(
                                child: Text(
                                  validationError,
                                  style: TextStyle(
                                    color: Colors.red.shade900,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ),
                      // Reading cards
                      Wrap(
                        spacing: 12,
                        runSpacing: 12,
                        children: readings.map((reading) {
                      return SizedBox(
                        width: cardWidth,
                        child: ReadingListCard(
                      reading: reading,
                      onPressRead: () async {
                        String searchQuery = '';

                        // Check if this is McCheyne 5 Psalms card
                        if (reading.id == 'mcheynepsalms') {
                          final currentDay = DateTime.now().day;
                          if (currentDay != 31) {
                            // Build query for 5 psalms based on day of month
                            final psalms = [
                              currentDay,
                              currentDay + 30,
                              currentDay + 60,
                              currentDay + 90,
                              currentDay + 120,
                            ];
                            searchQuery = psalms.map((p) => 'Psalm%20$p').join(',');
                          }
                        }
                        // Check if this is a McCheyne list
                        else if (reading.id.startsWith('mcheynelist')) {
                          // McCheyne lists use direct scripture references
                          final readingText = ReadingLists.getMcheyneReading(reading.id, reading.currentChapter);
                          if (readingText.isNotEmpty) {
                            searchQuery = readingText.replaceAll(' ', '%20');
                          }
                        } else {
                          // Horner lists use book/chapter lookup
                          // For 5 Psalms mode, use current day of month instead of progress
                          final bool is5PsalmsMode = reading.id == 'list6' && reading.fivePsalmsMode;
                          final int dayToUse = is5PsalmsMode ? DateTime.now().day : reading.currentChapter;

                          final bookInfo = ReadingLists.getBookAndChapter(reading.id, dayToUse, fivePsalmsMode: reading.fivePsalmsMode);

                          // Handle 5 Psalms mode differently
                          if (is5PsalmsMode) {
                            final psalms = bookInfo['psalms'] as List<int>?;
                            if (psalms != null && psalms.isNotEmpty) {
                              // Build query for multiple psalms: "Psalm 1, Psalm 31, Psalm 61, Psalm 91, Psalm 121"
                              searchQuery = psalms.map((p) => 'Psalm%20$p').join(',');
                            }
                          } else {
                            final book = bookInfo['book'] ?? '';
                            final chapter = bookInfo['chapter'] ?? 0;

                            if (book.isNotEmpty && chapter > 0) {
                              searchQuery = '$book $chapter'.replaceAll(' ', '%20');
                            }
                          }
                        }

                        // Launch the URL if we have a search query
                        if (searchQuery.isNotEmpty) {
                          final url = Uri.parse('https://www.biblegateway.com/passage/?search=$searchQuery');
                          if (await canLaunchUrl(url)) {
                            await launchUrl(url, mode: LaunchMode.externalApplication);
                          }
                        }
                      },
                      onMarkComplete: (reading.id == 'mcheynepsalms')
                          ? () {} // Disable mark complete for McCheyne 5 Psalms
                          : () {
                              ref.read(markReadingCompleteProvider).call(reading.id);
                            },
                      onReset: (reading.id == 'list6' && reading.fivePsalmsMode) || reading.id == 'mcheynepsalms'
                          ? null // Disable reset for Horner list6 in 5 Psalms mode and McCheyne 5 Psalms
                          : () {
                              ref.read(resetListCompletionProvider).call(reading.id);
                            },
                    ),
                  );
                    }).toList(),
                  ),
                        ],
                      ),
                    );
                  },
                ),
              ),
            );
          },
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (error, stack) => Center(child: Text('Error: $error')),
        ),
        bottomNavigationBar: NavigationBar(
          selectedIndex: 0,
          onDestinationSelected: (index) {
            switch(index){
                case 0:
                  context.go('/home');
                  break;
                case 1:
                  context.go('/stats');
                  break;
                case 2:
                  context.go('/settings');
                  break;
              }
          },
          destinations: const [
            NavigationDestination(icon: Icon(Icons.home), label: 'Home'),
            NavigationDestination(icon: Icon(Icons.bar_chart), label: 'Stats'),
            NavigationDestination(icon: Icon(Icons.settings), label: 'Settings'),
          ],
        ),
      );
    }
}
