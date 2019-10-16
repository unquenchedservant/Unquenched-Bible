package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.getYesterdayDate
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberEditInt
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberReadInt
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberReadString
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.statisticsEdit
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.statisticsRead

class DailyCheck : BroadcastReceiver() {
    private val isLogged = FirebaseAuth.getInstance().currentUser
    override fun onReceive(context: Context, intent: Intent) {
        val db = FirebaseFirestore.getInstance()
        var resetStreak  = false
        var resetCurrent = false
        val vacation = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("vacation_mode", false) //GOOD
        log("Vacation mode is $vacation")
        when (statisticsRead(context, "dailyStreak")) {
            1 -> {
                statisticsEdit(context, "dailyStreak", 0)
                resetStreak = true
                log("dailyStreak was set to 1, setting back to 0 for next day")
            }
            0 -> {
                when (vacation) {
                    true -> {
                        log("Vacation mode is on, not changing current streak negatively")
                    }
                    false -> {
                        when(listNumberReadString(context, "dateChecked")){
                            getYesterdayDate(false) -> {
                                log("still within a day of the last check, not resetting current value")
                            }
                            else -> {
                                log("Vacation mode is off, changing current streak to 0")
                                resetCurrent = true
                                statisticsEdit(context, "currentStreak", 0)
                            }
                        }
                    }
                }
            }
        }
        //RESETS ALL DONE LISTS AT MIDNIGHT EACH NIGHT
        when (listNumberReadInt(context, "list1Done")) {
            1 -> {
                resetList(context, "List 1", "list1Done")
            }
        }
        when (listNumberReadInt(context, "list2Done")) {
            1 -> {
                resetList(context, "List 2", "list2Done")
            }
        }
        when (listNumberReadInt(context, "list3Done")) {
            1 -> {
                resetList(context, "List 3", "list3Done")
            }
        }
        when (listNumberReadInt(context, "list4Done")) {
            1 -> {
                resetList(context, "List 4", "list4Done")
            }
        }
        when (listNumberReadInt(context, "list5Done")) {
            1 -> {
                resetList(context, "List 5", "list5Done")
            }
        }
        when (listNumberReadInt(context, "list6Done")) {
            1 -> {
                resetList(context, "List 6", "list6Done")
            }
        }
        when (listNumberReadInt(context, "list7Done")) {
            1 -> {
                resetList(context, "List 7", "list7Done")
            }
        }
        when (listNumberReadInt(context, "list8Done")) {
            1 -> {
                resetList(context, "List 8", "list8Done")
            }
        }
        when (listNumberReadInt(context, "list9Done")) {
            1 -> {
                resetList(context, "List 9", "list9Done")
            }
        }
        when (listNumberReadInt(context, "list10Done")) {
            1 -> {
                resetList(context, "List 10", "list10Done")
            }
        }
        listNumberEditInt(context, "listsDone", 0)
        if(isLogged != null) {
            val list1 = listNumberReadInt(context, "List 1")
            val list1Done = listNumberReadInt(context, "list1Done")
            val list2 = listNumberReadInt(context, "List 2")
            val list2Done = listNumberReadInt(context, "list2Done")
            val list3 = listNumberReadInt(context, "List 3")
            val list3Done = listNumberReadInt(context, "list3Done")
            val list4 = listNumberReadInt(context, "List 4")
            val list4Done = listNumberReadInt(context, "list4Done")
            val list5 = listNumberReadInt(context, "List 5")
            val list5Done = listNumberReadInt(context, "list5Done")
            val list6 = listNumberReadInt(context, "List 6")
            val list6Done = listNumberReadInt(context, "list6Done")
            val list7 = listNumberReadInt(context, "List 7")
            val list7Done = listNumberReadInt(context, "list7Done")
            val list8 = listNumberReadInt(context, "List 8")
            val list8Done = listNumberReadInt(context, "list8Done")
            val list9 = listNumberReadInt(context, "List 9")
            val list9Done = listNumberReadInt(context, "list9Done")
            val list10 = listNumberReadInt(context, "List 10")
            val list10Done = listNumberReadInt(context, "list10Done")
            val data : Map<String, Any>
            if(resetCurrent) {
                data = mapOf(
                        "list1" to list1,
                        "list1Done" to list1Done,
                        "list2" to list2,
                        "list2Done" to list2Done,
                        "list3" to list3,
                        "list3Done" to list3Done,
                        "list4" to list4,
                        "list4Done" to list4Done,
                        "list5" to list5,
                        "list5Done" to list5Done,
                        "list6" to list6,
                        "list6Done" to list6Done,
                        "list7" to list7,
                        "list7Done" to list7Done,
                        "list8" to list8,
                        "list8Done" to list8Done,
                        "list9" to list9,
                        "list9Done" to list9Done,
                        "list10" to list10,
                        "list10Done" to list10Done,
                        "listsDone" to 0,
                        "currentStreak" to 0
                )
            }else if(resetStreak) {
                data = mapOf(
                        "list1" to list1,
                        "list1Done" to list1Done,
                        "list2" to list2,
                        "list2Done" to list2Done,
                        "list3" to list3,
                        "list3Done" to list3Done,
                        "list4" to list4,
                        "list4Done" to list4Done,
                        "list5" to list5,
                        "list5Done" to list5Done,
                        "list6" to list6,
                        "list6Done" to list6Done,
                        "list7" to list7,
                        "list7Done" to list7Done,
                        "list8" to list8,
                        "list8Done" to list8Done,
                        "list9" to list9,
                        "list9Done" to list9Done,
                        "list10" to list10,
                        "list10Done" to list10Done,
                        "listsDone" to 0,
                        "dailyStreak" to 0
                )
            }else{
                data = mapOf(
                        "list1" to list1,
                        "list1Done" to list1Done,
                        "list2" to list2,
                        "list2Done" to list2Done,
                        "list3" to list3,
                        "list3Done" to list3Done,
                        "list4" to list4,
                        "list4Done" to list4Done,
                        "list5" to list5,
                        "list5Done" to list5Done,
                        "list6" to list6,
                        "list6Done" to list6Done,
                        "list7" to list7,
                        "list7Done" to list7Done,
                        "list8" to list8,
                        "list8Done" to list8Done,
                        "list9" to list9,
                        "list9Done" to list9Done,
                        "list10" to list10,
                        "list10Done" to list10Done,
                        "listsDone" to 0
                )
            }
            db.collection("main").document(isLogged.uid).update(data)
        }
    }
    private fun resetList(context: Context, listName: String, listNameDone: String){
        log("Resetting $listName for DailyCheck")
        listNumberEditInt(context, listName, listNumberReadInt(context, listName) + 1)
        log("$listName index is now ${listNumberReadInt(context, listName)}")
        listNumberEditInt(context, listNameDone, 0)
        log("$listNameDone set to 0")
    }
}
