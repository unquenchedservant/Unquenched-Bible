package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.resetDaily
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref

class OverridesFragment:PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.override_preferences, rootKey)
        val manual: Preference? = findPreference("manualSetLists")
        val dailyReset: Preference? = findPreference("resetDaily")
        val mainActivity = activity as MainActivity
        if(getStringPref(name="planType") == "calendar"){
            manual!!.isEnabled = false //Can't change the current date, can ya?
        }
        if(getStringPref(name="planType") == "numerical" && getIntPref(name="listsDone") != 0){
            manual!!.isEnabled  = false //can't make a change if you've already completed some of the lists.
        }
        manual!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            when(getStringPref(name="planType", defaultValue="horner")) {
                "horner" -> {
                    mainActivity.navController.navigate(R.id.navigation_manual)
                }
                "numerical"->{
                    mainActivity.navController.navigate(R.id.navigation_manual_numerical)
                }
            }
            false
        }
        dailyReset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            resetDaily(requireContext())
            Toast.makeText(mainActivity.applicationContext, "Forced Daily Reset", Toast.LENGTH_LONG).show()
            val homeId = if(getStringPref("planSystem") == "pgh") R.id.navigation_home else R.id.navigation_home_mcheyne
            mainActivity.navController.navigate(homeId)
            false
        }
    }
}