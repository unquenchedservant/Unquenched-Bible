package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

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
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.boolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.getDate

class AlarmReceiver : BroadcastReceiver() {

    private val _notificationId = 2
    private val _primaryChannelId = "primary_notification_channel"
    private var mNotificationManager: NotificationManager? = null

    override fun onReceive(context: Context, intent: Intent){
        val vacation = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("vacation_mode", false)
        log("Vacation mode is $vacation")
        when(vacation){
            false -> {
                if(boolPref("notif_switch", null)) {
                    log("Vacation mode off, sending notification")
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
        tapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val tapPending = PendingIntent.getActivity(context, 0, tapIntent, 0)
        val builder : Notification = NotificationCompat.Builder(context, _primaryChannelId)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(today)
                .setContentText("Tap here for today's reading!")
                .setContentIntent(tapPending)
                .setAutoCancel(false)
                .build()
        mNotificationManager!!.notify(_notificationId, builder)
    }
}