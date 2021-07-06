package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.isWeekend
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class RemindReceiver : BroadcastReceiver() {
    private var mNotificationManager: NotificationManager? = null
    lateinit var preferences: Preferences

    init {
        CoroutineScope(Dispatchers.IO).launch{
            val data = Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get().await().data
            preferences = Preferences(data!!, context = App().applicationContext)
        }
    }
    override fun onReceive(context: Context, intent: Intent) {
        traceLog(file = "RemindReceiver.kt", function = "onReceive()")
        when (preferences.settings.vacation || (preferences.settings.weekendMode && isWeekend())) {
            false -> {
                if (preferences.settings.notifications) {
                    val check = preferences.list.listsDone
                    when (check) {
                        0 -> deliverNotification(context, false)
                        in 1 until preferences.list.maxDone -> {
                            mNotificationManager?.cancel(1)
                            mNotificationManager?.cancel(2)
                            deliverNotification(context, true)
                        }
                    }
                }
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
