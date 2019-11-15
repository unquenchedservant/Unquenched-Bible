package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmCreator.createAlarm

class ServiceStarted  : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED" || intent?.action == "android.intent.action.MY_PACKAGE_REPLACED") {
            createAlarm("dailyCheck")
            createAlarm("daily")
            createAlarm("remind")
        }
    }
}