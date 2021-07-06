package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class DoneReceiver : BroadcastReceiver() {
    lateinit var preferences: Preferences

    init {
        CoroutineScope(Dispatchers.IO).launch{
            val data = Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get().await().data
            preferences = Preferences(data!!, context = App().applicationContext)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        traceLog(file = "DoneReceiver.kt", function = "onReceive()")
        when (preferences.list.listsDone) {
            in 0 until preferences.list.maxDone -> {
                preferences.list.markAll()
                Toast.makeText(context, "Lists Marked!", Toast.LENGTH_LONG).show()
            }
            preferences.list.maxDone -> Toast.makeText(
                context,
                "Already Done!",
                Toast.LENGTH_LONG
            ).show()
        }
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(1)
        mNotificationManager.cancel(2)

    }
}