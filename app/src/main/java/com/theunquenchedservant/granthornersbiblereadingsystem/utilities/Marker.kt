package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.app.AlertDialog
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_CHAPTERS
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_NAMES
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_NAMES_CODED
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.NT_BOOKS
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.OT_BOOKS
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.getDate
import java.util.*


object Marker {
    private val isLogged = Firebase.auth.currentUser
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
            "mcheyneList1"->R.array.mcheyne_list1
            "mcheyneList2"->R.array.mcheyne_list2
            "mcheyneList3"->R.array.mcheyne_list3
            "mcheyneList4"->R.array.mcheyne_list4
            else-> 0
        }
    }
    private fun getTestament(book:String=""): String{
       return when(book) {
           in OT_BOOKS -> "old"
           in NT_BOOKS -> "new"
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
    private fun updateStatistics(book: String, bookChaps: Int, testament: String, testamentChapters: Int, chapter: Int){
        val updateValues = mutableMapOf<String, Any>()
        val bookName = BOOK_NAMES[book]
        if (getIntPref(name="${book}ChaptersRead") == bookChaps){
            updateValues["${book}ChaptersRead"] = setIntPref(name="${book}ChaptersRead", value=0)
            updateValues["${book}AmountRead"] = increaseIntPref(name="${book}AmountRead",value=1)
            for (i in 1..bookChaps){
                updateValues["${book}${i}Read"] = setBoolPref(name="${book}${i}Read", value=false)
            }
            if(!getBoolPref(name="${book}DoneTestament")) {
                updateValues["${book}DoneTestament"] = setBoolPref(name="${book}DoneTestament", value=true)
                if (getIntPref(name="${testament}ChaptersRead") == testamentChapters) {
                    updateValues["${testament}ChaptersRead"] = setIntPref(name="${testament}ChaptersRead", value=0)
                    for(item in BOOK_NAMES){
                        updateValues["${item}DoneTestament"] = setBoolPref(name="${item}DoneTestament", value=false)
                    }
                    updateValues["${testament}AmountRead"] = increaseIntPref(name="${testament}AmountRead",  value=1)
                }
            }
            if(!getBoolPref(name="${book}DoneWhole")){
                updateValues["${book}DoneWhole"] = setBoolPref(name="${book}DoneWhole", value=true)
                if(getIntPref(name="totalChaptersRead") == 1189){
                    updateValues["totalChaptersRead"] = setIntPref(name="totalChaptersRead", value=0)
                    for(item in BOOK_NAMES){
                        updateValues["${item}DoneWhole"] = setBoolPref(name="${item}DoneWhole", value=false)
                    }
                    updateValues["bibleAmountRead"] = increaseIntPref(name="bibleAmountRead", value=1)
                }
            }
        }
        updateValues["${book}ChaptersRead"] = increaseIntPref(name="${book}ChaptersRead",value=1)
        updateValues["${book}${chapter}Read"] = setBoolPref(name="${book}${chapter}Read", value=true)
        updateValues["${book}${chapter}AmountRead"] = increaseIntPref(name="${book}${chapter}AmountRead", value=1)
        when(getBoolPref(name="${book}DoneTestament")){
            false->updateValues["${testament}ChaptersRead"] = increaseIntPref(name="${testament}ChaptersRead", value=1)
        }
        when(getBoolPref("${book}DoneWhole")){
            false->updateValues["totalChaptersRead"] = increaseIntPref(name="totalChaptersRead", value=1)
        }
        when(getIntPref("${book}ChaptersRead")){
            bookChaps->bibleAlertBuilder("book", bookName!!)
        }
        when(getIntPref("${testament}ChaptersRead")){
            testamentChapters->bibleAlertBuilder("testament", testament.capitalize(Locale.ROOT))
        }
        when (getIntPref("totalChaptersRead")){
            1189->bibleAlertBuilder("bible", "bible")
        }
        when (isLogged != null) {
            true -> {
                val db = Firebase.firestore
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
    }
    private fun updateReadingStatistic(listName: String){
        log(listName)
        val listId= getListId(listName)
        val list = App.applicationContext().resources.getStringArray(listId)
        val listIndex = when(getStringPref(name="planType", defaultValue="horner")){
            "horner"->getIntPref(listName)
            "numerical"->{
                var index = if(getStringPref(name="planSystem", defaultValue="pgh") == "pgh") getIntPref(name="currentDayIndex") else getIntPref(name="mcheyneCurrentDayIndex")
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
        if(listName == "list6" && getBoolPref(name="psalms")) {
            val codedBook = "psalm"
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            if (day != 31) {
                val bookChapters = 150
                if (!getBoolPref(name="psalm${day}Read")) updateStatistics(codedBook, bookChapters, testament="old", testamentChapters=929, chapter=day)
                if (!getBoolPref(name="psalm${day+30}Read")) updateStatistics(codedBook, bookChapters, testament="old", testamentChapters=929, chapter=day + 30)
                if (!getBoolPref(name="psalm${day+60}Read")) updateStatistics(codedBook, bookChapters, testament="old", testamentChapters=929, chapter=day + 60)
                if (!getBoolPref(name="psalm${day+90}Read")) updateStatistics(codedBook, bookChapters, testament="old", testamentChapters=929, chapter=day + 90)
                if (!getBoolPref(name="psalm${day+120}Read")) updateStatistics(codedBook, bookChapters, testament="old", testamentChapters=929, chapter=day + 120)
            }
        }else{
            log("LIST LENGTH ${list.size} LIST INDEX ${listIndex}")
            val reading = list[listIndex]
            val readingArray = reading.split(" ")
            val bookArray = readingArray.subList(0, reading.split(" ").lastIndex)
            val book = bookArray.joinToString(" ")
            log(book)
            val codedBook = BOOK_NAMES_CODED[book]
            val chapter = readingArray[readingArray.lastIndex]
            val bookChapters = BOOK_CHAPTERS[codedBook]
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
        val updateValues = mutableMapOf<String, Any>()
        val doneMax = when (planType){
            "pgh"->10
            "mcheyne"->4
            else->10
        }
        val listStart = if(planType=="pgh") "list" else "mcheyneList"
        val listsDone = "${listStart}sDone"
        for (i in 1..doneMax) {
            updateReadingStatistic(listName = "${listStart}${i}")
            updateValues["${listStart}${i}Done"] = setIntPref(name = "${listStart}${i}Done", value = 1)
            val doneDaily = getIntPref(name = "${listStart}${i}DoneDaily")
            if (doneDaily == 0) {
                updateValues["${listStart}${i}DoneDaily"] = setIntPref(name = "${listStart}${i}DoneDaily", value = 1)
            }
        }
        updateValues[listsDone] = setIntPref(listsDone, doneMax)
        if (getIntPref("dailyStreak") == 0 || getBoolPref("isGrace") && getIntPref("graceTime") == 1) {
            if(getBoolPref("isGrace") && getIntPref("graceTime") == 0){
                updateValues["graceTime"] = setIntPref("graceTime", 1)
            }
            if(getBoolPref("isGrace") && getIntPref("graceTime") == 1){
                updateValues["graceTime"] = setIntPref("graceTime", 2)
                updateValues["isGrace"] = setBoolPref("isGrace", false)
                updateValues["currentStreak"] = setIntPref("currentStreak", getIntPref("holdStreak") + 1)
                updateValues["holdStreak"] = setIntPref("holdStreak",0)
            }
            if(!checkDate("current", false)){
                val currentStreak = increaseIntPref("currentStreak", 1)
                updateValues["currentStreak"] = currentStreak
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
                updateValues["dateChecked"] = setStringPref("dateChecked", getDate(0,false))
                if(currentStreak > getIntPref("maxStreak"))
                    updateValues["maxStreak"] = setIntPref("maxStreak", currentStreak)
            }
            updateValues["dailyStreak"] = setIntPref("dailyStreak", 1)
        }

        if (isLogged != null) {
            val db = Firebase.firestore
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
        val updateValues = mutableMapOf<String, Any>()
        val doneMax = when(planSystem){
            "pgh"->10
            "mcheyne"->4
            else->10
        }
        val listStart = if(planSystem=="pgh") "list" else "mcheyneList"
        val listsDoneString = "${listStart}sDone"
        val cardDoneDaily = "${cardDone}Daily"
        val listName = cardDone.replace("Done", "")
        val db = Firebase.firestore
        val allowPartial = getBoolPref("allowPartial")
        val listDoneDaily = getIntPref(cardDoneDaily)
        val listsDone = if (listDoneDaily == 0){
            updateValues[cardDoneDaily] = setIntPref(cardDoneDaily, 1)
            increaseIntPref(listsDoneString, 1)
        }else{
            getIntPref(listsDoneString)
        }
        updateValues[listsDoneString] = listsDone
        if (getIntPref(cardDone) != 1) {
            updateReadingStatistic(listName)
            updateValues[cardDone] = setIntPref(cardDone, 1)
            updateValues["dateChecked"] = setStringPref("dateChecked", getDate(0, false))
            if (allowPartial || listsDone == doneMax) {
                if (getIntPref("dailyStreak") == 0 || getBoolPref("isGrace") && getIntPref("graceTime") == 1) {
                    if(getBoolPref("isGrace") && getIntPref("graceTime") == 0){
                        updateValues["graceTime"] = setIntPref("graceTime", 1)
                    }
                    if(getBoolPref("isGrace") && getIntPref("graceTime") == 1){
                        updateValues["graceTime"] = setIntPref("graceTime", 2)
                        updateValues["currentStreak"] = setIntPref("currentStreak", getIntPref("holdStreak"))
                        updateValues["holdStreak"] = setIntPref("holdStreak", 0)
                        updateValues["isGrace"] = setBoolPref("isGrace", false)
                    }
                    val currentStreak = increaseIntPref("currentStreak", 1)
                    updateValues["currentStreak"] = currentStreak
                    if (currentStreak > getIntPref("maxStreak")) {
                        updateValues["maxStreak"] = setIntPref("maxStreak", currentStreak)
                    }
                    updateValues["dailyStreak"] = setIntPref("dailyStreak", 1)
                }
            }
            if (isLogged != null) db.collection("main").document(isLogged.uid).update(updateValues)
        }
    }
}