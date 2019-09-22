package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.theunquenchedservant.granthornersbiblereadingsystem.*
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.cancelAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.createRemindAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.prefReadInt
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.prefReadString
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.reset
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.resetAmountRead
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.resetStatistics
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmReceiver
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.remindReceiver
import java.text.DecimalFormat




private const val TITLE_TAG = "settingsActivityTitle"

class SettingsActivity : AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.theunquenchedservant.granthornersbiblereadingsystem.R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(com.theunquenchedservant.granthornersbiblereadingsystem.R.id.settings, HeaderFragment())
                    .commit()
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setTitle(com.theunquenchedservant.granthornersbiblereadingsystem.R.string.title_activity_settings)
            }
        }
    }

    override fun onBackPressed() {
        when(title){
            "Reset Options", "Statistics" ->{
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
        when(title){
            "Reset Options", "Statistics" -> {
                if(supportFragmentManager.popBackStackImmediate()){
                    return true
                }
                return super.onSupportNavigateUp()
            }
            else ->{
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
                .replace(com.theunquenchedservant.granthornersbiblereadingsystem.R.id.settings, fragment)
                .addToBackStack(null)
                .commit()
        title = pref.title
        return true
    }

    class HeaderFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey)
            val dailyList : Preference? = findPreference("daily_time")
            val remindTime : Preference? = findPreference("remind_time")
            val psToggle : Preference? = findPreference("psalms")
            psToggle!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                val isOn = o as Boolean?
                true
            }
            val sharedpref = PreferenceManager.getDefaultSharedPreferences(activity!!)
            val daily_timeinminutes = sharedpref.getInt("daily_time", 0)
            var ampm = ""
            var d_hour = daily_timeinminutes / 60
            val d_minute = daily_timeinminutes % 60
            when(d_hour){
                in 13..23 -> {d_hour = d_hour - 12; ampm=" PM"}
                12 -> ampm = " PM"
                0 -> {d_hour = 12; ampm = " AM"}
                in 1..11 -> ampm=" AM"
            }
            val daily_time = Integer.toString(d_hour) + ":" + DecimalFormat("00").format(d_minute.toLong()) + ampm
            dailyList!!.summary = daily_time
            val reminderTimeInMinutes = sharedpref.getInt("remind_time", 0)
            ampm = ""
            var r_hour = reminderTimeInMinutes / 60
            val r_minute = reminderTimeInMinutes % 60
            when(r_hour){
                in 13..23 -> {r_hour = r_hour - 12; ampm=" PM"}
                12 -> ampm = " PM"
                0 -> {r_hour = 12; ampm = " AM"}
                in 1..11 -> ampm=" AM"
            }
            val remind_time = Integer.toString(r_hour) + ":" + DecimalFormat("00").format(r_minute.toLong()) + ampm
            remindTime!!.summary = remind_time
            val notif = findPreference<Preference>("notif_switch")
            notif!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                val isOn = o as Boolean
                val notifyIntent = Intent(activity, AlarmReceiver::class.java)
                val remindIntent = Intent(activity, remindReceiver::class.java)
                val notifyPendingIntent = PendingIntent.getBroadcast(activity, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                val remindPendingIntent = PendingIntent.getBroadcast(activity, 0, remindIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                if (isOn) {
                    createAlarm(context, notifyPendingIntent)
                    createRemindAlarm(context, remindPendingIntent)
                } else {
                    cancelAlarm(activity!!, notifyPendingIntent)
                    cancelAlarm(activity!!, remindPendingIntent)
                }
                true
            }
        }

        override fun onDisplayPreferenceDialog(preference: Preference) {
            var dialogFragment: DialogFragment? = null
            if (preference is TimePreference) {
                dialogFragment =TimePreferenceDialogFragmentCompat
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
            setPreferencesFromResource(com.theunquenchedservant.granthornersbiblereadingsystem.R.xml.reset_preferences, rootKey)
            val listReset : Preference? = findPreference("reset_lists")
            listReset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                reset(context)
                true
            }

        }
    }
    class StatisticsFragment : PreferenceFragmentCompat(){
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.statistics, rootKey)
            val curStreak : Preference? = findPreference("currentStreak")
            val maxStreak : Preference? = findPreference("MaximumStreak")
            val percentRead : Preference? = findPreference("percentRead")
            curStreak?.summary = String.format("%d", prefReadInt(context, "curStreak"))
            maxStreak?.summary = String.format("%d", prefReadInt(context, "maxStreak"))
            val amountRead = prefReadInt(context, "totalRead").toDouble() / 1189.0 * 100
            val df = DecimalFormat("##")
            percentRead?.summary = df.format(amountRead) + "%"
            val amountReset : Preference? = findPreference("reset_amount_read")
            amountReset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                resetAmountRead(context, true)
                true
            }
            val statReset : Preference? = findPreference("reset_statistics")
            statReset!!.onPreferenceClickListener  = Preference.OnPreferenceClickListener {
                resetStatistics(context)
                true
            }

        }
    }
}
