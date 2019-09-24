package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.theunquenchedservant.granthornersbiblereadingsystem.sharedPref.listNumberEditInt
import com.theunquenchedservant.granthornersbiblereadingsystem.sharedPref.listNumberReadInt
import com.theunquenchedservant.granthornersbiblereadingsystem.sharedPref.statisticsEdit
import com.theunquenchedservant.granthornersbiblereadingsystem.sharedPref.statisticsRead

class dailyCheck : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when(statisticsRead(context, "dailyStreak")) {
            1 -> statisticsEdit(context, "dailyStreak", 0)
            0 -> statisticsEdit(context, "curStreak", 0)
        }
        //RESETS ALL DONE LISTS AT MIDNIGHT EACH NIGHT
        when(listNumberReadInt(context, "list1Done")){
            1->{
                listNumberEditInt(context, "List 1", listNumberReadInt(context, "List 1") + 1);
                listNumberEditInt(context, "list1Done", 0)
            }
        }
        when(listNumberReadInt(context, "list2Done")){
            1->{
                listNumberEditInt(context, "List 2", listNumberReadInt(context, "List 2") + 1);
                listNumberEditInt(context, "list2Done", 0)
            }
        }
        when(listNumberReadInt(context, "list3Done")){
            1->{
                listNumberEditInt(context, "List 3", listNumberReadInt(context, "List 3") + 1);
                listNumberEditInt(context, "list3Done", 0)
            }
        }
        when(listNumberReadInt(context, "list4Done")){
            1->{
                listNumberEditInt(context, "List 4", listNumberReadInt(context, "List 4") + 1)
                listNumberEditInt(context, "list4Done", 0)
            }
        }
        when(listNumberReadInt(context, "list5Done")){
            1->{
                listNumberEditInt(context, "List 5", listNumberReadInt(context, "List 5") + 1)
                listNumberEditInt(context, "list5Done", 0)
            }
        }
        when(listNumberReadInt(context, "list6Done")){
            1->{
                listNumberEditInt(context, "List 6", listNumberReadInt(context, "List 6") + 1)
                listNumberEditInt(context, "list6Done", 0)
            }
        }
        when(listNumberReadInt(context, "list7Done")){
            1->{
                listNumberEditInt(context, "List 7", listNumberReadInt(context, "List 7") + 1)
                listNumberEditInt(context, "list7Done", 0)
            }
        }
        when(listNumberReadInt(context, "list8Done")){
            1->{
                listNumberEditInt(context, "List 8", listNumberReadInt(context, "List 8") + 1)
                listNumberEditInt(context, "list8Done", 0)
            }
        }
        when(listNumberReadInt(context, "list9Done")){
            1->{
                listNumberEditInt(context, "List 9", listNumberReadInt(context, "List 9") + 1)
                listNumberEditInt(context, "list9Done", 0)
            }
        }
        when(listNumberReadInt(context, "list10Done")){
            1->{
                listNumberEditInt(context, "List 10", listNumberReadInt(context, "List 10") + 1)
                listNumberEditInt(context, "list10Done", 0)
            }
        }
        listNumberEditInt(context, "listsDone", 0)
    }
}
