package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings.PlanSettings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref

class PlanTypeFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.plan_type_menu, rootKey)
        val mainActivity = activity as MainActivity
        val hornerMethod: Preference? = findPreference("horner")
        val numericalMethod: Preference? = findPreference("numericalDay")
        val calendarMethod: Preference? = findPreference("calendarDay")
        when(getStringPref(name="planType", defaultValue="pgh")){
            "horner"-> {hornerMethod!!.setDefaultValue(true); numericalMethod!!.setDefaultValue(false); calendarMethod!!.setDefaultValue(false)}
            "numerical" -> {hornerMethod!!.setDefaultValue(false); numericalMethod!!.setDefaultValue(true); calendarMethod!!.setDefaultValue(false)}
            "calendar" -> {hornerMethod!!.setDefaultValue(false); numericalMethod!!.setDefaultValue(false); calendarMethod!!.setDefaultValue(true)}
        }

        hornerMethod!!.onPreferenceClickListener = Preference.OnPreferenceClickListener{
            setStringPref(name="planType", value="horner", updateFS=true)
            setBoolPref(name="numericalDay", value=false)
            setBoolPref(name="calendarDay", value=false)
            mainActivity.navController.navigate(R.id.navigation_plan_type)
            true
        }
        numericalMethod!!.onPreferenceClickListener = Preference.OnPreferenceClickListener{
            setStringPref(name="planType", value="numerical", updateFS=true)
            setBoolPref(name="horner", value=false)
            setBoolPref(name="calendarDay", value=false)
            mainActivity.navController.navigate(R.id.navigation_plan_type)
            true
        }
        calendarMethod!!.onPreferenceClickListener = Preference.OnPreferenceClickListener{
            setStringPref(name="planType", value="calendar", updateFS=true)
            setBoolPref(name="numericalDay", value=false)
            setBoolPref(name="horner", value=false)
            setBoolPref(name="holdPlan", value=false, updateFS=true)
            mainActivity.navController.navigate(R.id.navigation_plan_type)
            true
        }
    }
}