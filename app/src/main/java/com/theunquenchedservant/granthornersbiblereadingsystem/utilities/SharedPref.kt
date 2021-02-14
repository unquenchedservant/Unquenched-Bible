package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.checkDate


object SharedPref {
    fun setStreak(){
        if(!checkDate(getStringPref("dateChecked"), option="both", fullMonth=false)){
            setIntPref(name="currentStreak", value=0)
        }
    }
    fun extractIntPref(currentData: MutableMap<String, Any>?, keyName:String, defaultValue:Int=0):Int{
        return if(currentData?.get(keyName) != null) (currentData[keyName] as Long).toInt() else defaultValue
    }
    fun extractStringPref(currentData: MutableMap<String, Any>?, keyName:String, defaultValue:String="itsdeadjim"):String{
        return if(currentData?.get(keyName) != null) currentData[keyName] as String else defaultValue
    }
    fun extractBoolPref(currentData: MutableMap<String, Any>?, keyName:String, defaultValue: Boolean=false):Boolean{
        return if(currentData?.get(keyName) != null) currentData[keyName] as Boolean else defaultValue
    }
    fun updateFS(name: String, value: Any) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        if (user != null)
            db.collection("main").document(user.uid).update(name, value)
    }
    fun doesNotExist(name:String):Boolean{
        return !PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).contains(name)
    }
    fun setIntPref(name: String, value: Int, updateFS:Boolean=false): Int{
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().putInt(name, value).apply()
        if(updateFS) {
            updateFS(name, value)
        }
        return value
    }
    private fun deletePref(name:String){
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().remove(name).apply()
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
        if(doesNotExist(name)){
            setIntPref(name, defaultValue, updateFS=true)
        }
        return PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).getInt(name, defaultValue)
    }

    fun setStringPref(name:String, value: String, updateFS: Boolean = false):String {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().putString(name, value).apply()
        if(updateFS) {
            updateFS(name, value)
        }
        return value
    }
    fun getStringPref(name:String, defaultValue: String = "itsdeadjim"): String{
        if(doesNotExist(name)){
            setStringPref(name, defaultValue, updateFS=true)
        }
        return PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).getString(name, defaultValue)!!
    }

    fun setBoolPref(name: String, value: Boolean, updateFS:Boolean=false):Boolean{
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().putBoolean(name, value).apply()
        if(updateFS) {
            updateFS(name, value)
        }
        return value
    }
    fun getBoolPref(name: String, defaultValue: Boolean=false): Boolean{
        if(doesNotExist(name)){
            setBoolPref(name, defaultValue, updateFS=true)
        }
        return PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).getBoolean(name, defaultValue)
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
        results["hasCompletedOnboarding"] = getBoolPref(name="hasCompletedOnboarding")
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
            if(data[key] is Int) {
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
        val data = database.data
        if(data != null) {
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
            updateIntPref(data, key = "graceTime")
            updateBoolPref(data, key = "isGrace")
            updateStringPref(data, key = "planSystem")
            if(getStringPref("planType") == "horner"){
                setBoolPref("horner", true)
                setBoolPref("numericalDay", false)
                setBoolPref("calendarDay", false)
            }else if(getStringPref("planType")== "numerical"){
                setBoolPref("horner", false)
                setBoolPref("numericalDay", true)
                setBoolPref("calendarDay", false)
            }else{
                setBoolPref("horner", false)
                setBoolPref("numericalDay", false)
                setBoolPref("calendarDay", true)
            }
            if (getStringPref("planSystem") == "pgh") {
                setBoolPref("mcheyneSystem", false)
                setBoolPref("pghSystem", true)
            } else {
                setBoolPref("mcheyneSystem", true)
                setBoolPref("pghSystem", false)
            }
            updateBoolPref(data, key = "hasCompletedOnboarding")
        }
    }

    fun listNumbersReset() { App.applicationContext().getSharedPreferences("listNumbers", Context.MODE_PRIVATE).edit().clear().apply() }

    fun updatePrefNames(){
        if (PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).contains("notif_switch")) {
            setBoolPref(name="notifications", value=getBoolPref(name="notif_switch"))
            deletePref(name="notif_switch")
        }
        if (PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).contains("vacation_mode")){
            setBoolPref(name="vacationMode", value= getBoolPref(name="vacation_mode"))
            deletePref(name="vacation_mode")
        }
        if(PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).contains("allow_partial_switch")) {
            setBoolPref(name = "allowPartial", value = getBoolPref(name = "allow_partial_switch"))
            deletePref(name="allow_partial_switch")
        }
        if(PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).contains("daily_time")) {
            setIntPref(name = "dailyNotif", value = getIntPref(name = "daily_time"))
            deletePref(name="daily_time")
        }
        if(PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).contains("remind_time")) {
            setIntPref(name = "remindNotif", value = getIntPref(name = "remind_time"))
            deletePref(name="remind_time")
        }
        setBoolPref(name="updatedPref", value=true)
    }
}