package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS

class PlanTypeFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.plan_type_menu, rootKey)
        val mainActivity = activity as MainActivity
        val hornerMethod: Preference? = findPreference("grantHorner")
        val numericalMethod: Preference? = findPreference("numericalDay")
        val calendarMethod: Preference? = findPreference("calendarDay")
        when(getStringPref("planType", "horner")){
            "horner"->{
                hornerMethod!!.setDefaultValue(true)
                numericalMethod!!.setDefaultValue(false)
                calendarMethod!!.setDefaultValue(false)
            }
            "numerical"->{
                hornerMethod!!.setDefaultValue(false)
                numericalMethod!!.setDefaultValue(true)
                calendarMethod!!.setDefaultValue(false)
            }
            "calendar"->{
                hornerMethod!!.setDefaultValue(false)
                numericalMethod!!.setDefaultValue(false)
                calendarMethod!!.setDefaultValue(true)
            }
        }
        hornerMethod!!.onPreferenceClickListener = Preference.OnPreferenceClickListener{
            setStringPref("planType", "horner")
            updateFS("planType", "horner")
            setBoolPref("numericalDay", false)
            setBoolPref("calendarDay", false)
            mainActivity.navController.navigate(R.id.navigation_plan_type)
            true
        }
        numericalMethod!!.onPreferenceClickListener = Preference.OnPreferenceClickListener{
            setStringPref("planType", "numerical")
            updateFS("planType", "numerical")
            setBoolPref("grantHorner", false)
            setBoolPref("calendarDay", false)
            setBoolPref("holdPlan", false)
            setBoolPref("allow_partial_switch", false)
            mainActivity.navController.navigate(R.id.navigation_plan_type)
            true
        }
        calendarMethod!!.onPreferenceClickListener = Preference.OnPreferenceClickListener{
            setStringPref("planType", "calendar")
            updateFS("planType", "calendar")
            setBoolPref("numericalDay", false)
            setBoolPref("grantHorner", false)
            setBoolPref("holdPlan", false)
            setBoolPref("allow_partial_switch", false)
            mainActivity.navController.navigate(R.id.navigation_plan_type)
            true
        }
    }
}