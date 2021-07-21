package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.getDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFirestore
import timber.log.Timber

object Marker {

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