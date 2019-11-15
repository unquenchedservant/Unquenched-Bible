package com.theunquenchedservant.granthornersbiblereadingsystem

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.getCurrentDate
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.boolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.intPref
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.stringPref


object Marker {
    private val isLogged = FirebaseAuth.getInstance().currentUser
    fun markAll() {
        for (i in 1..10) {
            intPref("list${i}Done", 1)
        }
        intPref("listsDone", 10)
        if (intPref("dailyStreak", null) == 0) {
            if(stringPref("dateChecked", null) != getCurrentDate(false)) {
                val currentStreak = intPref("currentStreak", null) + 1
                intPref("currentStreak", currentStreak)
                stringPref("dateChecked", getCurrentDate(false))
                if(currentStreak > intPref("maxStreak", null))
                    intPref("maxStreak", currentStreak)
            }
            intPref("dailyStreak", 1)
        }
        if (isLogged != null) {
            val db = FirebaseFirestore.getInstance()
            val updateValues = mutableMapOf<String, Any>()
            for (i in 1..10) {
                updateValues["list${i}Done"] = 1
            }
            updateValues["listsDone"] = 10
            updateValues["dateChecked"] = getCurrentDate(false)
            updateValues["dailyStreak"] = intPref("dailyStreak", null)
            updateValues["currentStreak"] = intPref("currentStreak", null)
            updateValues["maxStreak"] = intPref("maxStreak", null)
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
        val db = FirebaseFirestore.getInstance()
        val allowPartial = boolPref("allow_partial_switch", null)
        val listsDone = intPref("listsDone", intPref("listsDone", null) + 1)
        if (intPref(cardDone, null) != 1) {
            intPref(cardDone, 1)
            stringPref("dateChecked", getCurrentDate(false))
            if (allowPartial || listsDone == 10) {
                if (intPref("dailyStreak", null) == 0) {
                    val currentStreak = intPref("currentStreak", intPref("currentStreak", null) + 1)
                    if (currentStreak > intPref("maxStreak", null)) {
                        intPref("maxStreak", currentStreak)
                    }
                    intPref("dailyStreak", 1)
                }
            }
            if (isLogged != null) {
                val data = mutableMapOf<String, Any>()
                data["maxStreak"] = intPref("maxStreak", null)
                data["currentStreak"] = intPref("currentStreak", null)
                data["dailyStreak"] = 1
                data["listsDone"] = listsDone
                data["dateChecked"] = getCurrentDate(false)
                data[cardDone] = 1
                db.collection("main").document(isLogged.uid).update(data)
            }
        }
    }
}