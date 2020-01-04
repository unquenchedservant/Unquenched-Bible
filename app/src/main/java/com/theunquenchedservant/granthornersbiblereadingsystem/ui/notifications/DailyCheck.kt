package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.boolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.intPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.getDate

class DailyCheck : BroadcastReceiver() {
    private val isLogged = FirebaseAuth.getInstance().currentUser
    override fun onReceive(context: Context, intent: Intent) {
        val db = FirebaseFirestore.getInstance()
        var resetStreak  = false
        var resetCurrent = false
        val vacation = boolPref("vacation_mode", null)
        when (intPref("dailyStreak", null)) {
            1 -> {
                intPref("dailyStreak", 0)
                log("DAILY CHECK - daily streak set to 0")
                resetStreak = true
            }
            0 -> {
                when (vacation || boolPref("vacationOff", null)) {
                    false -> {
                        when(getStringPref("dateChecked")){
                            getDate(1, false) -> {
                            }
                            else -> {
                                resetCurrent = true
                                log("DAILY CHECK - currentStreak set to 0")
                                intPref("currentStreak", 0)
                            }
                        }
                    }
                    true -> {
                        if(boolPref("vacationOff", null)){
                            boolPref("vacationOff", false)
                        }
                    }
                }
            }
        }
        for(i in 1..10){
            when(intPref("list${i}Done", null)){
                1 -> {
                    resetList("list$i", "list${i}Done")
                }
            }
        }
        intPref("listsDone", 0)
        if(isLogged != null) {
            val data = mutableMapOf<String, Any>()
            for (i in 1..10){
                data["list$i"] = intPref("list$i", null)
                data["list${i}Done"] = intPref("list${i}Done", null)
            }
            data["listsDone"] = 0
            if(resetCurrent) {
                data["currentStreak"] = 0
            }else if(resetStreak) {
                data["dailyStreak"] = 0
            }
            db.collection("main").document(isLogged.uid).update(data)
        }
    }
    private fun resetList(listName: String, listNameDone: String){
        log("$listName is now set to ${intPref(listName, null)}")
        intPref(listName, intPref(listName, null) + 1)
        log("$listName index is now ${intPref(listName, null)}")
        intPref(listNameDone, 0)
        log("$listNameDone set to 0")
    }
}
