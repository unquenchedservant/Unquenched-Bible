package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.app.AlertDialog
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.bookChapters
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.bookNames
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.bookNamesCoded
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.ntBooks
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.otBooks
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.getDate
import java.util.*


object Marker {
    private val isLogged = FirebaseAuth.getInstance().currentUser
    private fun getListId(listName: String) : Int{
        return when(listName){
            "list1"-> R.array.list_1
            "list2"-> R.array.list_2
            "list3"-> R.array.list_3
            "list4"-> R.array.list_4
            "list5"-> R.array.list_5
            "list6"-> R.array.list_6
            "list7"-> R.array.list_7
            "list8"-> R.array.list_8
            "list9"-> R.array.list_9
            "list10"-> R.array.list_10
            "mcheyne_list1"->R.array.mcheyne_list1
            "mcheyne_list2"->R.array.mcheyne_list2
            "mcheyne_list3"->R.array.mcheyne_list3
            "mcheyne_list4"->R.array.mcheyne_list4
            else-> 0
        }
    }
    private fun getTestament(book:String=""): String{
       return when(book) {
           in otBooks -> "old"
           in ntBooks -> "new"
           else -> "old"
       }
    }
    private fun bibleAlertBuilder(type:String, name:String){
        val alert = AlertDialog.Builder(App.applicationContext())
        alert.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val message = when(type) {
            "book" -> "You just finished $name, keep it up!"
            "testament"-> "You just read through all of the $name Testament! Wow!"
            "bible"-> "You've officially read through the entire Bible using your reading system! Congratulations!"
            else->""
        }
        val title = when(type){
            "book"->"$name Finished!"
            "testament"->"$name Testament Finished!"
            "bible"->"Whole Bible Finished!"
            else->""
        }
        alert.setTitle(title)
        alert.setMessage(message)
        alert.show()
    }
    private fun updateStatistics(codedBook: String, bookChaps: Int, testament: String, testamentChapters: Int, chapter: Int){
        val updateValues = mutableMapOf<String, Any>()
        val bookName = bookNames[codedBook]
        if (getIntPref("${codedBook}_chapters_read") == bookChaps){
            updateValues["${codedBook}_chapters_read"] = 0
            setIntPref("${codedBook}_chapters_read", 0)
            setIntPref("${codedBook}_amount_read", getIntPref("${codedBook}_amount_read") + 1)
            updateValues["${codedBook}_amount_read"] = getIntPref("${codedBook}_amount_read")
            for (i in 1..bookChaps){
                updateValues["${codedBook}_${i}_read"] = false
                setBoolPref("${codedBook}_${i}_read", false)
            }
            if(!getBoolPref("${codedBook}_done_testament")) {
                updateValues["${codedBook}_done_testament"] = true
                setBoolPref("${codedBook}_done_testament", true)
                if (getIntPref("${testament}_chapters_read") == testamentChapters) {
                    updateValues["${testament}_chapters_read"]
                    setIntPref("${testament}_chapters_read", 0)
                    for(item in bookNames){
                        updateValues["${item}_done_testament"] = false
                        setBoolPref("${item}_done_testament", false)
                    }
                    setIntPref("${testament}_amount_read", getIntPref("${testament}_amount_read") + 1)
                    updateValues["${testament}_amount_read"] = getIntPref("${testament}_amount_read")
                }
            }
            if(!getBoolPref("${codedBook}_done_whole")){
                updateValues["${codedBook}_done_whole"] = true
                setBoolPref("${codedBook}_done_whole", true)
                if(getIntPref("total_chapters_read") == 1189){
                    updateValues["total_chapters_read"] = 0
                    setIntPref("total_chapters_read", 0)
                    for(item in bookNames){
                        updateValues["${item}_done_whole"] = false
                        setBoolPref("${item}_done_whole", false)
                    }
                    setIntPref("bible_amount_read", getIntPref("bible_amount_read") + 1)
                    updateValues["bible_amount_read"] = getIntPref("bible_amount_read")
                }
            }
        }
        setIntPref("${codedBook}_chapters_read", getIntPref("${codedBook}_chapters_read") + 1)
        setBoolPref("${codedBook}_${chapter}_read", true)
        setIntPref("${codedBook}_${chapter}_amount_read", getIntPref("${codedBook}_${chapter}_amount_read") + 1)
        if(!getBoolPref("${codedBook}_done_testament")){
            setIntPref("${testament}_chapters_read", getIntPref("${testament}_chapters_read") + 1)
            updateValues["${testament}_chapters_read"] = getIntPref("${testament}_chapters_read")
        }
        if(!getBoolPref("${codedBook}_done_whole")){
            updateValues["totalChaptersRead"] = increaseIntPref(name="total_chapters_read", value=1)
        }
        if (getIntPref("${codedBook}_chapters_read") == bookChaps){
            bibleAlertBuilder("book", bookName!!)
        }
        if (getIntPref("${testament}_chapters_read") == testamentChapters){
            bibleAlertBuilder("testament", testament.capitalize(Locale.ROOT))
        }
        if (getIntPref("total_chapters_read") == 1189){
            bibleAlertBuilder("bible", "bible")
        }
        if (isLogged != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("main").document(isLogged.uid).update(updateValues)
                    .addOnSuccessListener {
                        log("Successful update")
                    }
                    .addOnFailureListener {
                        val error = it
                        Log.w("PROFGRANT", "Failure writing to firestore", error)
                    }
        }
    }
    private fun updateReadingStatistic(listName: String, prefix: String =""){
        val listId= getListId(listName)
        val list = App.applicationContext().resources.getStringArray(listId)
        val listIndex = when(getStringPref("planType", "horner")){
            "horner"->getIntPref(listName)
            "numerical"->{
                var index = getIntPref("${prefix}currentDayIndex")
                while(index >= list.size){
                    index -= list.size
                }
                index
            }
            "calendar"->{
                var index = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1
                while (index >= list.size){
                    index -= list.size
                }
                index
            }
            else->getIntPref(listName)
        }
        if(listName == "list6" && getBoolPref("psalms")) {
            val codedBook = "psalm"
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            if (day != 31) {
                val bookChapters = 150
                if (!getBoolPref(name="psalm_${day}_read")) updateStatistics(codedBook, bookChapters, testament="old", testamentChapters=929, chapter=day)
                if (!getBoolPref(name="psalm_${day+30}_read")) updateStatistics(codedBook, bookChapters, testament="old", testamentChapters=929, chapter=day + 30)
                if (!getBoolPref(name="psalm_${day+60}_read")) updateStatistics(codedBook, bookChapters, testament="old", testamentChapters=929, chapter=day + 60)
                if (!getBoolPref(name="psalm_${day+90}_read")) updateStatistics(codedBook, bookChapters, testament="old", testamentChapters=929, chapter=day + 90)
                if (!getBoolPref(name="psalm_${day+120}_read")) updateStatistics(codedBook, bookChapters, testament="old", testamentChapters=929, chapter=day + 120)
            }
        }else{
            val reading = list[listIndex]
            val readingArray = reading.split(" ")
            val bookArray = readingArray.subList(0, reading.split(" ").lastIndex)
            val book = bookArray.joinToString(" ")
            val codedBook = bookNamesCoded[book]
            val chapter = readingArray[readingArray.lastIndex]
            val bookChapters = bookChapters[codedBook]
            val testament = getTestament(codedBook!!)
            val testamentChapters = if(testament == "old") 929 else 260
            updateStatistics(codedBook, bookChapters!!, testament, testamentChapters, chapter.toInt())
        }
    }
    private fun makeStreakAlert(type:String) {
        val alert = AlertDialog.Builder(App.applicationContext())
        alert.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val message = when(type) {
            "week" -> "You've kept up with your reading plan for 1 week! Keep going!"
            "month"-> "Wow! You've kept it up for a whole month!"
            "3month"-> "Keep going! You've read consistently every day for the last 3 months!"
            "year"-> "Big congrats, you've kept up with your reading plan every day for the last year!"
            else->""
        }
        alert.setTitle("Congratulations! Keep It Up!")
        alert.setMessage(message)
        alert.show()
    }

    fun markAll(planType: String = "") {
        val doneMax = when (planType){
            "pgh"->10
            "mcheyne"->4
            else->10
        }
        val prefix = when(planType){
            "pgh"->""
            "mcheyne"->"mcheyne_"
            else->""
        }
        for (i in 1..doneMax) {
            updateReadingStatistic(listName="${prefix}list${i}", prefix)
            setIntPref(name="${prefix}list${i}Done", value=1)
            val doneDaily = getIntPref(name="${prefix}list${i}DoneDaily")
            if(doneDaily == 0){
                setIntPref(name="${prefix}list${i}DoneDaily", value=1)
            }
        }
        setIntPref("listsDone", doneMax)
        if (getIntPref("dailyStreak") == 0 || getBoolPref("isGrace") && getIntPref("graceTime") == 1) {
            if(getBoolPref("isGrace") && getIntPref("graceTime") == 0){
                setIntPref("graceTime", 1)
            }
            if(getBoolPref("isGrace") && getIntPref("graceTime") == 1){
                setIntPref("graceTime", 2)
                setBoolPref("isGrace", false)
                setIntPref("currentStreak", getIntPref("holdStreak") + 1)
                setIntPref("holdStreak",0)
            }
            if(!checkDate("current", false)){
                val currentStreak = increaseIntPref("currentStreak", 1)
                val streak = if(currentStreak > 365){
                    currentStreak - 365
                }else{
                    currentStreak
                }
                when(streak){
                    7->makeStreakAlert("week")
                    30->makeStreakAlert("month")
                    90->makeStreakAlert("3month")
                    365->makeStreakAlert("1year")
                }
                setStringPref("dateChecked", getDate(0,false))
                if(currentStreak > getIntPref("maxStreak"))
                    setIntPref("maxStreak", currentStreak)
            }
            setIntPref("dailyStreak", 1)
        }

        if (isLogged != null) {
            val db = FirebaseFirestore.getInstance()
            val updateValues = mutableMapOf<String, Any>()
            for (i in 1..doneMax) {
                updateValues["${prefix}list${i}Done"] = 1
                val doneDaily = getIntPref("${prefix}list${i}DoneDaily")
                if(doneDaily == 0){
                    setIntPref("${prefix}list${i}DoneDaily", 1)
                }
            }
            updateValues["graceTime"] = getIntPref("graceTime")
            updateValues["isGrace"] = getBoolPref("isGrace")
            updateValues["listsDone"] = doneMax
            updateValues["holdStreak"] = getIntPref("holdStreak")
            updateValues["dateChecked"] = getDate(0,false)
            updateValues["dailyStreak"] = getIntPref("dailyStreak")
            updateValues["currentStreak"] = getIntPref("currentStreak")
            updateValues["maxStreak"] = getIntPref("maxStreak")
            db.collection("main").document(isLogged.uid).update(updateValues)
                    .addOnSuccessListener {
                        log("Successful update")
                    }
                    .addOnFailureListener {
                        val error = it
                        Log.w("PROFGRANT", "Failure writing to firestore", error)
                    }
        }
    }
    fun markSingle(cardDone: String, planSystem: String="") {
        val doneMax = when(planSystem){
            "pgh"->10
            "mcheyne"->4
            else->10
        }
        val cardDoneDaily = "${cardDone}Daily"
        val listName = cardDone.replace("Done", "")
        val db = FirebaseFirestore.getInstance()
        val allowPartial = getBoolPref("allow_partial_switch")
        val listDoneDaily = getIntPref(cardDoneDaily)
        val listsDone = if (listDoneDaily == 0){
            setIntPref(cardDoneDaily, 1)
            increaseIntPref("listsDone", 1)
        }else{
            getIntPref("listsDone")
        }
        log("Lists Done is ${getIntPref("listsDone")}")
        if (getIntPref(cardDone) != 1) {
            updateReadingStatistic(listName)
            setIntPref(cardDone, 1)
            setStringPref("dateChecked", getDate(0, false))
            if (allowPartial || listsDone == doneMax) {
                if (getIntPref("dailyStreak") == 0 || getBoolPref("isGrace") && getIntPref("graceTime") == 1) {
                    if(getBoolPref("isGrace") && getIntPref("graceTime") == 0){
                        setIntPref("graceTime", 1)
                    }
                    if(getBoolPref("isGrace") && getIntPref("graceTime") == 1){
                        setIntPref("graceTime", 2)
                        setIntPref("currentStreak", getIntPref("holdStreak"))
                        setIntPref("holdStreak", 0)
                        setBoolPref("isGrace", false)
                    }
                    val currentStreak = increaseIntPref("currentStreak", 1)
                    if (currentStreak > getIntPref("maxStreak")) {
                        setIntPref("maxStreak", currentStreak)
                    }
                    setIntPref("dailyStreak", 1)
                }
            }
            if (isLogged != null) {
                val data = mutableMapOf<String, Any>()
                data["maxStreak"] = getIntPref("maxStreak")
                data["currentStreak"] = getIntPref("currentStreak")
                data["isGrace"] = getBoolPref("isGrace")
                data["graceTime"] = getIntPref("graceTime")
                data["holdStreak"] = getIntPref("holdStreak")
                data["dailyStreak"] = 1
                data["listsDone"] = listsDone
                data["dateChecked"] = getDate(0, false)
                data[cardDone] = 1
                if(listDoneDaily == 0) {
                    data[cardDoneDaily] = 1
                }
                db.collection("main").document(isLogged.uid).update(data)
            }
        }
    }
}