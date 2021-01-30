package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.isWeekend

class DailyCheck : BroadcastReceiver() {
    private val isLogged = Firebase.auth.currentUser

    override fun onReceive(context: Context, intent: Intent) {
        val planSystem = getStringPref(name="planSystem", defaultValue="pgh")
        val data = mutableMapOf<String, Any>()
        val doneMax = when(planSystem){
            "pgh"->10
            "mcheyne"->4
            else->10
        }
        val planType = getStringPref(name="planType", defaultValue="horner")
        val db = Firebase.firestore
        var resetStreak  = false
        var resetCurrent = false
        val vacation = getBoolPref(name="vacationMode")
        when (getIntPref(name="dailyStreak")) {
            1 -> {
                setIntPref(name="dailyStreak", value=0)
                data["dailyStreak"] = 0
                setIntPref(name="graceTime", value=0)
                data["graceTime"] = 0
                resetStreak = true
            }
            0 -> {
                when (vacation || getBoolPref(name="vacationOff") || (getBoolPref(name="weekendMode") && isWeekend())) {
                    false -> {
                        if(!checkDate(option="yesterday", fullMonth=false)){
                                resetCurrent = true
                                if(!getBoolPref(name="isGrace")){
                                    setIntPref(name="holdStreak", getIntPref(name="currentStreak"))
                                    data["holdStreak"] = getIntPref(name="currentStreak")
                                    setBoolPref(name="isGrace", value=true)
                                    data["isGrace"] = true
                                }else{
                                    setBoolPref(name="isGrace", value=false)
                                    data["isGrace"] = false
                                    setIntPref(name="holdStreak", value=0)
                                    data["holdStreak"] = 0
                                }
                                setIntPref(name="currentStreak", value=0)
                                data["currentStreak"] = 0
                        }
                    }
                    true -> {
                        if(getBoolPref(name="vacationOff")){
                            setBoolPref(name="vacationOff", value=false)
                        }
                    }
                }
            }
        }
        for(i in 1..doneMax){
            if(planType == "horner") {
                if(planSystem == "pgh"){
                    when(getIntPref(name="list${i}DoneDaily")){
                        1-> {setIntPref(name="list${i}DoneDaily", value=0); data["list${i}DoneDaily"] = 0}
                    }
                    when(getIntPref(name="list${i}Done")){
                        1->{resetList(listName="list${i}", listNameDone="list${i}Done", doneMax)}
                    }
                }else{
                    when(getIntPref(name="mcheyneList${i}DoneDaily")){
                        1-> { setIntPref(name="mcheyneList${i}DoneDaily", value=0); data["mcheyneList${i}DoneDaily"] = 0}
                    }
                    when(getIntPref(name="mcheyneList${i}Done")){
                        1-> resetList(listName="mcheyneList${i}", listNameDone="mcheyneList${i}Done", doneMax)
                    }
                }
            }else{
                if(planSystem == "pgh"){
                    setIntPref(name="list${i}Done", value=0)
                    data["list${i}Done"] = 0
                }else{
                    setIntPref(name="mcheyneList${i}Done", value=0)
                    data["mcheyneList${i}Done"] = 0
                }
            }
        }

        if(planType== "numerical" && resetStreak) {
            if(planSystem == "pgh"){
                data["currentDayIndex"] = increaseIntPref(name="currentDayIndex", value=1)
            }else{
                data["mcheyneCurrentDayIndex"] = increaseIntPref(name="mcheyneCurrentDayIndex", value=1)
            }
        }
        if(!getBoolPref(name="holdPlan") || getIntPref(name="listsDone") == doneMax) {
            setIntPref(name="listsDone", value=0)
            data["listsDone"] = 0
        }

        if(isLogged != null) {
            for (i in 1..doneMax) {
                if (planType == "horner") {
                    if (planSystem == "pgh") {
                        data["list${i}"] = getIntPref(name = "list$i")
                    } else {
                        data["mcheyneList$i"] = getIntPref(name = "mcheyneList$i")
                    }
                }
                if (planSystem == "pgh") {
                    data["list${i}Done"] = getIntPref(name="list${i}Done")
                    data["list${i}DoneDaily"] = getIntPref(name="list${i}DoneDaily")
                }else{
                    data["mcheyneList${i}Done"] = getIntPref(name="mcheyneList${i}Done")
                    data["mcheyneList${i}DoneDaily"] = getIntPref(name="mcheyneList${i}DoneDaily")
                }
            }
            data["listsDone"] = getIntPref(name="listsDone")
            if(resetCurrent) {
                data["graceTime"] = 0
            }else if(resetStreak) {
                data["isGrace"] = getBoolPref("isGrace")
            }
            db.collection("main").document(isLogged.uid).update(data)
        }
    }
    private fun resetList(listName: String, listNameDone: String, maxDone:Int){
        if(!getBoolPref(name="holdPlan") || getIntPref(name="listsDone") == maxDone) {
            if(listName != "list6" || (listName == "list6" && !getBoolPref(name="psalms"))) {
                increaseIntPref(listName, value=1)
            }
            setIntPref(name="${listNameDone}Daily", value=0)
            setIntPref(listNameDone, value=0)
        }
    }
}
