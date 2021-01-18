package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
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
                hornerMethod!!.setDefaultValue("true")
                numericalMethod!!.setDefaultValue("false")
                calendarMethod!!.setDefaultValue("false")
            }
            "numerical"->{
                hornerMethod!!.setDefaultValue("false")
                numericalMethod!!.setDefaultValue("true")
                calendarMethod!!.setDefaultValue("false")
            }
            "calendar"->{
                hornerMethod!!.setDefaultValue("false")
                numericalMethod!!.setDefaultValue("false")
                calendarMethod!!.setDefaultValue("true")
            }
        }
        hornerMethod!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener{ pref, value ->
            val enabled = value as Boolean
            if(enabled){
                setStringPref("planType", "horner")
                updateFS("planType", "horner")
                numericalMethod!!.setDefaultValue("false")
                calendarMethod!!.setDefaultValue("false")
                mainActivity.navController.navigate(R.id.navigation_plan_type)
            }else{
                setStringPref("planType", "horner")
                updateFS("planType", "horner")
                numericalMethod!!.setDefaultValue("false")
                calendarMethod!!.setDefaultValue("false")
                mainActivity.navController.navigate(R.id.navigation_plan_type)
            }
            true
        }
        numericalMethod!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener{ pref, value ->
            val enabled = value as Boolean
            if(enabled){
                setStringPref("planType", "numerical")
                updateFS("planType", "numerical")
                hornerMethod.setDefaultValue("false")
                calendarMethod!!.setDefaultValue("false")
                mainActivity.navController.navigate(R.id.navigation_plan_type)
            }else{
                setStringPref("planType", "numerical")
                updateFS("planType", "numerical")
                hornerMethod.setDefaultValue("false")
                calendarMethod!!.setDefaultValue("false")
                mainActivity.navController.navigate(R.id.navigation_plan_type)
            }
            true
        }
        calendarMethod!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener{ pref, value ->
            val enabled = value as Boolean
            if(enabled){
                setStringPref("planType", "calendar")
                updateFS("planType", "calendar")
                numericalMethod.setDefaultValue("false")
                hornerMethod.setDefaultValue("false")
                mainActivity.navController.navigate(R.id.navigation_plan_type)
            }else{
                setStringPref("planType", "calendar")
                updateFS("planType", "calendar")
                numericalMethod.setDefaultValue("false")
                hornerMethod.setDefaultValue("false")
                mainActivity.navController.navigate(R.id.navigation_plan_type)
            }
            true
        }
    }
}