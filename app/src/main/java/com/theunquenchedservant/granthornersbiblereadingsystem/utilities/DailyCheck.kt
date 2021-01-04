package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.checkDate

class DailyCheck : BroadcastReceiver() {
    private val isLogged = FirebaseAuth.getInstance().currentUser
    override fun onReceive(context: Context, intent: Intent) {
        val db = FirebaseFirestore.getInstance()
        var resetStreak  = false
        var resetCurrent = false
        val vacation = getBoolPref("vacation_mode")
        when (getIntPref("dailyStreak")) {
            1 -> {
                setIntPref("dailyStreak", 0)
                log("DAILY CHECK - daily streak set to 0")
                resetStreak = true
            }
            0 -> {
                when (vacation || getBoolPref("vacationOff")) {
                    false -> {
                        if(!checkDate("yesterday", false)){
                                resetCurrent = true
                                log("DAILY CHECK - currentStreak set to 0")
                                setIntPref("currentStreak", 0)
                        }
                    }
                    true -> {
                        if(getBoolPref("vacationOff")){
                            setBoolPref("vacationOff", false)
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
        if(!getBoolPref("holdPlan") || getIntPref("listsDone") == 10) {
            setIntPref("listsDone", 0)
        }
        if(isLogged != null) {
            val data = mutableMapOf<String, Any>()
            for (i in 1..10){
                data["list$i"] = getIntPref("list$i")
                data["list${i}Done"] = getIntPref("list${i}Done")
                data["list${i}DoneDaily"] = getIntPref("list${i}DoneDaily")
            }
            data["listsDone"] = getIntPref("listsDone")
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
        if(!getBoolPref("holdPlan") || getIntPref("listsDone") == 10) {
            if(listName != "list6" || (listName == "list6" && !getBoolPref("psalms"))) {
                increaseIntPref(listName, 1)
                log("$listName index is now ${getIntPref(listName)}")
            }
            setIntPref("${listNameDone}Daily", 0)
            setIntPref(listNameDone, 0)
            log("$listNameDone set to 0")
        }
    }
}
