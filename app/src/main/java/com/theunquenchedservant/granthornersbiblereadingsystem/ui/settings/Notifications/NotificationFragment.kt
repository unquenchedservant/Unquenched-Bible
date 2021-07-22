package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings.Notifications

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.cancelAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS
import java.text.DecimalFormat

class NotificationsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.notification_preferences, rootKey)
        val vacationToggle: Preference? = findPreference("vacationMode")
        val dailyList: Preference? = findPreference("dailyNotif")
        val remindTime: Preference? = findPreference("remindNotif")

        val dailyMillis = getIntPref(name="dailyNotif")
        dailyList!!.summary = getSummary(dailyMillis)

        val remindMillis = getIntPref(name="remindNotif")
        remindTime!!.summary = getSummary(remindMillis)

        remindTime.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            setIntPref(name="remindNotif", value=o as Int, updateFS=true)
            true
        }
        dailyList.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            setIntPref(name="dailyNotif", value=o as Int, updateFS=true)
            true
        }

        val notifOn = findPreference<Preference>("notifications")
        notifOn!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            val isOn = o as Boolean
            updateFS(name="notifications", isOn)
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
                updateFS(name="vacationMode", o)
            }else{
                updateFS(name="vacationMode", o)
                setBoolPref(name="vacationOff", value=true, updateFS=true)
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
            dialogFragment.show(this.requireFragmentManager(),
                    "android.support.v7.preference" + ".PreferenceFragment.DIALOG")
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }
}