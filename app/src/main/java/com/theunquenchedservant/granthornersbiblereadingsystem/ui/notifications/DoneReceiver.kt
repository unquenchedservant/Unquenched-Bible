package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.getCurrentDate
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.Marker.markList
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberEditInt
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberEditString
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberReadInt
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.statisticsEdit
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.statisticsRead

class DoneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val listsDone = listNumberReadInt(context, "listsDone")
        var currentStreak = 0
        var maxStreak = 0
        when(listsDone) {
            in 0..9 -> {
                val isLogged = FirebaseAuth.getInstance().currentUser
                val db = FirebaseFirestore.getInstance()
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
                listNumberEditString(context, "dateChecked", getCurrentDate(false))
                listNumberEditInt(context, "listsDone", 10)
                if(statisticsRead(context, "dailyStreak") != 1){
                    currentStreak = statisticsRead(context, "currentStreak") + 1
                    statisticsEdit(context, "currentStreak", currentStreak)
                    maxStreak = if(statisticsRead(context, "maxStreak") < currentStreak){
                        statisticsEdit(context, "maxStreak", currentStreak)
                        currentStreak
                    }else{
                        statisticsRead(context, "maxStreak")
                    }
                }
                if(isLogged != null) {
                    val data = mapOf(
                            "list1Done" to 1,
                            "list2Done" to 1,
                            "list3Done" to 1,
                            "list4Done" to 1,
                            "list5Done" to 1,
                            "list6Done" to 1,
                            "list7Done" to 1,
                            "list8Done" to 1,
                            "list9Done" to 1,
                            "list10Done" to 1,
                            "currentStreak" to currentStreak,
                            "maxStreak" to maxStreak,
                            "listsDone" to 10,
                            "dateChecked" to getCurrentDate(false)
                            )
                    db.collection("main").document(isLogged.uid).update(data)
                }
                Toast.makeText(context, "Lists Marked!", Toast.LENGTH_LONG)
                        .show()
            }
            10 -> Toast.makeText(context, "Already Done!", Toast.LENGTH_LONG).show()
        }
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(1)
        mNotificationManager.cancel(2)

    }
}