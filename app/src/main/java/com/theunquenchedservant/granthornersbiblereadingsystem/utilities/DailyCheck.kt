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
        var data = mutableMapOf<String, Any>()
        val doneMax = when(planSystem){
            "pgh"->10
            "mcheyne"->4
            else->10
        }
        val listStart = if(planSystem == "pgh") "list" else "mcheyneLists"
        val planType = getStringPref(name="planType", defaultValue="horner")
        val db = Firebase.firestore
        var resetStreak  = false
        val vacation = getBoolPref(name="vacationMode")
        when (getIntPref(name="dailyStreak")) {
            1 -> {
                data["dailyStreak"] = setIntPref(name="dailyStreak", value=0)
                data["graceTime"] = setIntPref(name="graceTime", value=0)
                resetStreak = true
            }
            0 -> {
                when (vacation || getBoolPref(name="vacationOff") || (getBoolPref(name="weekendMode") && isWeekend())) {
                    false -> {
                        if(!checkDate(getStringPref("dateChecked"), option="yesterday", fullMonth=false)){
                                if(!getBoolPref(name="isGrace")){
                                    data["holdStreak"] = setIntPref(name="holdStreak", getIntPref(name="currentStreak"))
                                    data["isGrace"] = setBoolPref(name="isGrace", value=true)
                                }else{
                                    data["isGrace"] = setBoolPref(name="isGrace", value=false)
                                    data["holdStreak"] = setIntPref(name="holdStreak", value=0)
                                }
                            data["currentStreak"] = setIntPref(name="currentStreak", value=0)
                        }
                    }
                    true -> {
                        if(getBoolPref(name="vacationOff")){
                            data["vacationOff"] = setBoolPref(name="vacationOff", value=false)
                        }
                    }
                }
            }
        }
        for(i in 1..doneMax){
            if(planType == "horner") {
                when (getIntPref(name = "${listStart}${i}DoneDaily")) {
                    1 -> {
                        data["${listStart}${i}DoneDaily"] = setIntPref(name = "${listStart}${i}DoneDaily", value = 0)
                    }
                }
                when (getIntPref(name = "${listStart}${i}Done")) {
                    1 -> {
                        data = resetList(listName = "${listStart}${i}", listNameDone = "${listStart}${i}Done", doneMax, data, listStart)
                    }
                }
            }else data["${listStart}${i}Done"] = setIntPref(name="${listStart}${i}Done", value=0)
        }

        if(planType== "numerical" && resetStreak) {
            if(planSystem == "pgh"){
                data["currentDayIndex"] = increaseIntPref(name="currentDayIndex", value=1)
            }else{
                data["mcheyneCurrentDayIndex"] = increaseIntPref(name="mcheyneCurrentDayIndex", value=1)
            }
        }
        if(!getBoolPref(name="holdPlan") || getIntPref(name="${listStart}Done") == doneMax) {
            data["${listStart}sDone"] = setIntPref(name="${listStart}Done", value=0)
        }

        if(isLogged != null) {
            db.collection("main").document(isLogged.uid).update(data)
        }
    }
    private fun resetList(listName: String, listNameDone: String, maxDone:Int, data:MutableMap<String, Any>, listStart:String): MutableMap<String, Any>{
        if(!getBoolPref(name="holdPlan") || getIntPref(name="${listStart}sDone") == maxDone) {
            if(listName != "list6" || (listName == "list6" && !getBoolPref(name="psalms"))) {
                data[listName] = increaseIntPref(listName, value=1)
            }
            data["${listNameDone}Daily"] = setIntPref(name="${listNameDone}Daily", value=0)
            data[listNameDone] = setIntPref(listNameDone, value=0)
        }
        return data
    }
}
