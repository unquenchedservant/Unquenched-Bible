package com.theunquenchedservant.granthornersbiblereadingsystem

import android.app.Application
import android.content.Context
import timber.log.Timber


class MainApp : Application () {
    init {
        instance = this
    }
    override fun onCreate(){
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    companion object{
        private var instance : MainApp? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}