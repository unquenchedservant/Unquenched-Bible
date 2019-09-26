package com.theunquenchedservant.pghsystem.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.theunquenchedservant.pghsystem.sharedPref.listNumberEditInt
import com.theunquenchedservant.pghsystem.sharedPref.listNumberReadInt
import com.theunquenchedservant.pghsystem.sharedPref.statisticsEdit
import com.theunquenchedservant.pghsystem.sharedPref.statisticsRead
import com.theunquenchedservant.pghsystem.MainActivity.Companion.log

class dailyCheck : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        log("RECEIVED INTENT")
        log("Daily streak value - ${statisticsRead(context, "dailyStreak")}")
        when(statisticsRead(context, "dailyStreak")) {
            1 -> {
                statisticsEdit(context, "dailyStreak", 0)
                log("dailyStreak value set to 0")
            }
            0 -> {
                statisticsEdit(context, "currentStreak", 0)
                log("current streak set to 0 because dailyStreak was 0")
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
