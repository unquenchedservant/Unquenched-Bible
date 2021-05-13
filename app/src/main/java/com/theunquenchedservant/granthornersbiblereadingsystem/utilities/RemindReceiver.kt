package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.isWeekend
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref

class RemindReceiver : BroadcastReceiver() {
    private var mNotificationManager: NotificationManager? = null

    override fun onReceive(context: Context, intent: Intent) {
        traceLog(file="RemindReceiver.kt", function="onReceive()")
        when(getBoolPref(name="vacationMode") || (getBoolPref(name="weekendMode") && isWeekend())) {
            false -> {
                debugLog("Vacation mode off, preparing reminder notification")
                if(getBoolPref(name="notifications")) {
                    val check = getIntPref(name="listsDone")
                    debugLog("lists done so far = $check")
                    val allowPartial = getBoolPref(name="allowPartial")
                    debugLog("Allow partial is $allowPartial")
                    val doneMax = when(getStringPref(name="planSystem", defaultValue="pgh")){
                        "pgh"->10
                        "mcheyne"->4
                        else->10
                    }
                    mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    when (check) {
                        0 -> {
                            deliverNotification(context, false)
                        }
                         in 1 until doneMax -> {
                            mNotificationManager?.cancel(1)
                            mNotificationManager?.cancel(2)
                            deliverNotification(context, true)
                        }
                        doneMax ->{
                            debugLog("All lists done, not sending notification")
                        }
                    }
                }else{
                    debugLog("Notification switch off, not sending notification")
                }
            }
            true -> {
                debugLog("Vacation mode on, not sending notification")
            }
        }
    }

    private fun deliverNotification(context: Context, partial:Boolean) {
        traceLog("RemindReceiver.kt", "deliverNotification()")
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
