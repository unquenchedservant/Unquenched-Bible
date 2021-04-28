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
        var data = mutableMapOf<String, Any>()
        if(isLogged != null) {
            Firebase.firestore.collection("main").document(Firebase.auth.currentUser?.uid!!).get()
                    .addOnSuccessListener {
                        if (it.data != null) {
                            val planSystem: String = it.data!!["planSystem"] as String
                            val planType: String = it.data!!["planType"] as String
                            val dateChecked: String = it.data!!["dateChecked"] as String
                            val vacation: Boolean = it.data!!["vacationMode"] as Boolean
                            val vacationOff: Boolean = it.data!!["vacationOff"] as Boolean
                            val weekendMode: Boolean = it.data!!["weekendMode"] as Boolean
                            val isGrace: Boolean = it.data!!["isGrace"] as Boolean
                            val holdPlan: Boolean = it.data!!["holdPlan"] as Boolean
                            val psalms: Boolean = it.data!!["psalms"] as Boolean
                            val currentStreak: Int = (it.data!!["currentStreak"] as Long).toInt()
                            val dailyStreak: Int = (it.data!!["dailyStreak"] as Long).toInt()
                            var currentDayIndex: Int = (it.data!!["currentDayIndex"] as Long).toInt()
                            var mcheyneCurrentDayIndex: Int = (it.data!!["mcheyneCurrentDayIndex"] as Long).toInt()
                            val listStart = if (planSystem == "pgh") "list" else "mcheyneList"
                            val listsDone: Int = (it.data!!["${listStart}sDone"] as Long).toInt()

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
                                if (planType == "horner") {
                                    val listDoneDaily: Int = (it.data!!["${listStart}${i}DoneDaily"] as Long).toInt()
                                    val listDone: Int = (it.data!!["${listStart}Done"] as Long).toInt()
                                    when (listDoneDaily) {
                                        1 -> {
                                            data["${listStart}${i}DoneDaily"] = setIntPref(name = "${listStart}${i}DoneDaily", value=0)
                                        }
                                    }
                                    when (listDone) {
                                        1 -> {
                                            val listIndex: Int = (it.data!!["$listStart${i}"] as Long).toInt()
                                            data = resetList(listName = "${listStart}${i}", listNameDone = "${listStart}${i}Done", doneMax, data, listStart, holdPlan, listsDone, psalms, listIndex)
                                        }
                                    }
                                } else data["${listStart}${i}Done"] = setIntPref(name = "${listStart}${i}Done", value = 0)
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
    private fun resetList(listName: String, listNameDone: String, maxDone:Int, data:MutableMap<String, Any>, listStart:String, holdPlan:Boolean, listsDone:Int, psalms:Boolean, listIndex:Int): MutableMap<String, Any>{
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
