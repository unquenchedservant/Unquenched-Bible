import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import '../providers/auth_providers.dart';
import '../providers/reading_providers.dart';
import '../providers/data_providers.dart';
import '../providers/theme_provider.dart';

class SettingsPage extends ConsumerWidget {
  const SettingsPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authStateProvider);
    final user = authState.value!; // User is guaranteed to be authenticated

    return Scaffold(
      appBar: AppBar(
        title: const Text('Settings'),
      ),
      body: ListView(
        children: [
          const ListTile(
            title: Text('Appearance'),
            subtitle: Text('Theme and Display options'),
          ),
          Consumer(
            builder: (context, ref, child) {
              final themeMode = ref.watch(themeModeProvider);
              final isDarkMode = themeMode == ThemeMode.dark;

              return SwitchListTile(
                title: const Text('Dark Mode'),
                subtitle: Text(
                  themeMode == ThemeMode.system
                    ? 'Currently following system (tap to override)'
                    : isDarkMode ? 'Dark theme enabled' : 'Light theme enabled'
                ),
                value: isDarkMode,
                onChanged: (value) async {
                  final notifier = ref.read(themeModeProvider.notifier);
                  await notifier.setThemeMode(value ? ThemeMode.dark : ThemeMode.light);
                },
              );
            },
          ),

          const Divider(),

          const ListTile(
            title: Text('Reading Plan'),
            subtitle: Text('Customize your reading plan'),
          ),

          // Manual Override link
          ListTile(
            title: const Text('Manual Override'),
            subtitle: const Text('Manually adjust your reading progress'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {
              context.go('/manual-override');
            },
          ),

          // Plan System selector
          Consumer(
              builder: (context, ref, child) {
                final readingsAsync = ref.watch(todaysReadingsProvider);

                return readingsAsync.when(
                  data: (readings) {
                    if (readings.isEmpty) return const SizedBox.shrink();

                    // Get plan system from readings
                    final repository = ref.read(readingPlanRepositoryProvider);

                    return FutureBuilder(
                      future: repository.getReadingPlan(user.id),
                      builder: (context, snapshot) {
                        String planSystem = 'horner'; // default
                        String systemDisplay = 'Horner\'s System';

                        if (snapshot.hasData) {
                          snapshot.data?.fold(
                            (failure) {},
                            (plan) {
                              planSystem = plan.planSystem;
                              systemDisplay = planSystem == 'horner'
                                ? 'Horner\'s System'
                                : 'M\'Cheyne\'s System';
                            },
                          );
                        }

                        return ListTile(
                          title: const Text('Plan System'),
                          subtitle: Text(systemDisplay),
                          trailing: const Icon(Icons.chevron_right),
                          onTap: () {
                            showDialog(
                              context: context,
                              builder: (dialogContext) => AlertDialog(
                                title: const Text('Select Plan System'),
                                content: Column(
                                  mainAxisSize: MainAxisSize.min,
                                  children: [
                                    RadioListTile<String>(
                                      title: const Text('Horner\'s System'),
                                      subtitle: const Text('10 chapters per day from different sections'),
                                      value: 'horner',
                                      groupValue: planSystem,
                                      onChanged: (value) async {
                                        if (value != null) {
                                          final result = await repository.updatePlan(user.id, value, 'standard');
                                          if (dialogContext.mounted) {
                                            Navigator.of(dialogContext).pop();
                                            result.fold(
                                              (failure) {
                                                ScaffoldMessenger.of(context).showSnackBar(
                                                  SnackBar(content: Text('Error: ${failure.message}')),
                                                );
                                              },
                                              (_) {
                                                ScaffoldMessenger.of(context).showSnackBar(
                                                  const SnackBar(content: Text('Reading plan changed to Horner\'s System')),
                                                );
                                                ref.invalidate(todaysReadingsProvider);
                                              },
                                            );
                                          }
                                        }
                                      },
                                    ),
                                    RadioListTile<String>(
                                      title: const Text('M\'Cheyne\'s System'),
                                      subtitle: const Text('4 readings per day, complete Bible in 1 year'),
                                      value: 'mcheyne',
                                      groupValue: planSystem,
                                      onChanged: (value) async {
                                        if (value != null) {
                                          final result = await repository.updatePlan(user.id, value, 'standard');
                                          if (dialogContext.mounted) {
                                            Navigator.of(dialogContext).pop();
                                            result.fold(
                                              (failure) {
                                                ScaffoldMessenger.of(context).showSnackBar(
                                                  SnackBar(content: Text('Error: ${failure.message}')),
                                                );
                                              },
                                              (_) {
                                                ScaffoldMessenger.of(context).showSnackBar(
                                                  const SnackBar(content: Text('Reading plan changed to M\'Cheyne\'s System')),
                                                );
                                                ref.invalidate(todaysReadingsProvider);
                                              },
                                            );
                                          }
                                        }
                                      },
                                    ),
                                  ],
                                ),
                              ),
                            );
                          },
                        );
                      },
                    );
                  },
                  loading: () => const SizedBox.shrink(),
                  error: (_, __) => const SizedBox.shrink(),
                );
              },
            ),

          // Plan Type (Reading Mode) selector
          Consumer(
            builder: (context, ref, child) {
                final readingsAsync = ref.watch(todaysReadingsProvider);

                return readingsAsync.when(
                  data: (readings) {
                    if (readings.isEmpty) return const SizedBox.shrink();

                    // Determine if this is M'Cheyne or Horner
                    final isMcheyne = readings.first.id.startsWith('mcheynelist');
                    final planName = isMcheyne ? 'M\'Cheyne' : 'Horner';

                    final readingMode = readings.first.readingMode;
                    String modeDisplay;
                    switch (readingMode) {
                      case 'index':
                        modeDisplay = 'Index Mode';
                        break;
                      case 'calendar':
                        modeDisplay = 'Calendar Mode';
                        break;
                      default:
                        modeDisplay = 'Standard Mode';
                    }

                    return ListTile(
                      title: Text('Plan Type ($planName)'),
                      subtitle: Text(modeDisplay),
                      trailing: const Icon(Icons.chevron_right),
                      onTap: () {
                        showDialog(
                          context: context,
                          builder: (dialogContext) => AlertDialog(
                            title: Text('Select Plan Type ($planName)'),
                            content: Column(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                RadioListTile<String>(
                                  title: const Text('Standard'),
                                  subtitle: const Text('Lists increment independently'),
                                  value: 'standard',
                                  groupValue: readingMode,
                                  onChanged: (value) async {
                                    if (value != null) {
                                      final repository = ref.read(readingPlanRepositoryProvider);
                                      if (isMcheyne) {
                                        await repository.setMcheyneReadingMode(user.id, value);
                                      } else {
                                        await repository.setReadingMode(user.id, value);
                                      }
                                      ref.invalidate(todaysReadingsProvider);
                                      Navigator.of(dialogContext).pop();
                                    }
                                  },
                                ),
                                RadioListTile<String>(
                                  title: const Text('Index'),
                                  subtitle: const Text('All lists must be completed to increment'),
                                  value: 'index',
                                  groupValue: readingMode,
                                  onChanged: (value) async {
                                    if (value != null) {
                                      final repository = ref.read(readingPlanRepositoryProvider);
                                      if (isMcheyne) {
                                        await repository.setMcheyneReadingMode(user.id, value);
                                      } else {
                                        await repository.setReadingMode(user.id, value);
                                      }
                                      ref.invalidate(todaysReadingsProvider);
                                      Navigator.of(dialogContext).pop();
                                    }
                                  },
                                ),
                                RadioListTile<String>(
                                  title: const Text('Calendar'),
                                  subtitle: const Text('Everyone reads based on day of year (1-365)'),
                                  value: 'calendar',
                                  groupValue: readingMode,
                                  onChanged: (value) async {
                                    if (value != null) {
                                      final repository = ref.read(readingPlanRepositoryProvider);
                                      if (isMcheyne) {
                                        await repository.setMcheyneReadingMode(user.id, value);
                                      } else {
                                        await repository.setReadingMode(user.id, value);
                                      }
                                      ref.invalidate(todaysReadingsProvider);
                                      Navigator.of(dialogContext).pop();
                                    }
                                  },
                                ),
                              ],
                            ),
                          ),
                        );
                      },
                    );
                  },
                  loading: () => const SizedBox.shrink(),
                  error: (_, __) => const SizedBox.shrink(),
                );
              },
            ),

          // 5 Psalms A Day toggle - only show for Horner plan
          Consumer(
            builder: (context, ref, child) {
                final readingsAsync = ref.watch(todaysReadingsProvider);

                return readingsAsync.when(
                  data: (readings) {
                    // Check if we're on Horner plan by checking if we have the readings
                    if (readings.isEmpty || !readings.any((r) => r.id == 'list6')) {
                      return const SizedBox.shrink();
                    }

                    final list6 = readings.firstWhere((r) => r.id == 'list6');
                    final isFivePsalmsMode = list6.fivePsalmsMode;

                    return SwitchListTile(
                      title: const Text('5 Psalms A Day (Horner)'),
                      subtitle: const Text('Read 5 Psalms per day based on day of month (Day 31 is Day Off)'),
                      value: isFivePsalmsMode,
                      onChanged: (value) async {
                        final repository = ref.read(readingPlanRepositoryProvider);
                        final result = await repository.toggleFivePsalmsMode(user.id, value);

                        if (context.mounted) {
                          result.fold(
                            (failure) {
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(content: Text('Error: ${failure.message}')),
                              );
                            },
                            (_) {
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(content: Text(value ? '5 Psalms A Day enabled' : '5 Psalms A Day disabled')),
                              );
                              // Trigger rebuild
                              ref.invalidate(todaysReadingsProvider);
                            },
                          );
                        }
                      },
                    );
                  },
                  loading: () => const SizedBox.shrink(),
                  error: (_, __) => const SizedBox.shrink(),
                );
              },
            ),

          // MCheyne 5 Psalms A Day toggle - only show for M'Cheyne plan
          Consumer(
            builder: (context, ref, child) {
                final readingsAsync = ref.watch(todaysReadingsProvider);

                return readingsAsync.when(
                  data: (readings) {
                    // Check if we're on M'Cheyne plan
                    if (readings.isEmpty || !readings.any((r) => r.id.startsWith('mcheynelist'))) {
                      return const SizedBox.shrink();
                    }

                    final isFivePsalmsMode = readings.first.fivePsalmsMode;

                    return SwitchListTile(
                      title: const Text('5 Psalms A Day (M\'Cheyne)'),
                      subtitle: const Text('Read 5 Psalms per day based on day of month (Day 31 is Day Off)'),
                      value: isFivePsalmsMode,
                      onChanged: (value) async {
                        final repository = ref.read(readingPlanRepositoryProvider);
                        final result = await repository.toggleMcheyneFivePsalmsMode(user.id, value);

                        if (context.mounted) {
                          result.fold(
                            (failure) {
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(content: Text('Error: ${failure.message}')),
                              );
                            },
                            (_) {
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(content: Text(value ? 'M\'Cheyne 5 Psalms A Day enabled' : 'M\'Cheyne 5 Psalms A Day disabled')),
                              );
                              ref.invalidate(todaysReadingsProvider);
                            },
                          );
                        }
                      },
                    );
                  },
                  loading: () => const SizedBox.shrink(),
                  error: (_, __) => const SizedBox.shrink(),
                );
              },
            ),

          // Require All Lists for Streak toggle
          Consumer(
            builder: (context, ref, child) {
                final readingsAsync = ref.watch(todaysReadingsProvider);

                return readingsAsync.when(
                  data: (readings) {
                    if (readings.isEmpty) {
                      return const SizedBox.shrink();
                    }

                    // Determine if this is M'Cheyne or Horner
                    final isMcheyne = readings.first.id.startsWith('mcheynelist');
                    final listCount = isMcheyne ? 4 : 10;
                    final planName = isMcheyne ? 'M\'Cheyne' : 'Horner';

                    // Get the setting from any list (they all share the same plan)
                    final requireAllListsForStreak = readings.first.requireAllListsForStreak;

                    return SwitchListTile(
                      title: Text('Complete All Lists for Streak ($planName)'),
                      subtitle: Text('Streak increases only when all $listCount lists are completed in a day (off = any list counts)'),
                      value: requireAllListsForStreak,
                      onChanged: (value) async {
                        final repository = ref.read(readingPlanRepositoryProvider);
                        final result = isMcheyne
                          ? await repository.toggleMcheyneRequireAllListsForStreak(user.id, value)
                          : await repository.toggleRequireAllListsForStreak(user.id, value);

                        if (context.mounted) {
                          result.fold(
                            (failure) {
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(content: Text('Error: ${failure.message}')),
                              );
                            },
                            (_) {
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(content: Text(value ? 'Require all lists for streak enabled' : 'Require all lists for streak disabled')),
                              );
                              // Trigger rebuild
                              ref.invalidate(todaysReadingsProvider);
                            },
                          );
                        }
                      },
                    );
                  },
                  loading: () => const SizedBox.shrink(),
                  error: (_, __) => const SizedBox.shrink(),
                );
              },
            ),

          const Divider(),

          ListTile(
              title: const Text('Account'),
              subtitle: Text(user.email ?? 'No email'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () {
                context.go('/account-settings');
              },
            ),

            ListTile(
              title: const Text('Privacy Policy'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () async {
                // Construct absolute URL for privacy policy
                final uri = Uri.base;
                final privacyPolicyUri = Uri(
                  scheme: uri.scheme,
                  host: uri.host,
                  port: uri.port,
                  path: '/privacy_policy.html',
                );
                try {
                  await launchUrl(privacyPolicyUri, mode: LaunchMode.inAppBrowserView);
                } catch (e) {
                  if (context.mounted) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('Could not open Privacy Policy: $e')),
                    );
                  }
                }
              },
            ),

            const Divider(),

            ListTile(
              title: const Text('Bible Gateway Dark Mode'),
              subtitle: const Text('Browser extension by the same developer'),
              leading: const Icon(Icons.dark_mode),
              trailing: const Icon(Icons.chevron_right),
              onTap: () {
                showDialog(
                  context: context,
                  builder: (context) => AlertDialog(
                    title: const Text('Bible Gateway Dark Mode'),
                    content: Column(
                      mainAxisSize: MainAxisSize.min,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text('Available for:'),
                        const SizedBox(height: 16),
                        ListTile(
                          leading: const Icon(Icons.web),
                          title: const Text('Firefox'),
                          onTap: () async {
                            final uri = Uri.parse('https://addons.mozilla.org/en-US/firefox/addon/bible-gateway-darkmode/?utm_source=addons.mozilla.org&utm_medium=referral&utm_content=search');
                            try {
                              await launchUrl(uri, mode: LaunchMode.externalApplication);
                              if (context.mounted) {
                                Navigator.of(context).pop();
                              }
                            } catch (e) {
                              if (context.mounted) {
                                ScaffoldMessenger.of(context).showSnackBar(
                                  SnackBar(content: Text('Could not open link: $e')),
                                );
                              }
                            }
                          },
                        ),
                        ListTile(
                          leading: const Icon(Icons.web),
                          title: const Text('Chrome/Chromium'),
                          onTap: () async {
                            final uri = Uri.parse('https://chromewebstore.google.com/detail/bible-gateway-darkmode/fkmibejcfnaoglanjfceecmljgkjaici?pli=1');
                            try {
                              await launchUrl(uri, mode: LaunchMode.externalApplication);
                              if (context.mounted) {
                                Navigator.of(context).pop();
                              }
                            } catch (e) {
                              if (context.mounted) {
                                ScaffoldMessenger.of(context).showSnackBar(
                                  SnackBar(content: Text('Could not open link: $e')),
                                );
                              }
                            }
                          },
                        ),
                      ],
                    ),
                    actions: [
                      TextButton(
                        onPressed: () => Navigator.of(context).pop(),
                        child: const Text('Close'),
                      ),
                    ],
                  ),
                );
              },
            ),

            const Divider(),

            ListTile(
              title: const Text('Sign Out'),
              textColor: Colors.red,
              leading: const Icon(Icons.logout, color: Colors.red),
              onTap: () async {
                // Show confirmation dialog
                final shouldSignOut = await showDialog<bool>(
                  context: context,
                  builder: (context) => AlertDialog(
                    title: const Text('Sign Out'),
                    content: const Text('Are you sure you want to sign out?'),
                    actions: [
                      TextButton(
                        onPressed: () => Navigator.of(context).pop(false),
                        child: const Text('Cancel'),
                      ),
                      TextButton(
                        onPressed: () => Navigator.of(context).pop(true),
                        child: const Text('Sign Out', style: TextStyle(color: Colors.red)),
                      ),
                    ],
                  ),
                );

                if (shouldSignOut == true && context.mounted) {
                  final signOut = ref.read(signOutProvider);
                  await signOut();

                  if (context.mounted) {
                    context.go('/login');
                  }
                }
              },
            ),

            // Community icons at the bottom
            Padding(
              padding: const EdgeInsets.only(right: 8.0, top: 8.0, bottom: 8.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  IconButton(
                    icon: const FaIcon(FontAwesomeIcons.github),
                    iconSize: 28,
                    tooltip: 'GitHub',
                    onPressed: () async {
                      final Uri githubUri = Uri.parse('https://github.com/unquenchedservant/Unquenched-Bible');
                      try {
                        await launchUrl(githubUri, mode: LaunchMode.externalApplication);
                      } catch (e) {
                        if (context.mounted) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text('Could not open GitHub: $e')),
                          );
                        }
                      }
                    },
                  ),
                  const SizedBox(width: 8),
                  IconButton(
                    icon: const FaIcon(FontAwesomeIcons.discord),
                    iconSize: 28,
                    tooltip: 'Discord',
                    onPressed: () async {
                      final Uri discordUri = Uri.parse('https://discord.gg/J4JPjkB7S8');
                      try {
                        await launchUrl(discordUri, mode: LaunchMode.externalApplication);
                      } catch (e) {
                        if (context.mounted) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text('Could not open Discord: $e')),
                          );
                        }
                      }
                    },
                  ),
                  const SizedBox(width: 8),
                  IconButton(
                    icon: const FaIcon(FontAwesomeIcons.mugHot),
                    iconSize: 28,
                    tooltip: 'Ko-fi',
                    onPressed: () async {
                      final Uri kofiUri = Uri.parse('https://ko-fi.com/chillhumanoid');
                      try {
                        await launchUrl(kofiUri, mode: LaunchMode.externalApplication);
                      } catch (e) {
                        if (context.mounted) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text('Could not open Ko-fi: $e')),
                          );
                        }
                      }
                    },
                  ),
                ],
              ),
            ),
        ],
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: 2,
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
