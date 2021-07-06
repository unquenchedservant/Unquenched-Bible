package com.theunquenchedservant.granthornersbiblereadingsystem

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Preferences
import timber.log.Timber

class App : Application () {
    lateinit var res: Resources
    var preferences: Preferences? = null
    fun setPreference(preference: Preferences){
        preferences = preference
    }
    override fun onCreate(){
        super.onCreate()
        instance = this
        Timber.plant(Timber.DebugTree())
    }

    companion object{
        private var instance : App? = null
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}