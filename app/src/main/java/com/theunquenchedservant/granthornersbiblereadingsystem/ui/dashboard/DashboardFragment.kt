package com.theunquenchedservant.granthornersbiblereadingsystem.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R

class DashboardFragment : Fragment() {

    private var dashboardViewModel: DashboardViewModel? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val psCheck = MainActivity.prefReadInt(activity, "psalmSwitch")
        val psSwitch = root.findViewById<Switch>(R.id.psalms_switch)
        if (psCheck == 1) {
            psSwitch.isChecked = true
        } else {
            psSwitch.isChecked = false
        }
        val list1Force = root.findViewById<View>(R.id.list1Spinner) as Spinner
        val list2Force = root.findViewById<View>(R.id.list2Spinner) as Spinner
        val list3Force = root.findViewById<View>(R.id.list3Spinner) as Spinner
        val list4Force = root.findViewById<View>(R.id.list4Spinner) as Spinner
        val list5Force = root.findViewById<View>(R.id.list5Spinner) as Spinner
        val list6Force = root.findViewById<View>(R.id.list6Spinner) as Spinner
        val list7Force = root.findViewById<View>(R.id.list7Spinner) as Spinner
        val list8Force = root.findViewById<View>(R.id.list8Spinner) as Spinner
        val list9Force = root.findViewById<View>(R.id.list9Spinner) as Spinner
        val list10Force = root.findViewById<View>(R.id.list10Spinner) as Spinner
        listSetter(list1Force, root, activity, R.array.list_1, "List 1")
        listSetter(list2Force, root, activity, R.array.list_2, "List 2")
        listSetter(list3Force, root, activity, R.array.list_3, "List 3")
        listSetter(list4Force, root, activity, R.array.list_4, "List 4")
        listSetter(list5Force, root, activity, R.array.list_5, "List 5")
        listSetter(list6Force, root, activity, R.array.list_6, "List 6")
        listSetter(list7Force, root, activity, R.array.list_7, "List 7")
        listSetter(list8Force, root, activity, R.array.list_8, "List 8")
        listSetter(list9Force, root, activity, R.array.list_9, "List 9")
        listSetter(list10Force, root, activity, R.array.list_10, "List 10")
        return root
    }

    fun listSetter(spinner: Spinner, view: View, context: Context?, arrayId: Int, listName: String) {
        val adapter = ArrayAdapter.createFromResource(context!!, arrayId, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(MainActivity.prefReadInt(context, listName))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                MainActivity.prefEditInt(context, listName, pos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }
}