package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.theunquenchedservant.granthornersbiblereadingsystem.Marker.markAll
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref

class DoneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when(getIntPref("listsDone")) {
            in 0..9 -> {
                markAll()
                Toast.makeText(context, "Lists Marked!", Toast.LENGTH_LONG)
                        .show()
            }
            10 -> Toast.makeText(context, "Already Done!", Toast.LENGTH_LONG).show()
        }
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(1)
        mNotificationManager.cancel(2)

    }
}