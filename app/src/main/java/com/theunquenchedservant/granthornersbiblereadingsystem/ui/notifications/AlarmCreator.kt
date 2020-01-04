package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import java.util.*

object AlarmCreator {
    private val dailyIntent = Intent(App.applicationContext(), AlarmReceiver::class.java)
    private val remindIntent = Intent(App.applicationContext(), RemindReceiver::class.java)
    private val checkIntent = Intent(App.applicationContext(), DailyCheck::class.java)
    private val dailyPending = PendingIntent.getBroadcast(App.applicationContext(), 4, dailyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    private val remindPending = PendingIntent.getBroadcast(App.applicationContext(), 2, remindIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    private val checkPending = PendingIntent.getBroadcast(App.applicationContext(), 6, checkIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    fun cancelAlarm(alarmType: String) {
        val notifyPendingIntent = when(alarmType){
            "daily" -> dailyPending
            "remind" -> remindPending
            else -> checkPending
        }
        val ctx = App.applicationContext()
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(notifyPendingIntent)
    }

    fun createNotificationChannel() {
        val primaryChannelId = "primary_notification_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = App.applicationContext().resources.getString(R.string.channel_name)
            val description = App.applicationContext().getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(primaryChannelId, name, importance)
            channel.description = description
            channel.enableVibration(true)
            val notificationManager = App.applicationContext().getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createAlarm(alarmType: String) {
        val ctx = App.applicationContext()
        val notifPendingIntent : PendingIntent
        val timeInMinutes : Int
        when(alarmType) {
            "daily" -> {
                notifPendingIntent = dailyPending
                timeInMinutes = getIntPref("${alarmType}_time")
            }
            "remind" -> {
                notifPendingIntent = remindPending
                timeInMinutes = getIntPref("${alarmType}_time")
            }
            else -> {
                notifPendingIntent = checkPending
                timeInMinutes = 0
            }
        }
        val hour: Int
        val minute: Int
        if(timeInMinutes == 0){
            hour = 0
            minute = 0
        }else {
            hour = timeInMinutes / 60
            minute = timeInMinutes % 60
        }
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        if (calendar.before(Calendar.getInstance())) {
            if((alarmType == "daily" && getIntPref("listsDone") == 10) || alarmType != "daily") {
                calendar.add(Calendar.DATE, 1)
            }
        }
        if(alarmType == "dailyCheck"){
            alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, notifPendingIntent)
        }else{
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, notifPendingIntent)
        }
    }

    fun createAlarms() {
        val ctx = App.applicationContext()
        val notifyIntent = Intent(ctx, AlarmReceiver::class.java)
        val notifyUp = (PendingIntent.getBroadcast(ctx, 4, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null)

        if (!notifyUp) {
            createAlarm("daily")
        }

        val remindIntent = Intent(ctx, RemindReceiver::class.java)
        val remindUp = (PendingIntent.getBroadcast(ctx, 2, remindIntent, PendingIntent.FLAG_NO_CREATE) != null)

        if (!remindUp) {
            createAlarm("remind")
        }
    }
}