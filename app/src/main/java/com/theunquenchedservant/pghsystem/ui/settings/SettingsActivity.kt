package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.theunquenchedservant.granthornersbiblereadingsystem.*
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.cancelAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.createRemindAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.sharedPref.listNumbersReset
import com.theunquenchedservant.granthornersbiblereadingsystem.sharedPref.resetRead
import com.theunquenchedservant.granthornersbiblereadingsystem.sharedPref.statisticsEdit
import com.theunquenchedservant.granthornersbiblereadingsystem.sharedPref.statisticsRead
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmReceiver
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.remindReceiver
import java.text.DecimalFormat




private const val TITLE_TAG = "settingsActivityTitle"

class SettingsActivity : AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, HeaderFragment())
                    .commit()
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setTitle(R.string.title_activity_settings)
            }
        }
    }

    override fun onBackPressed() {
        when (title) {
            "Reset Lists", "Statistics", "Notifications" -> {
                onSupportNavigateUp()
            }
            else -> startActivity(parentActivityIntent)
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, title)
    }
    override fun onSupportNavigateUp(): Boolean {
        when (title) {
            "Reset Lists", "Statistics", "Notifications" -> {
                if (supportFragmentManager.popBackStackImmediate()) {
                    return true
                }
                return super.onSupportNavigateUp()
            }
            else -> {
                if (supportFragmentManager.popBackStackImmediate()) {
                    startActivity(parentActivityIntent)
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

    class HeaderFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey)
            val psToggle: Preference? = findPreference("psalms")
            val vacationToggle: Preference? = findPreference("vacation_mode")
            psToggle!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                true
            }
            vacationToggle!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                true
            }
        }
    }
    class NotificationsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.notification_preferences, rootKey)
            val dailyList: Preference? = findPreference("daily_time")
            val remindTime: Preference? = findPreference("remind_time")
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
            val notif = findPreference<Preference>("notif_switch")
            notif!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                val isOn = o as Boolean
                val notifyIntent = Intent(activity, AlarmReceiver::class.java)
                val remindIntent = Intent(activity, remindReceiver::class.java)
                val notifyPendingIntent = PendingIntent.getBroadcast(activity, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                val remindPendingIntent = PendingIntent.getBroadcast(activity, 0, remindIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                if (isOn) {
                    createAlarm(context, notifyPendingIntent); createRemindAlarm(context, remindPendingIntent)
                } else {
                    cancelAlarm(activity!!, notifyPendingIntent); cancelAlarm(activity!!, remindPendingIntent)
                }
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

    class ResetFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.reset_preferences, rootKey)
            val listAllReset: Preference? = findPreference("reset_all")
            val list1Reset: Preference? = findPreference("reset_list_1")
            val list2Reset: Preference? = findPreference("reset_list_2")
            val list3Reset: Preference? = findPreference("reset_list_3")
            val list4Reset: Preference? = findPreference("reset_list_4")
            val list5Reset: Preference? = findPreference("reset_list_5")
            val list6Reset: Preference? = findPreference("reset_list_6")
            val list7Reset: Preference? = findPreference("reset_list_7")
            val list8Reset: Preference? = findPreference("reset_list_8")
            val list9Reset: Preference? = findPreference("reset_list_9")
            val list10Reset: Preference? = findPreference("reset_list_10")
            listAllReset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { resetList("all", "all");true }
            list1Reset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { resetList("List 1", "list1Done"); true}
            list2Reset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { resetList("List 2", "list2Done"); true}
            list3Reset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { resetList("List 3", "list3Done"); true}
            list4Reset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { resetList("List 4", "list4Done"); true}
            list5Reset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { resetList("List 5", "list5Done"); true}
            list6Reset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { resetList("List 6", "list6Done"); true}
            list7Reset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { resetList("List 7", "list7Done"); true}
            list8Reset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { resetList("List 8", "list8Done"); true}
            list9Reset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { resetList("List 9", "list9Done"); true}
            list10Reset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { resetList("List 10", "list10Done"); true}
        }
        private fun resetList(list:String, listDone:String){
            log("Begin reset (list reset)")
            var title : String = ""
            val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.unquenchedAlert))
            log("created alertdialog builder with unquenched theme")
            builder.setPositiveButton(R.string.yes) { dialogInterface, i ->
                log("reset dialog button yes pressed")
                when(list){
                    "all" -> {
                        listNumbersReset(context)
                        log("Reset all list numbers")
                        statisticsEdit(context, "dailyStreak", 0)
                        log("reset daily streak to 0")
                    }
                    else -> {
                        listNumberEditInt(context, list, 0)
                        log("Reset $list to 0")
                        listNumberEditInt(context, listDone, 0)
                        log("set list to not done")
                        listNumberEditInt(context, "listsDone", listNumberReadInt(context, "listsDone")-1)
                        log("New listDone = ${listNumberReadInt(context, "listsDone")}")
                    }
                }
                log("navigating home")
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
            }
            builder.setNeutralButton(R.string.no) { dialogInterface, i ->
                log("reset dialog cancel button pressed");
                dialogInterface.cancel()
            }
            builder.setTitle(R.string.reset_title)
            when(list) {
                "all" -> {builder.setMessage(R.string.reset_confirm);builder.setTitle(R.string.reset_title)}
                else -> {builder.setMessage("Are you sure you want to reset $list? This is irreversible");builder.setTitle("Reset $list")}
            }
            log("Showing reset dialog")
            builder.create().show()
        }
    }

    class StatisticsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.statistics, rootKey)
            val curStreak: Preference? = findPreference("currentStreak")
            val maxStreak: Preference? = findPreference("MaximumStreak")
            val percentRead: Preference? = findPreference("percentRead")
            curStreak?.summary = String.format("%d", statisticsRead(context, "currentStreak"))
            maxStreak?.summary = String.format("%d", statisticsRead(context, "maxStreak"))
            val amountRead = statisticsRead(context, "totalRead").toDouble() / 1189.0 * 100
            val df = DecimalFormat("##")
            percentRead?.summary = df.format(amountRead) + "%"
            val amountReset: Preference? = findPreference("reset_amount_read")
            amountReset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                resetAmountRead(true)
                true
            }
            val statReset: Preference? = findPreference("reset_statistics")
            statReset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                resetAmountRead(false)
                true
            }
            val partialStreakAllow: Preference? = findPreference("allow_partial_switch")
            partialStreakAllow!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                true
            }
            partialStreakAllow.summary = "Streak won't break if you do less than 10 lists (but more than 1)"
        }

        fun resetAmountRead(standalone: Boolean) {
            log("Start resetAmountRead, standalone = $standalone")
            val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.unquenchedAlert))
            log("created alertdialog builder with unquenched theme")
            builder.setNeutralButton(R.string.no) { dialogInterface, i -> log("cancel pressed"); dialogInterface.cancel() }
            when (standalone) {
                false -> {
                    log("Standalone False, edit streaks as well")
                    builder.setMessage(R.string.resetStat_confirm)
                            .setTitle(R.string.reset_stats)
                    builder.setPositiveButton(R.string.yes) { dialogInterface, i ->
                        statisticsEdit(context, "totalRead", 0)
                        log("reset Amount Read/statistics = yes")
                        log("reset totalRead to 0")
                        resetRead(context)
                        statisticsEdit(context, "currentStreak", 0)
                        val curStreak: Preference? = findPreference("currentStreak")
                        val maxStreak: Preference? = findPreference("MaximumStreak")
                        val percentRead: Preference? = findPreference("percentRead")
                        statisticsEdit(context, "maxStreak", 0)
                        curStreak?.summary = "${0}"
                        maxStreak?.summary = "${0}"
                        percentRead?.summary = "${0}%"
                    }
                }
                true -> {
                    builder.setMessage(R.string.resetAmount_confirm).setTitle(R.string.resetPercent)
                    log("Standalone true, just reset amount read")
                    builder.setPositiveButton(R.string.yes) { dialogInterface, i ->
                        statisticsEdit(context, "totalRead", 0)
                        val percentRead: Preference? = findPreference("percentRead")
                        percentRead?.summary = "${0}%"
                        log("reset Amount Read/statistics = yes")
                        log("reset totalRead to 0")
                        resetRead(context)
                    }
                }
            }
            log("show resetAmountRead dialog")
            builder.create().show()
        }
    }
}
