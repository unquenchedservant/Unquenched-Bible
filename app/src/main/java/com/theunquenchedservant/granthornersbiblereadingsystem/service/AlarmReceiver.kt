package com.theunquenchedservant.granthornersbiblereadingsystem.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.getDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.isWeekend

class AlarmReceiver : BroadcastReceiver() {

    private val _notificationId = 2
    private val _primaryChannelId = "primary_notification_channel"
    private var mNotificationManager: NotificationManager? = null

    override fun onReceive(context: Context, intent: Intent){
        val vacation = getBoolPref(name="vacationMode", defaultValue=false)
        when(vacation || (getBoolPref(name="weekendMode") && isWeekend())){
            false -> {
                if(getBoolPref(name="notifications")) {
                    mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    deliverNotification(context)
                }else{
                    log("Notification switch off, not sending notification")
                }
            }
            true -> {
                log("Vacation mode on, not sending notification")
            }
        }
    }

    private fun deliverNotification(context: Context) {
        val today = getDate(0, false)
        val tapIntent = Intent(context, MainActivity::class.java)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(1)
        mNotificationManager.cancel(2)
        tapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val tapPending = PendingIntent.getActivity(context, 0, tapIntent, 0)
        val builder : Notification = NotificationCompat.Builder(context, _primaryChannelId)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(today)
                .setContentText("Tap here for today's reading!")
                .setContentIntent(tapPending)
                .setAutoCancel(false)
                .build()
        mNotificationManager.notify(_notificationId, builder)
    }
}