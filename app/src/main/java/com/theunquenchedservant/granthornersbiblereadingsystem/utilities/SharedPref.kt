package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.checkDate


object SharedPref {

    fun setStreak(){
        if(!checkDate("both", false)){
            setIntPref("currentStreak", 0)
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

    fun setIntPref(name: String, value: Int){
        getPref().edit().putInt(name, value).apply()
    }
    fun increaseIntPref(name: String, value: Int): Int{
        val start = getIntPref(name)
        setIntPref(name, start+value)
        return start+value
    }
    fun getIntPref(name: String): Int {
        return getPref().getInt(name, 0)
    }

    fun setStringPref(name:String, value: String) {
        getPref().edit().putString(name, value).apply()
    }
    fun getStringPref(name:String): String{
        return getPref().getString(name, "itsdeadjim")!!
    }

    fun setBoolPref(name: String, value: Boolean){
        getPref().edit().putBoolean(name, value).apply()
    }
    fun getBoolPref(name: String): Boolean{
        return getPref().getBoolean(name, false)
    }
}