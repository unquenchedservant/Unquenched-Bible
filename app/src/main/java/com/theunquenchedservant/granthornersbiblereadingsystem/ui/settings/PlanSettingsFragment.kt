package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
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
        val mainActivity = activity as MainActivity
        val planMethod: Preference? = findPreference("planMethod")
        val psalms: Preference? = findPreference("psalms")
        val holdPlan: Preference? = findPreference("holdTilAll")
        val partialStreakAllow : Preference? = findPreference("allow_partial_switch")
        val translation: DropDownPreference? = findPreference("bibleTranslation")
        val planType: Preference? = findPreference("planType")
        translation!!.setEntries(R.array.translationArray)
        translation.setEntryValues(R.array.translationArray)
        var currentTranslation = getStringPref("bibleVersion", "ESV")
        translation.value = currentTranslation
        planType!!.summary = "${getString(R.string.summary_plan_type)} Current Plan: ${getStringPref("planSystem", "pgh").toUpperCase()}"
        planType.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        planMethod!!.summary = "${getString(R.string.summary_reading_type)} Current Method: ${getStringPref("planType", "horner").capitalize()}"
        planMethod.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        translation.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_arrow_drop_down_24, mainActivity.theme)
        when (getStringPref("planType", "horner")){
            "horner"->{
                holdPlan!!.isEnabled = true
                partialStreakAllow!!.isEnabled = true
            }
            "numerical"->{
                holdPlan!!.isEnabled = true
                partialStreakAllow!!.isEnabled = true
            }
            else->{
                holdPlan!!.summary = "Not available under current reading method"
                holdPlan.isEnabled = false
            }
        }
        val ps = getBoolPref("psalms")
        if(ps){
            psalms!!.setDefaultValue(true)
        }
        val hold = getBoolPref("holdPlan")
        if(hold){
            holdPlan.setDefaultValue(true)
        }
        val partial = getBoolPref("allow_partial_switch")
        if(partial){
            partialStreakAllow!!.setDefaultValue(true)
        }
        planMethod.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.navController.navigate(R.id.navigation_plan_type)
            true
        }
        holdPlan.onPreferenceClickListener = Preference.OnPreferenceClickListener {
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
        translation.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            translation.value = newValue as String
            updateFS("bibleVersion", newValue)
            setStringPref("bibleVersion", newValue)
            false
        }
    }
}