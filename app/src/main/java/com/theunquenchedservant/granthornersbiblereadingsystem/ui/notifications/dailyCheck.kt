package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.sharedPref.listNumberEditInt
import com.theunquenchedservant.granthornersbiblereadingsystem.sharedPref.listNumberReadInt
import com.theunquenchedservant.granthornersbiblereadingsystem.sharedPref.statisticsEdit
import com.theunquenchedservant.granthornersbiblereadingsystem.sharedPref.statisticsRead

class dailyCheck : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val vacation = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("vacation_mode", false)
        log("Vacation mode is $vacation")
        when(statisticsRead(context, "dailyStreak")){
            1 -> {
                statisticsEdit(context, "dailyStreak", 0)
                log("dailyStreak was set to 1, setting back to 0 for next day")
            }
            0 -> {
                when(vacation){
                    true -> {
                        log("Vacation mode is on, not changing current streak negatively")
                    }
                    false -> {
                        log("Vacation mode is off, changing current streak to 0")
                        statisticsEdit(context, "currentStreak", 0)
                    }
                }
            }
        }
        //RESETS ALL DONE LISTS AT MIDNIGHT EACH NIGHT
        when(listNumberReadInt(context, "list1Done")){
            1->{ resetList(context, "List 1", "list1Done")
            }}
        when(listNumberReadInt(context, "list2Done")){
            1->{ resetList(context, "List 2", "list2Done")
            }}
        when(listNumberReadInt(context, "list3Done")){
            1->{ resetList(context, "List 3", "list3Done")
            }}
        when(listNumberReadInt(context, "list4Done")){
            1->{ resetList(context, "List 4", "list4Done")
            }}
        when(listNumberReadInt(context, "list5Done")){
            1->{ resetList(context, "List 5", "list5Done")
            }}
        when(listNumberReadInt(context, "list6Done")){
            1->{ resetList(context, "List 6", "list6Done")
            }}
        when(listNumberReadInt(context, "list7Done")){
            1->{ resetList(context, "List 7", "list7Done")
            }}
        when(listNumberReadInt(context, "list8Done")){
            1->{ resetList(context, "List 8", "list8Done")
            }}
        when(listNumberReadInt(context, "list9Done")){
            1->{ resetList(context, "List 9", "list9Done")
            }}
        when(listNumberReadInt(context, "list10Done")){
            1->{ resetList(context, "List 10", "list10Done")
            }}
        listNumberEditInt(context, "listsDone", 0)
    }
    private fun resetList(context: Context, listName: String, listNameDone: String){
        log("Resetting $listName for dailyCheck")
        listNumberEditInt(context, listName, listNumberReadInt(context, listName) + 1)
        log("$listName index is now ${listNumberReadInt(context, listName)}")
        listNumberEditInt(context, listNameDone, 0)
        log("$listNameDone set to 0")
    }
}
