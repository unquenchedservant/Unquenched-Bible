package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R

class BibleStatsNewFragment : PreferenceFragmentCompat()  {
    private val books = arrayOf("matthew", "mark", "luke", "john", "acts", "romans", "corinthians1",
            "corinthians2", "galatians", "ephesians", "philippians", "colossians", "thessalonians1",
            "thessalonians2", "timothy1", "timothy2", "titus", "philemon", "hebrews", "james",
            "peter2", "peter2", "john1", "john2", "john3", "jude", "revelation")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.new_testament_stats, rootKey)
        val mainActivity = activity as MainActivity
        for (book in books){
            val bookPref: Preference? = findPreference(book)
            val bundle = bundleOf("book" to book)
            bookPref!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                mainActivity.navController.navigate(R.id.navigation_book_stats, bundle)
                false
            }
        }
    }
}