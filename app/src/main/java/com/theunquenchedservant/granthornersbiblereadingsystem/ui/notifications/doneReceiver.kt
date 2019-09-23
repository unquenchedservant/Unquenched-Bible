package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.getCurrentDate
import com.theunquenchedservant.granthornersbiblereadingsystem.R

class doneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val check = MainActivity.prefReadString(context, "dateClicked")
        val today = getCurrentDate(false)
        if (check != today) {
            MainActivity.markList(context, "List 1", R.array.list_1, "list1Done")
            MainActivity.markList(context,"List 2", R.array.list_2, "list2Done")
            MainActivity.markList(context,"List 3", R.array.list_3, "list3Done")
            MainActivity.markList(context,"List 4", R.array.list_4, "list4Done")
            MainActivity.markList(context,"List 5", R.array.list_5, "list5Done")
            MainActivity.markList(context,"List 6", R.array.list_6, "list6Done")
            MainActivity.markList(context,"List 7", R.array.list_7, "list7Done")
            MainActivity.markList(context,"List 8", R.array.list_8, "list8Done")
            MainActivity.markList(context,"List 9", R.array.list_9, "list9Done")
            MainActivity.markList(context,"List 10", R.array.list_10, "list10Done")
            Toast.makeText(context,"Lists Marked!", Toast.LENGTH_LONG)
                    .show()
            MainActivity.prefEditString(context, "dateClicked", today)
        } else {
            Toast.makeText(context, "Already Done!", Toast.LENGTH_LONG).show()
        }
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(0)

    }
}