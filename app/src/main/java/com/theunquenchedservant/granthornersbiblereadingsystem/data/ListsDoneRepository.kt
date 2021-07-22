package com.theunquenchedservant.granthornersbiblereadingsystem.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref

class ListsDoneRepository {
    private val user: FirebaseUser? = Firebase.auth.currentUser

    fun getListsDone(): LiveData<ListsDone> {
        val data = MutableLiveData<ListsDone>()
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            if(key == "pghDone" || key == "mcheyneDone"){
                data.value = ListsDone(sharedPreferences.getInt(key, 0))
                debugLog("Data changed ${data.value!!.listsDone}")
            }
        }
        return data
    }
}