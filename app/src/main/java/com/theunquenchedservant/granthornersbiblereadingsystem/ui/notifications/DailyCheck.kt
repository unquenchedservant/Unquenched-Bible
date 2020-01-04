package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.boolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.getDate

class DailyCheck : BroadcastReceiver() {
    private val isLogged = FirebaseAuth.getInstance().currentUser
    override fun onReceive(context: Context, intent: Intent) {
        val db = FirebaseFirestore.getInstance()
        var resetStreak  = false
        var resetCurrent = false
        val vacation = boolPref("vacation_mode", null)
        when (getIntPref("dailyStreak")) {
            1 -> {
                setIntPref("dailyStreak", 0)
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
                                setIntPref("currentStreak", 0)
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
            when(getIntPref("list${i}Done")){
                1 -> {
                    resetList("list$i", "list${i}Done")
                }
            }
        }
        setIntPref("listsDone", 0)
        if(isLogged != null) {
            val data = mutableMapOf<String, Any>()
            for (i in 1..10){
                data["list$i"] = getIntPref("list$i")
                data["list${i}Done"] = getIntPref("list${i}Done")
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
        log("$listName is now set to ${getIntPref(listName)}")
        increaseIntPref(listName, 1)
        log("$listName index is now ${getIntPref(listName)}")
        setIntPref(listNameDone, 0)
        log("$listNameDone set to 0")
    }
}
