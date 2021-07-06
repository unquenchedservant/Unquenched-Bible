package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.data.ListArrays
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.getDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import kotlinx.coroutines.tasks.await
import java.util.*

class SharedPreferences(context: Context?){
    val preference = PreferenceManager.getDefaultSharedPreferences(context)
    fun getString(name:String, defaultValue:String="itsdeadjim"):String{
        return preference.getString(name, defaultValue)!!
    }
    fun setString(name:String, value:String){
        preference.edit().putString(name, value).apply()
    }
    fun getInt(name:String, defaultValue:Int=0):Int{
        return preference.getInt(name, defaultValue)
    }
    fun setInt(name:String, value:Int){
        preference.edit().putInt(name, value).apply()
    }
    fun getBoolean(name:String, defaultValue:Boolean=false):Boolean{
        return preference.getBoolean(name, defaultValue)
    }
    fun setBoolean(name:String, value:Boolean){
        preference.edit().putBoolean(name, value).apply()
    }
}
class Colors(val preferences:Preferences, val resources: Resources?, val context:Context){
    var darkMode = preferences.settings.darkMode
    var textColor: Int = if(darkMode) ContextCompat.getColor(context, R.color.unquenchedTextDark) else ContextCompat.getColor(context, R.color.unquenchedText)
    var emphColor: Int = if(darkMode) ContextCompat.getColor(context, R.color.unquenchedEmphDark) else ContextCompat.getColor(context, R.color.unquenchedEmph)
    var background: Int = if(darkMode) ContextCompat.getColor(context, R.color.backg_night) else ContextCompat.getColor(context, R.color.backg)
    var background2: Int = if(darkMode) ContextCompat.getColor(context, R.color.backg_night) else ContextCompat.getColor(context, R.color.background)
    var colorPrimary: Int = if(darkMode) ContextCompat.getColor(context, R.color.backg_night) else ContextCompat.getColor(context, R.color.colorPrimary)
    var buttonBackground: Int = if(darkMode) ContextCompat.getColor(context, R.color.buttonBackgroundDark) else ContextCompat.getColor(context, R.color.buttonBackground)
    var listSelectorDrawable: Drawable = if(darkMode) ResourcesCompat.getDrawable(resources!!, R.drawable.spinners_dark, context.theme)!! else ResourcesCompat.getDrawable(resources!!, R.drawable.spinners, context.theme)!!
    var textColor2: Int = if(darkMode) ContextCompat.getColor(context, R.color.unquenchedEmphDark) else ContextCompat.getColor(context, R.color.unquenchedOrange)
    var sAllDoneBackgroundColor: String = if(darkMode) "121212" else "FFFFFF"
    var sBackgroundColor: String = if(darkMode) "383838" else "e1e2e6"
    init{
        debugLog("darkmode is $darkMode")
    }
    fun update(){
        darkMode = preferences.settings.darkMode
        textColor = if(darkMode) ContextCompat.getColor(context, R.color.unquenchedTextDark) else ContextCompat.getColor(context, R.color.unquenchedText)
        emphColor = if(darkMode) ContextCompat.getColor(context, R.color.unquenchedEmphDark) else ContextCompat.getColor(context, R.color.unquenchedEmph)
        background= if(darkMode) ContextCompat.getColor(context, R.color.backg_night) else ContextCompat.getColor(context, R.color.backg)
        background2= if(darkMode) ContextCompat.getColor(context, R.color.backg_night) else ContextCompat.getColor(context, R.color.background)
        colorPrimary = if(darkMode) ContextCompat.getColor(context, R.color.backg_night) else ContextCompat.getColor(context, R.color.colorPrimary)
        buttonBackground = if(darkMode) ContextCompat.getColor(context, R.color.buttonBackgroundDark) else ContextCompat.getColor(context, R.color.buttonBackground)
        listSelectorDrawable = if(darkMode) ResourcesCompat.getDrawable(resources!!, R.drawable.spinners_dark, context.theme)!! else ResourcesCompat.getDrawable(resources!!, R.drawable.spinners, context.theme)!!
        textColor2 = if(darkMode) ContextCompat.getColor(context, R.color.unquenchedEmphDark) else ContextCompat.getColor(context, R.color.unquenchedOrange)
        sAllDoneBackgroundColor = if(darkMode) "121212" else "FFFFFF"
        sBackgroundColor= if(darkMode) "383838" else "e1e2e6"
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
    fun extractIntToBool(keyName:String, defaultValue:Int=0):Boolean{
        if(data[keyName] != null){
            val x = (data[keyName] as Long).toInt()
            when(x){
                0 -> return false
                1 -> return true
            }
        }
        return defaultValue == 1
    }
}
class Firestore{
    fun getFirestoreData(){
        try{
            Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    if(it.data != null){
                        App().preferences = Preferences(it.data!!, App.applicationContext().resources, App.applicationContext())
                    }
                }
        } catch (e:Exception){
            debugLog("Firestore initialization failed for $e")
            Firebase.crashlytics.log("Error getting user info")
            Firebase.crashlytics.recordException(e.cause!!)
            Firebase.crashlytics.setCustomKey("userId", Firebase.auth.currentUser?.uid!!)
        }
    }
    suspend fun updateFirestoreData(data:MutableMap<String, Any>):Boolean{
        return try{
            Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).update(data).await()
            return true
        }catch (e: Exception){
            debugLog("Firestore update failed for $e")
            Firebase.crashlytics.log("Error updating firestore")
            Firebase.crashlytics.recordException(e.cause!!)
            Firebase.crashlytics.setCustomKey("userId", Firebase.auth.currentUser?.uid!!)
            false
        }
    }
}
class ListItem(val listName: String, var listIndex:Int, var listDone:Boolean, var listDoneDaily:Boolean, val settings:Settings, val listId: Int, val listTitle:String, val preferences:Preferences, val listArray:Array<String>) {

    var listReading = getList(listIndex)
    fun resetList(soft:Boolean=false) {
        if(!checkPsalms()){
            if (settings.planType == "pgh") this.listIndex += 1
            this.listDone = false
            this.listDoneDaily = soft
            this.preferences.list.subtractListsDone(1)
        }else{
            this.listDone = soft
            this.listDoneDaily = soft
        }
    }

    fun checkPsalms():Boolean{
        return this.listName == "list6" && preferences.settings.psalms
    }

    fun setDone() {
        this.listDone = true
        this.listDoneDaily = true
    }

    fun softReset() {
        this.listIndex += 1
        this.listDone = false
    }
    fun hardReset(){
        this.listIndex = 0
        this.listDone = false
        this.listDoneDaily = false
    }
    fun getMap(incomingData:MutableMap<String, Any>): MutableMap<String, Any> {
        incomingData[this.listName] = listIndex
        incomingData["${this.listName}Done"] = if(this.listDone) 1 else 0
        incomingData["${this.listName}DoneDaily"] = if(this.listDoneDaily) 1 else 0
        return incomingData
    }

    fun markDone(single:Boolean=false){
        if(single) {
            if (!this.listDoneDaily)
                preferences.list.addListsDone(1)
            if(preferences.settings.allowPartial || preferences.list.listsDone == 10) {
                preferences.streak.dateChecked = getDate(0, false)
                preferences.streak.swapStreak()
                if(preferences.streak.dailyStreak == 1) {
                    preferences.streak.increaseStreak()
                }
                preferences.streak.dailyStreak = 1
            }
        }
        if(!this.listDoneDaily){
            this.listDoneDaily = true
        }
        this.listDone = true


    }

    fun getList(index: Int): String {
        val list = listArray
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        when (listName) {
            "list6" -> {
                when (preferences.settings.psalms) {
                    true -> {
                        return when (day) {
                            in 1..30 -> "$day, ${day + 30}, ${day + 60}, ${day + 90}, ${day + 120}"
                            else -> "Day Off"
                        }
                    }
                }
            }
        }
        when (preferences.settings.planType) {
            "horner" -> {
                return when (index) {
                    list.size -> {
                        this.listIndex = 0
                        list[0]
                    }
                    else -> {
                        this.listIndex = index
                        list[index]
                    }
                }
            }
            "numerical" -> {
                var newIndex = preferences.list.currentIndex
                while (newIndex >= list.size) {
                    newIndex -= list.size
                }
                return list[newIndex]
            }
            "calendar" -> {
                var newIndex = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 3
                val cal = Calendar.getInstance();
                if (cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365 && cal.get(Calendar.DAY_OF_YEAR) > 60) {
                    newIndex -= 1
                }
                while (newIndex >= list.size) {
                    newIndex -= list.size
                }
                return list[newIndex]
            }
            else -> return list[index]
        }
    }
}

class PGHList(val preferences:Preferences){
    private val extractor:Extractor = preferences.extractor
    val settings = preferences.settings
    val streak = preferences.streak
    var list1: ListItem  = ListItem("list1", extractor.extractInt("list1"), extractor.extractIntToBool("list1Done"), extractor.extractIntToBool("list1DoneDaily"), settings, R.array.list_1,  "The Gospels", preferences, ListArrays.pghList1)
    var list2: ListItem  = ListItem("list2", extractor.extractInt("list2"), extractor.extractIntToBool("list2Done"), extractor.extractIntToBool("list2DoneDaily"), settings, R.array.list_2, "The Pentateuch", preferences, ListArrays.pghList2)
    var list3: ListItem  = ListItem("list3", extractor.extractInt("list3"), extractor.extractIntToBool("list3Done"), extractor.extractIntToBool("list3DoneDaily"), settings, R.array.list_3, "Epistles I", preferences, ListArrays.pghList3)
    var list4: ListItem  = ListItem("list4", extractor.extractInt("list4"), extractor.extractIntToBool("list4Done"), extractor.extractIntToBool("list4DoneDaily"), settings, R.array.list_4, "Epistles II", preferences, ListArrays.pghList4)
    var list5: ListItem  = ListItem("list5", extractor.extractInt("list5"), extractor.extractIntToBool("list5Done"), extractor.extractIntToBool("list5DoneDaily"), settings, R.array.list_5, "Poetry", preferences, ListArrays.pghList5)
    var list6: ListItem  = ListItem("list6", extractor.extractInt("list6"), extractor.extractIntToBool("list6Done"), extractor.extractIntToBool("list6DoneDaily"), settings, R.array.list_6, "Psalms", preferences, ListArrays.pghList6)
    var list7: ListItem  = ListItem("list7", extractor.extractInt("list7"), extractor.extractIntToBool("list7Done"), extractor.extractIntToBool("list7DoneDaily"), settings, R.array.list_7, "Proverbs", preferences, ListArrays.pghList7)
    var list8: ListItem  = ListItem("list8", extractor.extractInt("list8"), extractor.extractIntToBool("list8Done"), extractor.extractIntToBool("list8DoneDaily"), settings, R.array.list_8, "History", preferences, ListArrays.pghList8)
    var list9: ListItem  = ListItem("list9", extractor.extractInt("list9"), extractor.extractIntToBool("list9Done"), extractor.extractIntToBool("list9DoneDaily"), settings, R.array.list_9, "Prophets", preferences, ListArrays.pghList9)
    var list10: ListItem = ListItem("list10", extractor.extractInt("list10"), extractor.extractIntToBool("list10Done"),extractor.extractIntToBool("list10DoneDaily"), settings, R.array.list_10, "Acts", preferences, ListArrays.pghList10)
    var listsDone: Int = extractor.extractInt("listsDone")
    var currentIndex: Int = extractor.extractInt("currentDayIndex")
    val list = mutableMapOf(Pair("list1", list1), Pair("list2", list2), Pair("list3", list3), Pair("list4", list4), Pair("list5", list5),
                                        Pair("list6", list6), Pair("list7", list7), Pair("list8", list8), Pair("list9", list9), Pair("list10", list10),
                                        Pair("listsDone", listsDone), Pair("currentIndex", currentIndex))

    override fun toString():String{
        val psText = if(settings.psalms) "5Psalms" else "${this.list6}"
        return "(${this.list1}, ${this.list2}, ${this.list3}, ${this.list4}, ${this.list5}, $psText, ${this.list7}, ${this.list8}, ${this.list9}, ${this.list10})"
    }
    fun markAll(){
        this.list1.markDone()
        this.list2.markDone()
        this.list3.markDone()
        this.list4.markDone()
        this.list5.markDone()
        this.list6.markDone()
        this.list7.markDone()
        this.list8.markDone()
        this.list9.markDone()
        this.list10.markDone()
        this.listsDone = 10
        if(this.streak.dailyStreak == 0 || this.streak.isGrace && this.streak.graceTime == 1){
            this.streak.swapStreak()
            if(!checkDate(this.streak.dateChecked, "current", false)){
                this.streak.increaseStreak()
            }
        }
        this.streak.dateChecked = getDate(0, false)
        this.streak.dailyStreak = 1
    }

    fun getData(incomingData:MutableMap<String, Any>):MutableMap<String, Any>{
        var data = incomingData
        data = this.list1.getMap(data)
        data = this.list2.getMap(data)
        data = this.list3.getMap(data)
        data = this.list4.getMap(data)
        data = this.list5.getMap(data)
        data = this.list6.getMap(data)
        data = this.list7.getMap(data)
        data = this.list8.getMap(data)
        data = this.list9.getMap(data)
        data = this.list10.getMap(data)
        data["listsDone"] = this.listsDone
        data["currentDayIndex"] = this.currentIndex
        return data
    }
    fun hardReset(){
        this.list1.hardReset()
        this.list2.hardReset()
        this.list3.hardReset()
        this.list4.hardReset()
        this.list5.hardReset()
        this.list6.hardReset()
        this.list7.hardReset()
        this.list8.hardReset()
        this.list9.hardReset()
        this.list10.hardReset()
        this.currentIndex = 0
        this.listsDone = 0
    }
    fun forcedReset(){
        this.list1.resetList(true)
        this.list2.resetList(true)
        this.list3.resetList(true)
        this.list4.resetList(true)
        this.list5.resetList(true)
        this.list6.resetList(true)
        this.list7.resetList(true)
        this.list8.resetList(true)
        this.list9.resetList(true)
        this.list10.resetList(true)
        if(this.settings.planType == "numerical" && (this.settings.allowPartial && this.listsDone > 0) || this.listsDone == 10) this.currentIndex += 1
        this.listsDone = if(this.settings.psalms) 1 else 0
    }
    fun resetLists(){
        if((this.settings.holdPlan && this.listsDone == 10) || !this.settings.holdPlan) {
            this.list1.resetList()
            this.list2.resetList()
            this.list3.resetList()
            this.list4.resetList()
            this.list5.resetList()
            this.list6.resetList()
            this.list7.resetList()
            this.list8.resetList()
            this.list9.resetList()
            this.list10.resetList()
            this.listsDone = 0
            if(this.settings.planType == "numerical" && (this.settings.allowPartial && this.listsDone > 0) || this.listsDone == 10 ) this.currentIndex += 1
            if(this.listsDone == 0 && !this.settings.vacation){
                if(checkDate(this.streak.dateChecked, "yesterday", false)){
                    this.streak.holdStreak()
                }else{
                    this.streak.resetGrace()
                }

            }
        }
    }
}

class McheyneList(val preferences: Preferences){
    private val extractor:Extractor = preferences.extractor
    val settings = preferences.settings
    val streak = preferences.streak
    var list1: ListItem = ListItem("mcheyneList1", extractor.extractInt("mcheyneList1"), extractor.extractIntToBool("mcheyneList1Done"), extractor.extractIntToBool("mcheyneList1DoneDaily"), settings, R.array.mcheyne_list1, "Family I", preferences, ListArrays.mcheyneList1)
    var list2: ListItem = ListItem("mcheyneList2", extractor.extractInt("mcheyneList2"), extractor.extractIntToBool("mcheyneList2Done"), extractor.extractIntToBool("mcheyneList2DoneDaily"), settings, R.array.mcheyne_list2, "Family II", preferences, ListArrays.mcheyneList2)
    var list3: ListItem = ListItem("mcheyneList3", extractor.extractInt("mcheyneList3"), extractor.extractIntToBool("mcheyneList3Done"), extractor.extractIntToBool("mcheyneList3DoneDaily"), settings, R.array.mcheyne_list3, "Secret I", preferences, ListArrays.mcheyneList3)
    var list4: ListItem = ListItem("mcheyneList4", extractor.extractInt("mcheyneList4"), extractor.extractIntToBool("mcheyneList4Done"), extractor.extractIntToBool("mcheyneList4DoneDaily"), settings, R.array.mcheyne_list4, "Secret II", preferences, ListArrays.mcheyneList4)
    var listsDone: Int = extractor.extractInt("mcheyneListsDone")
    var currentIndex: Int = extractor.extractInt("mcheyneCurrentDayIndex")
    val list = mutableMapOf(Pair("list1", list1), Pair("list2", list2), Pair("list3", list3), Pair("list4", list4),
                            Pair("listsDone", listsDone), Pair("currentIndex", currentIndex))

    override fun toString():String{
        return "(${this.list1}, ${this.list2}, ${this.list3}, ${this.list4})"
    }
    fun markAll(){
        this.list1.markDone()
        this.list2.markDone()
        this.list3.markDone()
        this.list4.markDone()
        this.listsDone = 4
        if(this.streak.dailyStreak == 0 || this.streak.isGrace && this.streak.graceTime == 1){
            this.streak.swapStreak()
            if(!checkDate(this.streak.dateChecked, "current", false)){
                this.streak.increaseStreak()
            }
        }
        this.streak.dateChecked = getDate(0, false)
        this.streak.dailyStreak = 1
    }
    fun hardReset(){
        this.list1.hardReset()
        this.list2.hardReset()
        this.list3.hardReset()
        this.list4.hardReset()
        this.currentIndex = 0
        this.listsDone = 0
    }
    fun getData(incomingData:MutableMap<String, Any>):MutableMap<String, Any>{
        var data = incomingData
        data = this.list1.getMap(data)
        data = this.list2.getMap(data)
        data = this.list3.getMap(data)
        data = this.list4.getMap(data)
        data["mcheyneListsDone"] = this.listsDone
        data["mcheyneCurrentDayIndex"] = this.currentIndex
        return data
    }
    fun forcedReset(){
        this.list1.resetList(true)
        this.list2.resetList(true)
        this.list3.resetList(true)
        this.list4.resetList(true)
        if(this.settings.planType == "numerical" && (this.settings.allowPartial && this.listsDone > 0) || this.listsDone == 4) this.currentIndex += 1
        this.listsDone = 0
    }
    fun resetLists(){
        if((this.settings.holdPlan && this.listsDone == 10) || !this.settings.holdPlan) {
            this.list1.resetList()
            this.list2.resetList()
            this.list3.resetList()
            this.list4.resetList()
            if(this.settings.planType == "numerical" && (this.settings.allowPartial && this.listsDone > 0) || this.listsDone == 4 ) this.currentIndex += 1
            if((this.settings.allowPartial && this.listsDone > 0) || this.listsDone == 4) this.streak.increaseStreak()
            if(this.listsDone == 0 && !this.settings.vacation){
                if(checkDate(this.streak.dateChecked, "yesterday", false)){
                    this.streak.holdStreak()
                }else{
                    this.streak.resetGrace()

                }
            }
            listsDone = 0
        }
    }
}
class Settings(val preferences:Preferences){
    private val extractor:Extractor = preferences.extractor
    var notifications: Boolean = extractor.extractBool("notifications")
    var psalms: Boolean = extractor.extractBool("psalms")
    var holdPlan: Boolean = extractor.extractBool("holdPlan")
    var vacation: Boolean = extractor.extractBool("vacationMode")
    var vacationOff: Boolean = extractor.extractBool("vacationOff")
    var allowPartial: Boolean = extractor.extractBool("allowPartial")
    var darkMode: Boolean = extractor.extractBool("darkMode", defaultValue=true)
    var hasCompletedOnboarding: Boolean = extractor.extractBool("hasCompletedOnboarding")
    var dailyNotif: Int = extractor.extractInt("dailyNotif")
    var remindNotif: Int = extractor.extractInt("remindNotif")
    var versionNumber: Int = extractor.extractInt("versionNumber")
    var planType: String = extractor.extractString("planType", defaultValue="horner")
    var bibleVersion: String = extractor.extractString("bibleVersion", defaultValue="niv")
    var planSystem: String = extractor.extractString("planSystem", defaultValue="pgh")
    var weekendMode: Boolean = extractor.extractBool("weekendMode", defaultValue=false)

    fun getData(data: MutableMap<String, Any>):MutableMap<String, Any>{
        data["notifications"] = this.notifications
        data["psalms"] = this.psalms
        data["holdPlan"] = this.holdPlan
        data["vacationMode"] = this.vacation
        data["allowPartial"] = this.allowPartial
        data["darkMode"] = this.darkMode
        data["hasCompletedOnboarding"] = this.hasCompletedOnboarding
        data["dailyNotif"] = this.dailyNotif
        data["remindNotif"] = this.remindNotif
        data["versionNumber"] = this.versionNumber
        data["planType"] = this.planType
        data["bibleVersion"] = this.bibleVersion
        data["planSystem"] = this.planSystem
        data["weekendMode"] = this.weekendMode
        return data
    }
}
class Streak(val preferences:Preferences){
    var currentStreak: Int = preferences.extractor.extractInt("currentStreak")
    var maxStreak: Int = preferences.extractor.extractInt("maxStreak")
    var dailyStreak: Int = preferences.extractor.extractInt("dailyStreak")
    var graceTime: Int = preferences.extractor.extractInt("graceTime")
    var isGrace: Boolean = preferences.extractor.extractBool("isGrace")
    var dateChecked: String = preferences.extractor.extractString("dateChecked")
    var holdStreak: Int = preferences.extractor.extractInt("holdStreak")

    fun increaseStreak(){
        this.currentStreak += 1
        if(this.currentStreak >= this.maxStreak){
            this.maxStreak = this.currentStreak
        }
    }

    fun swapStreak(){
        if(this.dailyStreak == 0 && this.isGrace && this.graceTime == 1){
            this.graceTime = 2
            this.isGrace = false
            this.currentStreak = this.holdStreak
            this.holdStreak = 0
        }
    }
    fun holdStreak(){
        this.isGrace = true
        this.graceTime = 0
        this.holdStreak = this.currentStreak
        this.currentStreak = 0
    }
    fun resetGrace(){
        this.graceTime = 0
        this.isGrace = false
        this.holdStreak = 0
        this.currentStreak = 0
    }
    fun getData(data: MutableMap<String, Any>):MutableMap<String, Any>{
        data["currentStreak"] = this.currentStreak
        data["maxStreak"] = this.maxStreak
        data["dailyStreak"] = this.dailyStreak
        data["graceTime"] = this.graceTime
        data["isGrace"] = this.isGrace
        data["dateChecked"] = this.dateChecked
        data["holdStreak"] = this.holdStreak
        return data
    }
}
class CurrentList(val preferences: Preferences){
    val pgh = PGHList(preferences)
    val mcheyne = McheyneList(preferences)
    val isPGH = preferences.settings.planSystem == "pgh"
    val blank = ListItem("mcheyneList", 0, false, false, preferences.settings, R.array.list_1, "None", preferences, ListArrays.mcheyneList1)
    val list1 = if(this.isPGH) this.pgh.list1 else this.mcheyne.list1
    val list2 = if(this.isPGH) this.pgh.list2 else this.mcheyne.list2
    val list3 = if(this.isPGH) this.pgh.list3 else this.mcheyne.list3
    val list4 = if(this.isPGH) this.pgh.list4 else this.mcheyne.list4
    val list5 = if(this.isPGH) this.pgh.list5 else this.blank
    val list6 = if(this.isPGH) this.pgh.list6 else this.blank
    val list7 = if(this.isPGH) this.pgh.list7 else this.blank
    val list8 = if(this.isPGH) this.pgh.list8 else this.blank
    val list9 = if(this.isPGH) this.pgh.list9 else this.blank
    val list10 = if(this.isPGH) this.pgh.list10 else this.blank
    var currentIndex = if(this.isPGH) this.pgh.currentIndex else this.mcheyne.currentIndex
    var listsDone = if(this.isPGH) this.pgh.listsDone else this.mcheyne.listsDone
    val maxDone = if(this.isPGH) 10 else 4

    fun hardReset(){
        if(this.isPGH) this.pgh.hardReset() else this.mcheyne.hardReset()
        this.setListDone(0)
    }
    fun forcedReset(){
        if(this.isPGH) this.pgh.forcedReset() else this.mcheyne.forcedReset()
        this.setListDone(if(preferences.settings.psalms) 1 else 0)
    }
    fun resetList(){
        if(this.isPGH) this.pgh.resetLists() else this.mcheyne.resetLists()
        this.setListDone(0)
    }
    fun markAll(){
        if(this.isPGH) this.pgh.markAll() else this.mcheyne.markAll()
        this.setListDone(if(this.isPGH) 10 else 4)
    }

    fun getData(data: MutableMap<String, Any>): MutableMap<String, Any> {
        return if (this.isPGH) {
            this.pgh.getData(data)
        } else {
            this.mcheyne.getData(data)
        }
    }
    fun subtractListsDone(number:Int){
        val x = this.listsDone - number
        if(this.isPGH) this.pgh.listsDone = x else this.mcheyne.listsDone = x
    }
    fun addListsDone(number:Int){
        val x = this.listsDone + number
        this.listsDone = x
        if(this.isPGH) this.pgh.listsDone = x else this.mcheyne.listsDone = x
    }
    fun setListDone(number:Int){
        this.listsDone = number
        if(this.isPGH) this.pgh.listsDone = number else this.mcheyne.listsDone = number
    }
}
class Preferences(var data:MutableMap<String, Any>, val resources:Resources? = null, val context:Context){
    val extractor = Extractor(data)
    var streak:Streak = Streak(this)
    var settings:Settings = Settings(this)
    var list = CurrentList(this)
    var colors = Colors(this, resources, context)

    fun writeToFirestore(): Task<Void> {
        return Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).update(this.getMap())
    }

    fun getMap():MutableMap<String, Any>{
        data = this.streak.getData(data)
        data = this.settings.getData(data)
        data = this.list.getData(data)
        data = this.streak.getData(data)
        return data
    }
}