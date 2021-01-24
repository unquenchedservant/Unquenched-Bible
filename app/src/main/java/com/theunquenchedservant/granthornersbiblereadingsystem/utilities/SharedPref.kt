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
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.bookChapters
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.ntBooks
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.otBooks
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
    fun getIntPref(name: String, defaultValue: Int = 0): Int {
        return getPref().getInt(name, defaultValue)
    }

    fun setStringPref(name:String, value: String) {
        getPref().edit().putString(name, value).apply()
    }
    fun getStringPref(name:String, defaultValue: String = "itsdeadjim"): String{
        return getPref().getString(name, defaultValue)!!
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
            results["list${i}DoneDaily"] = getIntPref("list${i}DoneDaily")
        }
        for(i in 1..4){
            results["mcheyne_list$i"] = getIntPref("mcheyne_list$i")
            results["mcheyne_${i}Done"] = getIntPref("mcheyne_${i}Done")
            results["mcheyne_${i}DoneDaily"] = getIntPref("mcheyne_${i}DoneDaily")
        }
        results["listsDone"] = getIntPref("listsDone")
        results["currentStreak"] = getIntPref("currentStreak")
        results["dailyStreak"] = getIntPref("dailyStreak")
        results["maxStreak"] = getIntPref("maxStreak")
        results["notifications"] = getBoolPref("notif_switch")
        results["psalms"] = getBoolPref("psalms")
        results["holdPlan"] = getBoolPref("holdPlan")
        results["graceTime"] = getIntPref("graceTime")
        results["isGrace"] = getBoolPref("isGrace")
        results["currentDayIndex"] = getIntPref("currentDayIndex")
        results["mcheyne_currentDayIndex"] = getIntPref("mcheyne_currentDayIndex")
        results["grantHorner"] = getBoolPref("grantHorner", true)
        results["numericalDay"] = getBoolPref("numericalDay", false)
        results["calendarDay"] = getBoolPref("calendarDay", false)
        results["vacationMode"] = getBoolPref("vacation_mode")
        results["allowPartial"] = getBoolPref("allow_partial_switch")
        results["dailyNotif"] = getIntPref( "daily_time")
        results["remindNotif"] = getIntPref("remind_time")
        results["dateChecked"] = getStringPref( "dateChecked")
        results["versionNumber"] = getIntPref("versionNumber")
        results["darkMode"] = getBoolPref("darkMode", true)
        results["planType"] = getStringPref("planType", "horner")
        results["bibleVersion"] = getStringPref("bibleVersion", "esv")
        results["old_chapters_read"] = getIntPref("old_chapters_read")
        results["new_chapters_read"] = getIntPref("new_chapters_read")
        results["old_amount_read"] = getIntPref("old_amount_read")
        results["new_amount_read"] = getIntPref("new_amount_read")
        results["bible_amount_read"] = getIntPref("bible_amount_read")
        results["total_chapters_read"] = getIntPref("total_chapters_read")
        results["planSystem"] = getStringPref("planSystem")
        results["pgh_system"] = getBoolPref("pgh_system")
        results["mcheyne_system"] = getBoolPref("mcheyne_system")
        results["hasCompletedOnboarding"] = getBoolPref("hasCompletedOnboarding")
        for(book in otBooks){
            results["${book}_amount_read"] = getIntPref("${book}_amount_read")
            results["${book}_chapters_read"] = getIntPref("${book}_chapters_read")
            results["${book}_done_testament"] = getBoolPref("${book}_done_testament")
            results["${book}_done_whole"] = getBoolPref("${book}_done_whole")
            for(chapter in 1..bookChapters[book]!!){
                results["${book}_${chapter}_read"] = getBoolPref("${book}_${chapter}_read")
                results["${book}_${chapter}_amount_read"] = getIntPref("${book}_${chapter}_amount_read")
            }
        }
        for(book in ntBooks){
            results["${book}_amount_read"] = getIntPref("${book}_amount_read")
            results["${book}_chapters_read"] = getIntPref("${book}_chapters_read")
            results["${book}_done_testament"] = getBoolPref("${book}_done_testament")
            results["${book}_done_whole"] = getBoolPref("${book}_done_whole")
            for(chapter in 1..bookChapters[book]!!){
                results["${book}_${chapter}_read"] = getBoolPref("${book}_${chapter}_read")
                results["${book}_${chapter}_amount_read"] = getIntPref("${book}_${chapter}_amount_read")
            }
        }
        db.collection("main").document(user2!!.uid).set(results)
                .addOnSuccessListener { MainActivity.log("Data transferred to firestore") }
                .addOnFailureListener {e -> Log.w("PROFGRANT", "Error writing to firestore", e) }
    }
    fun updateIntPref(data: MutableMap<String, Any>, key:String, secondKey: String=""){
        val prefKey = if(secondKey == ""){
            key
        }else{
            secondKey
        }
        if(data[key] != null){
            setIntPref(prefKey, (data[key] as Long).toInt())
        }
    }
    fun updateBoolPref(data: MutableMap<String, Any>, key:String, secondKey:String = ""){
        val prefKey = if (secondKey == ""){
            key
        }else{
            secondKey
        }
        if(data[key] != null){
            setBoolPref(prefKey, data[key] as Boolean)
        }
    }
    fun updateStringPref(data: MutableMap<String, Any>, key:String, secondKey:String=""){
        val prefKey = if(secondKey == ""){
            key
        }else{
            secondKey
        }
        if(data[key] != null){
            setStringPref(prefKey, data[key] as String)
        }
    }
    fun firestoneToPreference(database: DocumentSnapshot){
        val data = database.data
        if(data != null) {
            MainActivity.log("User document exists")
            for (i in 1..10) {
                updateIntPref(data, "list${i}")
                updateIntPref(data, "list${i}Done")
                updateIntPref(data, "list${i}DoneDaily")
            }
            for (i in 1..4){
                updateIntPref(data, "mcheyne_list$i")
                updateIntPref(data, "mcheyne_list$i")
                updateIntPref(data, "mcheyne_list$i")
            }
            updateIntPref(data, "dailyStreak")
            updateIntPref(data, "currentStreak")
            updateIntPref(data, "maxStreak")
            updateBoolPref(data, "psalms")
            updateBoolPref(data, "allowPartial", "allow_partial_switch")
            updateBoolPref(data, "vacationMode", "vacation_mode")
            updateBoolPref(data, "notifications", "notif_switch")
            updateIntPref(data, "dailyNotif", "daily_time")
            updateIntPref(data, "remindNotif", "remind_time")
            updateStringPref(data, "dateChecked")
            updateBoolPref(data, "holdPlan")
            updateIntPref(data, "versionNumber")
            updateBoolPref(data, "darkMode")
            updateIntPref(data, "listsDone")
            updateStringPref(data, "planType")
            updateStringPref(data, "bibleVersion")
            updateIntPref(data, "old_chapters_read")
            updateIntPref(data, "new_chapters_read")
            updateIntPref(data, "old_amount_read")
            updateIntPref(data, "new_amount_read")
            updateIntPref(data, "bible_amount_read")
            updateIntPref(data, "total_chapters_read")
            updateIntPref(data, "mcheyne_currentDayIndex")
            updateIntPref(data, "currentDayIndex")
            updateBoolPref(data, "grantHorner")
            updateBoolPref(data, "numericalDay")
            updateBoolPref(data, "calendarDay")
            updateIntPref(data, "graceTime")
            updateBoolPref(data, "isGrace")
            updateStringPref(data, "planSystem")
            updateBoolPref(data, "pgh_system")
            updateBoolPref(data, "mcheyne_system")
            updateBoolPref(data, "hasCompletedOnboarding")
            for(book in otBooks){
                updateIntPref(data, "${book}_amount_read")
                updateIntPref(data, "${book}_chapters_read")
                updateBoolPref(data, "${book}_done_testament")
                updateBoolPref(data, "${book}_done_whole")
                for(chapter in 1..bookChapters[book]!!){
                    updateBoolPref(data, "${book}_${chapter}_read")
                    updateIntPref(data, "${book}_${chapter}_amount_read")
                }
            }
            for(book in ntBooks){
                updateIntPref(data, "${book}_amount_read")
                updateIntPref(data, "${book}_chapters_read")
                updateBoolPref(data, "${book}_done_testament")
                updateBoolPref(data, "${book}_done_whole")
                for(chapter in 1..bookChapters[book]!!){
                    updateBoolPref(data, "${book}_${chapter}_read")
                    updateIntPref(data, "${book}_${chapter}_amount_read")
                }
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