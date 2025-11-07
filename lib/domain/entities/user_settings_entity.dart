import 'package:equatable/equatable.dart';

class UserSettingsEntity extends Equatable {
  final String userId;
  final bool notificationsEnabled;
  final String notificationTime;
  final bool darkMode;
  final DateTime lastUpdated;

  const UserSettingsEntity({
    required this.userId,
    required this.notificationsEnabled,
    required this.notificationTime,
    required this.darkMode,
    required this.lastUpdated,
  });

  @override
  List<Object> get props => [userId, notificationsEnabled, notificationTime, darkMode, lastUpdated];
}
