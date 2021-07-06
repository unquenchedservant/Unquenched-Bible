package com.theunquenchedservant.granthornersbiblereadingsystem.service

import android.app.Notification
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
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.getDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.isWeekend
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Firestore
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    private val _notificationId = 2
    private val _primaryChannelId = "primary_notification_channel"
    private var mNotificationManager: NotificationManager? = null
    lateinit var preferences: Preferences
    init{
        CoroutineScope(Dispatchers.IO).launch{
            val data = Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get().await().data
            preferences = Preferences(data!!, context = App().applicationContext)
        }
    }
    private fun isOffDay():Boolean{
        traceLog(file="AlarmReceiver.kt", function="isOffDay()")
        return (preferences.settings.vacation ||
                (preferences.settings.weekendMode && isWeekend()) ||
                !preferences.settings.notifications ||
                (Calendar.getInstance().get(Calendar.MONTH) == Calendar.FEBRUARY &&
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 29 &&
                        preferences.settings.planType == "calendar" &&
                        preferences.settings.planSystem == "mcheyne"))
    }
    private fun isListsDone():Boolean{
        traceLog(file="AlarmReceiver.kt", function="isListsDone()")
        val planSystem = preferences.settings.planSystem
        return ((planSystem == "pgh" && preferences.list.listsDone == 10) || (planSystem == "mcheyne" && preferences.list.listsDone == 4))
    }
    override fun onReceive(context: Context, intent: Intent){
        traceLog(file="AlarmReceiver.kt", function="onReceive()")
        if(isOffDay()){
            debugLog(message="Vacation Mode on, not sending notification")
        }else{
            mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if(!isListsDone() && preferences.settings.notifications) {
                deliverNotification(context)
            }
        }
    }

    private fun deliverNotification(context: Context) {
        traceLog(file="AlarmReceiver.kt", function="deliverNotification()")
        val today = getDate(option=0, fullMonth=false)
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