package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
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
        oldTestament!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        newTestament!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        val old_percent = if(getIntPref("old_chapters_read") != 0) {
            val old_percent_1: Double = (getIntPref("old_chapters_read").toDouble() / 929)
            (old_percent_1 * 100).roundToInt()
        }else{
            0
        }
        val new_percent = if(getIntPref("new_chapters_read") != 0){
            val new_percent_1 = getIntPref("new_chapters_read").toDouble() / 260
            (new_percent_1 * 100).roundToInt()
        }else{
            0
        }
        val new_amount_read = getIntPref("new_amount_read")
        val old_amount_read = getIntPref("old_amount_read")
        val bible_read = getIntPref("bible_amount_read")
        oldTestament!!.summary = "$old_percent % | Times Read: $old_amount_read"
        newTestament!!.summary = "$new_percent % | Times Read: $new_amount_read"
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