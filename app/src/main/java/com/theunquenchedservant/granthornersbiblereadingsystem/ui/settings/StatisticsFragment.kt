package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.os.Bundle
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
        curStreak?.summary = String.format("%d", getIntPref(name="currentStreak"))
        maxStreak?.summary = String.format("%d", getIntPref(name="maxStreak"))
        val biblePercentRead = if(getIntPref(name="totalChaptersRead") != 0){
            val biblePercentRead_1 = getIntPref(name="totalChaptersRead").toDouble() / 1189
            (biblePercentRead_1 * 100).roundToInt()
        }else{
            0
        }

        val bibleAmountRead = getIntPref(name="bibleAmountRead")
        bibleStats?.summary = "$biblePercentRead % | Times Read: $bibleAmountRead"
        val mainActivity = activity as MainActivity
        val statReset : Preference? = findPreference("resetStatistics")
        val bibleResetMenu: Preference? = findPreference("resetBibleMenu")
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
        val builder = AlertDialog.Builder(context)
        builder.setNeutralButton(R.string.no){diag, _ ->
            diag.cancel()
        }.setPositiveButton(R.string.yes){ _, _ ->
            setIntPref(name="currentStreak", value=0, updateFS=true)
            setIntPref(name="maxStreak", value=0, updateFS=true)
            findPreference<Preference>("currentStreak")?.summary = "0"
            findPreference<Preference>("maxStreak")?.summary = "0"
            (activity as MainActivity).navController.navigate(R.id.navigation_stats)
        }.setMessage(R.string.msg_reset_stats_confirm)
                .setTitle(R.string.summary_reset_stats).create().show()
    }
}