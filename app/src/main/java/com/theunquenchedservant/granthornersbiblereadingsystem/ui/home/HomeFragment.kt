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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.getCurrentDate
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.markList
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.prefEditInt
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.prefEditString
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.prefReadInt
import com.theunquenchedservant.granthornersbiblereadingsystem.R

import java.text.DecimalFormat
import java.util.Calendar

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView = root.findViewById<TextView>(R.id.text_home)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        homeViewModel.text.observe(this, Observer {
            textView.setTextColor(ContextCompat.getColor(activity!!, R.color.unquenchedEmph))

            textView.isAllCaps = true
        })
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val str_today = getCurrentDate(false)
        val check = MainActivity.prefReadString(activity, "dateClicked")
        val button = root.findViewById<Button>(R.id.material_button)
        if (check == str_today) {
            button.setText(R.string.done)
            button.setBackgroundColor(Color.parseColor("#00383838"))
            textView.setText(R.string.tomorrow)
            button.isEnabled = false
        } else {
            textView.text = MainActivity.getCurrentDate(true)
            button.setBackgroundColor(Color.parseColor("#383838"))
        }
        val sharedpref = PreferenceManager.getDefaultSharedPreferences(activity!!)
        setList(root, "List 1", R.array.list_1, R.id.list1_reading)
        setList(root, "List 2", R.array.list_2, R.id.list2_reading)
        setList(root, "List 3", R.array.list_3, R.id.list3_reading)
        setList(root, "List 4", R.array.list_4, R.id.list4_reading)
        setList(root, "List 5", R.array.list_5, R.id.list5_reading)
        val psCheck = sharedpref.getBoolean("psalms", false)
        if (psCheck) {
            val list_reading = root.findViewById<TextView>(R.id.list6_reading)
            val pa1 = day.toString() + ", " + Integer.toString(day + 30) + ", " + Integer.toString(day + 60) + ", " + Integer.toString(day + 90) + ", " + Integer.toString(day + 120)
            list_reading.text = pa1
        } else {
            setList(root, "List 6", R.array.list_6, R.id.list6_reading)
        }
        setList(root, "List 7", R.array.list_7, R.id.list7_reading)
        setList(root, "List 8", R.array.list_8, R.id.list8_reading)
        setList(root, "List 9", R.array.list_9, R.id.list9_reading)
        setList(root, "List 10", R.array.list_10, R.id.list10_reading)
        return root
    }

    private fun setList(view: View, list_string: String, listId: Int, readingId: Int) {
        val res = resources
        val listNum = MainActivity.prefReadInt(activity, list_string)
        val list = res.getStringArray(listId)
        val reading = list[listNum]
        val listReading = view.findViewById<TextView>(readingId)
        listReading.text = reading
    }
}