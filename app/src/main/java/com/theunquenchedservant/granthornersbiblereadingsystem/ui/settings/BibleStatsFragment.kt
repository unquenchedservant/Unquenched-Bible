package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R

class BibleStatsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.bible_statistics_main, rootKey)
        val oldTestament: Preference? = findPreference("oldTestament")
        val newTestament: Preference? = findPreference("newTestament")
        val bibleRead: Preference? = findPreference("bibleRead")
        val mainActivity = activity as MainActivity
        oldTestament!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val bundle = bundleOf("testament" to "old")
            mainActivity.navController.navigate(R.id.navigation_bible_testament_stats, bundle)
            false
        }
        newTestament!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val bundle = bundleOf("testament" to "new")
            mainActivity.navController.navigate(R.id.navigation_bible_testament_stats, bundle)
            false
        }
    }
}