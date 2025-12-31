import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../core/constants/reading_lists.dart';
import '../providers/auth_providers.dart';
import '../providers/reading_providers.dart';
import '../providers/data_providers.dart';

class ManualOverridePage extends ConsumerStatefulWidget {
  const ManualOverridePage({super.key});

  @override
  ConsumerState<ManualOverridePage> createState() => _ManualOverridePageState();
}

class _ManualOverridePageState extends ConsumerState<ManualOverridePage> {
  final TextEditingController _indexController = TextEditingController();
  bool _hasChanges = false;
  bool _indexControllerInitialized = false;

  @override
  void dispose() {
    _indexController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final authState = ref.watch(authStateProvider);
    final user = authState.value;

    if (user == null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Manual Override')),
        body: const Center(child: Text('Please log in')),
        bottomNavigationBar: BottomAppBar(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: ElevatedButton.icon(
              onPressed: () => context.go('/settings'),
              icon: const Icon(Icons.arrow_back),
              label: const Text('Back to Settings'),
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 12),
              ),
            ),
          ),
        ),
      );
    }

    final readingsAsync = ref.watch(todaysReadingsProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Manual Override'),
        actions: [
          if (_hasChanges)
            TextButton(
              onPressed: () {
                // Refresh to discard changes
                setState(() {
                  _hasChanges = false;
                  _indexControllerInitialized = false;
                });
                ref.invalidate(todaysReadingsProvider);
              },
              child: const Text('Cancel'),
            ),
        ],
      ),
      body: readingsAsync.when(
        data: (result) {
        final readings = result.readings;
          if (readings.isEmpty) {
            return const Center(child: Text('No reading plan set'));
          }

          // Determine plan type and system
          final isMcheyne = readings.first.id.startsWith('mcheynelist');
          final readingMode = readings.first.readingMode;
          final planName = isMcheyne ? 'M\'Cheyne' : 'Horner';

          // Calendar mode - don't show manual override
          if (readingMode == 'calendar') {
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(24.0),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.calendar_today, size: 64, color: Theme.of(context).disabledColor),
                    const SizedBox(height: 16),
                    Text(
                      'Manual Override Not Available',
                      style: Theme.of(context).textTheme.titleLarge,
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'Calendar mode automatically sets readings based on the day of the year. '
                      'Change to Standard or Index mode in Settings to use manual override.',
                      style: Theme.of(context).textTheme.bodyMedium,
                      textAlign: TextAlign.center,
                    ),
                  ],
                ),
              ),
            );
          }

          return ListView(
            padding: const EdgeInsets.all(16),
            children: [
              // Info card
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Icon(Icons.info_outline, color: Theme.of(context).colorScheme.primary),
                          const SizedBox(width: 8),
                          Text(
                            'Manual Override',
                            style: Theme.of(context).textTheme.titleMedium?.copyWith(
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 8),
                      Text(
                        readingMode == 'index'
                            ? 'In Index mode, all lists share the same progress. Set the index number below.'
                            : 'In Standard mode, each list has its own progress. Set each list\'s reading below.',
                        style: Theme.of(context).textTheme.bodyMedium,
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'Plan: $planName ${readingMode == 'index' ? 'Index' : 'Standard'} Mode',
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),

              if (readingMode == 'index') ...[
                // Index mode - single text field
                Text(
                  'Set Index Progress',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 8),
                Builder(
                  builder: (context) {
                    // Initialize controller only once
                    if (!_indexControllerInitialized) {
                      _indexController.text = readings.first.indexProgress.toString();
                      _indexControllerInitialized = true;
                    }
                    return TextField(
                      controller: _indexController,
                      decoration: InputDecoration(
                        labelText: 'Index Number',
                        hintText: 'Enter reading index (1 or higher)',
                        border: const OutlineInputBorder(),
                        suffixIcon: IconButton(
                          icon: const Icon(Icons.save),
                          onPressed: () async {
                            final newIndex = int.tryParse(_indexController.text);
                            if (newIndex != null && newIndex > 0) {
                              final repository = ref.read(readingPlanRepositoryProvider);

                              if (isMcheyne) {
                                // Update McCheyne index progress
                                await repository.setMcheyneIndexProgress(user.id, newIndex);
                              } else {
                                // Update Horner index progress
                                await repository.setIndexProgress(user.id, newIndex);
                              }

                              ref.invalidate(todaysReadingsProvider);

                              if (context.mounted) {
                                ScaffoldMessenger.of(context).showSnackBar(
                                  SnackBar(content: Text('Index set to $newIndex')),
                                );
                              }

                              setState(() {
                                _hasChanges = false;
                                _indexControllerInitialized = false;
                              });
                            } else {
                              ScaffoldMessenger.of(context).showSnackBar(
                                const SnackBar(content: Text('Please enter a valid number (1 or higher)')),
                              );
                            }
                          },
                        ),
                      ),
                      keyboardType: TextInputType.number,
                      inputFormatters: [
                        FilteringTextInputFormatter.digitsOnly,
                      ],
                      onChanged: (value) {
                        setState(() {
                          _hasChanges = true;
                        });
                      },
                    );
                  },
                ),
                const SizedBox(height: 8),
                Text(
                  'Current index: ${readings.first.indexProgress}',
                  style: Theme.of(context).textTheme.bodySmall,
                ),
              ] else ...[
                // Standard mode - dropdown for each list
                Text(
                  'Set List Progress',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 8),
                // Filter out the McCheyne 5 Psalms card if it exists
                ...readings.where((r) => r.id != 'mcheynepsalms').map((reading) {
                  // Get available options for this list
                  List<int> options = [];
                  if (isMcheyne) {
                    // McCheyne lists have numbered readings
                    final totalReadings = reading.totalChapters;
                    options = List.generate(totalReadings, (i) => i + 1);
                  } else {
                    // Horner lists - generate options up to total chapters
                    options = List.generate(reading.totalChapters, (i) => i + 1);
                  }

                  return Card(
                    margin: const EdgeInsets.only(bottom: 12),
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            reading.name,
                            style: Theme.of(context).textTheme.titleSmall?.copyWith(
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const SizedBox(height: 8),
                          Row(
                            children: [
                              Expanded(
                                child: DropdownButtonFormField<int>(
                                  initialValue: reading.currentChapter <= options.length
                                    ? reading.currentChapter
                                    : options.last,
                                  decoration: InputDecoration(
                                    labelText: isMcheyne ? 'Reading #' : 'Chapter',
                                    border: const OutlineInputBorder(),
                                  ),
                                  items: options.map((chapter) {
                                    String displayText;
                                    if (isMcheyne) {
                                      // Show the scripture reference for McCheyne
                                      final scriptureRef = ReadingLists.getMcheyneReading(reading.id, chapter);
                                      displayText = '$chapter: $scriptureRef';
                                    } else {
                                      // Show book and chapter for Horner
                                      final bookInfo = ReadingLists.getBookAndChapter(reading.id, chapter);
                                      final book = bookInfo['book'] ?? '';
                                      final chapterNum = bookInfo['chapter'] ?? 0;
                                      displayText = '$chapter: $book $chapterNum';
                                    }
                                    return DropdownMenuItem<int>(
                                      value: chapter,
                                      child: Text(displayText, overflow: TextOverflow.ellipsis),
                                    );
                                  }).toList(),
                                  onChanged: (newValue) async {
                                    if (newValue != null) {
                                      final repository = ref.read(readingPlanRepositoryProvider);
                                      await repository.updateProgress(
                                        user.id,
                                        reading.id,
                                        newValue,
                                      );
                                      ref.invalidate(todaysReadingsProvider);

                                      if (context.mounted) {
                                        ScaffoldMessenger.of(context).showSnackBar(
                                          SnackBar(content: Text('${reading.name} updated to reading $newValue')),
                                        );
                                      }
                                    }
                                  },
                                  isExpanded: true,
                                  menuMaxHeight: 400,
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 4),
                          Text(
                            'Current: ${reading.currentChapter}/${reading.totalChapters}',
                            style: Theme.of(context).textTheme.bodySmall,
                          ),
                        ],
                      ),
                    ),
                  );
                }),
              ],
            ],
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (error, stack) => Center(child: Text('Error: $error')),
      ),
      bottomNavigationBar: BottomAppBar(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: ElevatedButton.icon(
              onPressed: () => context.go('/settings'),
              icon: const Icon(Icons.arrow_back),
              label: const Text('Back to Settings'),
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 12),
              ),
            ),
          ),
        ),
    );
  }
}
