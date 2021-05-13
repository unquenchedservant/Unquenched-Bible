package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.isWeekend
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractStringPref

class DailyCheck : BroadcastReceiver() {
    private val isLogged = Firebase.auth.currentUser

    override fun onReceive(context: Context, intent: Intent) {
        traceLog(file = "DailyCheck.kt", function = "onReceive()")
        var data = mutableMapOf<String, Any>()
        if(isLogged != null) {
            Firebase.firestore.collection("main").document(Firebase.auth.currentUser?.uid!!).get()
                    .addOnSuccessListener {
                        if (it.data != null) {
                            val currentData = it.data!!
                            val planSystem: String = extractStringPref(currentData, "planSystem")
                            val planType: String = extractStringPref(currentData, "planType")
                            val dateChecked: String = extractStringPref(currentData, "dateChecked")
                            val vacation: Boolean = extractBoolPref(currentData, "vacationMode")
                            val vacationOff: Boolean = extractBoolPref(currentData, "vacationOff")
                            val weekendMode: Boolean = extractBoolPref(currentData, "weekendMode")
                            val isGrace: Boolean = extractBoolPref(currentData, "isGrace")
                            val holdPlan: Boolean = extractBoolPref(currentData, "holdPlan")
                            val psalms: Boolean = extractBoolPref(currentData, "psalms")
                            val currentStreak: Int = extractIntPref(currentData, "currentStreak")
                            val dailyStreak: Int = extractIntPref(currentData, "dailyStreak")
                            var currentDayIndex: Int = extractIntPref(currentData, "currentDayIndex")
                            var mcheyneCurrentDayIndex: Int = extractIntPref(currentData, "mcheyneCurrentDayIndex")
                            val listStart = if (planSystem == "pgh") "list" else "mcheyneList"
                            val listsDone: Int = extractIntPref(currentData, "${listStart}Done")


                            val doneMax = when (planSystem) {
                                "pgh" -> 10
                                "mcheyne" -> 4
                                else -> 10
                            }
                            var resetStreak = false


                            when (dailyStreak) {
                                1 -> {
                                    data["dailyStreak"] = setIntPref(name = "dailyStreak", value = 0)
                                    data["graceTime"] = setIntPref(name = "graceTime", value = 0)
                                    resetStreak = true
                                }
                                0 -> {
                                    when (vacation || vacationOff || (weekendMode && isWeekend())) {
                                        false -> {
                                            if (!checkDate(dateChecked, option = "yesterday", fullMonth = false)) {
                                                if (!isGrace) {
                                                    data["holdStreak"] = setIntPref(name = "holdStreak", currentStreak)
                                                    data["isGrace"] = setBoolPref(name = "isGrace", value = true)
                                                } else {
                                                    data["isGrace"] = setBoolPref(name = "isGrace", value = false)
                                                    data["holdStreak"] = setIntPref(name = "holdStreak", value = 0)
                                                }
                                                data["currentStreak"] = setIntPref(name = "currentStreak", value = 0)
                                            }
                                        }
                                        true -> {
                                            if (vacationOff) {
                                                data["vacationOff"] = setBoolPref(name = "vacationOff", value = false)
                                            }
                                        }
                                    }
                                }
                            }
                            for (i in 1..doneMax) {
                                val listName = "${listStart}$i"
                                if (planType == "horner") {
                                    val listDone: Int = extractIntPref(it.data!!, "${listName}Done")
                                    val listIndex: Int = extractIntPref(it.data!!, listName)
                                    data["${listName}Done"] = setIntPref(name="${listName}Done", value=0)
                                    data["${listName}DoneDaily"] = setIntPref(name="${listName}DoneDaily", value=0)
                                    if(listDone == 1) data = resetList(listName =listName, listNameDone="${listName}Done", doneMax, data, holdPlan, listsDone, psalms, listIndex)

                                } else data["${listName}Done"] = setIntPref(name = "${listName}Done", value = 0)
                            }
                            if (planType == "numerical" && resetStreak) {
                                if (planSystem == "pgh") {
                                    currentDayIndex += 1
                                    data["currentDayIndex"] = setIntPref(name = "currentDayIndex", value = currentDayIndex)
                                } else {
                                    mcheyneCurrentDayIndex += 1
                                    data["mcheyneCurrentDayIndex"] = setIntPref(name = "mcheyneCurrentDayIndex", value = mcheyneCurrentDayIndex)
                                }
                            }
                            if (!holdPlan || listsDone == doneMax) {
                                data["${listStart}sDone"] = setIntPref(name = "${listStart}sDone", value = 0)
                            }

                            Firebase.firestore.collection("main").document(isLogged.uid).update(data)

                        }
                    }
        }

    }
    private fun resetList(listName: String, listNameDone: String, maxDone:Int, data:MutableMap<String, Any>, holdPlan:Boolean, listsDone:Int, psalms:Boolean, listIndex:Int): MutableMap<String, Any>{
        traceLog(file = "DailyCheck.kt", function = "resetList()")
        if(!holdPlan || listsDone == maxDone) {
            if(listName != "list6" || (listName == "list6" && !psalms)) {
                data[listName] = setIntPref(name=listName, value=listIndex + 1)
            }
            data["${listNameDone}Daily"] = setIntPref(name="${listNameDone}Daily", value=0)
            data[listNameDone] = setIntPref(listNameDone, value=0)
        }
        return data
    }
}
