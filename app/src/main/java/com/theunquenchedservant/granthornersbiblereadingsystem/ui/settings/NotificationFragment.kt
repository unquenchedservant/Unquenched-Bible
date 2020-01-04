package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmCreator.cancelAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmCreator.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.boolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.intPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS
import java.text.DecimalFormat

class NotificationsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.notification_preferences, rootKey)
        val vacationToggle: Preference? = findPreference("vacation_mode")
        val dailyList: Preference? = findPreference("daily_time")
        val remindTime: Preference? = findPreference("remind_time")

        val dailyMillis = intPref("daily_time", null)
        dailyList!!.summary = getSummary(dailyMillis)

        val remindMillis = intPref("remind_time", null)
        remindTime!!.summary = getSummary(remindMillis)

        remindTime.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            intPref("remind_time", o)
            updateFS("remindNotif", o)
            true
        }
        dailyList.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            intPref("daily_time", o)
            updateFS("remindNotif", o)
            true
        }

        val notifOn = findPreference<Preference>("notif_switch")
        notifOn!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            val isOn = o as Boolean
            updateFS("notifications", isOn)
            if (isOn) {
                createAlarm("daily"); createAlarm("remind")
                true
            } else {
                cancelAlarm("remind");cancelAlarm("daily")
                true
            }
        }

        vacationToggle!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            o as Boolean
            if(o){
                updateFS("vacationMode", o)
            }else{
                updateFS("vacationMode", o)
                boolPref("vacationOff", true)
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