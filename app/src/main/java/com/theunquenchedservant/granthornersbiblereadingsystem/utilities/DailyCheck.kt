package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DailyCheck : BroadcastReceiver() {
    lateinit var preferences: Preferences

    init {
        CoroutineScope(Dispatchers.IO).launch{
            val data = Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get().await().data
            preferences = Preferences(data!!, context = App().applicationContext)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        traceLog(file = "DailyCheck.kt", function = "onReceive()")
        preferences.list.hardReset()
        CoroutineScope(Dispatchers.IO).launch {
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }
}
