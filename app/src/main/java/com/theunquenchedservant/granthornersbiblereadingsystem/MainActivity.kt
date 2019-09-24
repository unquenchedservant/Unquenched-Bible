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
        Log.d("PROFGRANT", "Dark Mode Enabled")
        setContentView(R.layout.activity_main)
        Log.d("PROFGRANT", "Main Activity Content View Enabled")
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        val appBarConfiguration = AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_settings)
                .build()
        Log.d("PROFGRANT", "Created App Bar Config")
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController!!, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController!!)
        createNotificationChannel()
        Log.d("PROFGRANT", "Finished Main.onCreate")
    }



    private fun createNotificationChannel() {
        Log.d("PROFGRANT", "start createNotificationChannel")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = this.getString(R.string.channel_name)
            Log.d("PROFGRANT", "Name of channel: $name")
            val description = this.getString(R.string.channel_description)
            Log.d("PROFGRANT", "Description of Channel: $description")
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(PRIMARY_CHANNEL_ID, name, importance)
            channel.description = description
            channel.enableVibration(true)
            val notificationManager = this.getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
            Log.d("PROFGRANT", "Notification channel created")

        }
    }
    companion object{
        val navController = MainActivity::class.objectInstance
        fun createDailyCheck(context: Context?) {
            Log.d("PROFGRANT", "Begin createDailyCheck")
            val alarmMgr: AlarmManager
            val alarmIntent: PendingIntent
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1)
            }
            Log.d("PROFGRANT", "Calendar item: $calendar")
            alarmMgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, dailyCheck::class.java)
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)
            Log.d("PROFGRANT", "dailyCheck Alarm created/updated")
        }
        fun getCurrentDate(fullMonth: Boolean): String {
            Log.d("PROFGRANT", "Begin getCurrentDate")
            val today = Calendar.getInstance().time
            val formatter: SimpleDateFormat
            if (fullMonth) {
                formatter = SimpleDateFormat("MMMM dd", Locale.US)
            } else {
                formatter = SimpleDateFormat("MMM dd", Locale.US)
            }
            Log.d("PROFGRANT", "getCurrentDate returning ${formatter.format(today)}")
            return formatter.format(today)
        }

        fun markRead(context: Context?, chapterName:String){
            Log.d("PROFGRANT", "Begin markRead")
            Log.d("PROFGRANT", "Checking Chapter - $chapterName")
            val beenRead = prefReadInt(context, chapterName)
            Log.d("PROFGRANT", "beenRead Value - $beenRead")
            when(beenRead){
                0->{
                    prefEditInt(context, chapterName, 1)
                    Log.d("PROFGRANT", "Been Read set to 1")
                    prefEditInt(context, "totalRead", prefReadInt(context, "totalRead")+1)
                    Log.d("PROFGRANT", "totalRead added 1, total now - ${prefReadInt(context, "totalRead")}")
                }
                else -> {Log.d("PROFGRANT", "Been read added 1(value should not have been 0 before)");prefEditInt(context, chapterName, beenRead+1)}
            }
            Log.d("PROFGRANT", "End markRead")
        }
        fun markList(context: Context?, listString: String, arrayId: Int, listDoneName: String) {
            Log.d("PROFGRANT", "Start markList")
            prefEditInt(context, listDoneName, 1)
            Log.d("PROFGRANT", "Set $listDoneName to 1")
            val number = prefReadInt(context, listString)
            val list = context!!.resources.getStringArray(arrayId)
            Log.d("PROFGRANT", "passing list to markRead with current index")
            markRead(context, list[number])
            Log.d("PROFGRANT", "End markList")
        }

        fun prefReadString(context: Context?, stringName: String): String {
            Log.d("PROFGRANT", "Start prefReadString")
            lateinit var string: String
            context?.let {
                val pref = getPrefRead(context)
                Log.d("PROFGRANT", "Getting $stringName")
                string = pref.getString(stringName, "")!!
            }
            Log.d("PROFGRANT", "prefReadString returning $string")
            return string
        }

        fun prefEditString(context: Context?, name: String, str: String) {
            Log.d("PROFGRANT", "Start prefEditString")
            context?.let {
                val pref = getPrefEdit(context)
                Log.d("PROFGRANT", "Editing string $name")
                pref.putString(name, str)
                Log.d("PROFGRANT", "new string for $name - $str")
                pref.apply()
            }
        }
        fun prefEditInt(context: Context?, s: String?, i: Int) {
            Log.d("PROFGRANT", "Start prefEditInt")
            context?.let {
                val pref = getPrefEdit(context)
                Log.d("PROFGRANT", "Editing int $s")
                pref.putInt(s, i)
                Log.d("PROFGRANT", "New Int for $s - $i")
                pref.apply()
            }
        }

        fun prefReadInt(context: Context?, intName: String): Int {
            Log.d("PROFGRANT", "Start prefReadInt")
            context?.let {
                val pref = getPrefRead(context)
                Log.d("PROFGRANT", "Getting and returning $intName")
                return pref.getInt(intName, 0)
            }
            return 0
        }
        fun getPrefRead(context: Context): SharedPreferences {
            Log.d("PROFGRANT", "return sharedpreference")
            return context.getSharedPreferences(
                    "com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE)
        }

        fun getPrefEdit(context: Context): SharedPreferences.Editor {
            Log.d("PROFGRANT", "return sharedpreference.edit()")
            return context.getSharedPreferences(
                    "com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE).edit()
        }

        fun cancelAlarm(context: Context, notifyPendingIntent: PendingIntent) {
            Log.d("PROFGRANT", "Start cancelAlarm")
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            Log.d("PROFGRANT", "Cancelling $notifyPendingIntent")
            alarmManager.cancel(notifyPendingIntent)
        }

        fun createRemindAlarm(context: Context?, notifyPendingIntent: PendingIntent) {
            Log.d("PROFGRANT", "Start createRemindAlarm")
            val sharedpref = PreferenceManager.getDefaultSharedPreferences(context)
            val timeinMinutes = sharedpref.getInt("remind_time", 0)
            Log.d("PROFGRANT", "time in minutes $timeinMinutes")
            val rHour = timeinMinutes / 60
            val rMinute = timeinMinutes % 60
            Log.d("PROFGRANT", "remindHour - $rHour")
            Log.d("PROFGRANT", "remindMinute - $rMinute")
            val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, rHour)
            calendar.set(Calendar.MINUTE, rMinute)
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1)
            }
            Log.d("PROFGRANT", "Calendar - $calendar")
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, notifyPendingIntent)
            Log.d("PROFGRANT", "Created Reminder Alarm at $rHour:$rMinute")
        }

        fun createAlarm(context: Context?, notifyPendingIntent: PendingIntent) {
            Log.d("PROFGRANT", "Start createAlarm (dailyAlarm)")
            val sharedpref = PreferenceManager.getDefaultSharedPreferences(context)
            val timeinMinutes = sharedpref.getInt("daily_time", 0)
            Log.d("PROFGRANT", "time in minutes $timeinMinutes")
            val hour = timeinMinutes / 60
            val minute = timeinMinutes % 60
            Log.d("PROFGRANT", "dailyHour - $hour")
            Log.d("PROFGRANT", "dailyMinute - $minute")
            val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1)
            }
            Log.d("PROFGRANT", "Calendar - $calendar")
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, notifyPendingIntent)
            Log.d("PROFGRANT", "Created daily Alarm at $hour:$minute")

        }
        fun resetStatistics(context: Context?){
            Log.d("PROFGRANT","Start resetStatistics")
            resetAmountRead(context, false)
            Log.d("PROFGRANT", "end resetStatistics")
        }
        fun resetAmountRead(context: Context?, standalone:Boolean) {
            Log.d("PROFGRANT", "Start resetAmountRead, standalone = $standalone")
            val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.unquenchedAlert))
            Log.d("PROFGRANT", "created alertdialog builder with unquenched theme")
            builder.setPositiveButton(R.string.yes) { dialogInterface, i ->
                prefEditInt(context, "totalRead", 0)
                Log.d("PROFGRANT", "reset Amount Read/statistics = yes")
                Log.d("PROFGRANT", "reset totalRead to 0")
                resetReadList(R.array.list_1, context)
                Log.d("PROFGRANT", "reset list1")
                resetReadList(R.array.list_2, context)
                Log.d("PROFGRANT", "reset list2")
                resetReadList(R.array.list_3, context)
                Log.d("PROFGRANT", "reset list3")
                resetReadList(R.array.list_4, context)
                Log.d("PROFGRANT", "reset list4")
                resetReadList(R.array.list_5, context)
                Log.d("PROFGRANT", "reset list5")
                resetReadList(R.array.list_6, context)
                Log.d("PROFGRANT", "reset list6")
                resetReadList(R.array.list_7, context)
                Log.d("PROFGRANT", "reset list7")
                resetReadList(R.array.list_8, context)
                Log.d("PROFGRANT", "reset list8")
                resetReadList(R.array.list_9, context)
                Log.d("PROFGRANT", "reset list9")
                resetReadList(R.array.list_10, context)
                Log.d("PROFGRANT", "reset list10")
            }
            builder.setNeutralButton(R.string.no) { dialogInterface, i -> Log.d("PROFGRANT", "cancel pressed"); dialogInterface.cancel() }
            when(standalone){
                false -> {
                    Log.d("PROFGRANT", "Standalone False, edit streaks as well")
                    builder.setMessage(R.string.resetStat_confirm)
                            .setTitle(R.string.reset_stats)
                    prefEditInt(context, "curStreak", 0)
                    prefEditInt(context, "maxStreak", 0)
                }
                true -> {
                    builder.setMessage(R.string.resetAmount_confirm).setTitle(R.string.resetPercent)
                    Log.d("PROFGRANT", "Standalone true, just reset amount read")
                }
            }
            Log.d("PROFGRANT", "show resetAmountRead dialog")
            builder.create().show()
        }

        fun resetReadList(arrayId: Int, context: Context?) {
            Log.d("PROFGRANT", "Begin resetReadList")
            val res = context!!.resources
            val list = res.getStringArray(arrayId)
            Log.d("PROFGRANT", "Getting stringArray for list")
            for (s in list) {
                Log.d("PROFGRANT", "Reset $s to 0")
                prefEditInt(context, s, 0)
            }
        }

        fun reset(context: Context?) {
            Log.d("PROFGRANT", "Begin reset (list reset)")
            val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.unquenchedAlert))
            Log.d("PROFGRANT", "created alertdialog builder with unquenched theme")
            builder.setPositiveButton(R.string.yes) { dialogInterface, i ->
                Log.d("PROFGRANT", "reset dialog button yes pressed")
                prefEditInt(context, "List 1", 0)
                Log.d("PROFGRANT", "Reset List 1 position to 0")
                prefEditInt(context, "List 2", 0)
                Log.d("PROFGRANT", "Reset List 2 position to 0")
                prefEditInt(context, "List 3", 0)
                Log.d("PROFGRANT", "Reset List 3 position to 0")
                prefEditInt(context, "List 4", 0)
                Log.d("PROFGRANT", "Reset List 4 position to 0")
                prefEditInt(context, "List 5", 0)
                Log.d("PROFGRANT", "Reset List 5 position to 0")
                prefEditInt(context, "List 6", 0)
                Log.d("PROFGRANT", "Reset List 6 position to 0")
                prefEditInt(context, "List 7", 0)
                Log.d("PROFGRANT", "Reset List 7 position to 0")
                prefEditInt(context, "List 8", 0)
                Log.d("PROFGRANT", "Reset List 8 position to 0")
                prefEditInt(context, "List 9", 0)
                Log.d("PROFGRANT", "Reset List 9 position to 0")
                prefEditInt(context, "List 10", 0)
                Log.d("PROFGRANT", "Reset List 10 position to 0")
                prefEditInt(context, "list1Done", 0)
                Log.d("PROFGRANT", "Reset List 1 Done to 0")
                prefEditInt(context, "list2Done", 0)
                Log.d("PROFGRANT", "Reset List 2 Done to 0")
                prefEditInt(context, "list3Done", 0)
                Log.d("PROFGRANT", "Reset List 3 Done to 0")
                prefEditInt(context, "list4Done", 0)
                Log.d("PROFGRANT", "Reset List 4 Done to 0")
                prefEditInt(context, "list5Done", 0)
                Log.d("PROFGRANT", "Reset List 5 Done to 0")
                prefEditInt(context, "list6Done", 0)
                Log.d("PROFGRANT", "Reset List 6 Done to 0")
                prefEditInt(context, "list7Done", 0)
                Log.d("PROFGRANT", "Reset List 7 Done to 0")
                prefEditInt(context, "list8Done", 0)
                Log.d("PROFGRANT", "Reset List 8 Done to 0")
                prefEditInt(context, "list9Done", 0)
                Log.d("PROFGRANT", "Reset List 9 Done to 0")
                prefEditInt(context, "list10Done", 0)
                Log.d("PROFGRANT", "Reset List 10 Done to 0")
                prefEditInt(context, "listsDone", 0)
                Log.d("PROFGRANT", "Reset listsDone to 0")
                prefEditInt(context, "dailyStreak", 0)
                Log.d("PROFGRANT", "reset daily streak to 0")
                prefEditString(context, "dateClicked", "None")
                Log.d("PROFGRANT", "reset dateClicked")
                prefEditInt(context, "curStreak", 0)
                Log.d("PROFGRANT", "reset curStreak to 0")
                Log.d("PROFGRANT", "navigating home")
                navController?.navController?.navigate(R.id.navigation_home)
            }
            builder.setNeutralButton(R.string.no) { dialogInterface, i -> Log.d("PROFGRANT", "reset dialog cancel button pressed"); dialogInterface.cancel() }
            builder.setMessage(R.string.reset_confirm)
                    .setTitle(R.string.reset_title)
            Log.d("PROFGRANT", "Showing reset dialog")
            builder.create().show()
        }
    }
}