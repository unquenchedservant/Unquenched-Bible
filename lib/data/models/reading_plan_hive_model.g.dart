// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'reading_plan_hive_model.dart';

// **************************************************************************
// TypeAdapterGenerator
// **************************************************************************

class ReadingPlanHiveModelAdapter extends TypeAdapter<ReadingPlanHiveModel> {
  @override
  final int typeId = 0;

  @override
  ReadingPlanHiveModel read(BinaryReader reader) {
    final numOfFields = reader.readByte();
    final fields = <int, dynamic>{
      for (int i = 0; i < numOfFields; i++) reader.readByte(): reader.read(),
    };
    return ReadingPlanHiveModel(
      userId: fields[0] as String,
      planSystem: fields[1] as String,
      planType: fields[2] as String,
      readingMode: fields[12] as String,
      progress: (fields[3] as Map).cast<String, int>(),
      indexProgress: fields[13] as int,
      lastUpdated: fields[4] as DateTime,
      completedToday: (fields[5] as Map).cast<String, DateTime>(),
      listCompletionStatus: (fields[9] as Map).cast<String, bool>(),
      currentStreak: fields[6] as int,
      longestStreak: fields[7] as int,
      lastCompletionDate: fields[8] as DateTime?,
      lastResetDate: fields[21] as DateTime?,
      fivePsalmsMode: fields[10] as bool,
      requireAllListsForStreak: fields[11] as bool,
      mcheyneProgress: (fields[14] as Map).cast<String, int>(),
      mcheyneIndexProgress: fields[15] as int,
      mcheyneReadingMode: fields[16] as String,
      mcheyneCompletedToday: (fields[17] as Map).cast<String, DateTime>(),
      mcheyneListCompletionStatus: (fields[18] as Map).cast<String, bool>(),
      mcheyneFivePsalmsMode: fields[19] as bool,
      mcheyneRequireAllListsForStreak: fields[20] as bool,
    );
  }

  @override
  void write(BinaryWriter writer, ReadingPlanHiveModel obj) {
    writer
      ..writeByte(22)
      ..writeByte(0)
      ..write(obj.userId)
      ..writeByte(1)
      ..write(obj.planSystem)
      ..writeByte(2)
      ..write(obj.planType)
      ..writeByte(3)
      ..write(obj.progress)
      ..writeByte(4)
      ..write(obj.lastUpdated)
      ..writeByte(5)
      ..write(obj.completedToday)
      ..writeByte(6)
      ..write(obj.currentStreak)
      ..writeByte(7)
      ..write(obj.longestStreak)
      ..writeByte(8)
      ..write(obj.lastCompletionDate)
      ..writeByte(9)
      ..write(obj.listCompletionStatus)
      ..writeByte(10)
      ..write(obj.fivePsalmsMode)
      ..writeByte(11)
      ..write(obj.requireAllListsForStreak)
      ..writeByte(12)
      ..write(obj.readingMode)
      ..writeByte(13)
      ..write(obj.indexProgress)
      ..writeByte(14)
      ..write(obj.mcheyneProgress)
      ..writeByte(15)
      ..write(obj.mcheyneIndexProgress)
      ..writeByte(16)
      ..write(obj.mcheyneReadingMode)
      ..writeByte(17)
      ..write(obj.mcheyneCompletedToday)
      ..writeByte(18)
      ..write(obj.mcheyneListCompletionStatus)
      ..writeByte(19)
      ..write(obj.mcheyneFivePsalmsMode)
      ..writeByte(20)
      ..write(obj.mcheyneRequireAllListsForStreak)
      ..writeByte(21)
      ..write(obj.lastResetDate);
  }

  @override
  int get hashCode => typeId.hashCode;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is ReadingPlanHiveModelAdapter &&
          runtimeType == other.runtimeType &&
          typeId == other.typeId;
}
