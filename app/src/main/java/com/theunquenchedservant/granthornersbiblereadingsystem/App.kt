package com.theunquenchedservant.granthornersbiblereadingsystem

import android.app.Application
import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Preferences
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application () {
    init {
        instance = this
    }
    var preferences: Preferences? = null

    override fun onCreate(){
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
    fun initFirestore(){
        Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                preferences = Preferences(it.data!!)
            }
            .addOnFailureListener {
                debugLog("Firestore initialization failed for $it")
            }
    }
    companion object{
        private var instance : App? = null
        private var preferences: Preferences? = null
        private var data: MutableMap<String, Any>? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}