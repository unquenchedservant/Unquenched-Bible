package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity

class dailyCheck : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val check = MainActivity.prefReadString(context, "dateClicked")
        val str_today = MainActivity.getYesterdayDate(false)
        if (check != str_today) {
            MainActivity.prefEditInt(context, "curStreak", 0)
        }
    }
}