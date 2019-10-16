package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import java.util.*

class ServiceStarted  : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        log("test")
        if (intent?.action == "android.intent.action.BOOT_COMPLETED" || intent?.action == "android.intent.action.MY_PACKAGE_REPLACED") {
            log("test")
            val alarmDailyCheck: AlarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmDailyIntent: PendingIntent
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            if (calendar.before(Calendar.getInstance())) {
                log("ADDING ONE DAY(SHOULDN'T HAPPEN)")
                calendar.add(Calendar.DATE, 1)
            }
            log("Calendar item: $calendar")
            val i = Intent(context, DailyCheck::class.java)
            alarmDailyIntent = PendingIntent.getBroadcast(context, 6, i, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmDailyCheck.setInexactRepeating(AlarmManager.RTC, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmDailyIntent)

            val alarmDaily: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmDailyInt: PendingIntent
            val calendar2 = Calendar.getInstance()
            calendar2.timeInMillis = System.currentTimeMillis()
            val sharedpref = PreferenceManager.getDefaultSharedPreferences(context)
            val timeinMinutesDaily = sharedpref.getInt("daily_time", 0)
            log("time in minutes $timeinMinutesDaily")
            val hour = timeinMinutesDaily / 60
            val minute = timeinMinutesDaily % 60
            log("dailyHour - $hour")
            log("dailyMinute - $minute")
            calendar2.set(Calendar.HOUR_OF_DAY, hour)
            calendar2.set(Calendar.MINUTE, minute)
            if (calendar2.before(Calendar.getInstance())) {
                calendar2.add(Calendar.DATE, 1)
            }
            log("Calendar - $calendar2")
            val notifyIntent = Intent(context, AlarmReceiver::class.java)
            alarmDailyInt = PendingIntent.getBroadcast(context, 4, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmDaily.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmDailyInt)
            log("Created daily Alarm at $hour:$minute")

            val alarmRemind: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val remindPendingIntent: PendingIntent
            val calendar3 = Calendar.getInstance()
            val timeinMinutesRemind = sharedpref.getInt("remind_time", 0)
            log("time in minutes $timeinMinutesRemind")
            val rHour = timeinMinutesRemind / 60
            val rMinute = timeinMinutesRemind % 60
            log("remindHour - $rHour")
            log("remindMinute - $rMinute")
            calendar3.set(Calendar.HOUR_OF_DAY, rHour)
            calendar3.set(Calendar.MINUTE, rMinute)
            if (calendar3.before(Calendar.getInstance())) {
                calendar3.add(Calendar.DATE, 1)
            }
            val remindIntent = Intent(context, RemindReceiver::class.java)
            remindPendingIntent = PendingIntent.getBroadcast(context, 2, remindIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            log("Calendar - $calendar3")
            alarmRemind.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, remindPendingIntent)
            log("Created Reminder Alarm at $rHour:$rMinute")
        }
    }
}