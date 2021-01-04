package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.checkDate
import java.io.File


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
    fun getBoolPref(name: String, defaultValue: Boolean=false): Boolean{
        return getPref().getBoolean(name, defaultValue)
    }

    private val user = FirebaseAuth.getInstance().currentUser

    fun preferenceToFireStone(){
        val db = FirebaseFirestore.getInstance()
        val user2 = FirebaseAuth.getInstance().currentUser
        val results = mutableMapOf<String?, Any?>()
        for(i in 1..10){
            results["list$i"] = getIntPref("list$i")
            results["list${i}Done"] = getIntPref("list${i}Done")
        }
        results["listsDone"] = getIntPref("listsDone")
        results["currentStreak"] = getIntPref("currentStreak")
        results["dailyStreak"] = getIntPref("dailyStreak")
        results["maxStreak"] = getIntPref("maxStreak")
        results["notifications"] = getBoolPref("notif_switch")
        results["psalms"] = getBoolPref("psalms")
        results["planHold"] = getBoolPref("planHold")
        results["vacationMode"] = getBoolPref("vacation_mode")
        results["allowPartial"] = getBoolPref("allow_partial_switch")
        results["dailyNotif"] = getIntPref( "daily_time")
        results["remindNotif"] = getIntPref("remind_time")
        results["dateChecked"] = getStringPref( "dateChecked")
        db.collection("main").document(user2!!.uid).set(results)
                .addOnSuccessListener { MainActivity.log("Data transferred to firestore") }
                .addOnFailureListener {e -> Log.w("PROFGRANT", "Error writing to firestore", e) }
    }
    fun firestoneToPreference(database: DocumentSnapshot){
        val data = database.data
        if(data != null) {
            MainActivity.log("User document exists")
            for (i in 1..10) {
                setIntPref("list$i", (data["list$i"] as Long).toInt())
                setIntPref("List${i}Done", (data["list${i}Done"] as Long).toInt())
            }
            setIntPref("dailyStreak", (data["dailyStreak"] as Long).toInt())
            setIntPref("currentStreak", (data["currentStreak"] as Long).toInt())
            setIntPref("maxStreak", (data["maxStreak"] as Long).toInt())
            setBoolPref("psalms", data["psalms"] as Boolean)
            setBoolPref("allow_partial_switch", data["allowPartial"] as Boolean)
            setBoolPref("vacation_mode", data["vacationMode"] as Boolean)
            setBoolPref("notif_switch", data["notifications"] as Boolean)
            setIntPref("daily_time", (data["dailyNotif"] as Long).toInt())
            setIntPref("remind_time", (data["remindNotif"] as Long).toInt())
            setStringPref("dateChecked", (data["dateChecked"] as String))
            if(data["holdPlan"] != null) {
                setBoolPref("holdPlan", data["holdPlan"] as Boolean)
            }
        }
    }

    fun listNumbersReset() { App.applicationContext().getSharedPreferences("listNumbers", Context.MODE_PRIVATE).edit().clear().apply() }

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