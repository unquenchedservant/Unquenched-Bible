package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.marker.markList
import com.theunquenchedservant.granthornersbiblereadingsystem.sharedPref.listNumberReadInt

class doneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val listsDone = listNumberReadInt(context, "listsDone")
        when(listsDone) {
            in 0..9 -> {
                markList(context, "List 1", R.array.list_1, "list1Done")
                markList(context, "List 2", R.array.list_2, "list2Done")
                markList(context, "List 3", R.array.list_3, "list3Done")
                markList(context, "List 4", R.array.list_4, "list4Done")
                markList(context, "List 5", R.array.list_5, "list5Done")
                markList(context, "List 6", R.array.list_6, "list6Done")
                markList(context, "List 7", R.array.list_7, "list7Done")
                markList(context, "List 8", R.array.list_8, "list8Done")
                markList(context, "List 9", R.array.list_9, "list9Done")
                markList(context, "List 10", R.array.list_10, "list10Done")
                Toast.makeText(context, "Lists Marked!", Toast.LENGTH_LONG)
                        .show()
            }
            10 -> Toast.makeText(context, "Already Done!", Toast.LENGTH_LONG).show()
        }
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(0)

    }
}