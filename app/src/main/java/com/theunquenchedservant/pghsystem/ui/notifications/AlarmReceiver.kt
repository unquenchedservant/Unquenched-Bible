package com.theunquenchedservant.pghsystem.ui.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.theunquenchedservant.pghsystem.MainActivity
import com.theunquenchedservant.pghsystem.R

class AlarmReceiver : BroadcastReceiver() {
    private val NOTIFICATION_ID = 0
    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    private var mNotificationManager: NotificationManager? = null
    override fun onReceive(context: Context, intent: Intent){
        mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        deliverNotification(context)
    }
    private fun deliverNotification(context: Context) {
        val today = MainActivity.getCurrentDate(false)
        val tapIntent = Intent(context, MainActivity::class.java)
        tapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val tapPending = PendingIntent.getActivity(context, 0, tapIntent, 0)
        val detailsIntent = Intent(context, doneReceiver::class.java)
        val detailsPendingIntent = PendingIntent.getBroadcast(context, 0, detailsIntent, 0)
        val builder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(today)
                .setContentText("Tap here for today's reading!")
                .addAction(android.R.drawable.ic_menu_save, "Done", detailsPendingIntent)
                .setContentIntent(tapPending)
                .setAutoCancel(false)
        mNotificationManager!!.notify(NOTIFICATION_ID, builder.build())
    }
}