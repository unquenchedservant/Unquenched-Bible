package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R

class MainSettings : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_settings, rootKey)
        val plan: Preference? = findPreference("planSettings")
        val notifications: Preference? = findPreference("notifications")
        val overrides: Preference? = findPreference("overrides")
        val infoSupport: Preference? = findPreference("infoSupport")
        val mainActivity = activity as MainActivity

        notifications!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.supportActionBar?.title = "Notifications"
            mainActivity.navController.navigate(R.id.navigation_notifications)
            false
        }
        overrides!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.supportActionBar?.title = "Overrides"
            mainActivity.navController.navigate(R.id.navigation_overrides)
            false
        }
        infoSupport!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.supportActionBar?.title = "Information & Support"
            mainActivity.navController.navigate(R.id.navigation_information)
            false
        }
    }
}