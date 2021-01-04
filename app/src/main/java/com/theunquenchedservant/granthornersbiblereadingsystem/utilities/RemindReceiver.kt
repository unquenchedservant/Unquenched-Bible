package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref

class RemindReceiver : BroadcastReceiver() {
    private var mNotificationManager: NotificationManager? = null

    override fun onReceive(context: Context, intent: Intent) {
        when(getBoolPref("vacation_mode")) {
            false -> {
                log("Vacation mode off, preparing reminder notification")
                if(getBoolPref("notif_switch")) {
                    val check = getIntPref("listsDone")
                    log("lists done so far = $check")
                    val allowPartial = getBoolPref("allow_partial_switch")
                    log("Allow partial is $allowPartial")
                    mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    when (check) {
                        0 -> {
                            deliverNotification(context, false)
                        }
                        in 0..9 -> {
                            mNotificationManager?.cancel(1)
                            mNotificationManager?.cancel(2)
                            deliverNotification(context, true)
                        }
                        10 ->{
                            log("All lists done, not sending notification")
                        }
                    }
                }else{
                    log("Notification switch off, not sending notification")
                }
            }
            true -> {
                log("Vacation mode on, not sending notification")
            }
        }
    }

    private fun deliverNotification(context: Context, partial:Boolean) {
        val rem = context.resources.getString(R.string.title_reminder)
        val content : String = if(partial){
            "Don't forget to finish the reading!"
        }else{
            "Don't forget to do the reading"
        }
        val tapIntent = Intent(context, MainActivity::class.java)
        tapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val tapPending = PendingIntent.getActivity(context, 0, tapIntent, 0)
        val detailsIntent = Intent(context, DoneReceiver::class.java)
        val detailsPendingIntent = PendingIntent.getBroadcast(context, 0, detailsIntent, 0)
        val builder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(rem)
                .setContentText(content)
                .addAction(android.R.drawable.ic_menu_save, "Done", detailsPendingIntent)
                .setContentIntent(tapPending)
                .setAutoCancel(false)
        mNotificationManager!!.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }
}
