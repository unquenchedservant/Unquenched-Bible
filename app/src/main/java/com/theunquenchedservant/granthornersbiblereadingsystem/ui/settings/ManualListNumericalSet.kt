package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS

class ManualListNumericalSet: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manual_numerical, container, false)
    }
    override fun onResume() {
        super.onResume()
        val root = requireView()
        val mainActivity = activity as MainActivity
        val dark = SharedPref.getBoolPref("darkMode", true)
        val dayPicker = root.findViewById<NumberPicker>(R.id.dayPickerSpinner)
        val selectButton = root.findViewById<Button>(R.id.set_button)
        if(dark){
            dayPicker.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners_dark, (activity as MainActivity).theme)
            selectButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383838"))
            selectButton.backgroundTintMode = PorterDuff.Mode.ADD
            selectButton.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedTextDark))
        }else{
            dayPicker.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners, (activity as MainActivity).theme)
            selectButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e1e2e6"))
            selectButton.backgroundTintMode = PorterDuff.Mode.ADD
            selectButton.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedText))
        }
        dayPicker.minValue = 1
        dayPicker.maxValue = 9999
        val prefix = when(getStringPref("planSystem", "pgh")){
            "pgh" ->""
            "mcheyne"->"mcheyne_"
            else->""
        }
        val homeId = when(getStringPref("planSystem", "pgh")){
            "pgh"->R.id.navigation_home
            "mcheyne"->R.id.navigation_home_mcheyne
            else->R.id.navigation_home
        }
        dayPicker.value = getIntPref("${prefix}currentDayIndex", 0) + 1
        selectButton.setOnClickListener {
            val alert             = AlertDialog.Builder(requireContext())
            alert.setTitle("Set Day?")
            alert.setMessage("Are you sure you want to set the current day of reading to ${dayPicker.value}")
            alert.setPositiveButton("Yes") { dialogInterface, _ ->
                setIntPref("${prefix}currentDayIndex", dayPicker.value)
                updateFS("${prefix}currentDayIndex", dayPicker.value)
                dialogInterface.dismiss()
                Toast.makeText(context, "Changed current day of reading", Toast.LENGTH_LONG).show()
                mainActivity.navController.navigate(homeId)
            }
            alert.setNeutralButton("Cancel"){dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            alert.create().show()
        }
    }
}