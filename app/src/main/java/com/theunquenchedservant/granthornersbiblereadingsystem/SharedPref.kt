package com.theunquenchedservant.granthornersbiblereadingsystem

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.FileUtils
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import java.io.File

object SharedPref {
    private val user = FirebaseAuth.getInstance().currentUser

    fun preferenceToFireStone(){
        val db = FirebaseFirestore.getInstance()
        val user2 = FirebaseAuth.getInstance().currentUser
        val results = mutableMapOf<String?, Any?>()
        for(i in 1..10){
            results["list$i"] = intPref("list$i", null)
            results["list${i}Done"] = intPref("list${i}Done", null)
        }
        results["listsDone"] = intPref("listsDone", null)
        results["currentStreak"] = intPref("currentStreak", null)
        results["dailyStreak"] = intPref("dailyStreak", null)
        results["maxStreak"] = intPref("maxStreak", null)
        results["notifications"] = boolPref( "notif_switch", null)
        results["psalms"] = boolPref( "psalms", null)
        results["vacationMode"] = boolPref( "vacation_mode", null)
        results["allowPartial"] = boolPref("allow_partial_switch", null)
        results["dailyNotif"] = intPref( "daily_time", null)
        results["remindNotif"] = intPref("remind_time", null)
        results["dateChecked"] = stringPref( "dateChecked", null)
        db.collection("main").document(user2!!.uid).set(results)
                .addOnSuccessListener { log("Data transferred to firestore") }
                .addOnFailureListener {e -> Log.w("PROFGRANT", "Error writing to firestore", e) }
    }
    fun firestoneToPreference(database: DocumentSnapshot){
        val data = database.data
        if(data != null) {
            log("User document exists")
            for (i in 1..10) {
                intPref("list$i", (data["list$i"] as Long).toInt())
                intPref("List${i}Done", (data["list${i}Done"] as Long).toInt())
            }
            intPref("dailyStreak", (data["dailyStreak"] as Long).toInt())
            intPref("currentStreak", (data["currentStreak"] as Long).toInt())
            intPref("maxStreak", (data["maxStreak"] as Long).toInt())
            boolPref("psalms", data["psalms"] as Boolean)
            boolPref("allow_partial_switch", data["allowPartial"] as Boolean)
            boolPref("vacation_mode", data["vacationMode"] as Boolean)
            boolPref("notif_switch", data["notifications"] as Boolean)
            intPref("daily_time", (data["dailyNotif"] as Long).toInt())
            intPref("remind_time", (data["remindNotif"] as Long).toInt())
            stringPref("dateChecked", (data["dateChecked"] as String))
        }
    }

    fun listNumbersReset() { App.applicationContext().getSharedPreferences("listNumbers", Context.MODE_PRIVATE).edit().clear().apply() }
    fun updateFS(name: String, value: Any){
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null)
            db.collection("main").document(user.uid).update(name, value)
    }
    fun intPref(name: String, value: Any?): Int{
        val context = App.applicationContext()
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return if(value !=null){
            pref.edit().putInt(name, value as Int).apply()
            0
        }else{
            pref.getInt(name, 0)
        }
    }
    fun stringPref(name: String, value: Any?): String{
        val context = App.applicationContext()
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return if(value != null) {
            pref.edit().putString(name, value as String).apply()
            "0"
        }else{
            pref.getString(name, "itsdeadjim")!!
        }
    }
    fun boolPref(name: String, value: Any?): Boolean{
        val pref = PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
        return if(value != null){
            pref.edit().putBoolean(name, value as Boolean).apply()
            false
        }else{
            pref.getBoolean(name, false)
        }
    }

    fun mergePref(){
        val context = App.applicationContext()
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val listPref = context.getSharedPreferences("listNumbers", Context.MODE_PRIVATE)
        val statPref = context.getSharedPreferences("statistics", Context.MODE_PRIVATE)
        pref.edit().putInt("list1", listPref.getInt("List 1", 0)).apply()
        pref.edit().putInt("list2", listPref.getInt("List 2", 0)).apply()
        pref.edit().putInt("list3", listPref.getInt("List 3", 0)).apply()
        pref.edit().putInt("list4", listPref.getInt("List 4", 0)).apply()
        pref.edit().putInt("list5", listPref.getInt("List 5", 0)).apply()
        pref.edit().putInt("list6", listPref.getInt("List 6", 0)).apply()
        pref.edit().putInt("list7", listPref.getInt("List 7", 0)).apply()
        pref.edit().putInt("list8", listPref.getInt("List 8", 0)).apply()
        pref.edit().putInt("list9", listPref.getInt("List 9", 0)).apply()
        pref.edit().putInt("list10", listPref.getInt("List 10", 0)).apply()
        pref.edit().putInt("list1Done", listPref.getInt("list1Done", 0)).apply()
        pref.edit().putInt("list2Done", listPref.getInt("list2Done", 0)).apply()
        pref.edit().putInt("list3Done", listPref.getInt("list3Done", 0)).apply()
        pref.edit().putInt("list4Done", listPref.getInt("list4Done", 0)).apply()
        pref.edit().putInt("list5Done", listPref.getInt("list5Done", 0)).apply()
        pref.edit().putInt("list6Done", listPref.getInt("list6Done", 0)).apply()
        pref.edit().putInt("list7Done", listPref.getInt("list7Done", 0)).apply()
        pref.edit().putInt("list8Done", listPref.getInt("list8Done", 0)).apply()
        pref.edit().putInt("list9Done", listPref.getInt("list9Done", 0)).apply()
        pref.edit().putInt("list10Done", listPref.getInt("list10Done", 0)).apply()
        pref.edit().putInt("listsDone", listPref.getInt("listsDone", 0)).apply()
        pref.edit().putInt("currentStreak", statPref.getInt("currentStreak", 0)).apply()
        pref.edit().putInt("maxStreak", statPref.getInt("maxStreak", 0)).apply()
        pref.edit().putInt("dailyStreak", statPref.getInt("dailyStreak", 0)).apply()
        pref.edit().putString("dateChecked", listPref.getString("dateChecked", "Jan 01")).apply()
        pref.edit().putBoolean("has_merged", true).apply()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.deleteSharedPreferences("statistics")
            context.deleteSharedPreferences("listNumbers")
        }else {
            val dir = File(context.filesDir?.parent + "/shared_prefs/")
            File(dir, "statistics.xml").delete()
            File(dir, "listNumber.xml").delete()
        }
    }
}
