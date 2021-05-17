package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractBoolPref
import timber.log.Timber

object Utilities{
    fun isPGH(planSystem:String):Boolean{
        return planSystem == "pgh"
    }
    fun getMaxDone(planSystem:String):Int{
        return if(planSystem=="pgh") 10 else 4
    }
}
class Extractor(val data:MutableMap<String, Any>){
    fun extractInt(keyName:String, defaultValue:Int=0):Int{
        return if(data[keyName] != null) (data[keyName] as Long).toInt() else defaultValue
    }
    fun extractString(keyName:String, defaultValue:String="itsdeadjim"):String{
        return if(data[keyName] != null) data[keyName] as String else defaultValue
    }
    fun extractBool(keyName:String, defaultValue:Boolean=false):Boolean{
        return if(data[keyName] != null) data[keyName] as Boolean else defaultValue
    }
}
class ListItem(val listName: String, var listIndex:Int, var listDone:Boolean, var listDoneDaily:Boolean, val settings:Settings, val listId: Int, val listTitle:String){
    fun resetList(){
        if(settings.planType == "pgh") listIndex += 1
        listDone = false
        listDoneDaily = false
    }
    fun setDone(){
        listDone = true
        listDoneDaily = true
    }
    fun softReset(){
        listIndex += 1
        listDone = false
    }
    fun getMap():MutableMap<String, Any>{
        return mutableMapOf(Pair(listName, listIndex), Pair("${listName}Done", if(listDone) 1 else 0), Pair("${listName}DoneDaily", if(listDoneDaily) 1 else 0))
    }
}

class PGHList(preferences:Preferences){
    private val extractor:Extractor = Extractor(preferences.data)
    val settings = preferences.settings
    val streak = preferences.streak
    var list1: ListItem  = ListItem("list1", extractor.extractInt("list1"), extractor.extractBool("list1Done"), extractor.extractBool("list1DoneDaily"), settings, R.array.list_1, "The Gospels")
    var list2: ListItem  = ListItem("list2", extractor.extractInt("list2"), extractor.extractBool("list2Done"), extractor.extractBool("list2DoneDaily"), settings, R.array.list_2, "The Pentateuch")
    var list3: ListItem  = ListItem("list3", extractor.extractInt("list3"), extractor.extractBool("list3Done"), extractor.extractBool("list3DoneDaily"), settings, R.array.list_3, "Epistles I")
    var list4: ListItem  = ListItem("list4", extractor.extractInt("list4"), extractor.extractBool("list4Done"), extractor.extractBool("list4DoneDaily"), settings, R.array.list_4, "Epistles II")
    var list5: ListItem  = ListItem("list5", extractor.extractInt("list5"), extractor.extractBool("list5Done"), extractor.extractBool("list5DoneDaily"), settings, R.array.list_5, "Poetry")
    var list6: ListItem  = ListItem("list6", extractor.extractInt("list6"), extractor.extractBool("list6Done"), extractor.extractBool("list6DoneDaily"), settings, R.array.list_6, "Psalms")
    var list7: ListItem  = ListItem("list7", extractor.extractInt("list7"), extractor.extractBool("list7Done"), extractor.extractBool("list7DoneDaily"), settings, R.array.list_7, "Proverbs")
    var list8: ListItem  = ListItem("list8", extractor.extractInt("list8"), extractor.extractBool("list8Done"), extractor.extractBool("list8DoneDaily"), settings, R.array.list_8, "History")
    var list9: ListItem  = ListItem("list9", extractor.extractInt("list9"), extractor.extractBool("list9Done"), extractor.extractBool("list9DoneDaily"), settings, R.array.list_9, "Prophets")
    var list10: ListItem = ListItem("list10", extractor.extractInt("list10"), extractor.extractBool("list10Done"), extractor.extractBool("list10DoneDaily"), settings, R.array.list_10, "Acts")
    var listsDone: Int = extractor.extractInt("listsDone")
    var currentIndex: Int = extractor.extractInt("currentDayIndex")
    val list = mutableMapOf(Pair("list1", list1), Pair("list2", list2), Pair("list3", list3), Pair("list4", list4), Pair("list5", list5),
                                        Pair("list6", list6), Pair("list7", list7), Pair("list8", list8), Pair("list9", list9), Pair("list10", list10),
                                        Pair("listsDone", listsDone), Pair("currentIndex", currentIndex))
    val holdPlan = settings.holdPlan
    val planType = settings.planType
    val allowPartial = settings.allowPartial

    fun getData(data:MutableMap<String, Any>):MutableMap<String, Any>{
        data["list1"] = list1.listIndex
        data["list1Done"] = list1.listDone
        data["list1DoneDaily"] = list1.listDoneDaily
        data["list2"] = list2.listIndex
        data["list2Done"] = list2.listDone
        data["list2DoneDaily"] = list2.listDoneDaily
        data["list3"] = list3.listIndex
        data["list3Done"] = list3.listDone
        data["list3DoneDaily"] = list3.listDoneDaily
        data["list4"] = list4.listIndex
        data["list4Done"] = list4.listDone
        data["list4DoneDaily"] = list4.listDoneDaily
        data["list5"] = list5.listIndex
        data["list5Done"] = list5.listDone
        data["list5DoneDaily"] = list5.listDoneDaily
        data["list6"] = list6.listIndex
        data["list6Done"] = list6.listDone
        data["list6DoneDaily"] = list6.listDoneDaily
        data["list7"] = list7.listIndex
        data["list7Done"] = list7.listDone
        data["list7DoneDaily"] = list7.listDoneDaily
        data["list8"] = list8.listIndex
        data["list8Done"] = list8.listDone
        data["list8DoneDaily"] = list8.listDoneDaily
        data["list9"] = list9.listIndex
        data["list9Done"] = list9.listDone
        data["list9DoneDaily"] = list9.listDoneDaily
        data["list10"] = list10.listIndex
        data["list10Done"] = list10.listDone
        data["list10DoneDaily"] = list10.listDoneDaily
        data["listsDone"] = listsDone
        data["currentDayIndex"] = currentIndex
        return data
    }

    fun resetLists(){
        if((holdPlan && listsDone == 10) || !holdPlan) {
            list1.resetList()
            list2.resetList()
            list3.resetList()
            list4.resetList()
            list5.resetList()
            list6.resetList()
            list7.resetList()
            list8.resetList()
            list9.resetList()
            list10.resetList()
            listsDone = 0
            if(planType == "numerical" && (allowPartial && listsDone > 0) || listsDone == 10 ) currentIndex += 1
            if((allowPartial && listsDone > 0) || listsDone == 10) streak.currentStreak += 1
            if(streak.currentStreak > streak.maxStreak) streak.maxStreak = streak.currentStreak
            if(listsDone == 0 && !settings.vacation){
                if(checkDate(streak.dateChecked, "yesterday", false)){
                    streak.isGrace = true
                    streak.graceTime = 0
                    streak.holdStreak = streak.currentStreak
                }else{
                    streak.graceTime = 0
                    streak.isGrace = false
                    streak.holdStreak = 0
                }
            }
        }
    }
}

class McheyneList(preferences: Preferences){
    private val extractor:Extractor = Extractor(preferences.data)
    val settings = preferences.settings
    val streak = preferences.streak
    var list1: ListItem = ListItem("mcheyneList1", extractor.extractInt("mcheyneList1"), extractor.extractBool("mcheyneList1Done"), extractor.extractBool("mcheyneList1DoneDaily"), settings, R.array.mcheyne_list1, "Family I")
    var list2: ListItem = ListItem("mcheyneList2", extractor.extractInt("mcheyneList2"), extractor.extractBool("mcheyneList2Done"), extractor.extractBool("mcheyneList2DoneDaily"), settings, R.array.mcheyne_list2, "Family II")
    var list3: ListItem = ListItem("mcheyneList3", extractor.extractInt("mcheyneList3"), extractor.extractBool("mcheyneList3Done"), extractor.extractBool("mcheyneList3DoneDaily"), settings, R.array.mcheyne_list3, "Secret I")
    var list4: ListItem = ListItem("mcheyneList4", extractor.extractInt("mcheyneList4"), extractor.extractBool("mcheyneList4Done"), extractor.extractBool("mcheyneList4DoneDaily"), settings, R.array.mcheyne_list4, "Secret II")
    var listsDone: Int = extractor.extractInt("mcheyneListsDone")
    var currentIndex: Int = extractor.extractInt("mcheyneCurrentDayIndex")
    val list = mutableMapOf(Pair("list1", list1), Pair("list2", list2), Pair("list3", list3), Pair("list4", list4),
                            Pair("listsDone", listsDone), Pair("currentIndex", currentIndex))
    val holdPlan = settings.holdPlan
    val planType = settings.planType
    val allowPartial = settings.allowPartial

    fun getData(data:MutableMap<String, Any>):MutableMap<String, Any>{
        data["mcheyneList1"] = list1.listIndex
        data["mcheyneList1Done"] = list1.listDone
        data["mcheyneList1DoneDaily"] = list1.listDoneDaily
        data["mcheyneList2"] = list2.listIndex
        data["mcheyneList2Done"] = list2.listDone
        data["mcheyneList2DoneDaily"] = list2.listDoneDaily
        data["mcheyneList3"] = list3.listIndex
        data["mcheyneList3Done"] = list3.listDone
        data["mcheyneList3DoneDaily"] = list3.listDoneDaily
        data["mcheyneList4"] = list4.listIndex
        data["mcheyneList4Done"] = list4.listDone
        data["mcheyneList4DoneDaily"] = list4.listDoneDaily
        data["mcheyneListsDone"] = listsDone
        data["mcheyneCurrentDayIndex"] = currentIndex
        return data
    }

    fun resetLists(){
        if((holdPlan && listsDone == 10) || !holdPlan) {
            list1.resetList()
            list2.resetList()
            list3.resetList()
            list4.resetList()
            if(planType == "numerical" && (allowPartial && listsDone > 0) || listsDone == 4 ) currentIndex += 1
            if((allowPartial && listsDone > 0) || listsDone == 4) streak.currentStreak += 1
            if(streak.currentStreak > streak.maxStreak) streak.maxStreak = streak.currentStreak
            if(listsDone == 0 && !settings.vacation){
                if(checkDate(streak.dateChecked, "yesterday", false)){
                    streak.isGrace = true
                    streak.graceTime = 0
                    streak.holdStreak = streak.currentStreak
                }else{
                    streak.graceTime = 0
                    streak.isGrace = false
                    streak.holdStreak = 0
                }
            }
            listsDone = 0
        }
    }
}
class Settings(preferences:Preferences){
    private val extractor:Extractor = Extractor(preferences.data)
    var notifications: Boolean = extractor.extractBool("notifications")
    var psalms: Boolean = extractor.extractBool("psalms")
    var holdPlan: Boolean = extractor.extractBool("holdPlan")
    var vacation: Boolean = extractor.extractBool("vacationMode")
    var allowPartial: Boolean = extractor.extractBool("allowPartial")
    var darkMode: Boolean = extractor.extractBool("darkMode", defaultValue=true)
    var hasCompletedOnboarding: Boolean = extractor.extractBool("hasCompletedOnboarding")
    var dailyNotif: Int = extractor.extractInt("dailyNotif")
    var remindNotif: Int = extractor.extractInt("remindNotif")
    var versionNumber: Int = extractor.extractInt("versionNumber")
    var planType: String = extractor.extractString("planType", defaultValue="horner")
    var bibleVersion: String = extractor.extractString("bibleVersion", defaultValue="niv")
    var planSystem: String = extractor.extractString("planSystem", defaultValue="pgh")

    fun getData(data: MutableMap<String, Any>):MutableMap<String, Any>{
        data["notifications"] = notifications
        data["psalms"] = psalms
        data["holdPlan"] = holdPlan
        data["vacationMode"] = vacation
        data["allowPartial"] = allowPartial
        data["darkMode"] = darkMode
        data["hasCompletedOnboarding"] = hasCompletedOnboarding
        data["dailyNotif"] = dailyNotif
        data["remindNotif"] = remindNotif
        data["versionNumber"] = versionNumber
        data["planType"] = planType
        data["bibleVersion"] = bibleVersion
        data["planSystem"] = planSystem
        return data
    }
}
class Streak(preferences:Preferences){
    private val extractor:Extractor = Extractor(preferences.data)
    var currentStreak: Int = extractor.extractInt("currentStreak")
    var maxStreak: Int = extractor.extractInt("maxStreak")
    var dailyStreak: Int = extractor.extractInt("dailyStreak")
    var graceTime: Int = extractor.extractInt("graceTime")
    var isGrace: Boolean = extractor.extractBool("isGrace")
    var dateChecked: String = extractor.extractString("dateChecked")
    var holdStreak: Int = extractor.extractInt("holdStreak")

    fun getData():MutableMap<String, Any>{
        val data = mutableMapOf<String, Any>()
        data["currentStreak"] = currentStreak
        data["maxStreak"] = maxStreak
        data["dailyStreak"] = dailyStreak
        data["graceTime"] = graceTime
        data["isGrace"] = isGrace
        data["dateChecked"] = dateChecked
        data["holdStreak"] = holdStreak
        return data
    }
}
class CurrentList(preferences: Preferences){
    val pgh = PGHList(preferences)
    val mcheyne = McheyneList(preferences)
    val isPGH = preferences.settings.planSystem == "pgh"
    val settings = App().preferences!!.settings
    val blank = ListItem("mcheyneList", 0, false, false, settings, R.array.list_1, "None")
    val list1 = if(isPGH) pgh.list1 else mcheyne.list1
    val list2 = if(isPGH) pgh.list2 else mcheyne.list2
    val list3 = if(isPGH) pgh.list3 else mcheyne.list3
    val list4 = if(isPGH) pgh.list4 else mcheyne.list4
    val list5 = if(isPGH) pgh.list5 else blank
    val list6 = if(isPGH) pgh.list6 else blank
    val list7 = if(isPGH) pgh.list7 else blank
    val list8 = if(isPGH) pgh.list8 else blank
    val list9 = if(isPGH) pgh.list9 else blank
    val list10 = if(isPGH) pgh.list10 else blank
    val currentIndex = if(isPGH) pgh.currentIndex else mcheyne.currentIndex
    var listsDone = if(isPGH) pgh.listsDone else mcheyne.listsDone
    val maxDone = if(isPGH) 10 else 4
    fun resetList(){
        if(isPGH) pgh.resetLists() else mcheyne.resetLists()
    }
    fun getData(data:MutableMap<String, Any>):MutableMap<String, Any>{
        val updateData = if(isPGH){
            pgh.getData(data)
        }else{
            mcheyne.getData(data)
        }
        return updateData
    }
}
class Preferences(val data:MutableMap<String, Any>){
    var streak:Streak = Streak(this)
    var settings:Settings = Settings(this)
    var listsDone = if(settings.planSystem == "pgh") PGHList(this).listsDone else McheyneList(this).listsDone
    var list = CurrentList(this)

    fun updateFS(){
        var updateData: MutableMap<String, Any> = streak.getData()
        updateData = settings.getData(updateData)
        updateData = list.getData(updateData)
        Firebase.firestore.collection("main").document(Firebase.auth.uid!!).update(updateData)
            .addOnSuccessListener {
                debugLog("Update firestore successful")
            }
            .addOnFailureListener {
                debugLog("Update firestore failed with $it")
            }
    }
}
object SharedPref {
    fun setStreak(){
        traceLog(file="SharedPref.kt", function="setStreak()")
        if(!checkDate(getStringPref("dateChecked"), option="both", fullMonth=false)){
            setIntPref(name="currentStreak", value=0)
        }
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
        if (user != null)
            db.collection("main").document(user.uid).update(name, value)
    }
    fun doesNotExist(name:String):Boolean{
        traceLog(file="SharedPref.kt", function="doesNotExist()", message="variable: $name")
        return !PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).contains(name)
    }
    fun setIntPref(name: String, value: Int, updateFS:Boolean=false): Int{
        traceLog(file="SharedPref.kt", function="setIntPref()")
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().putInt(name, value).apply()
        if(updateFS) {
            updateFS(name, value)
        }
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
        return newValue
    }
    fun getIntPref(name: String, defaultValue: Int = 0): Int {
        traceLog(file="SharedPref.kt", function="getIntPref()")
        if(doesNotExist(name)){
            setIntPref(name, defaultValue, updateFS=true)
        }
        return PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).getInt(name, defaultValue)
    }

    fun setStringPref(name:String, value: String, updateFS: Boolean = false):String {
        traceLog(file="SharedPref.kt", function="setStringPref()")
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).edit().putString(name, value).apply()
        if(updateFS) {
            updateFS(name, value)
        }
        return value
    }
    fun getStringPref(name:String, defaultValue: String = "itsdeadjim"): String{
        traceLog(file="SharedPref.kt", function="getStringPref()")
        if(doesNotExist(name)){
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
        return value
    }
    fun getBoolPref(name: String, defaultValue: Boolean=false): Boolean{
        traceLog(file="SharedPref.kt", function="getBoolPref()")
        if(doesNotExist(name)){
            setBoolPref(name, defaultValue, updateFS=true)
        }
        return PreferenceManager.getDefaultSharedPreferences(App.applicationContext()).getBoolean(name, defaultValue)
    }

    private val user = Firebase.auth.currentUser

    fun preferenceToFirestore(){
        traceLog(file="SharedPref.kt", function="preferenceToFirestore()")
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
        results["planSystem"] = getStringPref(name="planSystem")
        results["hasCompletedOnboarding"] = getBoolPref(name="hasCompletedOnboarding")
        db.collection("main").document(user2!!.uid).set(results)
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
    fun firestoreToPreference(database: DocumentSnapshot){
        traceLog(file="SharedPref", function="firestoreToPreference()")
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

    fun listNumbersReset() { traceLog(file = "SharedPref.kt", function = "listNumbersReset()"); App.applicationContext().getSharedPreferences("listNumbers", Context.MODE_PRIVATE).edit().clear().apply() }

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