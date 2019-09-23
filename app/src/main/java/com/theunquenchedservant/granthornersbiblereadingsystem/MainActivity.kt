package com.theunquenchedservant.granthornersbiblereadingsystem

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.dailyCheck

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(R.layout.activity_main)
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        val appBarConfiguration = AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_settings)
                .build()
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController!!, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController!!)
        createNotificationChannel()
        createDailyCheck(this)
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = this.getString(R.string.channel_name)
            val description = this.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(PRIMARY_CHANNEL_ID, name, importance)
            channel.description = description
            channel.enableVibration(true)
            val notificationManager = this.getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
        }
    }
    companion object{
        val navController = MainActivity::class.objectInstance

        fun getCurrentDate(fullMonth: Boolean): String {
            val today = Calendar.getInstance().time
            val formatter: SimpleDateFormat
            if (fullMonth) {
                formatter = SimpleDateFormat("MMMM dd", Locale.US)
            } else {
                formatter = SimpleDateFormat("MMM dd", Locale.US)
            }
            return formatter.format(today)
        }
        fun getYesterdayDate(fullMonth: Boolean): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -1)
            val today = calendar.time
            val formatter: SimpleDateFormat
            if (fullMonth) {
                formatter = SimpleDateFormat("MMMM dd", Locale.US)
            } else {
                formatter = SimpleDateFormat("MMM dd", Locale.US)
            }
            return formatter.format(today)
        }

        fun markRead(context: Context?, chapterName:String){
            val beenRead = prefReadInt(context, chapterName)
            when(beenRead){
                0->{
                    prefEditInt(context, chapterName, 1)
                    prefEditInt(context, "totalRead", prefReadInt(context, "totalRead")+1)
                }
                else -> prefEditInt(context, chapterName, beenRead+1)
            }
        }
        fun markList(context: Context?, listString: String, arrayId: Int, listDoneName: String) {
            prefEditInt(context, listDoneName, 1)
            val number = prefReadInt(context, listString)
            val list = context!!.resources.getStringArray(arrayId)
            markRead(context, list[number])
        }

        fun prefReadString(context: Context?, stringName: String): String {
            lateinit var string: String
            context?.let {
                val pref = getPrefRead(context)
                string = pref.getString(stringName, "")!!
            }
            return string
        }

        fun prefEditString(context: Context?, name: String, str: String) {
            context?.let {
                val pref = getPrefEdit(context)
                pref.putString(name, str)
                pref.apply()
            }
        }
        fun prefEditInt(context: Context?, s: String?, i: Int) {
            context?.let {
                val pref = getPrefEdit(context)
                pref.putInt(s, i)
                pref.apply()
            }
        }

        fun prefReadInt(context: Context?, intName: String): Int {
            context?.let {
                val pref = getPrefRead(context)
                return pref.getInt(intName, 0)
            }
            return 0
        }
        fun getPrefRead(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                    "com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE)
        }

        fun getPrefEdit(context: Context): SharedPreferences.Editor {
            return context.getSharedPreferences(
                    "com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE).edit()
        }

        fun cancelAlarm(context: Context, notifyPendingIntent: PendingIntent) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(notifyPendingIntent)
        }

        fun createRemindAlarm(context: Context?, notifyPendingIntent: PendingIntent) {
            Log.d("createRemindAlarm", "Created Alarm")
            val sharedpref = PreferenceManager.getDefaultSharedPreferences(context)
            val timeinMinutes = sharedpref.getInt("remind_time", 0)
            val rHour = timeinMinutes / 60
            val rMinute = timeinMinutes % 60
            val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, rHour)
            calendar.set(Calendar.MINUTE, rMinute)
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1)
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, notifyPendingIntent)
        }

        fun createAlarm(context: Context?, notifyPendingIntent: PendingIntent) {
            Log.d("createAlarm", "Created Alarm")
            val sharedpref = PreferenceManager.getDefaultSharedPreferences(context)
            val timeinMinutes = sharedpref.getInt("daily_time", 0)
            val hour = timeinMinutes / 60
            val minute = timeinMinutes % 60
            val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1)
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, notifyPendingIntent)
        }
        fun resetStatistics(context: Context?){
            resetAmountRead(context, false)
        }
        fun resetAmountRead(context: Context?, standalone:Boolean) {
            val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.unquenchedAlert))
            builder.setPositiveButton(R.string.yes) { dialogInterface, i ->
                prefEditInt(context, "totalRead", 0)
                resetReadList(R.array.list_1, context)
                resetReadList(R.array.list_2, context)
                resetReadList(R.array.list_3, context)
                resetReadList(R.array.list_4, context)
                resetReadList(R.array.list_5, context)
                resetReadList(R.array.list_6, context)
                resetReadList(R.array.list_7, context)
                resetReadList(R.array.list_8, context)
                resetReadList(R.array.list_9, context)
                resetReadList(R.array.list_10, context)
            }
            builder.setNeutralButton(R.string.no) { dialogInterface, i -> dialogInterface.cancel() }
            when(standalone){
                false -> {
                    builder.setMessage(R.string.resetStat_confirm)
                            .setTitle(R.string.reset_stats)
                    prefEditInt(context, "curStreak", 0)
                    prefEditInt(context, "maxStreak", 0)
                }
                true -> builder.setMessage(R.string.resetAmount_confirm).setTitle(R.string.resetPercent)
            }
            builder.create().show()
        }

        fun resetReadList(arrayId: Int, context: Context?) {
            val res = context!!.resources
            val list = res.getStringArray(arrayId)
            for (s in list) {
                prefEditInt(context, s, 0)
            }
        }

        fun reset(context: Context?) {
            val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.unquenchedAlert))
            builder.setPositiveButton(R.string.yes) { dialogInterface, i ->
                prefEditInt(context, "List 1", 0)
                prefEditInt(context, "List 2", 0)
                prefEditInt(context, "List 3", 0)
                prefEditInt(context, "List 4", 0)
                prefEditInt(context, "List 5", 0)
                prefEditInt(context, "List 6", 0)
                prefEditInt(context, "List 7", 0)
                prefEditInt(context, "List 8", 0)
                prefEditInt(context, "List 9", 0)
                prefEditInt(context, "List 10", 0)
                prefEditInt(context, "list1Done", 0)
                prefEditInt(context, "list2Done", 0)
                prefEditInt(context, "list3Done", 0)
                prefEditInt(context, "list4Done", 0)
                prefEditInt(context, "list5Done", 0)
                prefEditInt(context, "list6Done", 0)
                prefEditInt(context, "list7Done", 0)
                prefEditInt(context, "list8Done", 0)
                prefEditInt(context, "list9Done", 0)
                prefEditInt(context, "list10Done", 0)
                prefEditInt(context, "listsDone", 0)
                prefEditInt(context, "dailyStreak", 0)
                prefEditString(context, "dateClicked", "None")
                prefEditInt(context, "curStreak", 0)
                navController?.navController?.navigate(R.id.navigation_home)
            }
            builder.setNeutralButton(R.string.no) { dialogInterface, i -> dialogInterface.cancel() }
            builder.setMessage(R.string.reset_confirm)
                    .setTitle(R.string.reset_title)
            builder.create().show()

        }

    }




    fun createDailyCheck(context: Context) {
        val alarmMgr: AlarmManager
        val alarmIntent: PendingIntent
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, dailyCheck::class.java)
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)
    }

}



