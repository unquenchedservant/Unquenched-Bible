package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.*
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.cancelAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberEditInt
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberReadInt
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.statisticsEdit
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.statisticsRead
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmReceiver
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.RemindReceiver
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.home.ManualListSet
import java.text.DecimalFormat







private const val TITLE_TAG = "settingsActivityTitle"

class SettingsActivity : AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setSupportActionBar(findViewById(R.id.settings_toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val b = intent.extras
        if(b != null){
            log("B wasn't null")
            if(b.getBoolean("Statistics")){
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.settings, StatisticsFragment())
                        .commit()
                title = "Statistics"
            }else if(b.getBoolean("Notifications")){
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.settings, NotificationsFragment())
                        .commit()
                title = "Notifications"
            }else if(b.getBoolean("Information")){
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.settings, InformationFragment())
                        .commit()
                title = "Information"
            }else if(b.getBoolean("Manual")){
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.settings, ManualListSet())
                        .commit()
                title="Set Lists Manually"
            }
        } else {
            title = savedInstanceState?.getCharSequence(TITLE_TAG)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                    setTitle(R.string.title_activity_settings)
            }
        }
    }

    override fun onBackPressed() {
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null) {
            val db = FirebaseFirestore.getInstance()
            val data = mutableMapOf<String, Any>()
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
            if (listNumberReadInt(this, "psToggleChanged") == 1) {
                data.put("psalms", sharedPref.getBoolean("psalms", false))
                listNumberEditInt(this, "psToggleChanged", 0)
            }
            if (listNumberReadInt(this, "vacationModeChanged") == 1) {
                data.put("vacationMode", sharedPref.getBoolean("vacation_mode", false))
                listNumberEditInt(this, "vacationModeChanged", 0)
            }
            if (listNumberReadInt(this, "remindtimeChanged") == 1) {
                data.put("remindNotif", sharedPref.getInt("remind_time", 1200))
                listNumberEditInt(this, "remindtimeChanged", 0)
            }
            if (listNumberReadInt(this, "dailytimeChanged") == 1) {
                data.put("dailyNotif", sharedPref.getInt("daily_time", 300))
                listNumberEditInt(this, "dailyTimeChanged", 0)
            }
            if (listNumberReadInt(this, "notifChanged") == 1) {
                data.put("notifications", sharedPref.getBoolean("notif_switch", false))
                listNumberEditInt(this, "notifChanged", 1)
            }
            if (listNumberReadInt(this, "allListsReset") == 1) {
                data.put("list1", 0)
                data.put("list1Done", 0)
                data.put("list2", 0)
                data.put("list2Done", 0)
                data.put("list3", 0)
                data.put("list3Done", 0)
                data.put("list4", 0)
                data.put("list4Done", 0)
                data.put("list5", 0)
                data.put("list5Done", 0)
                data.put("list6", 0)
                data.put("list6Done", 0)
                data.put("list7", 0)
                data.put("list7Done", 0)
                data.put("list8", 0)
                data.put("list8Done", 0)
                data.put("list9", 0)
                data.put("list9Done", 0)
                data.put("list10", 0)
                data.put("list10Done", 0)
                data.put("listsDone", 0)
                listNumberEditInt(this, "allListsReset", 0)
            }
            if (listNumberReadInt(this, "allStatsChanged") == 1) {
                data.put("read", "")
                data.put("currentStreak", 0)
                data.put("maxStreak", 0)
                data.put("dailyStreak", 0)
                listNumberEditInt(this, "allStatsChanged", 0)
            }
            if (listNumberReadInt(this, "partialChanged") == 1) {
                data.put("allowPartial", sharedPref.getBoolean("allow_partial_switch", false))
                listNumberEditInt(this, "partialChanged", 0)
            }
            if (data != mutableMapOf<String, Any>()) {
                db.collection("main").document(user.uid).update(data)
            }
        }
        when (title) {
            "Reset Lists", "Statistics", "Notification Options" -> {
                onSupportNavigateUp()
            }
            else -> this.finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, title)
    }

    override fun onSupportNavigateUp(): Boolean {
        val isLogged = FirebaseAuth.getInstance().currentUser

        if(isLogged != null) {
            val db = FirebaseFirestore.getInstance()
            val data = mutableMapOf<String, Any>()
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
            if (listNumberReadInt(this, "psToggleChanged") == 1) {
                data.put("psalms", sharedPref.getBoolean("psalms", false))
                listNumberEditInt(this, "psToggleChanged", 0)
            }
            if (listNumberReadInt(this, "vacationModeChanged") == 1) {
                data.put("vacationMode", sharedPref.getBoolean("vacation_mode", false))
                listNumberEditInt(this, "vacationModeChanged", 0)
            }
            if (listNumberReadInt(this, "remindtimeChanged") == 1) {
                data.put("remindNotif", sharedPref.getInt("remind_time", 1200))
                listNumberEditInt(this, "remindtimeChanged", 0)
            }
            if (listNumberReadInt(this, "dailytimeChanged") == 1) {
                data.put("dailyNotif", sharedPref.getInt("daily_time", 300))
                listNumberEditInt(this, "dailyTimeChanged", 0)
            }
            if (listNumberReadInt(this, "notifChanged") == 1) {
                data.put("notifications", sharedPref.getBoolean("notif_switch", false))
                listNumberEditInt(this, "notifChanged", 0)
            }
            if (listNumberReadInt(this, "allListsReset") == 1) {
                data.put("list1", 0)
                data.put("list1Done", 0)
                data.put("list2", 0)
                data.put("list2Done", 0)
                data.put("list3", 0)
                data.put("list3Done", 0)
                data.put("list4", 0)
                data.put("list4Done", 0)
                data.put("list5", 0)
                data.put("list5Done", 0)
                data.put("list6", 0)
                data.put("list6Done", 0)
                data.put("list7", 0)
                data.put("list7Done", 0)
                data.put("list8", 0)
                data.put("list8Done", 0)
                data.put("list9", 0)
                data.put("list9Done", 0)
                data.put("list10", 0)
                data.put("list10Done", 0)
                data.put("listsDone", 0)
                listNumberEditInt(this, "allListsReset", 0)
            }
            if (listNumberReadInt(this, "allStatsChanged") == 1) {
                data.put("read", "")
                data.put("currentStreak", 0)
                data.put("maxStreak", 0)
                data.put("dailyStreak", 0)
                listNumberEditInt(this, "allStatsChanged", 0)
            }
            if (listNumberReadInt(this, "partialChanged") == 1) {
                data.put("allowPartial", sharedPref.getBoolean("allow_partial_switch", false))
                listNumberEditInt(this, "partialChanged", 0)
            }
            if (data != mutableMapOf<String, Any>()) {
                db.collection("main").document(isLogged.uid).update(data)
            }
        }
        when (title) {
            "Reset Lists", "Statistics", "Notification Options" -> {
                if (supportFragmentManager.popBackStackImmediate()) {
                    return true
                }
                return super.onSupportNavigateUp()
            }
            else -> {
                if (supportFragmentManager.popBackStackImmediate()) {
                    finish()
                    return true
                }
                return super.onSupportNavigateUp()
            }
        }
    }

    override fun onPreferenceStartFragment(
            caller: PreferenceFragmentCompat,
            pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                pref.fragment
        ).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit()
        title = pref.title
        return true
    }
    class InformationFragment : PreferenceFragmentCompat(){
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?){
            setPreferencesFromResource(R.xml.information_preferences, rootKey)
            val license: Preference? = findPreference("licenses")
            license!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                false
            }
        }
    }
    class NotificationsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.notification_preferences, rootKey)
            val vacationToggle: Preference? = findPreference("vacation_mode")
            val dailyList: Preference? = findPreference("daily_time")
            val remindTime: Preference? = findPreference("remind_time")
            val notifyIntent = Intent(activity, AlarmReceiver::class.java)
            val remindIntent = Intent(activity, RemindReceiver::class.java)
            val notifyPendingIntent = PendingIntent.getBroadcast(activity, 4, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val remindPendingIntent = PendingIntent.getBroadcast(activity, 2, remindIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val sharedpref = PreferenceManager.getDefaultSharedPreferences(activity!!)
            val dailyTimeInMinutes = sharedpref.getInt("daily_time", 0)
            var dailyAMPM = ""
            var dailyHour = dailyTimeInMinutes / 60
            val dailyMinute = dailyTimeInMinutes % 60
            when (dailyHour) {
                in 13..23 -> {
                    dailyHour -= 12; dailyAMPM = "PM"
                }
                12 -> dailyAMPM = "PM"
                0 -> {
                    dailyHour = 12; dailyAMPM = "AM"
                }
                in 1..11 -> dailyAMPM = "AM"
            }
            val dailyTime = "$dailyHour:${DecimalFormat("00").format(dailyMinute)} $dailyAMPM"
            dailyList!!.summary = dailyTime
            val reminderTimeInMinutes = sharedpref.getInt("remind_time", 0)
            var remindAMPM = ""
            var remindHour = reminderTimeInMinutes / 60
            val remindMinute = reminderTimeInMinutes % 60
            when (remindHour) {
                in 13..23 -> {
                    remindHour -= 12; remindAMPM = "PM"
                }
                12 -> remindAMPM = "PM"
                0 -> {
                    remindHour = 12; remindAMPM = "AM"
                }
                in 1..11 -> remindAMPM = "AM"
            }
            val reminderTime = "$remindHour:${DecimalFormat("00").format(remindMinute)} $remindAMPM"
            remindTime!!.summary = reminderTime
            val context = context
            remindTime.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                if(sharedpref.getInt("remind_time", 0) != o as Int) {
                    listNumberEditInt(context, "remindtimeChanged", 1)
                    sharedpref.edit().putInt("remind_time", o).apply()
                }
                true
            }
            dailyList.onPreferenceChangeListener = Preference.OnPreferenceChangeListener{ _, o ->
                if(sharedpref.getInt("daily_time", 0) != o as Int) {
                    listNumberEditInt(context, "dailyTimeChanged", 1)
                    sharedpref.edit().putInt("daily_time", o).apply()
                }
                true
            }
            val notif = findPreference<Preference>("notif_switch")
            notif!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                val isOn = o as Boolean
                listNumberEditInt(context, "notifChanged", 1)
                if (isOn) {
                    log("Creating alarms")
                    createAlarm(context, notifyPendingIntent, true); createAlarm(context, remindPendingIntent, false)
                } else {
                    log("Cancelling alarms")
                    cancelAlarm(activity!!, notifyPendingIntent); cancelAlarm(activity!!, remindPendingIntent)
                }
                true
            }
            vacationToggle!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
                listNumberEditInt(context, "vacationModeChanged", 1)
                true
            }
        }

        override fun onDisplayPreferenceDialog(preference: Preference) {
            var dialogFragment: DialogFragment? = null
            if (preference is TimePreference) {
                dialogFragment = TimePreferenceDialogFragmentCompat
                        .newInstance(preference.getKey())
            }
            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0)
                dialogFragment.show(this.fragmentManager!!,
                        "android.support.v7.preference" + ".PreferenceFragment.DIALOG")
            } else {
                super.onDisplayPreferenceDialog(preference)
            }
        }
    }

    class StatisticsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.statistics, rootKey)
            val curStreak: Preference? = findPreference("currentStreak")
            val maxStreak: Preference? = findPreference("MaximumStreak")
            val context = activity?.applicationContext
            curStreak?.summary = String.format("%d", statisticsRead(context, "currentStreak"))
            maxStreak?.summary = String.format("%d", statisticsRead(context, "maxStreak"))
            val statReset : Preference? = findPreference("reset_statistics")
            statReset!!.onPreferenceClickListener  = Preference.OnPreferenceClickListener {
                listNumberEditInt(context, "allStatsChanged", 1)
                resetAmountRead(false)
                true
            }
            val partialStreakAllow : Preference? = findPreference("allow_partial_switch")
            partialStreakAllow!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener {_, _ ->
                listNumberEditInt(context, "partialChanged", 1)
                true
            }
            partialStreakAllow.summary = "Streak won't break if you do less than 10 lists (but more than 1)"
        }

        private fun resetAmountRead(standalone:Boolean){
            log("Start resetAmountRead, standalone = $standalone")
            val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.unquenchedAlert))
            log("created alertdialog builder with unquenched theme")
            builder.setNeutralButton(R.string.no) { dialogInterface, _ -> log("cancel pressed"); dialogInterface.cancel() }
            when(standalone){
                false -> {
                    log("Standalone False, edit streaks as well")
                    builder.setMessage(R.string.resetStat_confirm)
                            .setTitle(R.string.reset_stats)
                    builder.setPositiveButton(R.string.yes) { _, _ ->
                        log("reset Amount Read/statistics = yes")
                        statisticsEdit(context, "currentStreak", 0)
                        val curStreak : Preference? = findPreference("currentStreak")
                        val maxStreak : Preference? = findPreference("MaximumStreak")
                        statisticsEdit(context, "maxStreak", 0)
                        curStreak?.summary = "${0}"
                        maxStreak?.summary = "${0}"
                    }
                }
            }
            log("show resetAmountRead dialog")
            builder.create().show()
        }
    }
}
