package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import kotlin.math.roundToInt

class StatisticsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.statistics, rootKey)
        val curStreak: Preference? = findPreference("currentStreak")
        val maxStreak: Preference? = findPreference("MaximumStreak")
        val bibleStats: Preference? = findPreference("bibleStatMain")
        curStreak?.summary = String.format("%d", getIntPref("currentStreak"))
        maxStreak?.summary = String.format("%d", getIntPref("maxStreak"))
        val biblePercentRead = if(getIntPref("total_chapters_read") != 0){
            val biblePercentRead_1 = getIntPref("total_chapters_read").toDouble() / 1189
            (biblePercentRead_1 * 100).roundToInt()
        }else{
            0
        }

        val bibleAmountRead = getIntPref("bible_amount_read")
        bibleStats?.summary = "$biblePercentRead % | Times Read: $bibleAmountRead"
        val mainActivity = activity as MainActivity
        val statReset : Preference? = findPreference("reset_statistics")
        val bibleResetMenu: Preference? = findPreference("reset_bible_menu")
        bibleStats!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        bibleResetMenu!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        bibleStats.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.navController.navigate(R.id.navigation_bible_stats_main)
            true
        }
        statReset!!.onPreferenceClickListener  = Preference.OnPreferenceClickListener {
            resetCheck()
            true
        }
        bibleResetMenu.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.navController.navigate(R.id.navigation_bible_reset_menu)
            true
        }

    }
    private fun resetCheck(){
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.unquenchedAlert))
        builder.setNeutralButton(R.string.no){diag, _ ->
            diag.cancel()
        }.setPositiveButton(R.string.yes){ _, _ ->
            setIntPref("currentStreak", 0, true)
            setIntPref("maxStreak", 0, true)
            findPreference<Preference>("currentStreak")?.summary = "0"
            findPreference<Preference>("maxStreak")?.summary = "0"
            (activity as MainActivity).navController.navigate(R.id.navigation_stats)
        }.setMessage(R.string.msg_reset_stats_confirm)
                .setTitle(R.string.summary_reset_stats).create().show()
    }
}