package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref

class MainSettings : PreferenceFragmentCompat() {

    private var _rcSignIn = 96

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_settings, rootKey)
        val plan: Preference? = findPreference("planSettings")
        val notifications: Preference? = findPreference("notifications")
        val overrides: Preference? = findPreference("overrides")
        val infoSupport: Preference? = findPreference("infoSupport")
        val account: Preference? = findPreference("googleSignIn")
        val darkMode: Preference? = findPreference("darkMode")
        val discord: Preference? = findPreference("discordLink")
        val mainActivity = activity as MainActivity
        if (getStringPref(name="planType", defaultValue="horner") == "calendar"){
            overrides!!.isEnabled = false
            overrides.summary = "This option is not available with the current reading method"
        }else{
            overrides!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        }

        plan!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        notifications!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)

        infoSupport!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)

        val dark = getBoolPref(name="darkMode", defaultValue=true)
        darkMode!!.setDefaultValue(true)
        if(dark){
            darkMode.setDefaultValue(true)
        }else{
            darkMode.setDefaultValue(false)
        }

        if(FirebaseAuth.getInstance().currentUser != null){
            account?.title = getString(R.string.title_account_settings)
            account?.summary = ""
            account!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                mainActivity.navController.navigate(R.id.navigation_account_settings)
                false
            }
        }
        darkMode.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, value ->
            val boolVal = value as Boolean
            setBoolPref(name="darkMode", value=boolVal, updateFS=true)
            if(boolVal) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            mainActivity.navController.navigate(R.id.navigation_home)
            false
        }
        plan.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.supportActionBar?.title="Plan Settings"
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.navController.navigate(R.id.navigation_plan_settings)
            false
        }
        notifications.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.supportActionBar?.title = "Notifications"
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.navController.navigate(R.id.navigation_notifications)
            false
        }
        overrides.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.supportActionBar?.title = "Overrides"
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.navController.navigate(R.id.navigation_overrides)
            false
        }
        infoSupport.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.supportActionBar?.title = "Information & Support"
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.navController.navigate(R.id.navigation_information)
            false
        }
        discord!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://discord.gg/AKrefXRyuA"))
            try {
                startActivity(i)
            }catch(e: ActivityNotFoundException){
                Toast.makeText(mainActivity.applicationContext, "No browser or Discord installed", Toast.LENGTH_LONG).show()
            }
            false
        }
    }
}