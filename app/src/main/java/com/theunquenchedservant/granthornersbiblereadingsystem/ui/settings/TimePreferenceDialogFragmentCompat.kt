package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.TimePicker
import androidx.preference.PreferenceDialogFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmReceiver
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.remindReceiver
import java.text.DecimalFormat

class TimePreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {
    private var mTimePicker: TimePicker? = null
    companion object{
        fun newInstance(key: String): TimePreferenceDialogFragmentCompat {
            val fragment = TimePreferenceDialogFragmentCompat()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }


    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        mTimePicker = view.findViewById(R.id.timepicker) as TimePicker

        checkNotNull(mTimePicker) { "Dialog view must contain a TimePicker with id 'timepicker'" }

        // Get the time from the related Preference
        var minutesAfterMidnight: Int? = null
        val preference = preference
        if (preference is TimePreference) {
            minutesAfterMidnight = preference.getTime()
        }

        // Set the time to the TimePicker
        if (minutesAfterMidnight != null) {
            val hours = minutesAfterMidnight / 60
            val minutes = minutesAfterMidnight % 60
            val is24hour = DateFormat.is24HourFormat(context)

            mTimePicker!!.setIs24HourView(is24hour)
            mTimePicker!!.hour = hours
            mTimePicker!!.minute = minutes
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            // generate value to save
            var hours : Int = mTimePicker!!.hour
            val minutes : Int =  mTimePicker!!.minute
            val minutesAfterMidnight : Int = hours * 60 + minutes
            Log.d("preference", preference.dialogTitle as String)
            if (preference is TimePreference && preference.callChangeListener(minutesAfterMidnight)) {
                if (preference.dialogTitle.equals("Daily List Time")) {
                    val notifyIntent = Intent(activity, AlarmReceiver::class.java)
                    val notifyPendingIntent = PendingIntent.getBroadcast(activity, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    MainActivity.cancelAlarm(activity!!, notifyPendingIntent)
                    MainActivity.createAlarm(activity, notifyPendingIntent)
                } else if(preference.dialogTitle.equals("Reminder Time")) {
                    val remindIntent = Intent(activity, remindReceiver::class.java)
                    val remindPendingIntent = PendingIntent.getBroadcast(activity, 0, remindIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    MainActivity.cancelAlarm(activity!!, remindPendingIntent)
                    MainActivity.createRemindAlarm(activity, remindPendingIntent)
                }
                var ampm = ""
                when(hours) {
                    0 -> { hours = 12; ampm = " AM" }
                    in 1..11 -> ampm = " AM"
                    12 -> ampm = " PM"
                    in 13..23 -> { hours = hours - 12; ampm = " PM" }
                }
                val df = DecimalFormat("00")
                val time = Integer.toString(hours) + ":" + df.format(minutes.toLong()) + ampm
                preference.setSummary(time)
                (preference as TimePreference).setTime(minutesAfterMidnight)
            }
        }
    }
}