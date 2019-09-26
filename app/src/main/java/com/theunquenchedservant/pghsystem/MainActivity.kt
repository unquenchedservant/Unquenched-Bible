package com.theunquenchedservant.pghsystem

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.theunquenchedservant.pghsystem.ui.notifications.dailyCheck

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.theunquenchedservant.pghsystem.sharedPref.statisticsEdit
import com.theunquenchedservant.pghsystem.sharedPref.statisticsRead
import com.theunquenchedservant.pghsystem.ui.notifications.AlarmReceiver
import com.theunquenchedservant.pghsystem.ui.notifications.remindReceiver

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val _primaryChannelId = "primary_notification_channel"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when(statisticsRead(this, "firstRun")){
            0 -> {
                statisticsEdit(this, "firstRun", 1)
            }
            1->{
                val notifyIntent = Intent(this, AlarmReceiver::class.java)
                val remindIntent = Intent(this, remindReceiver::class.java)
                val notifyPendingIntent = PendingIntent.getBroadcast(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                val remindPendingIntent = PendingIntent.getBroadcast(this, 0, remindIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                createAlarm(this,notifyPendingIntent)
                createRemindAlarm(this, remindPendingIntent)

            }
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        log("Dark Mode Enabled")
        super.
        setContentView(R.layout.activity_main)
        log("activity int - ${R.layout.activity_main}")
        log("Main Activity Content View Enabled")
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        val appBarConfiguration = AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_settings)
                .build()
        log("Created App Bar Config")
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)
        createNotificationChannel()
        log("Finished Main.onCreate")
    }



    private fun createNotificationChannel() {
        log( "start createNotificationChannel")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = this.getString(R.string.channel_name)
            log("Name of channel: $name")
            val description = this.getString(R.string.channel_description)
            log("Description of Channel: $description")
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(_primaryChannelId, name, importance)
            channel.description = description
            channel.enableVibration(true)
            val notificationManager = this.getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
            log("Notification channel created")
        }
    }
    companion object{
        fun log(logString:String){
            Log.d("PROFGRANT", logString)
        }
        fun createDailyCheck(context: Context?) {
            log("Begin createDailyCheck")
            val alarmMgr: AlarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent: PendingIntent
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            if (calendar.before(Calendar.getInstance())) {
                log("ADDING ONE DAY(SHOULDNT HAPPEN)")
                calendar.add(Calendar.DATE, 1)
            }
            log("Calendar item: $calendar")
            val intent = Intent(context, dailyCheck::class.java)
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)
            log("dailyCheck Alarm created/updated")
        }
        fun getCurrentDate(fullMonth: Boolean): String {
            log("Begin getCurrentDate")
            val today = Calendar.getInstance().time
            val formatter: SimpleDateFormat
            if (fullMonth) {
                formatter = SimpleDateFormat("MMMM dd", Locale.US)
            } else {
                formatter = SimpleDateFormat("MMM dd", Locale.US)
            }
            log("getCurrentDate returning ${formatter.format(today)}")
            return formatter.format(today)
        }


        fun cancelAlarm(context: Context, notifyPendingIntent: PendingIntent) {
            log("Start cancelAlarm")
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            log("Cancelling $notifyPendingIntent")
            alarmManager.cancel(notifyPendingIntent)
        }

        fun createRemindAlarm(context: Context?, notifyPendingIntent: PendingIntent) {
            log("Start createRemindAlarm")
            val sharedpref = PreferenceManager.getDefaultSharedPreferences(context)
            val timeinMinutes = sharedpref.getInt("remind_time", 0)
            log("time in minutes $timeinMinutes")
            val rHour = timeinMinutes / 60
            val rMinute = timeinMinutes % 60
            log("remindHour - $rHour")
            log("remindMinute - $rMinute")
            val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, rHour)
            calendar.set(Calendar.MINUTE, rMinute)
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1)
            }
            log("Calendar - $calendar")
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, notifyPendingIntent)
            log("Created Reminder Alarm at $rHour:$rMinute")
        }

        fun createAlarm(context: Context?, notifyPendingIntent: PendingIntent) {
            log("Start createAlarm (dailyAlarm)")
            val sharedpref = PreferenceManager.getDefaultSharedPreferences(context)
            val timeinMinutes = sharedpref.getInt("daily_time", 0)
            log("time in minutes $timeinMinutes")
            val hour = timeinMinutes / 60
            val minute = timeinMinutes % 60
            log("dailyHour - $hour")
            log("dailyMinute - $minute")
            val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1)
            }
            log("Calendar - $calendar")
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, notifyPendingIntent)
            log("Created daily Alarm at $hour:$minute")

        }


    }
}