package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.ALL_BOOKS
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_CHAPTERS
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.checkDate


object SharedPref {
    val context = App.applicationContext()
    fun setStreak(){
        if(!checkDate(option="both", fullMonth=false)){
            setIntPref(name="currentStreak", value=0)
        }
    }

        val context = App.applicationContext()
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
    fun updateFS(name: String, value: Any) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        if (user != null)
            db.collection("main").document(user.uid).update(name, value)
    }

    fun setIntPref(name: String, value: Int, updateFS:Boolean=false): Int{
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(name, value).apply()
        if(updateFS) {
            updateFS(name, value)
        }
        return value
    }
    private fun deletePref(name:String){
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(name).apply()
    }
    fun increaseIntPref(name: String, value: Int, updateFS:Boolean=false): Int{
        val newValue = getIntPref(name) + value
        setIntPref(name, value=newValue)
        if(updateFS) {
            updateFS(name, value=newValue)
        }
        return newValue
    }
    fun getIntPref(name: String, defaultValue: Int = 0): Int {
        return getPref().getInt(name, defaultValue)
    }
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(name, defaultValue)

    fun setStringPref(name:String, value: String, updateFS: Boolean = false):String {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(name, value).apply()
        if(updateFS) {
            updateFS(name, value)
        }
        return value
    }
    fun getStringPref(name:String, defaultValue: String = "itsdeadjim"): String{
        return getPref().getString(name, defaultValue)!!
    }
        return PreferenceManager.getDefaultSharedPreferences(context).getString(name, defaultValue)!!

    fun setBoolPref(name: String, value: Boolean, updateFS:Boolean=false):Boolean{
        getPref().edit().putBoolean(name, value).apply()
        if(updateFS) {
            updateFS(name, value)
        }
        return value
    }
    fun getBoolPref(name: String, defaultValue: Boolean=false): Boolean{
        return getPref().getBoolean(name, defaultValue)
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(name, defaultValue)
    }

    private val user = Firebase.auth.currentUser

    fun preferenceToFirestore(){
        val db = Firebase.firestore
        val user2 = Firebase.auth.currentUser
        val results = mutableMapOf<String?, Any?>()
        for(i in 1..10){
            results["list$i"] = getIntPref(name="list$i")
            results["list${i}Done"] = getIntPref(name="list${i}Done")
            results["list${i}DoneDaily"] = getIntPref(name="list${i}DoneDaily")
        }
        for(i in 1..4){
            results["mcheyneList$i"] = getIntPref(name="mcheyneList$i")
            results["mcheyneList${i}Done"] = getIntPref(name="mcheyneList${i}Done")
            results["mcheyneList${i}DoneDaily"] = getIntPref(name="mcheyneList${i}DoneDaily")
        }
        results["listsDone"] = getIntPref(name="listsDone")
        results["mcheyneListsDone"] = getIntPref(name="mcheyneListsDone")
        results["currentStreak"] = getIntPref(name="currentStreak")
        results["dailyStreak"] = getIntPref(name="dailyStreak")
        results["maxStreak"] = getIntPref(name="maxStreak")
        results["notifications"] = getBoolPref(name="notifications")
        results["psalms"] = getBoolPref(name="psalms")
        results["holdPlan"] = getBoolPref(name="holdPlan")
        results["graceTime"] = getIntPref(name="graceTime")
        results["isGrace"] = getBoolPref(name="isGrace")
        results["currentDayIndex"] = getIntPref(name="currentDayIndex")
        results["mcheyneCurrentDayIndex"] = getIntPref(name="mcheyneCurrentDayIndex")
        results["horner"] = getBoolPref(name="grantHorner", defaultValue=true)
        results["numericalDay"] = getBoolPref(name="numericalDay", defaultValue=false)
        results["calendarDay"] = getBoolPref(name="calendarDay", defaultValue=false)
        results["vacationMode"] = getBoolPref(name="vacationMode")
        results["allowPartial"] = getBoolPref(name="allowPartial")
        results["dailyNotif"] = getIntPref( name="dailyNotif")
        results["remindNotif"] = getIntPref(name="remindNotif")
        results["dateChecked"] = getStringPref( name="dateChecked")
        results["versionNumber"] = getIntPref(name="versionNumber")
        results["darkMode"] = getBoolPref(name="darkMode", defaultValue=true)
        results["planType"] = getStringPref(name="planType", defaultValue="horner")
        results["bibleVersion"] = getStringPref(name="bibleVersion", defaultValue="esv")
        results["oldChaptersRead"] = getIntPref(name="oldChaptersRead")
        results["newChaptersRead"] = getIntPref(name="newChaptersRead")
        results["oldAmountRead"] = getIntPref(name="oldAmountRead")
        results["newAmountRead"] = getIntPref(name="newAmountRead")
        results["bibleAmountRead"] = getIntPref(name="bibleAmountRead")
        results["totalChaptersRead"] = getIntPref(name="totalChaptersRead")
        results["planSystem"] = getStringPref(name="planSystem")
        results["pghSystem"] = getBoolPref(name="pghSystem")
        results["mcheyneSystem"] = getBoolPref(name="mcheyneSystem")
        results["hasCompletedOnboarding"] = getBoolPref(name="hasCompletedOnboarding")
        for(book in ALL_BOOKS) {
            results["${book}AmountRead"] = getIntPref(name = "${book}AmountRead")
            results["${book}ChaptersRead"] = getIntPref(name = "${book}ChaptersRead")
            results["${book}DoneTestament"] = getBoolPref(name = "${book}DoneTestament")
            results["${book}DoneWhole"] = getBoolPref(name = "${book}DoneWhole")
            for (chapter in 1..(BOOK_CHAPTERS[book] ?: error(""))) {
                results["${book}${chapter}Read"] = getBoolPref(name = "${book}${chapter}Read")
                results["${book}${chapter}AmountRead"] = getIntPref(name = "${book}${chapter}AmountRead")
            }
        }
        db.collection("main").document(user2!!.uid).set(results)
                .addOnSuccessListener {log("Data transferred to firestore") }
                .addOnFailureListener {e -> Log.w("PROFGRANT", "Error writing to firestore", e) }
    }
    private fun updateIntPref(data: MutableMap<String, Any>?, key:String, secondKey: String=""):MutableMap<String, Any>{
        val prefKey = if(secondKey == ""){
            key
        }else{
            secondKey
        }
        return if(data!![key] != null){
            if(data[key] is Integer) {
                setIntPref(prefKey, data[key] as Int)
            }else{
                setIntPref(prefKey, (data[key] as Long).toInt())
            }
            data
        }else{
            data[key] = setIntPref(prefKey, 0)
            data
        }
    }
    private fun updateBoolPref(data: MutableMap<String, Any>?, key:String, secondKey:String = ""){
        val prefKey = if (secondKey == ""){
            key
        }else{
            secondKey
        }
        if(data!![key] != null){
            setBoolPref(prefKey, data[key] as Boolean)
        }else{
            data[key] = setBoolPref(prefKey, false)
        }
    }
    private fun updateStringPref(data: MutableMap<String, Any>?, key:String, secondKey:String=""){
        val prefKey = if(secondKey == ""){
            key
        }else{
            secondKey
        }
        if(data!![key] != null){
            setStringPref(prefKey, data[key] as String)
        }else{
            data[key] = setStringPref(prefKey, "itsdeadjim")
        }
    }
    fun firestoreToPreference(database: DocumentSnapshot){
        var data = database.data
        if(data != null) {
            log("User document exists")
            for (i in 1..10) {
                updateIntPref(data, key = "list${i}")
                updateIntPref(data, key = "list${i}Done")
                updateIntPref(data, key = "list${i}DoneDaily")
            }
            for (i in 1..4) {
                updateIntPref(data, key = "mcheyneList$i")
                updateIntPref(data, key = "mcheyneList${i}Done")
                updateIntPref(data, key = "mcheyneList${i}DoneDaily")
            }
            updateIntPref(data, key = "dailyStreak")
            updateIntPref(data, key = "currentStreak")
            updateIntPref(data, key = "maxStreak")
            updateBoolPref(data, key = "psalms")
            updateBoolPref(data, key = "allowPartial")
            updateBoolPref(data, key = "vacationMode")
            updateBoolPref(data, key = "notifications")
            updateIntPref(data, key = "dailyNotif")
            updateIntPref(data, key = "remindNotif")
            updateStringPref(data, key = "dateChecked")
            updateBoolPref(data, key = "holdPlan")
            updateIntPref(data, key = "versionNumber")
            updateBoolPref(data, key = "darkMode")
            updateIntPref(data, key = "listsDone")
            updateIntPref(data, key = "mcheyneListsDone")
            updateStringPref(data, key = "planType")
            updateStringPref(data, key = "bibleVersion")
            updateIntPref(data, key = "oldChaptersRead")
            updateIntPref(data, key = "newChaptersRead")
            updateIntPref(data, key = "oldAmountRead")
            updateIntPref(data, key = "newAmountRead")
            updateIntPref(data, key = "bibleAmountRead")
            updateIntPref(data, key = "totalChaptersRead")
            updateIntPref(data, key = "mcheyneCurrentDayIndex")
            updateIntPref(data, key = "currentDayIndex")
            updateBoolPref(data, key = "horner")
            updateBoolPref(data, key = "numericalDay")
            updateBoolPref(data, key = "calendarDay")
            updateIntPref(data, key = "graceTime")
            updateBoolPref(data, key = "isGrace")
            updateStringPref(data, key = "planSystem")
            if (getStringPref("planSystem") == "pgh") {
                setBoolPref("mcheyneSystem", false)
                setBoolPref("pghSystem", true)
            } else {
                setBoolPref("mcheyneSystem", true)
                setBoolPref("pghSystem", false)
            }
            updateBoolPref(data, key = "hasCompletedOnboarding")
            for (book in ALL_BOOKS) {
                updateIntPref(data, key = "${book}AmountRead")
                updateIntPref(data, key = "${book}ChaptersRead")
                updateBoolPref(data, key = "${book}DoneTestament")
                updateBoolPref(data, key = "${book}DoneWhole")
                for (chapter in 1..(BOOK_CHAPTERS[book] ?: error(""))) {
                    updateBoolPref(data, key = "${book}${chapter}Read")
                    updateIntPref(data, key = "${book}${chapter}AmountRead")
                }
            }
        }
    }

    fun listNumbersReset() { App.applicationContext().getSharedPreferences("listNumbers", Context.MODE_PRIVATE).edit().clear().apply() }

    fun updatePrefNames(){
        if (PreferenceManager.getDefaultSharedPreferences(context).contains("notif_switch")) {
            setBoolPref(name="notifications", value=getBoolPref(name="notif_switch"))
            deletePref(name="notif_switch")
        }
        if (PreferenceManager.getDefaultSharedPreferences(context).contains("vacation_mode")){
            setBoolPref(name="vacationMode", value= getBoolPref(name="vacation_mode"))
            deletePref(name="vacation_mode")
        }
        if(PreferenceManager.getDefaultSharedPreferences(context).contains("allow_partial_switch")) {
            setBoolPref(name = "allowPartial", value = getBoolPref(name = "allow_partial_switch"))
            deletePref(name="allow_partial_switch")
        }
        if(PreferenceManager.getDefaultSharedPreferences(context).contains("daily_time")) {
            setIntPref(name = "dailyNotif", value = getIntPref(name = "daily_time"))
            deletePref(name="daily_time")
        }
        if(PreferenceManager.getDefaultSharedPreferences(context).contains("remind_time")) {
            setIntPref(name = "remindNotif", value = getIntPref(name = "remind_time"))
            deletePref(name="remind_time")
        }
        setBoolPref(name="updatedPref", value=true)
    }
}