package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import android.widget.Switch
import androidx.core.content.res.ResourcesCompat
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import java.util.*

class PlanSettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.plan_preferences, rootKey)
        val mainActivity = activity as MainActivity
        val planMethod: Preference? = findPreference("planMethod")
        val psalms: Preference? = findPreference("psalms")
        val holdPlan: Preference? = findPreference("holdPlan")
        val partialStreakAllow : Preference? = findPreference("allowPartial")
        val translation: DropDownPreference? = findPreference("bibleTranslation")
        val planType: Preference? = findPreference("planType")
        val weekendMode: SwitchPreference? = findPreference("weekendMode")
        translation!!.setEntries(R.array.translationArray)
        translation.setEntryValues(R.array.translationArray)
        val currentTranslation = getStringPref("bibleVersion", "ESV")
        translation.value = currentTranslation
        planType!!.summary = "${getString(R.string.summary_plan_type)} Current Plan: ${getStringPref(name="planSystem", defaultValue="pgh").toUpperCase(Locale.ROOT)}"
        planType.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        planMethod!!.summary = "${getString(R.string.summary_reading_type)} Current Method: ${getStringPref(name="planType", defaultValue="horner").capitalize(Locale.ROOT)}"
        planMethod.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        translation.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_arrow_drop_down_24, mainActivity.theme)
        when (getStringPref(name="planType", defaultValue="horner")){
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
        if (getStringPref("planSystem", "pgh") == "pgh") {
            if (ps) {
                psalms!!.setDefaultValue(true)
            }
        }else{
            setBoolPref("psalms", false)
            psalms!!.setDefaultValue(false)
            psalms.isEnabled = false
        }
        val hold = getBoolPref("holdPlan")
        if(hold){
            holdPlan.setDefaultValue(true)
        }
        val partial = getBoolPref("allowPartial")
        if(partial){
            partialStreakAllow!!.setDefaultValue(true)
        }
        planType.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.navController.navigate(R.id.navigation_plan_system)
            true
        }
        planMethod.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.navController.navigate(R.id.navigation_plan_type)
            true
        }
        holdPlan.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if(hold){
                setBoolPref(name="holdPlan", value=false, updateFS=true)
            }else{
                setBoolPref(name="holdPlan", value=true, updateFS=true)
            }
            true
        }
        weekendMode!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, value ->
            val boolValue = value as Boolean
            setBoolPref(name="weekendMode", value=boolValue, updateFS=true)
            false
        }
        psalms!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if(ps){
                setBoolPref(name="psalms", value=false, updateFS=true)
            }else{
                setBoolPref(name="psalms", value=true, updateFS=true)
            }
            true
        }

        partialStreakAllow!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
            if(partial){
                setBoolPref(name="allowPartial", value=false, updateFS=true)
            }else{
                setBoolPref(name="allowPartial", value=true, updateFS=true)
            }
            true
        }
        translation.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            translation.value = newValue as String
            setStringPref(name="bibleVersion", value=newValue, updateFS=true)
            false
        }
    }
}