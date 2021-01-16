package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

class BibleStatsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.bible_statistics_main, rootKey)
        val oldTestament: Preference? = findPreference("oldTestament")
        val newTestament: Preference? = findPreference("newTestament")
        val bibleRead: Preference? = findPreference("bibleRead")
        val mainActivity = activity as MainActivity
        val old_percent_1 :Double = (getIntPref("old_chapters_read").toDouble() / 929)
        val old_percent = (old_percent_1 * 100).roundToInt()
        val new_percent_1 = getIntPref("new_chapters_read").toDouble() / 260
        val new_percent = (new_percent_1 * 100).roundToInt()
        val bible_read = getIntPref("bible_read_amount")
        oldTestament!!.summary = "$old_percent %"
        newTestament!!.summary = "$new_percent %"
        bibleRead!!.summary = "$bible_read"
        oldTestament.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val bundle = bundleOf("testament" to "old")
            mainActivity.navController.navigate(R.id.navigation_bible_testament_stats, bundle)
            false
        }
        newTestament.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val bundle = bundleOf("testament" to "new")
            mainActivity.navController.navigate(R.id.navigation_bible_testament_stats, bundle)
            false
        }
    }
}