import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
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
          ),
          _buildPlanCard(
            context,
            ref,
            user?.id,
            'M\'cheyne\'s System',
            '4 readings per day, complete Bible in 1 year',
            'mcheyne',
            'standard',
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
  ) {
    return Card(
      child: ListTile(
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
    );
  }
}
