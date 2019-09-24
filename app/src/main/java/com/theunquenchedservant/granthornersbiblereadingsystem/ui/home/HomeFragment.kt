package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home


import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.createDailyCheck
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.getCurrentDate
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.markList
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.prefEditInt
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.prefEditString
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.prefReadInt
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.prefReadString
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import kotlinx.android.synthetic.main.fragment_home.*

import java.util.Calendar

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        createDailyCheck(context)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val str_today = getCurrentDate(false)
        val check = prefReadString(activity, "dateClicked")
        val button = root.findViewById<Button>(R.id.material_button)
        button.setBackgroundColor(Color.parseColor("#383838"))
        val cardList1 = root.findViewById<CardView>(R.id.cardList1)
        val cardList2 = root.findViewById<CardView>(R.id.cardList2)
        val cardList3 = root.findViewById<CardView>(R.id.cardList3)
        val cardList4 = root.findViewById<CardView>(R.id.cardList4)
        val cardList5 = root.findViewById<CardView>(R.id.cardList5)
        val cardList6 = root.findViewById<CardView>(R.id.cardList6)
        val cardList7 = root.findViewById<CardView>(R.id.cardList7)
        val cardList8 = root.findViewById<CardView>(R.id.cardList8)
        val cardList9 = root.findViewById<CardView>(R.id.cardList9)
        val cardList10 = root.findViewById<CardView>(R.id.cardList10)
        when(prefReadInt(context, "list1Done")){1 -> disableSingle(cardList1, button) }
        when(prefReadInt(context, "list2Done")){1 -> disableSingle(cardList2, button) }
        when(prefReadInt(context, "list3Done")){1 -> disableSingle(cardList3, button) }
        when(prefReadInt(context, "list4Done")){1 -> disableSingle(cardList4, button) }
        when(prefReadInt(context, "list5Done")){1 -> disableSingle(cardList5, button) }
        when(prefReadInt(context, "list6Done")){1 -> disableSingle(cardList6, button) }
        when(prefReadInt(context, "list7Done")){1 -> disableSingle(cardList7, button) }
        when(prefReadInt(context, "list8Done")){1 -> disableSingle(cardList8, button) }
        when(prefReadInt(context, "list9Done")){1 -> disableSingle(cardList9, button) }
        when(prefReadInt(context, "list10Done")){1 -> disableSingle(cardList10, button) }
        when(check){
            str_today ->{
                button.setText(R.string.done)
                button.setBackgroundColor(Color.parseColor("#00383838"))
                button.isEnabled = false
            }
            else -> {
                when(prefReadInt(context, "listsDone")){
                    in 0..9 -> (activity as AppCompatActivity).supportActionBar?.title = getCurrentDate(true)
                    10 -> {
                        button.setText(R.string.done)
                        button.setBackgroundColor(Color.parseColor("#00383838"))
                        button.isEnabled = false
                    }
                }
            }
        }
        (activity as AppCompatActivity).supportActionBar?.title = getCurrentDate(true)
        val sharedpref = PreferenceManager.getDefaultSharedPreferences(activity!!)
        setList(root, "List 1", R.array.list_1, R.id.list1_reading)
        setList(root, "List 2", R.array.list_2, R.id.list2_reading)
        setList(root, "List 3", R.array.list_3, R.id.list3_reading)
        setList(root, "List 4", R.array.list_4, R.id.list4_reading)
        setList(root, "List 5", R.array.list_5, R.id.list5_reading)
        val psCheck = sharedpref.getBoolean("psalms", false)
        if (psCheck) {
            val list_reading = root.findViewById<TextView>(R.id.list6_reading)
            val pal = "$day, ${day+30}, ${day + 60}, ${day + 90}, ${day + 120}"
            list_reading.text = pal
        } else {
            setList(root, "List 6", R.array.list_6, R.id.list6_reading)
        }
        setList(root, "List 7", R.array.list_7, R.id.list7_reading)
        setList(root, "List 8", R.array.list_8, R.id.list8_reading)
        setList(root, "List 9", R.array.list_9, R.id.list9_reading)
        setList(root, "List 10", R.array.list_10, R.id.list10_reading)
        cardList1.setOnClickListener{markSingle(cardList1, "list1Done", "List 1", R.array.list_1, button)}
        cardList2.setOnClickListener{markSingle(cardList2, "list2Done", "List 2", R.array.list_2, button)}
        cardList3.setOnClickListener{markSingle(cardList3, "list3Done", "List 3", R.array.list_3, button)}
        cardList4.setOnClickListener{markSingle(cardList4, "list4Done", "List 4", R.array.list_4, button)}
        cardList5.setOnClickListener{markSingle(cardList5, "list5Done", "List 5", R.array.list_5, button)}
        cardList6.setOnClickListener{markSingle(cardList6, "list6Done", "List 6", R.array.list_6, button)}
        cardList7.setOnClickListener{markSingle(cardList7, "list7Done", "List 7", R.array.list_7, button)}
        cardList8.setOnClickListener{markSingle(cardList8, "list8Done", "List 8", R.array.list_8, button)}
        cardList9.setOnClickListener{markSingle(cardList9, "list9Done", "List 9", R.array.list_9, button)}
        cardList10.setOnClickListener{markSingle(cardList10, "list10Done", "List 10", R.array.list_10, button)}
        button.setOnClickListener{markAll(button)}
        return root
    }

    private fun markAll(button: Button){
        val today = getCurrentDate(false)
        val check = prefReadString(context, "dateClicked") //date clicked
        if (check != today) {
            prefEditString(context, "dateClicked", today)
            markSingle(cardList1, "list1Done", "List 1", R.array.list_1, button)
            markSingle(cardList2, "list2Done", "List 2", R.array.list_2, button)
            markSingle(cardList3, "list3Done", "List 3", R.array.list_3, button)
            markSingle(cardList4, "list4Done", "List 4", R.array.list_4, button)
            markSingle(cardList5, "list5Done", "List 5", R.array.list_5, button)
            markSingle(cardList6, "list6Done", "List 6", R.array.list_6, button)
            markSingle(cardList7, "list7Done", "List 7", R.array.list_7, button)
            markSingle(cardList8, "list8Done", "List 8", R.array.list_8, button)
            markSingle(cardList9, "list9Done", "List 9", R.array.list_9, button)
            markSingle(cardList10, "list10Done", "list 10", R.array.list_10, button)
            if(prefReadInt(context, "dailyStreak")==0) {
                prefEditInt(context, "curStreak", prefReadInt(context, "curStreak") + 1)
                val maxStreak = prefReadInt(context, "maxStreak")
                if (prefReadInt(context, "curStreak") > maxStreak) {
                    prefEditInt(context, "maxStreak", prefReadInt(context, "curStreak"))
                }
                prefEditInt(context, "dailyStreak", 1)
            }
            button.isEnabled = false
            button.setText(R.string.done)
            button.setBackgroundColor(Color.parseColor("#00383838"))
            val mNotificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(0)
        }
    }
    private fun markSingle(cardList:CardView, cardName:String, listName:String, arrayId: Int, button:Button) {
        if (cardList.isEnabled) {
            cardList.isEnabled = false
            cardList.setCardBackgroundColor(Color.parseColor("#00383838"))
            prefEditInt(context, "listsDone", prefReadInt(context, "listsDone")+ 1)
            Log.d("CHECKING", "${prefReadInt(context, "listsDone")}")
            if(prefReadInt(context, "listsDone") == 10){
                button.setText(R.string.done)
                button.setBackgroundColor(Color.parseColor("#00383838"))
            }else{
                button.setText(R.string.markRemaining)
            }
            prefEditInt(context, cardName, 1)
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            when (listName) {
                "List 6" -> {
                    when (prefReadInt(context, "psalmSwitch")) {
                        0 -> markList(context, listName, arrayId, cardName)
                        1 -> {
                            prefEditInt(context, "list6Done", 1)
                            MainActivity.markRead(context, "Ps. $day")
                            MainActivity.markRead(context, "Ps. ${day + 30}")
                            MainActivity.markRead(context, "Ps. ${day + 60}")
                            MainActivity.markRead(context, "Ps. ${day + 90}")
                            MainActivity.markRead(context, "Ps. ${day + 120}")
                        }
                    }
                }
                else -> markList(context, listName, arrayId, cardName)
            }
            if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("allow_partial_switch", false)){
                if(prefReadInt(context, "dailyStreak")==0) {
                    prefEditInt(context, "curStreak", prefReadInt(context, "curStreak") + 1)
                    val maxStreak = prefReadInt(context, "maxStreak")
                    if (prefReadInt(context, "curStreak") > maxStreak) {
                        prefEditInt(context, "maxStreak", prefReadInt(context, "curStreak"))
                    }
                    prefEditInt(context, "dailyStreak", 1)
                }
                button.isEnabled = false
            }
        }
    }
    private fun disableSingle(cardList:CardView, button:Button){
        button.setText(R.string.markRemaining)
        cardList.isEnabled = false
        cardList.setCardBackgroundColor(Color.parseColor("#00383838"))
    }
    private fun setList(view: View, list_string: String, listId: Int, readingId: Int) {
        val res = resources
        val listNum = prefReadInt(activity, list_string)
        val list = res.getStringArray(listId)
        val reading = list[listNum]
        val listReading = view.findViewById<TextView>(readingId)
        listReading.text = reading
    }
}