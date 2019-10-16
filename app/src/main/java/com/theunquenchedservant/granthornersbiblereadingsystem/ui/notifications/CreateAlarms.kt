package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity

object CreateAlarms {
    fun createAlarms(ctx: Context?) {
        val notifyIntent = Intent(ctx, AlarmReceiver::class.java)
        val notifyUp = (PendingIntent.getBroadcast(ctx, 4, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null)

        if (!notifyUp) {
            MainActivity.log("Created Notify Alarm")
            val notifyPendingIntent = PendingIntent.getBroadcast(ctx, 4, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            MainActivity.createAlarm(ctx, notifyPendingIntent, true)
        }

        val remindIntent = Intent(ctx, RemindReceiver::class.java)
        val remindUp = (PendingIntent.getBroadcast(ctx, 2, remindIntent, PendingIntent.FLAG_NO_CREATE) != null)

        if (!remindUp) {
            MainActivity.log("Created Remind Alarm")
            val remindPendingIntent = PendingIntent.getBroadcast(ctx, 2, remindIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            MainActivity.createAlarm(ctx, remindPendingIntent, false)
        }
    }
}