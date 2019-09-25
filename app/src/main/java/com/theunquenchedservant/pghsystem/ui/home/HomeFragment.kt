package com.theunquenchedservant.pghsystem.ui.home

import android.graphics.Color
import android.os.Bundle
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

import com.theunquenchedservant.pghsystem.MainActivity.Companion.createDailyCheck
import com.theunquenchedservant.pghsystem.MainActivity.Companion.getCurrentDate
import com.theunquenchedservant.pghsystem.MainActivity.Companion.log
import com.theunquenchedservant.pghsystem.R
import com.theunquenchedservant.pghsystem.marker.markAll
import com.theunquenchedservant.pghsystem.marker.markSingle
import com.theunquenchedservant.pghsystem.sharedPref.clearOldPref
import com.theunquenchedservant.pghsystem.sharedPref.listNumberEditInt
import com.theunquenchedservant.pghsystem.sharedPref.listNumberReadInt
import com.theunquenchedservant.pghsystem.sharedPref.prefReadInt
import com.theunquenchedservant.pghsystem.sharedPref.readEdit
import com.theunquenchedservant.pghsystem.sharedPref.readRead
import com.theunquenchedservant.pghsystem.sharedPref.statisticsEdit
import com.theunquenchedservant.pghsystem.sharedPref.statisticsRead

import java.util.Calendar

class HomeFragment : Fragment() {
    private var cardList1 : CardView? = null
    private var cardList2 : CardView? = null
    private var cardList3 : CardView? = null
    private var cardList4 : CardView? = null
    private var cardList5 : CardView? = null
    private var cardList6 : CardView? = null
    private var cardList7 : CardView? = null
    private var cardList8 : CardView? = null
    private var cardList9 : CardView? = null
    private var cardList10 : CardView? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        cardList1 = root.findViewById(R.id.cardList1)!!
        cardList2 = root.findViewById(R.id.cardList2)!!
        cardList3 = root.findViewById(R.id.cardList3)!!
        cardList4 = root.findViewById(R.id.cardList4)!!
        cardList5 = root.findViewById(R.id.cardList5)!!
        cardList6 = root.findViewById(R.id.cardList6)!!
        cardList7 = root.findViewById(R.id.cardList7)!!
        cardList8 = root.findViewById(R.id.cardList8)!!
        cardList9 = root.findViewById(R.id.cardList9)!!
        cardList10 = root.findViewById(R.id.cardList10)!!
        temporaryChange()
        createDailyCheck(context)
        listNumberEditInt(context, "List 4", 65)
        val button = root.findViewById<Button>(R.id.material_button)
        button.setBackgroundColor(Color.parseColor("#383838"))
        checkLists(button)
        createListeners(button, root)
        setLists(root)
        when(listNumberReadInt(context, "listsDone")){
            10 -> {
                button.setText(R.string.done)
                button.setBackgroundColor(Color.parseColor("#00383838"))
                button.isEnabled = false
            }
        }
        (activity as AppCompatActivity).supportActionBar?.title = getCurrentDate(true)
        return root
    }
    private fun setLists(root:View){
        val sharedpref = PreferenceManager.getDefaultSharedPreferences(activity!!)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        setList(root, "List 1", R.array.list_1, R.id.list1_reading)
        setList(root, "List 2", R.array.list_2, R.id.list2_reading)
        setList(root, "List 3", R.array.list_3, R.id.list3_reading)
        setList(root, "List 4", R.array.list_4, R.id.list4_reading)
        setList(root, "List 5", R.array.list_5, R.id.list5_reading)
        val psCheck = sharedpref.getBoolean("psalms", false)
        if (psCheck) {
            val listReading = root.findViewById<TextView>(R.id.list6_reading)
            val pal = "$day, ${day+30}, ${day + 60}, ${day + 90}, ${day + 120}"
            listReading.text = pal
        } else {
            setList(root, "List 6", R.array.list_6, R.id.list6_reading)
        }
        setList(root, "List 7", R.array.list_7, R.id.list7_reading)
        setList(root, "List 8", R.array.list_8, R.id.list8_reading)
        setList(root, "List 9", R.array.list_9, R.id.list9_reading)
        setList(root, "List 10", R.array.list_10, R.id.list10_reading)
    }
    private fun temporaryChange(){
        val check = listNumberReadInt(context, "movedValues")
        when(check){
            0 ->{
                val stringArray1 = resources.getStringArray(R.array.list_1)
                val stringArray2 = resources.getStringArray(R.array.list_2)
                val stringArray3 = resources.getStringArray(R.array.list_3)
                val stringArray4 = resources.getStringArray(R.array.list_4)
                val stringArray5 = resources.getStringArray(R.array.list_5)
                val stringArray6 = resources.getStringArray(R.array.list_6)
                val stringArray7 = resources.getStringArray(R.array.list_7)
                val stringArray8 = resources.getStringArray(R.array.list_8)
                val stringArray9 = resources.getStringArray(R.array.list_9)
                val stringArray10 = resources.getStringArray(R.array.list_10)
                val list1 = prefReadInt(context, "List 1")
                val list2 = prefReadInt(context, "List 2")
                val list3 = prefReadInt(context, "List 3")
                val list4 = prefReadInt(context, "List 4")
                val list5 = prefReadInt(context, "List 5")
                val list6 = prefReadInt(context, "List 6")
                val list7 = prefReadInt(context, "List 7")
                val list8 = prefReadInt(context, "List 8")
                val list9 = prefReadInt(context, "List 9")
                val list10 = prefReadInt(context, "List 10")
                val daily = prefReadInt(context, "dailyStreak")
                val current = prefReadInt(context, "curStreak")
                val max = prefReadInt(context, "maxStreak")
                val total = prefReadInt(context, "totalRead")
                listNumberEditInt(context, "list1Done", prefReadInt(context, "list1Done"))
                log("list1Done set to - ${listNumberReadInt(context, "list1Done")}")
                listNumberEditInt(context, "list2Done", prefReadInt(context, "list2Done"))
                log("list2Done set to - ${listNumberReadInt(context, "list2Done")}")
                listNumberEditInt(context, "list3Done", prefReadInt(context, "list3Done"))
                log("list3Done set to - ${listNumberReadInt(context, "list3Done")}")
                listNumberEditInt(context, "list4Done", prefReadInt(context, "list4Done"))
                log("list4Done set to - ${listNumberReadInt(context, "list4Done")}")
                listNumberEditInt(context, "list5Done", prefReadInt(context, "list5Done"))
                log("list5Done set to - ${listNumberReadInt(context, "list5Done")}")
                listNumberEditInt(context, "list6Done", prefReadInt(context, "list6Done"))
                log("list6Done set to - ${listNumberReadInt(context, "list6Done")}")
                listNumberEditInt(context, "list7Done", prefReadInt(context, "list7Done"))
                log("list7Done set to - ${listNumberReadInt(context, "list7Done")}")
                listNumberEditInt(context, "list8Done", prefReadInt(context, "list8Done"))
                log("list8Done set to - ${listNumberReadInt(context, "list8Done")}")
                listNumberEditInt(context, "list9Done", prefReadInt(context, "list9Done"))
                log("list9Done set to - ${listNumberReadInt(context, "list9Done")}")
                listNumberEditInt(context, "list10Done", prefReadInt(context, "list10Done"))
                log("list10Done set to - ${listNumberReadInt(context, "list10Done")}")
                listNumberEditInt(context, "listsDone", prefReadInt(context, "listsDone"))
                log("listsDone set to - ${listNumberReadInt(context, "listsDone")}")
                when(list1){!in 0..0 -> {
                    listNumberEditInt(context, "List 1", list1); log("list1 set to ${listNumberReadInt(context, "List 1")}")
                }}
                when(list2){!in 0..0 -> {
                    listNumberEditInt(context, "List 2", list2);log("list2 set to ${listNumberReadInt(context, "List 2")}")
                }}
                when(list3){!in 0..0 -> {
                    listNumberEditInt(context, "List 3", list3);log("list3 set to ${listNumberReadInt(context, "List 3")}")
                }}
                when(list4){!in 0..0 -> {
                    listNumberEditInt(context, "List 4", list4);log("list4 set to ${listNumberReadInt(context, "List 4")}")
                }}
                when(list5){!in 0..0 -> {
                    listNumberEditInt(context, "List 5", list5);log("list5 set to ${listNumberReadInt(context, "List 5")}")
                }}
                when(list6){!in 0..0 -> {
                    listNumberEditInt(context, "List 6", list6);log("list6 set to ${listNumberReadInt(context, "List 6")}")
                }}
                when(list7){!in 0..0 -> {
                    listNumberEditInt(context, "List 7", list7);log("list7 set to ${listNumberReadInt(context, "List 7")}")
                }}
                when(list8){!in 0..0 -> {
                    listNumberEditInt(context, "List 8", list8);log("list8 set to ${ listNumberReadInt(context, "List 8")}")
                }}
                when(list9){!in 0..0 -> {
                    listNumberEditInt(context, "List 9", list9);log("list9 set to ${ listNumberReadInt(context, "List 9")}")
                }}
                when(list10){!in 0..0 -> {
                    listNumberEditInt(context, "List 10", list10);log("list10 set to ${ listNumberReadInt(context, "List 10")}")
                }}
                when(daily){!in 0..0->{
                    statisticsEdit(context, "dailyStreak", daily);log("dailyStreak set to -  ${ statisticsRead(context, "dailyStreak")}")
                }}
                when(current){!in 0..0->{
                    statisticsEdit(context, "currentStreak", current);log("currentStreak set to ${ statisticsRead(context, "currentStreak")}")
                }}
                when(max){!in 0..0->{
                    statisticsEdit(context, "maxStreak", max);log("maxStreak set to ${ statisticsRead(context, "maxStreak")}")
                }}
                when(total){!in 0..0->{
                    statisticsEdit(context, "totalRead", total);log("totalRead set to ${ statisticsRead(context, "totalRead")}")
                }}
                for(item in stringArray1){ readEdit(context, item, prefReadInt(context, item));log("$item number set to - ${readRead(context, item)}") }
                for(item in stringArray2){ readEdit(context, item, prefReadInt(context, item));log("$item number set to - ${readRead(context, item)}") }
                for(item in stringArray3){ readEdit(context, item, prefReadInt(context, item));log("$item number set to - ${readRead(context, item)}") }
                for(item in stringArray4){ readEdit(context, item, prefReadInt(context, item));log("$item number set to - ${readRead(context, item)}") }
                for(item in stringArray5){ readEdit(context, item, prefReadInt(context, item));log("$item number set to - ${readRead(context, item)}") }
                for(item in stringArray6){ readEdit(context, item, prefReadInt(context, item));log("$item number set to - ${readRead(context, item)}") }
                for(item in stringArray7){ readEdit(context, item, prefReadInt(context, item));log("$item number set to - ${readRead(context, item)}") }
                for(item in stringArray8){ readEdit(context, item, prefReadInt(context, item));log("$item number set to - ${readRead(context, item)}") }
                for(item in stringArray9){ readEdit(context, item, prefReadInt(context, item));log("$item number set to - ${readRead(context, item)}") }
                for(item in stringArray10){ readEdit(context, item, prefReadInt(context, item));log("$item number set to - ${readRead(context, item)}") }
                listNumberEditInt(context, "movedValues", 1)
                log("Clearing existing preference file")
                clearOldPref(context!!)
                log("Values moved, shouldn't happen again boss!")
            }
        }
    }
    private fun createListeners(button: Button, view: View){
        cardList1!!.setOnClickListener{markSingle(context, cardList1!!, "list1Done", "List 1", R.array.list_1, button)}
        cardList2!!.setOnClickListener{markSingle(context, cardList2!!, "list2Done", "List 2", R.array.list_2, button)}
        cardList3!!.setOnClickListener{markSingle(context, cardList3!!, "list3Done", "List 3", R.array.list_3, button)}
        cardList4!!.setOnClickListener{markSingle(context, cardList4!!, "list4Done", "List 4", R.array.list_4, button)}
        cardList5!!.setOnClickListener{markSingle(context, cardList5!!, "list5Done", "List 5", R.array.list_5, button)}
        cardList6!!.setOnClickListener{markSingle(context, cardList6!!, "list6Done", "List 6", R.array.list_6, button)}
        cardList7!!.setOnClickListener{markSingle(context, cardList7!!, "list7Done", "List 7", R.array.list_7, button)}
        cardList8!!.setOnClickListener{markSingle(context, cardList8!!, "list8Done", "List 8", R.array.list_8, button)}
        cardList9!!.setOnClickListener{markSingle(context, cardList9!!, "list9Done", "List 9", R.array.list_9, button)}
        cardList10!!.setOnClickListener{markSingle(context, cardList10!!, "list10Done", "List 10", R.array.list_10, button)}
        button.setOnClickListener{markAll(context, button, view)}
    }
    private fun checkLists(button: Button){
        when(listNumberReadInt(context, "list1Done")){1 -> disableSingle(cardList1!!, button) }
        when(listNumberReadInt(context, "list2Done")){1 -> disableSingle(cardList2!!, button) }
        when(listNumberReadInt(context, "list3Done")){1 -> disableSingle(cardList3!!, button) }
        when(listNumberReadInt(context, "list4Done")){1 -> disableSingle(cardList4!!, button) }
        when(listNumberReadInt(context, "list5Done")){1 -> disableSingle(cardList5!!, button) }
        when(listNumberReadInt(context, "list6Done")){1 -> disableSingle(cardList6!!, button) }
        when(listNumberReadInt(context, "list7Done")){1 -> disableSingle(cardList7!!, button) }
        when(listNumberReadInt(context, "list8Done")){1 -> disableSingle(cardList8!!, button) }
        when(listNumberReadInt(context, "list9Done")){1 -> disableSingle(cardList9!!, button) }
        when(listNumberReadInt(context, "list10Done")){1 -> disableSingle(cardList10!!, button) }
    }

    private fun disableSingle(cardList:CardView, button:Button){
        button.setText(R.string.markRemaining)
        cardList.isEnabled = false
        cardList.setCardBackgroundColor(Color.parseColor("#00383838"))
    }
    private fun setList(view: View, list_string: String, listId: Int, readingId: Int) {
        var listNum = listNumberReadInt(context, list_string)
        val list = resources.getStringArray(listId)
        when(listNum){
            list.size ->{ listNum = 0; listNumberEditInt(context, list_string, 0)
            }
        }
        val reading = list[listNum]
        val listReading = view.findViewById<TextView>(readingId)
        listReading.text = reading
    }
}