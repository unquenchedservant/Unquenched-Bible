package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.cancelAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class NotificationsFragment : PreferenceFragmentCompat() {
    val preferences = App().preferences!!
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.notification_preferences, rootKey)
        val vacationToggle: Preference? = findPreference("vacationMode")
        val dailyList: Preference? = findPreference("dailyNotif")
        val remindTime: Preference? = findPreference("remindNotif")

        val dailyMillis = preferences.settings.dailyNotif
        dailyList!!.summary = getSummary(dailyMillis)

        val remindMillis = preferences.settings.remindNotif
        remindTime!!.summary = getSummary(remindMillis)

        remindTime.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            preferences.settings.remindNotif = o as Int
            true
        }
        dailyList.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            preferences.settings.dailyNotif = o as Int
            true
        }

        val notifOn = findPreference<Preference>("notifications")
        notifOn!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            val isOn = o as Boolean
            preferences.settings.notifications = isOn
            if (isOn) {
                createAlarm(alarmType="daily"); createAlarm(alarmType="remind")
                true
            } else {
                cancelAlarm(alarmType="remind");cancelAlarm(alarmType="daily")
                true
            }
        }

        vacationToggle!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            o as Boolean
            if(o){
                preferences.settings.vacation = true
            }else{
                preferences.settings.vacation = false
                preferences.settings.vacationOff = true
            }
            true
        }
    }
    private fun getSummary(timeInMillis: Int): String{
        var hour = timeInMillis / 60
        val minute = timeInMillis % 60
        var amPm = "AM"
        when (hour) {
            in 13..23 -> {
                hour -= 12; amPm = "PM"
            }
            12 ->  amPm = "PM"
            0 -> {
                hour = 12; amPm = "AM"
            }
            in 1..11 -> amPm = "AM"
        }
        return "$hour:${DecimalFormat("00").format(minute)} $amPm"
    }

    override fun onDestroy() {
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }

    override fun onStop() {
        super.onStop()
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }
    override fun onPause(){
        super.onPause()
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
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
            dialogFragment.show(this.requireFragmentManager(),
                    "android.support.v7.preference" + ".PreferenceFragment.DIALOG")
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }
}