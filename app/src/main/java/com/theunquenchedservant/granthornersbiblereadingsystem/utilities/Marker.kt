package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_CHAPTERS
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_NAMES
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_NAMES_CODED
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.NT_BOOKS
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.OT_BOOKS
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.getDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import timber.log.Timber
import java.util.*


object Marker {
    private val isLogged = Firebase.auth.currentUser
    private fun getListId(listName: String): Int {
        return when (listName) {
            "list1" -> R.array.list_1
            "list2" -> R.array.list_2
            "list3" -> R.array.list_3
            "list4" -> R.array.list_4
            "list5" -> R.array.list_5
            "list6" -> R.array.list_6
            "list7" -> R.array.list_7
            "list8" -> R.array.list_8
            "list9" -> R.array.list_9
            "list10" -> R.array.list_10
            "mcheyneList1" -> R.array.mcheyne_list1
            "mcheyneList2" -> R.array.mcheyne_list2
            "mcheyneList3" -> R.array.mcheyne_list3
            "mcheyneList4" -> R.array.mcheyne_list4
            else -> 0
        }
    }

    private fun getTestament(book: String = ""): String {
        return when (book) {
            in OT_BOOKS -> "old"
            in NT_BOOKS -> "new"
            else -> "old"
        }
    }

    /**fun bibleAlertBuilder(type: String, name: String, context: Context?) {
        val alert = AlertDialog.Builder(context)
        alert.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val message = when (type) {
            "book" -> "You just finished $name, keep it up!"
            "testament" -> "You just read through all of the $name Testament! Wow!"
            "bible" -> "You've officially read through the entire Bible using your reading system! Congratulations!"
            else -> ""
        }
        val title = when (type) {
            "book" -> "$name Finished!"
            "testament" -> "$name Testament Finished!"
            "bible" -> "Whole Bible Finished!"
            else -> ""
        }
        alert.setTitle(title)
        alert.setMessage(message)
        alert.show()
    }*/

    private fun updateStatistics(currentData: MutableMap<String, Any>?, book: String, bookChaps: Int, testament: String, testamentChapters: Int, chapter: Int, updateValue: MutableMap<String, Any>):MutableMap<String, Any> {
        if (extractIntPref(currentData, "${book}ChaptersRead") == bookChaps) {
            updateValue["${book}ChaptersRead"] = 0
            updateValue["${book}AmountRead"] = extractIntPref(currentData, "${book}AmountRead") + 1
            for (i in 1..bookChaps) {
                updateValue["${book}${i}Read"] = false
            }
            if (!extractBoolPref(currentData, "${book}DoneTestament")) {
                updateValue["${book}DoneTestament"] = true
                if (extractIntPref(currentData,"${testament}ChaptersRead") == testamentChapters) {
                    updateValue["${testament}ChaptersRead"] = 0
                    for (item in BOOK_NAMES) {
                        updateValue["${item}DoneTestament"] = false
                    }
                    updateValue["${testament}AmountRead"] = extractIntPref(currentData, "${testament}AmountRead") +1
                }
            }
            if (!extractBoolPref(currentData,"${book}DoneWhole")) {
                updateValue["${book}DoneWhole"] = true
                if (getIntPref(name = "totalChaptersRead") == 1189) {
                    updateValue["totalChaptersRead"] = 0
                    for (item in BOOK_NAMES) {
                        updateValue["${item}DoneWhole"] = false
                    }
                    updateValue["bibleAmountRead"] = extractIntPref(currentData, "bibleAmountRead") + 1
                }
            }
        }
        if (!extractBoolPref(currentData, "${book}${chapter}Read")) {
            updateValue["${book}ChaptersRead"] = extractIntPref(currentData, "${book}ChaptersRead") + 1
            updateValue["${book}${chapter}Read"] = true
        }
        updateValue["${book}${chapter}AmountRead"] = extractIntPref(currentData, "${book}${chapter}AmountRead") + 1
        when (extractBoolPref(currentData,"${book}DoneTestament")) {
            false -> {
                updateValue["${testament}ChaptersRead"] = extractIntPref(currentData, "${testament}ChaptersRead") + 1
                updateValue["${book}DoneTestament"] = true
            }
        }
        when (extractBoolPref(currentData, "${book}DoneWhole")) {
            false -> {
                updateValue["totalChaptersRead"] = extractIntPref(currentData, "totalChaptersRead") + 1
                updateValue["${book}DoneWhole"] = true
            }
        }
       /** when (extractIntPref(currentData,"${book}ChaptersRead") + 1) {
            bookChaps -> bibleAlertBuilder("book", bookName!!, context)
        }
        when (extractIntPref(current Data,"${testament}ChaptersRead") + 1) {
            testamentChapters -> bibleAlertBuilder("testament", testament.capitalize(Locale.ROOT), context)
        }
        when (extractIntPref(currentData,"totalChaptersRead") + 1) {
            1189 -> bibleAlertBuilder("bible", "bible", context)
        }*/
        return updateValue
    }

    private fun updateReadingStatistic(currentData: MutableMap<String, Any>?, listName: String, updateValue: MutableMap<String, Any>):MutableMap<String, Any> {
        var updateValues = updateValue
        val listId = getListId(listName)
        val list = App.applicationContext().resources.getStringArray(listId)
        val planType = extractStringPref(currentData, "planType", defaultValue="horner")
        val planSystem = extractStringPref(currentData, "planSystem", "pgh")
        val listIndex = when (planType) {
            "horner" -> extractIntPref(currentData,listName)
            "numerical" -> {
                var index = if (planSystem == "pgh") extractIntPref(currentData,"currentDayIndex") else extractIntPref(currentData, "mcheyneCurrentDayIndex")
                while (index >= list.size) {
                    index -= list.size
                }
                index
            }
            "calendar" -> {
                var index = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1
                while (index >= list.size) {
                    index -= list.size
                }
                index
            }
            else -> extractIntPref(currentData,listName)
        }
        if (listName == "list6" && extractBoolPref(currentData,"psalms")) {
            val codedBook = "psalm"
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            if (day != 31) {
                val bookChapters = 150
                if (!extractBoolPref(currentData,"psalm${day}Read")) updateValues=updateStatistics(currentData, codedBook, bookChapters, testament = "old", testamentChapters = 929, chapter = day, updateValue = updateValues)
                if (!extractBoolPref(currentData, "psalm${day + 30}Read")) updateValues=updateStatistics(currentData, codedBook, bookChapters, testament = "old", testamentChapters = 929, chapter = day + 30, updateValue = updateValues)
                if (!extractBoolPref(currentData, "psalm${day + 60}Read")) updateValues=updateStatistics(currentData, codedBook, bookChapters, testament = "old", testamentChapters = 929, chapter = day + 60, updateValue = updateValues)
                if (!extractBoolPref(currentData,"psalm${day + 90}Read")) updateValues=updateStatistics(currentData, codedBook, bookChapters, testament = "old", testamentChapters = 929, chapter = day + 90, updateValue = updateValues)
                if (!extractBoolPref(currentData,"psalm${day + 120}Read")) updateValues=updateStatistics(currentData, codedBook, bookChapters, testament = "old", testamentChapters = 929, chapter = day + 120, updateValue = updateValues)
            }
        } else if(planSystem == "pgh"){
            val reading = list[listIndex]
            val readingArray = reading.split(" ")
            var lastIndex = if(readingArray.lastIndex == 0) 1 else readingArray.lastIndex
            lastIndex = if(readingArray[0].toIntOrNull() != null && readingArray[lastIndex].toIntOrNull() == null) lastIndex + 1 else lastIndex
            val bookArray = readingArray.subList(0, lastIndex)
            val book = bookArray.joinToString(" ")
            val codedBook = BOOK_NAMES_CODED[book]
            val chapter = if(readingArray[readingArray.lastIndex].toIntOrNull() != null) readingArray[readingArray.lastIndex].toInt() else 1
            val bookChapters = BOOK_CHAPTERS[codedBook]
            val testament = getTestament(codedBook!!)
            val testamentChapters = if (testament == "old") 929 else 260
            updateValues = updateStatistics(currentData, codedBook, bookChapters!!, testament, testamentChapters, chapter, updateValues)
        }
        return updateValues
    }

    private fun makeStreakAlert(type: String, context: Context?) {
        val alert = AlertDialog.Builder(context)
        alert.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val message = when (type) {
            "week" -> "You've kept up with your reading plan for 1 week! Keep going!"
            "month" -> "Wow! You've kept it up for a whole month!"
            "3month" -> "Keep going! You've read consistently every day for the last 3 months!"
            "year" -> "Big congrats, you've kept up with your reading plan every day for the last year!"
            else -> ""
        }
        alert.setTitle("Congratulations! Keep It Up!")
        alert.setMessage(message)
        alert.show()
    }

    fun markAll(planSystem: String = "", context: Context?) {
        val updateValues = mutableMapOf<String, Any>()
        val doneMax = when (planSystem) {
            "pgh" -> 10
            "mcheyne" -> 4
            else -> 10
        }
        val listStart = if (planSystem == "pgh") "pgh" else "mcheyne"
        val listsDone = "${listStart}Done"
        for (i in 1..doneMax) {
            updateValues["${listStart}${i}Done"] = setBoolPref("$listStart${i}Done", true)
            if (!getBoolPref("${listStart}${i}DoneDaily")) {
                updateValues["${listStart}${i}DoneDaily"] =
                    setBoolPref("$listStart${i}DoneDaily", true)
            }
        }
        updateValues[listsDone] = setIntPref(listsDone, doneMax)
        val isGrace = getBoolPref("isGrace")
        val graceTime = getIntPref("graceTime")
        if (getIntPref("dailyStreak") == 0 || isGrace && graceTime == 1) {
            if (isGrace && graceTime == 0) {
                updateValues["graceTime"] = setIntPref("graceTime", 1)
            }
            if (isGrace && graceTime == 1) {
                updateValues["graceTime"] = setIntPref("graceTime", 0)
                updateValues["isGrace"] = setBoolPref("isGrace", false)
                updateValues["currentStreak"] =
                    setIntPref("currentStreak", getIntPref("holdStreak") + 1)
                updateValues["holdStreak"] = setIntPref("holdStreak", 0)
            }
            if (!checkDate(getStringPref("dateChecked"), "current", false)) {
                val currentStreak = getIntPref("currentStreak") + 1
                updateValues["currentStreak"] = currentStreak
                setIntPref("currentStreak", currentStreak)
                var streak = currentStreak
                while (streak > 365) {
                    streak -= 365
                }
                when (streak) {
                    7 -> makeStreakAlert("week", context)
                    30 -> makeStreakAlert("month", context)
                    90 -> makeStreakAlert("3month", context)
                    365 -> makeStreakAlert("1year", context)
                }
                updateValues["dateChecked"] = setStringPref("dateChecked", getDate(0, false))
                if (currentStreak > getIntPref("maxStreak"))
                    updateValues["maxStreak"] = setIntPref("maxStreak", currentStreak)
            }
            updateValues["dailyStreak"] = setIntPref("dailyStreak", 1)
        }
        updateFirestore(updateValues).addOnSuccessListener {
            debugLog("Successful Update")
        }
            .addOnFailureListener { error ->
                Timber.tag("PROFGRANT").e(error, "Error writing to firestore")
            }
    }

    fun markSingle(cardDone: String, planSystem: String = "", context: Context?){
        val updateValues = hashMapOf<String, Any>()
        val doneMax = when (getStringPref("planSystem")) {
            "pgh" -> 10
            "mcheyne" -> 4
            else -> 10
        }
        val listStart = planSystem
        val listsDoneString = "${listStart}Done"
        debugLog("THIS IS THE CARD DONE STRING: ${listsDoneString}")
        val cardDoneDaily = "${cardDone}Daily"
        val allowPartial = getBoolPref("allowPartial")
        val listDoneDaily = getBoolPref(cardDoneDaily)
        val currentListsDone = setIntPref(listsDoneString, getIntPref(listsDoneString) + 1)
        if (!listDoneDaily)  updateValues[cardDoneDaily] = setBoolPref(cardDoneDaily, true)
        updateValues[listsDoneString] = currentListsDone
        debugLog("this is the listsDone from mark Single ${currentListsDone}")
        if (!getBoolPref(cardDone)) {
            updateValues[cardDone] = setBoolPref(cardDone, true)
            updateValues["dateChecked"] =
                setStringPref("dateChecked", getDate(0, false))
            if (allowPartial || currentListsDone == doneMax) {
                val isGrace = getBoolPref("isGrace")
                val graceTime = getIntPref("graceTime")
                if (getIntPref("dailyStreak") == 0 || isGrace && graceTime == 1) {
                    if (isGrace && graceTime == 0) {
                        updateValues["graceTime"] = 1
                    }
                    if (isGrace && graceTime == 1) {
                        updateValues["graceTime"] = 2
                        updateValues["currentStreak"] =
                            setIntPref("currentStreak", getIntPref("holdStreak"))
                        updateValues["holdStreak"] = setIntPref("holdStreak", 0)
                        updateValues["isGrace"] = false
                    }
                    if(!checkDate(getStringPref("dateChecked"), "current", false)) {
                        val currentStreak = getIntPref("currentStreak") + 1
                        updateValues["currentStreak"] =
                            setIntPref("currentStreak", currentStreak)
                        if (currentStreak > getIntPref("maxStreak")) {
                            updateValues["maxStreak"] =
                                setIntPref("maxStreak", currentStreak)
                        }

                        val streak = if (currentStreak > 365) {
                            currentStreak - 365
                        } else {
                            currentStreak
                        }
                        when (streak) {
                            7 -> makeStreakAlert("week", context)
                            30 -> makeStreakAlert("month", context)
                            90 -> makeStreakAlert("3month", context)
                            365 -> makeStreakAlert("1year", context)
                        }
                        updateValues["dailyStreak"] = setIntPref("dailyStreak", 1)
                    }
                }
            }
            updateFirestore(updateValues)
                .addOnSuccessListener {
                    debugLog("Firestore successfully updated")
                }
                .addOnFailureListener { error ->
                    debugLog("FAILURE WRITING TO FIRESTORE $error")
                }
        }
    }
}