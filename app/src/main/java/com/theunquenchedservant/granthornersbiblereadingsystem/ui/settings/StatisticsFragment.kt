package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
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
        val partialStreakAllow : Preference? = findPreference("allow_partial_switch")
        partialStreakAllow!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->

            true
        }
        partialStreakAllow.summary = "Streak won't break if you do less than 10 lists (but more than 1)"
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
            fragmentManager?.beginTransaction()?.replace(R.id.nav_host_fragment, StatisticsFragment())?.commit()
        }.setMessage(R.string.msg_reset_stats_confirm)
                .setTitle(R.string.summary_reset_stats).create().show()
    }
}