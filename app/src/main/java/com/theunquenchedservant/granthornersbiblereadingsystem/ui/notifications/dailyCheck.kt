package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.prefEditInt
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.prefReadInt

class dailyCheck : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when(prefReadInt(context, "dailyStreak")) {
            1 -> prefEditInt(context, "dailyStreak", 0)
            0 -> prefEditInt(context, "curStreak", 0)
        }
        //RESETS ALL DONE LISTS AT MIDNIGHT EACH NIGHT
        when(prefReadInt(context, "list1Done")){1->{ prefEditInt(context, "List 1", prefReadInt(context, "List 1") + 1); prefEditInt(context, "list1Done", 0)}}
        when(prefReadInt(context, "list2Done")){1->{ prefEditInt(context, "List 2", prefReadInt(context, "List 2") + 1); prefEditInt(context, "list2Done", 0)}}
        when(prefReadInt(context, "list3Done")){1->{ prefEditInt(context, "List 3", prefReadInt(context, "List 3") + 1); prefEditInt(context, "list3Done", 0)}}
        when(prefReadInt(context, "list4Done")){1->{ prefEditInt(context, "List 4", prefReadInt(context, "List 4") + 1); prefEditInt(context, "list4Done", 0)}}
        when(prefReadInt(context, "list5Done")){1->{ prefEditInt(context, "List 5", prefReadInt(context, "List 5") + 1); prefEditInt(context, "list5Done", 0)}}
        when(prefReadInt(context, "list6Done")){1->{ prefEditInt(context, "List 6", prefReadInt(context, "List 6") + 1); prefEditInt(context, "list6Done", 0)}}
        when(prefReadInt(context, "list7Done")){1->{ prefEditInt(context, "List 7", prefReadInt(context, "List 7") + 1); prefEditInt(context, "list7Done", 0)}}
        when(prefReadInt(context, "list8Done")){1->{ prefEditInt(context, "List 8", prefReadInt(context, "List 8") + 1); prefEditInt(context, "list8Done", 0)}}
        when(prefReadInt(context, "list9Done")){1->{ prefEditInt(context, "List 9", prefReadInt(context, "List 9") + 1); prefEditInt(context, "list9Done", 0)}}
        when(prefReadInt(context, "list10Done")){1->{ prefEditInt(context, "List 10", prefReadInt(context, "List 10") + 1); prefEditInt(context, "list10Done", 0)}}
        prefEditInt(context, "listsDone", 0)
    }
}
