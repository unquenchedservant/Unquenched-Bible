package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Marker.markAll
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref

class DoneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        traceLog(file = "DoneReceiver.kt", function = "onReceive()")
        val doneMax = when(getStringPref(name="planSystem", defaultValue="pgh")){
            "pgh"->10
            "mcheyne"->4
            else->10
        }
        val listsDone = if(getStringPref("planSystem", defaultValue = "pgh") =="pgh") "listsDone" else "mcheyneListsDone"
        when(getIntPref(name=listsDone)) {
            in 0 until doneMax -> {
                if(getStringPref(name="planSystem", defaultValue="pgh") == "pgh") {
                    markAll(planType="pgh", context)
                }else{
                    markAll(planType="mcheyne", context)
                }
                Toast.makeText(context, "Lists Marked!", Toast.LENGTH_LONG)
                        .show()
            }
            doneMax -> Toast.makeText(context, "Already Done!", Toast.LENGTH_LONG).show()
        }
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(1)
        mNotificationManager.cancel(2)

    }
}