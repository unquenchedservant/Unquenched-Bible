package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
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
        val oldPercent = if(getIntPref(name="oldChaptersRead") != 0) {
            val oldPercent1: Double = (getIntPref(name="oldChaptersRead").toDouble() / 929)
            (oldPercent1 * 100).roundToInt()
        }else{
            0
        }
        val newPercent = if(getIntPref(name="newChaptersRead") != 0){
            val newPercent1 = getIntPref(name="newChaptersRead").toDouble() / 260
            (newPercent1 * 100).roundToInt()
        }else{
            0
        }
        val newAmountRead = getIntPref(name="newAmountRead")
        val oldAmountRead = getIntPref(name="oldAmountRead")
        val bibleReadInt = getIntPref(name="bibleAmountRead")
        oldTestament.summary = "$oldPercent % | Times Read: $oldAmountRead"
        newTestament.summary = "$newPercent % | Times Read: $newAmountRead"
        bibleRead!!.summary = "$bibleReadInt"
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