import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:url_launcher/url_launcher.dart';
import '../providers/auth_providers.dart';
import '../providers/data_providers.dart';

class PlanSelectionPage extends ConsumerWidget {
  const PlanSelectionPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authStateProvider);
    final user = authState.value;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Choose Reading Plan'),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _buildPlanCard(
            context,
            ref,
            user?.id,
            'Grant Horner\'s System',
            '10 chapters per day from different sections',
            'horner',
            'standard',
            'https://sohmer.net/media/professor_grant_horners_bible_reading_system.pdf',
          ),
          _buildPlanCard(
            context,
            ref,
            user?.id,
            'M\'cheyne\'s System',
            '4 readings per day, complete Bible in 1 year',
            'mcheyne',
            'standard',
            'https://bibleplan.org/plans/mcheyne/',
          ),
        ],
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

  Widget _buildPlanCard(
    BuildContext context,
    WidgetRef ref,
    String? userId,
    String title,
    String description,
    String planSystem,
    String planType,
    String moreInfoUrl,
  ) {
    return Card(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          ListTile(
            title: Text(title),
            subtitle: Text(description),
            onTap: () async {
              if (userId == null) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Please sign in to change your reading plan')),
                );
                return;
              }

              // Update plan in Hive and Firestore
              final repository = ref.read(readingPlanRepositoryProvider);
              final result = await repository.updatePlan(userId, planSystem, planType);

              if (context.mounted) {
                result.fold(
                  (failure) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('Error updating plan: ${failure.message}')),
                    );
                  },
                  (_) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('Reading plan changed to $title')),
                    );
                    context.go('/settings');
                  },
                );
              }
            },
          ),
          Padding(
            padding: const EdgeInsets.only(left: 16, right: 16, bottom: 8),
            child: Align(
              alignment: Alignment.centerLeft,
              child: InkWell(
                onTap: () async {
                  final uri = Uri.parse(moreInfoUrl);
                  try {
                    await launchUrl(uri, mode: LaunchMode.externalApplication);
                  } catch (e) {
                    if (context.mounted) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text('Could not open link: $e')),
                      );
                    }
                  }
                },
                child: const Text(
                  'More Info',
                  style: TextStyle(
                    color: Colors.blue,
                    decoration: TextDecoration.underline,
                    fontSize: 12,
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
