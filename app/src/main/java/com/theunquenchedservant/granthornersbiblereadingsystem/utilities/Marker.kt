package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.bookChapters
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.bookNames
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.bookNamesCoded
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.getDate
import java.util.*


object Marker {
    private val isLogged = FirebaseAuth.getInstance().currentUser
    fun getListId(listName: String) : Int{
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
            else-> 0
        }
    }
    fun get_testament(listName: String): String{
        return when(listName){
            "list1"->"new"
            "list2"->"old"
            "list3"->"new"
            "list4"->"new"
            "list5"->"old"
            "list6"->"old"
            "list7"->"old"
            "list8"->"old"
            "list9"->"old"
            "list10"->"new"
            else->"old"
        }
    }

    fun update_statistics(codedBook: String, bookChaps: Int, testament: String, testament_chapters: Int, chapter: Int){
        val updateValues = mutableMapOf<String, Any>()
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
                if (getIntPref("${testament}_chapters_read") == testament_chapters) {
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
            setIntPref("total_chapters_read", getIntPref("total_chapters_read") + 1)
            updateValues["total_chapters_read"] = getIntPref("total_chapters_read")
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
    fun update_reading_statistic(listName: String): String{
        val listId= getListId(listName)
        val list = App.applicationContext().resources.getStringArray(listId)
        val list_index = getIntPref(listName)
        if(listName == "list6" && getBoolPref("psalms")) {
            val codedBook = "psalm"
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            if (day != 31) {
                val bookChapters = 150
                if(!getBoolPref("psalm_${day}_read") && !getBoolPref("psalm_${day+30}_read") && !getBoolPref("psalm_${day+60}_read") && getBoolPref("psalm_${day+90}_false") && getBoolPref("psalm_${day+120}_false")){
                    update_statistics(codedBook, bookChapters, "old", 929, day)
                    update_statistics(codedBook, bookChapters, "old", 929, day + 30)
                    update_statistics(codedBook, bookChapters, "old", 929, day + 60)
                    update_statistics(codedBook, bookChapters, "old", 929, day + 90)
                    update_statistics(codedBook, bookChapters, "old", 929, day + 120)
                }else {
                    if (!getBoolPref("psalm_${day}_read")) update_statistics(codedBook, bookChapters, "old", 929, day)
                    if (!getBoolPref("psalm_${day+30}_read")) update_statistics(codedBook, bookChapters, "old", 929, day + 30)
                    if (!getBoolPref("psalm_${day+60}_read")) update_statistics(codedBook, bookChapters, "old", 929, day + 60)
                    if (!getBoolPref("psalm_${day+90}_read")) update_statistics(codedBook, bookChapters, "old", 929, day + 90)
                    if (!getBoolPref("psalm_${day+120}_read")) update_statistics(codedBook, bookChapters, "old", 929, day + 120)
                }
            }
        }else{
            val reading = list[list_index]
            val readingArray = reading.split(" ")
            val bookArray = readingArray.subList(0, reading.split(" ").lastIndex)
            val book = bookArray.joinToString(" ")
            val codedBook = bookNamesCoded[book]
            val chapter = readingArray[readingArray.lastIndex]
            val bookChapters = bookChapters[codedBook]
            val testament = get_testament(listName)
            val testament_chapters = if(testament == "old") 929 else 260
            update_statistics(codedBook!!, bookChapters!!, testament, testament_chapters, chapter.toInt())
        }

        return "Hi"
    }
    fun markAll() {
        for (i in 1..10) {
            update_reading_statistic("list${i}")
            setIntPref("list${i}Done", 1)
            val doneDaily = getIntPref("list${i}DoneDaily")
            if(doneDaily == 0){
                setIntPref("list${i}DoneDaily", 1)
            }
        }
        setIntPref("listsDone", 10)
        if (getIntPref("dailyStreak") == 0) {
            if(!checkDate("current", false)){
                val currentStreak = increaseIntPref("currentStreak", 1)
                setStringPref("dateChecked", getDate(0,false))
                if(currentStreak > getIntPref("maxStreak"))
                    setIntPref("maxStreak", currentStreak)
            }
            setIntPref("dailyStreak", 1)
        }
        if (isLogged != null) {
            val db = FirebaseFirestore.getInstance()
            val updateValues = mutableMapOf<String, Any>()
            for (i in 1..10) {
                updateValues["list${i}Done"] = 1
                val doneDaily = getIntPref("list${i}DoneDaily")
                if(doneDaily == 0){
                    setIntPref("list${i}DoneDaily", 1)
                }
            }
            updateValues["listsDone"] = 10
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
    fun markSingle(cardDone: String) {
        val cardDoneDaily = "${cardDone}Daily"
        val listName = cardDone.replace("Done", "")
        val db = FirebaseFirestore.getInstance()
        val allowPartial = getBoolPref("allow_partial_switch")
        val listDoneDaily = getIntPref(cardDoneDaily)
        val listsDone = if (listDoneDaily == 0){
            setIntPref(cardDoneDaily, 1)
            log("Should be increasing lists done")
            increaseIntPref("listsDone", 1)
        }else{
            log("should not be increasing lists done")
            getIntPref("listsDone")
        }
        log("Lists Done is ${getIntPref("listsDone")}")
        if (getIntPref(cardDone) != 1) {
            update_reading_statistic(listName)
            setIntPref(cardDone, 1)
            setStringPref("dateChecked", getDate(0, false))
            if (allowPartial || listsDone == 10) {
                if (getIntPref("dailyStreak") == 0) {
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