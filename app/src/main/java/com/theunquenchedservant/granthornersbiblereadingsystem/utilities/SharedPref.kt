package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.getDate


object SharedPref {

    fun setStreak(){
        val date = getStringPref("dateChecked")
        if(date != getDate(1, false) && date != getDate(0, false)){
            intPref("currentStreak", 0)
        }
    }
    private fun getPref(): SharedPreferences{
        val context = App.applicationContext()
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
    fun updateFS(name: String, value: Any) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null)
            db.collection("main").document(user.uid).update(name, value)
    }

    fun intPref(name: String, value: Any?): Int {
        val context = App.applicationContext()
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return if (value != null) {
            pref.edit().putInt(name, value as Int).apply()
            0
        } else {
            pref.getInt(name, 0)
        }
    }

    fun setStringPref(name:String, value: String) {
        val pref = getPref()
        pref.edit().putString(name, value).apply()
    }
    fun getStringPref(name:String): String{
        val pref = getPref()
        return pref.getString(name, "itsdeadjim")!!
    }

    fun boolPref(name: String, value: Any?): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
        return if (value != null) {
            pref.edit().putBoolean(name, value as Boolean).apply()
            false
        } else {
            pref.getBoolean(name, false)
        }
    }
}