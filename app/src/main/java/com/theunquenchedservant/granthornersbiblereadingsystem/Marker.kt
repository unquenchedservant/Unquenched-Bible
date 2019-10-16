package com.theunquenchedservant.granthornersbiblereadingsystem

import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.getCurrentDate
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberEditInt
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberEditString
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberReadInt
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.readEdit
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.readRead
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.statisticsEdit
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.statisticsRead
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.updateFS
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.util.*

object Marker {
    private val isLogged = FirebaseAuth.getInstance().currentUser
    fun markAll(context: Context?, button: Button, view: View?) {
        if (isLogged != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("main").document(isLogged.uid).get()
                    .addOnSuccessListener {
                        val result = it.data!!
                        var dailyStreak = (result["dailyStreak"] as Long).toInt()
                        var currentStreak = (result["currentStreak"] as Long).toInt()
                        var maxStreak = (result["maxStreak"] as Long).toInt()
                        val updateValues: Map<String, Any>
                        if (dailyStreak == 0) {
                            statisticsEdit(context, "currentStreak", currentStreak + 1)
                            currentStreak++
                            dailyStreak++
                            statisticsEdit(context, "dailyStreak", 1)
                            if (currentStreak > maxStreak) {
                                statisticsEdit(context, "maxStreak", currentStreak)
                                maxStreak++
                                updateValues = mapOf(
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
                                        "listsDone" to 10,
                                        "dateChecked" to getCurrentDate(false),
                                        "currentStreak" to currentStreak,
                                        "maxStreak" to maxStreak,
                                        "dailyStreak" to dailyStreak
                                )
                            } else {
                                updateValues = mapOf(
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
                                        "listsDone" to 10,
                                        "dateChecked" to getCurrentDate(false),
                                        "currentStreak" to currentStreak,
                                        "dailyStreak" to dailyStreak
                                )
                            }
                        }else{
                            updateValues = mapOf(
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
                                    "listsDone" to 10,
                                    "dateChecked" to getCurrentDate(false)
                            )
                        }

                        db.collection("main").document(isLogged.uid).update(updateValues)
                                .addOnSuccessListener {
                                    log("Successful update")
                                }
                                .addOnFailureListener {
                                    val error = it
                                    Log.w("PROFGRANT", "Failure writing to firestore", error)
                                }
                    }

        }
        when (listNumberReadInt(context, "listsDone")) {
            in 0..9 -> {
                val cardList1 = view?.cardList1
                val dailyStreak = statisticsRead(context, "dailyStreak")
                var currentStreak = statisticsRead(context, "currentStreak")
                val maxStreak = statisticsRead(context, "maxStreak")
                val cardList2 = view?.cardList2
                val cardList3 = view?.cardList3
                val cardList4 = view?.cardList4
                val cardList5 = view?.cardList5
                val cardList6 = view?.cardList6
                val cardList7 = view?.cardList7
                val cardList8 = view?.cardList8
                val cardList9 = view?.cardList9
                val cardList10 = view?.cardList10
                markSingle(context, cardList1!!, "list1Done", "List 1", R.array.list_1, button, true)
                markSingle(context, cardList2!!, "list2Done", "List 2", R.array.list_2, button, true)
                markSingle(context, cardList3!!, "list3Done", "List 3", R.array.list_3, button, true)
                markSingle(context, cardList4!!, "list4Done", "List 4", R.array.list_4, button, true)
                markSingle(context, cardList5!!, "list5Done", "List 5", R.array.list_5, button, true)
                markSingle(context, cardList6!!, "list6Done", "List 6", R.array.list_6, button, true)
                markSingle(context, cardList7!!, "list7Done", "List 7", R.array.list_7, button, true)
                markSingle(context, cardList8!!, "list8Done", "List 8", R.array.list_8, button, true)
                markSingle(context, cardList9!!, "list9Done", "List 9", R.array.list_9, button, true)
                markSingle(context, cardList10!!, "list10Done", "list 10", R.array.list_10, button, true)
                if (dailyStreak == 0) {
                    statisticsEdit(context, "currentStreak", currentStreak + 1)
                    currentStreak++
                    statisticsEdit(context, "dailyStreak", 1)
                    if (currentStreak > maxStreak) {
                        statisticsEdit(context, "maxStreak", currentStreak)
                    }

                }
                button.isEnabled = false
                button.setText(R.string.done)
                button.setBackgroundColor(Color.parseColor("#00383838"))
                val mNotificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                mNotificationManager.cancel(0)
            }
        }
    }
    fun markSingle(context: Context?, cardList: CardView?, cardName:String, listName:String, arrayId: Int, button:Button?, fromAll: Boolean) {
        val db = FirebaseFirestore.getInstance()
        if (cardList?.isEnabled!!) {
            cardList.isEnabled = false
            cardList.setCardBackgroundColor(Color.parseColor("#00383838"))
            var isNewMax = false
            val psalmSwitch = PreferenceManager.getDefaultSharedPreferences(context!!).getBoolean("psalms", false)
            val allowPartial = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("allow_partial_switch", false)
            val listsDone = listNumberReadInt(context, "listsDone")+1

            listNumberEditString(context, "dateChecked", getCurrentDate(false))
            listNumberEditInt(context, "listsDone", listsDone)

            if (listsDone == 10) {
                button?.setText(R.string.done)
                button?.setBackgroundColor(Color.parseColor("#00383838"))

            } else {
                button?.setText(R.string.markRemaining)
            }

            listNumberEditInt(context, cardName, 1)
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            when (listName) {
                "List 6" -> {
                    when (psalmSwitch) {
                        false -> markList(context, listName, arrayId, cardName)
                        true -> {
                            listNumberEditInt(context, "list6Done", 1)
                            markRead(context, "Ps. $day")
                            markRead(context, "Ps. ${day + 30}")
                            markRead(context, "Ps. ${day + 60}")
                            markRead(context, "Ps. ${day + 90}")
                            markRead(context, "Ps. ${day + 120}")
                        }
                    }
                }
                else -> markList(context, listName, arrayId, cardName)
            }
            var maxStreak  = 0
            var currentStreak = 0
            if (allowPartial) {
                if (statisticsRead(context, "dailyStreak") == 0) {
                    currentStreak = statisticsRead(context, "currentStreak") + 1
                    maxStreak = statisticsRead(context, "maxStreak")
                    statisticsEdit(context, "currentStreak", currentStreak)
                    isNewMax = if (currentStreak > maxStreak) {
                        statisticsEdit(context, "maxStreak", currentStreak)
                        true
                    }else{
                        false
                    }
                    statisticsEdit(context, "dailyStreak", 1)
                }
                button?.isEnabled = false
            }
            if(isLogged != null && fromAll){
                updateFS(cardName, 1)
            }else if(isLogged != null && !fromAll){
                val data : Map<String, Any> = if(isNewMax){
                    mapOf(
                            "maxStreak" to maxStreak + 1,
                            "currentStreak" to currentStreak,
                            "dailyStreak" to 1,
                            "dateChecked" to getCurrentDate(false),
                            cardName to 1
                    )
                }else {
                    mapOf(
                            "currentStreak" to currentStreak,
                            "dailyStreak" to 1,
                            "dateChecked" to getCurrentDate(false),
                            cardName to 1
                    )
                }
                db.collection("main").document(isLogged.uid).update(data)
            }
        }
    }
    fun markList(context: Context?, listString: String, arrayId: Int, listDoneName: String) {
        log("Start markList")
        listNumberEditInt(context, listDoneName, 1)
        log("Set $listDoneName to 1")
        val number = listNumberReadInt(context, listString)
        val list = context!!.resources.getStringArray(arrayId)
        log("passing list to markRead with current index")
        markRead(context, list[number])
        log("End markList")
    }

    private fun markRead(context: Context?, chapterName:String){
        log("Begin markRead")
        log("Checking Chapter - $chapterName")
        val beenRead = readRead(context, chapterName)
        log("beenRead Value - $beenRead")
        var totalRead = statisticsRead(context, "totalRead")
        when(beenRead){
            0->{
                readEdit(context, chapterName, 1)
                log("Been Read set to 1")
                totalRead++
                statisticsEdit(context, "totalRead", totalRead)
                log("totalRead added 1, total now - $totalRead")
            }
            else -> {
                log("Been read added 1(value should not have been 0 before)")
                readEdit(context, chapterName, beenRead + 1)
            }
        }
        log("End markRead")
    }
}