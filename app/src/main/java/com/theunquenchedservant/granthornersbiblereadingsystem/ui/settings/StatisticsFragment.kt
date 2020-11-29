package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref

class StatisticsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.statistics, rootKey)
        val curStreak: Preference? = findPreference("currentStreak")
        val maxStreak: Preference? = findPreference("MaximumStreak")
        curStreak?.summary = String.format("%d", getIntPref("currentStreak"))
        maxStreak?.summary = String.format("%d", getIntPref("maxStreak"))
        val statReset : Preference? = findPreference("reset_statistics")
        statReset!!.onPreferenceClickListener  = Preference.OnPreferenceClickListener {
            resetCheck()
            true
        }

    }
    private fun resetCheck(){
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.unquenchedAlert))
        builder.setNeutralButton(R.string.no){diag, _ ->
            diag.cancel()
        }.setPositiveButton(R.string.yes){ _, _ ->
            setIntPref("currentStreak", 0)
            setIntPref("maxStreak", 0)
            findPreference<Preference>("currentStreak")?.summary = "0"
            findPreference<Preference>("maxStreak")?.summary = "0"
            (activity as MainActivity).navController.navigate(R.id.navigation_stats)
        }.setMessage(R.string.msg_reset_stats_confirm)
                .setTitle(R.string.summary_reset_stats).create().show()
    }
}