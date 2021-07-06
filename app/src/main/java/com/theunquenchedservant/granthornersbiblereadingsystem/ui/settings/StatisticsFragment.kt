package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Preferences

class StatisticsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.statistics, rootKey)
        if((activity as MainActivity).isPreferenceInitialized()) {
            val preferences = (activity as MainActivity).preferences
            val curStreak: Preference? = findPreference("currentStreak")
            val maxStreak: Preference? = findPreference("MaximumStreak")
            curStreak?.summary = String.format("%d", preferences.streak.currentStreak)
            maxStreak?.summary = String.format("%d", preferences.streak.maxStreak)
            val statReset: Preference? = findPreference("resetStatistics")
            statReset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                resetCheck(preferences)
                true
            }
        }
    }
    private fun resetCheck(preferences:Preferences){
        val builder = AlertDialog.Builder(context)
        builder.setNeutralButton(R.string.no){diag, _ ->
            diag.cancel()
        }.setPositiveButton(R.string.yes){ _, _ ->
            preferences.streak.currentStreak = 0
            preferences.streak.maxStreak = 0
            findPreference<Preference>("currentStreak")?.summary = "0"
            findPreference<Preference>("maxStreak")?.summary = "0"
            (activity as MainActivity).navController.navigate(R.id.navigation_stats)
        }.setMessage(R.string.msg_reset_stats_confirm)
                .setTitle(R.string.summary_reset_stats).create().show()
    }
}