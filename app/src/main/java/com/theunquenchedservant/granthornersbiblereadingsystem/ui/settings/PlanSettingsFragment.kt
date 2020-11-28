package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS

class PlanSettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.plan_preferences, rootKey)
        val psalms: Preference? = findPreference("psalms")

        val ps = getBoolPref("psalms")
        if(ps){
            psalms!!.setDefaultValue("true")
        }
        psalms!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if(ps){
                setBoolPref("psalms", false)
                if(FirebaseAuth.getInstance().currentUser != null){
                    updateFS("psalms", false)
                }
            }else{
                setBoolPref("psalms", true)
                if(FirebaseAuth.getInstance().currentUser != null){
                    updateFS("psalms", true)
                }
            }
            true
        }
    }
}