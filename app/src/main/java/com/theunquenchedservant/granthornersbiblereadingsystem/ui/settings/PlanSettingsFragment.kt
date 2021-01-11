package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS

class PlanSettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.plan_preferences, rootKey)
        val psalms: Preference? = findPreference("psalms")
        val holdPlan: Preference? = findPreference("holdTilAll")
        val partialStreakAllow : Preference? = findPreference("allow_partial_switch")
        val translation: DropDownPreference? = findPreference("bibleTranslation")
        translation!!.setEntries(R.array.translationArray)
        translation.setEntryValues(R.array.translationArray)
        var currentTranslation = getStringPref("bibleVersion", "ESV")
        translation.value = currentTranslation

        val ps = getBoolPref("psalms")
        if(ps){
            psalms!!.setDefaultValue(true)
        }
        val hold = getBoolPref("holdPlan")
        if(hold){
            holdPlan!!.setDefaultValue(true)
        }
        val partial = getBoolPref("allow_partial_switch")
        if(partial){
            partialStreakAllow!!.setDefaultValue(true)
        }
        holdPlan!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if(hold){
                setBoolPref("holdPlan", false)
                if(FirebaseAuth.getInstance().currentUser != null){
                    updateFS("holdPlan", false)
                }
            }else{
                setBoolPref("holdPlan", true)
                if(FirebaseAuth.getInstance().currentUser != null){
                    updateFS("holdPlan", true)
                }
            }
            true
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

        partialStreakAllow!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
            if(partial){
                setBoolPref("allow_partial_switch", false)
                if(FirebaseAuth.getInstance().currentUser != null){
                    updateFS("allowPartial", false)
                }
            }else{
                setBoolPref("allow_partial_switch", true)
                if(FirebaseAuth.getInstance().currentUser != null){
                    updateFS("allowPartial", true)
                }
            }
            true
        }
        translation.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { it, newValue ->
            translation.value = newValue as String
            updateFS("bibleVersion", newValue)
            setStringPref("bibleVersion", newValue)
            false
        }
    }
}