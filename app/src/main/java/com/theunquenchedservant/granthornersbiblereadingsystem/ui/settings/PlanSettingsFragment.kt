package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Firestore
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPreferences
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Strings.capitalize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class PlanSettingsFragment: PreferenceFragmentCompat() {
    val preferences = App().preferences!!
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
        val currentTranslation = preferences.settings.bibleVersion
        translation.value = currentTranslation
        planType!!.summary = "${getString(R.string.summary_plan_type)} Current Plan: ${preferences.settings.planSystem.uppercase(Locale.ROOT)}"
        planType.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        planMethod!!.summary = "${getString(R.string.summary_reading_type)} Current Method: ${capitalize(preferences.settings.planType)}"
        planMethod.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        translation.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_arrow_drop_down_24, mainActivity.theme)
        when (preferences.settings.planType){
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
        if (preferences.settings.planSystem == "pgh") {
            if (preferences.settings.psalms) {
                psalms!!.setDefaultValue(true)
            }
        }else{
            preferences.settings.psalms = false
            psalms!!.setDefaultValue(false)
            psalms.isEnabled = false
        }
        if(preferences.settings.holdPlan){
            holdPlan.setDefaultValue(true)
        }
        if(preferences.settings.allowPartial){
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
        holdPlan.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, value ->
            preferences.settings.holdPlan = value as Boolean
            true
        }
        weekendMode!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, value ->
            preferences.settings.weekendMode = value as Boolean
            false
        }
        psalms!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, value->
            preferences.settings.psalms = value as Boolean
            true
        }

        partialStreakAllow!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, value ->
            preferences.settings.allowPartial = value as Boolean
            true
        }
        translation.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            translation.value = newValue as String
            preferences.settings.bibleVersion = newValue
            false
        }
    }

    override fun onStop() {
        super.onStop()
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }
    override fun onDestroy(){
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }
}

class PlanSystemFragment : PreferenceFragmentCompat() {
    val preferences = App().preferences!!
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.plan_system_menu, rootKey)
        val mainActivity = activity as MainActivity
        val pgh: Preference? = findPreference("pghSystem")
        val mcheyne: Preference? = findPreference("mcheyneSystem")
        val moreInfo: Preference? = findPreference("planMoreInfo")

        when(preferences.settings.planSystem){
            "pgh"-> {pgh!!.setDefaultValue(true); mcheyne!!.setDefaultValue(false)}
            "mcheyne" -> {pgh!!.setDefaultValue(false); mcheyne!!.setDefaultValue(true)}
        }

        pgh!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            preferences.settings.planSystem = "pgh"
            SharedPreferences(context).setBoolean(name="mcheyneSystem", value=false)
            preferences.settings.psalms = SharedPreferences(context).getBoolean("psalmsHold", true)
            mainActivity.navController.navigate(R.id.navigation_plan_system)
            true
        }
        mcheyne!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            preferences.settings.planSystem = "mcheyne"
            SharedPreferences(context).setBoolean(name="pghSystem", false)
            SharedPreferences(context).setBoolean(name="psalmsHold", value= preferences.settings.psalms)
            mainActivity.navController.navigate(R.id.navigation_plan_system)
            true
        }
        moreInfo!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.unquenched.bible/the-reading-plans/"))
            try {
                startActivity(i)
            }catch(e: ActivityNotFoundException){
                Toast.makeText(mainActivity, "No browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
    }
    override fun onStop() {
        super.onStop()
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }
    override fun onDestroy(){
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }
}

class PlanTypeFragment : PreferenceFragmentCompat() {
    val preferences = App().preferences!!
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.plan_type_menu, rootKey)
        val mainActivity = activity as MainActivity
        val hornerMethod: Preference? = findPreference("horner")
        val numericalMethod: Preference? = findPreference("numericalDay")
        val calendarMethod: Preference? = findPreference("calendarDay")
        when(preferences.settings.planType){
            "horner"-> {hornerMethod!!.setDefaultValue(true); numericalMethod!!.setDefaultValue(false); calendarMethod!!.setDefaultValue(false)}
            "numerical" -> {hornerMethod!!.setDefaultValue(false); numericalMethod!!.setDefaultValue(true); calendarMethod!!.setDefaultValue(false)}
            "calendar" -> {hornerMethod!!.setDefaultValue(false); numericalMethod!!.setDefaultValue(false); calendarMethod!!.setDefaultValue(true)}
        }
        val sharedPref = SharedPreferences(context)
        hornerMethod!!.onPreferenceClickListener = Preference.OnPreferenceClickListener{
            preferences.settings.planType = "horner"
            sharedPref.setBoolean("numericalDay", false)
            sharedPref.setBoolean("calendarDay", false)
            mainActivity.navController.navigate(R.id.navigation_plan_type)
            true
        }
        numericalMethod!!.onPreferenceClickListener = Preference.OnPreferenceClickListener{
            preferences.settings.planType = "numerical"
            sharedPref.setBoolean("horner", false)
            sharedPref.setBoolean("calendarDay", false)
            mainActivity.navController.navigate(R.id.navigation_plan_type)
            true
        }
        calendarMethod!!.onPreferenceClickListener = Preference.OnPreferenceClickListener{
            preferences.settings.planType = "calendar"
            sharedPref.setBoolean("horner", value=false)
            sharedPref.setBoolean("numericalDay", value=false)
            preferences.settings.holdPlan = false
            mainActivity.navController.navigate(R.id.navigation_plan_type)
            true
        }
    }
    override fun onStop() {
        super.onStop()
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }
    override fun onDestroy(){
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }
}