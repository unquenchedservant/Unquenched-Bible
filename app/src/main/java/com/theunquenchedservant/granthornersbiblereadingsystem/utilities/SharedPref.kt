package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.os.Build
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.BuildConfig
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import timber.log.Timber
import java.util.*


object SharedPref {
    fun setStreak(){
        traceLog(file="SharedPref.kt", function="setStreak()")
        if(!checkDate(getStringPref("dateChecked"), option="both", fullMonth=false)){
            setIntPref(name="currentStreak", value=0)
        }
    }
    fun updateFirestore(data:MutableMap<String, Any>):Task<Void>{
        data["lastUpdated"] = Calendar.getInstance().time.time
        return Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).update(data)
    }
    fun extractIntPref(currentData: MutableMap<String, Any>?, keyName:String, defaultValue:Int=0):Int{
        traceLog(file="SharedPref.kt", function="extractIntPref()")
        return if(currentData?.get(keyName) != null) (currentData[keyName] as Long).toInt() else defaultValue
    }
    fun extractStringPref(currentData: MutableMap<String, Any>?, keyName:String, defaultValue:String="itsdeadjim"):String{
        traceLog(file="SharedPref.kt", function="extractStringPref()")
        return if(currentData?.get(keyName) != null) currentData[keyName] as String else defaultValue
    }
    fun extractBoolPref(currentData: MutableMap<String, Any>?, keyName:String, defaultValue: Boolean=false):Boolean{
        traceLog(file="SharedPref.kt", function="extractBoolPref()")
        return if(currentData?.get(keyName) != null) currentData[keyName] as Boolean else defaultValue
    }
    fun updateFS(name: String, value: Any) {
        traceLog(file="SharedPref.kt", function="updateFS()")
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val data = hashMapOf(
            name to value,
            "lastUpdated" to Calendar.getInstance().time.time
        )
        if (user != null) {
            db.collection("main").document(Firebase.auth.currentUser!!.uid).update(data)
                .addOnSuccessListener {
                    debugLog("successfully updated $name to $value")
                }
                .addOnFailureListener {
                    debugLog("ERROR: DID NOT UPDATE ${it.message}")
                }
                .addOnCompleteListener {
                    debugLog("SUCCESS? ${it}")
                }
        }else{
            debugLog("user was null")
        }
    }
    private fun doesExist(name:String):Boolean{
        traceLog(file="SharedPref.kt", function="doesNotExist()", message="variable: $name")
        return PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).contains(name)
    }
    fun setIntPref(name: String, value: Int, updateFS:Boolean=false): Int{
        traceLog(file="SharedPref.kt", function="setIntPref()")
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().putInt(name, value).apply()
        if(updateFS) {
            updateFS(name, value)
        }
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().putLong("lastUpdated", Calendar.getInstance().timeInMillis).apply()
        return value
    }
    private fun deletePref(name:String){
        traceLog(file="SharedPref.kt", function="deletePref()")
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().remove(name).apply()
    }
    fun increaseIntPref(name: String, value: Int, updateFS:Boolean=false): Int{
        traceLog(file="SharedPref.kt", function="increaseIntPref()")
        val newValue = getIntPref(name) + value
        setIntPref(name, value=newValue)
        if(updateFS) {
            updateFS(name, value=newValue)
        }
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().putLong("lastUpdated", Calendar.getInstance().timeInMillis).apply()
        return newValue
    }
    fun getIntPref(name: String, defaultValue: Int = 0): Int {
        traceLog(file="SharedPref.kt", function="getIntPref()")
        if(!doesExist(name)){
            debugLog("$name did not exist, updating with defaultValue")
            setIntPref(name, defaultValue, updateFS=true)
        }
        return PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).getInt(name, defaultValue)
    }
    fun getLongPref(name: String, defaultValue: Long = 0): Long {
        traceLog(file="SharedPref.kt", function="getLongPref()")
        if(!doesExist(name)){
            debugLog("$name did not exist, updating with defaultValue")
            setLongPref(name, defaultValue)
        }
        return PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).getLong(name, defaultValue)
    }
    private fun setLongPref(name:String, value:Long){
        traceLog(file="SharedPref.kt", function="setLongPref()")
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().putLong(name, value).apply()
    }
    fun setStringPref(name:String, value: String, updateFS: Boolean = false):String {
        traceLog(file="SharedPref.kt", function="setStringPref()")
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().putString(name, value).apply()
        if(updateFS) {
            updateFS(name, value)
        }
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().putLong("lastUpdated", Calendar.getInstance().timeInMillis).apply()
        return value
    }
    fun getStringPref(name:String, defaultValue: String = "itsdeadjim"): String{
        traceLog(file="SharedPref.kt", function="getStringPref()")
        if(!doesExist(name)){
            debugLog("$name did not exist, updating with defaultValue")
            setStringPref(name, defaultValue, updateFS=true)
        }
        return PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).getString(name, defaultValue)!!
    }

    fun setBoolPref(name: String, value: Boolean, updateFS:Boolean=false):Boolean{
        traceLog(file="SharedPref.kt", function="setBoolPref()")
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().putBoolean(name, value).apply()
        if(updateFS) {
            updateFS(name, value)
        }
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().putLong("lastUpdated", Calendar.getInstance().timeInMillis).apply()
        return value
    }
    fun getBoolPref(name: String, defaultValue: Boolean=false): Boolean{
        traceLog(file="SharedPref.kt", function="getBoolPref()")
        if(!doesExist(name)){
            debugLog("$name did not exist, updating with defaultValue")
            setBoolPref(name, defaultValue, updateFS=true)
        }
        return PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).getBoolean(name, defaultValue)
    }

    private val user = Firebase.auth.currentUser
    fun newUser():Task<Void>{
        val results = mutableMapOf<String?, Any?>()
        for(i in 1..10){
            results["pgh${i}Index"] = setIntPref(name="pgh${i}Index", 0)
            results["pgh${i}Done"] = setBoolPref(name="pgh${i}Done", false)
            results["pgh${i}DoneDaily"] = setBoolPref(name="pgh${i}DoneDaily", false)
        }
        for(i in 1..4){
            results["mcheyneList$i"] = setIntPref(name="mcheyne${i}Index", 0)
            results["mcheyneList${i}Done"] = setBoolPref(name="mcheyne${i}Done", false)
            results["mcheyneList${i}DoneDaily"] = setBoolPref(name="mcheyne${i}DoneDaily", false)
        }
        results["updatedPreferences"]     = setBoolPref(name="updatedPreferences", true)
        results["pghDone"]                = setIntPref(name="pghDone", 0)
        results["mcheyneDone"]            = setIntPref(name="mcheyneDone", 0)
        results["currentStreak"]          = setIntPref(name="currentStreak", 0)
        results["dailyStreak"]            = setIntPref(name="dailyStreak", 0)
        results["maxStreak"]              = setIntPref(name="maxStreak", 0)
        results["notifications"]          = setBoolPref(name="notifications", true)
        results["psalms"]                 = setBoolPref(name="psalms", false)
        results["holdPlan"]               = setBoolPref(name="holdPlan", false)
        results["graceTime"]              = setIntPref(name="graceTime", 0)
        results["isGrace"]                = setBoolPref(name="isGrace", false)
        results["pghIndex"]               = setIntPref(name="pghIndex", 0)
        results["mcheyneIndex"]           = setIntPref(name="mcheyneIndex", 0)
        results["vacationMode"]           = setBoolPref(name="vacationMode", false)
        results["weekendMode"]            = setBoolPref(name="weekendMode", false)
        results["allowPartial"]           = setBoolPref(name="allowPartial", false)
        results["dailyNotif"]             = setIntPref( name="dailyNotif", 600)
        results["remindNotif"]            = setIntPref(name="remindNotif", 1200)
        results["dateChecked"]            = setStringPref( name="dateChecked", "")
        results["dateReset"]              = setStringPref(name="dateReset", "")
        results["versionNumber"]          = setIntPref(name="versionNumber", BuildConfig.VERSION_CODE)
        results["darkMode"]               = setBoolPref(name="darkMode", true)
        results["planType"]               = setStringPref(name="planType", "")
        results["bibleVersion"]           = setStringPref(name="bibleVersion", "niv")
        results["planSystem"]             = setStringPref(name="planSystem", "")
        results["hasCompletedOnboarding"] = setBoolPref(name="hasCompletedOnboarding", false)
        results["lastUpdated"] = Calendar.getInstance().timeInMillis
        return Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).set(results)
            .addOnSuccessListener { debugLog("Data transferred to firestore") }
            .addOnFailureListener {ex -> Timber.tag("PROFGRANT").e(ex, "Error writing to firestore") }
    }
    fun preferenceToFirestore():Task<Void>{
        traceLog(file="SharedPref.kt", function="preferenceToFirestore()")
        val db = Firebase.firestore
        val results = mutableMapOf<String?, Any?>()
        for(i in 1..10){
            results["pgh${i}Index"] = getIntPref(name="pgh${i}Index")
            results["pgh${i}Done"] = getBoolPref(name="pgh${i}Done")
            results["pgh${i}DoneDaily"] = getBoolPref(name="pgh${i}DoneDaily")
        }
        for(i in 1..4){
            results["mcheyneList$i"] = getIntPref(name="mcheyne${i}Index")
            results["mcheyneList${i}Done"] = getBoolPref(name="mcheyne${i}Done")
            results["mcheyneList${i}DoneDaily"] = getBoolPref(name="mcheyne${i}DoneDaily")
        }
        results["updatedPreferences"]     = getBoolPref(name="updatedPreferences")
        results["pghDone"]                = getIntPref(name="pghDone")
        results["mcheyneDone"]            = getIntPref(name="mcheyneDone")
        results["currentStreak"]          = getIntPref(name="currentStreak")
        results["dailyStreak"]            = getIntPref(name="dailyStreak")
        results["maxStreak"]              = getIntPref(name="maxStreak")
        results["notifications"]          = getBoolPref(name="notifications")
        results["psalms"]                 = getBoolPref(name="psalms")
        results["holdPlan"]               = getBoolPref(name="holdPlan")
        results["graceTime"]              = getIntPref(name="graceTime")
        results["isGrace"]                = getBoolPref(name="isGrace")
        results["graceTime"]              = getIntPref(name="graceTime")
        results["pghIndex"]               = getIntPref(name="pghIndex")
        results["mcheyneIndex"]           = getIntPref(name="mcheyneIndex")
        results["vacationMode"]           = getBoolPref(name="vacationMode")
        results["weekendMode"]            = getBoolPref(name="weekendMode")
        results["allowPartial"]           = getBoolPref(name="allowPartial")
        results["dailyNotif"]             = getIntPref( name="dailyNotif")
        results["remindNotif"]            = getIntPref(name="remindNotif")
        results["dateChecked"]            = getStringPref( name="dateChecked")
        results["dateReset"]              = getStringPref(name="dateReset")
        results["versionNumber"]          = getIntPref(name="versionNumber")
        results["darkMode"]               = getBoolPref(name="darkMode", defaultValue=true)
        results["planType"]               = getStringPref(name="planType", defaultValue="horner")
        results["bibleVersion"]           = getStringPref(name="bibleVersion", defaultValue="esv")
        results["planSystem"]             = getStringPref(name="planSystem")
        results["hasCompletedOnboarding"] = getBoolPref(name="hasCompletedOnboarding")
        results["lastUpdated"] = Calendar.getInstance().timeInMillis
        return db.collection("main").document(Firebase.auth.currentUser!!.uid).set(results)
                .addOnSuccessListener { debugLog("Data transferred to firestore") }
                .addOnFailureListener {ex -> Timber.tag("PROFGRANT").e(ex, "Error writing to firestore") }
    }
    private fun updateIntPref(data: MutableMap<String, Any>?, key:String, secondKey: String=""):MutableMap<String, Any>{
        traceLog(file="SharedPref", function="updateIntPref()")
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
        traceLog(file="SharedPref", function="updateBoolPref()")
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
        traceLog(file="SharedPref", function="updateStringPref()")
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
    fun firestoreToPreference(data: MutableMap<String, Any>){
        traceLog(file="SharedPref", function="firestoreToPreference()")
        for (i in 1..10) {
            updateIntPref(data, key = "pgh${i}Index")
            updateBoolPref(data, key = "pgh${i}Done")
            updateBoolPref(data, key = "pgh${i}DoneDaily")
        }
        for (i in 1..4) {
            updateIntPref(data, key = "mcheyne${i}Index")
            updateBoolPref(data, key = "mcheyne${i}Done")
            updateBoolPref(data, key = "mcheyne${i}DoneDaily")
        }
        updateIntPref(data, key = "dailyStreak")
        updateIntPref(data, key = "currentStreak")
        updateIntPref(data, key = "maxStreak")
        updateBoolPref(data, key = "psalms")
        updateBoolPref(data, key = "allowPartial")
        updateBoolPref(data, key = "vacationMode")
        updateBoolPref(data, key = "weekendMode")
        updateBoolPref(data, key = "updatedPreferences")
        updateBoolPref(data, key = "notifications")
        updateIntPref(data, key = "dailyNotif")
        updateIntPref(data, key = "remindNotif")
        updateStringPref(data, key = "dateChecked")
        updateStringPref(data, key = "dateReset")
        updateBoolPref(data, key = "holdPlan")
        updateIntPref(data, key = "versionNumber")
        updateBoolPref(data, key = "darkMode")
        updateIntPref(data, key = "pghDone")
        updateIntPref(data, key = "mcheyneDone")
        updateStringPref(data, key = "planType")
        updateStringPref(data, key = "bibleVersion")
        updateIntPref(data, key = "mcheyneIndex")
        updateIntPref(data, key = "pghIndex")
        updateIntPref(data, key = "graceTime")
        updateBoolPref(data, key = "isGrace")
        updateStringPref(data, key = "planSystem")
        updateBoolPref(data, key = "hasCompletedOnboarding")
        when {
            getStringPref("planType") == "horner" -> {
                setBoolPref("horner", true)
                setBoolPref("numericalDay", false)
                setBoolPref("calendarDay", false)
            }
            getStringPref("planType")== "numerical" -> {
                setBoolPref("horner", false)
                setBoolPref("numericalDay", true)
                setBoolPref("calendarDay", false)
            }
            else -> {
                setBoolPref("horner", false)
                setBoolPref("numericalDay", false)
                setBoolPref("calendarDay", true)
            }
        }
        if (getStringPref("planSystem") == "pgh") {
            setBoolPref("mcheyneSystem", false)
            setBoolPref("pghSystem", true)
        } else {
            setBoolPref("mcheyneSystem", true)
            setBoolPref("pghSystem", false)
        }

    }

    private fun updateDone(key:String, newKey:String):Boolean{
        val newValue = when(getIntPref(key)){
            0 -> false
            1 -> true
            else -> false
        }
        deletePref(key)
        setBoolPref(newKey, newValue)
        return newValue
    }
    private fun updatePref(key:String, newKey:String=""):Int{
        val value = getIntPref(key)
        deletePref(key)
        setIntPref(newKey, value)
        return value
    }
    fun updateFirestoreAndPrefs(): Task<Void> {
        val data = mutableMapOf<String, Any>()
        data["pgh1Index"]              = updatePref("list1", "pgh1Index")
        data["pgh1Done"]               = updateDone("list1Done", "pgh1Done")
        data["pgh1DoneDaily"]          = updateDone("list1DoneDaily", "pgh1DoneDaily")
        data["pgh2Index"]              = updatePref("list2", "pgh2Index")
        data["pgh2Done"]               = updateDone("list2Done", "pgh2Done")
        data["pgh2DoneDaily"]          = updateDone("list2DoneDaily", "pgh2DoneDaily")
        data["pgh3Index"]              = updatePref("list3", "pgh3Index")
        data["pgh3Done"]               = updateDone("list3Done", "pgh3Done")
        data["pgh3DoneDaily"]          = updateDone("list3DoneDaily", "pgh3DoneDaily")
        data["pgh4Index"]              = updatePref("list4", "pgh4Index")
        data["pgh4Done"]               = updateDone("list4Done", "pgh4Done")
        data["pgh4DoneDaily"]          = updateDone("list4DoneDaily", "pgh4DoneDaily")
        data["pgh5Index"]              = updatePref("list5", "pgh5Index")
        data["pgh5Done"]               = updateDone("list5Done", "pgh5Done")
        data["pgh5DoneDaily"]          = updateDone("list5DoneDaily", "pgh5DoneDaily")
        data["pgh6Index"]              = updatePref("list6", "pgh6Index")
        data["pgh6Done"]               = updateDone("list6Done", "pgh6Done")
        data["pgh6DoneDaily"]          = updateDone("list6DoneDaily", "pgh6DoneDaily")
        data["pgh7Index"]              = updatePref("list7", "pgh7Index")
        data["pgh7Done"]               = updateDone("list7Done", "pgh7Done")
        data["pgh7DoneDaily"]          = updateDone("list7DoneDaily", "pgh7DoneDaily")
        data["pgh8Index"]              = updatePref("list8", "pgh8Index")
        data["pgh8Done"]               = updateDone("list8Done", "pgh8Done")
        data["pgh8DoneDaily"]          = updateDone("list8DoneDaily", "pgh8DoneDaily")
        data["pgh9Index"]              = updatePref("list9", "pgh9Index")
        data["pgh9Done"]               = updateDone("list9Done", "pgh9Done")
        data["pgh9DoneDaily"]          = updateDone("list9DoneDaily", "pgh9DoneDaily")
        data["pgh10Index"]             = updatePref("list10", "pgh10Index")
        data["pgh10Done"]              = updateDone("list10Done", "pgh10Done")
        data["pgh10DoneDaily"]         = updateDone("list10DoneDaily", "pgh10DoneDaily")
        data["mcheyne1Index"]          = updatePref("mcheyneList1", "mcheyne1Index")
        data["mcheyne1Done"]           = updateDone("mcheyneList1Done", "mcheyne1Done")
        data["mcheyne1DoneDaily"]      = updateDone("mcheyneList1DoneDaily", "mcheyne1DoneDaily")
        data["mcheyne2Index"]          = updatePref("mcheyneList2", "mcheyne2Index")
        data["mcheyne2Done"]           = updateDone("mcheyneList2Done", "mcheyne2Done")
        data["mcheyne2DoneDaily"]      = updateDone("mcheyneList2DoneDaily", "mcheyne2DoneDaily")
        data["mcheyne3Index"]          = updatePref("mcheyneList3", "mcheyne3Index")
        data["mcheyne3Done"]           = updateDone("mcheyneList3Done", "mcheyne3Done")
        data["mcheyne3DoneDaily"]      = updateDone("mcheyneList3DoneDaily", "mcheyne3DoneDaily")
        data["mcheyne4Index"]          = updatePref("mcheyneList4", "mcheyne4Index")
        data["mcheyne4Done"]           = updateDone("mcheyneList4Done", "mcheyne4Done")
        data["mcheyne4DoneDaily"]      = updateDone("mcheyneList4DoneDaily", "mcheyne4DoneDaily")
        data["pghDone"]                = updatePref("listsDone", "pghDone")
        data["mcheyneDone"]            = updatePref("mcheyneListsDone", "mcheyneDone")
        data["pghIndex"]               = updatePref("currentDayIndex", "pghIndex")
        data["mcheyneIndex"]           = updatePref("mcheyneCurrentDayIndex", "mcheyneIndex")
        data["dailyNotif"]             = getIntPref("dailyNotif")
        data["remindNotif"]            = getIntPref("remindNotif")
        data["versionNumber"]          = getIntPref("versionNumber")
        data["dailyStreak"]            = getIntPref("dailyStreak")
        data["currentStreak"]          = getIntPref("currentStreak")
        data["maxStreak"]              = getIntPref("maxStreak")
        data["graceTime"]              = getIntPref("graceTime")
        data["notifications"]          = getBoolPref("notifications")
        data["psalms"]                 = getBoolPref("psalms")
        data["allowPartial"]           = getBoolPref("allowPartial")
        data["hasCompletedOnboarding"] = getBoolPref("hasCompletedOnboarding")
        data["vacationMode"]           = getBoolPref("vacationMode")
        data["weekendMode"]            = getBoolPref("weekendMode")
        data["holdPlan"]               = getBoolPref("holdPlan")
        data["darkMode"]               = getBoolPref("darkMode")
        data["isGrace"]                = getBoolPref("isGrace")
        data["dateChecked"]            = getStringPref("dateChecked")
        data["planSystem"]             = getStringPref("planSystem")
        data["planType"]               = getStringPref("planType")
        data["bibleVersion"]           = getStringPref("bibleVersion")
        data["dateReset"]              = getStringPref("dateReset")
        data["updatedPreferences"]     = setBoolPref("updatedPreferences", true)
        return Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).set(data)
    }
    fun updatePrefNames(){
        traceLog(file="SharedPref.kt", function="updatePrefNames()")
        val context = App.applicationContext()
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