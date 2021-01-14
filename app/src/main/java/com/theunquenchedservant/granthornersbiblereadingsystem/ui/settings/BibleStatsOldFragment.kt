package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R

class BibleStatsOldFragment : PreferenceFragmentCompat()  {
    private val books = arrayOf("genesis", "exodus", "leviticus", "numbers", "deuteronomy",
                "joshua", "judges", "ruth", "samuel1", "samuel2", "kings1", "kings2",
                "chronicles1", "chronicles2", "ezra", "nehemiah", "esther", "job",
                "psalm", "proverbs", "ecclesiastes", "song", "isaiah", "jeremiah",
                "lamentations", "ezekiel", "daniel", "hosea", "joel", "amos", "obadiah",
                "jonah", "micah", "nahum", "habakkuk", "zephaniah", "haggai", "zechariah", "malachi")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.old_testament_stats, rootKey)
        val mainActivity = activity as MainActivity
        for (book in books){
            log("$book")
            val bookPref: Preference? = findPreference(book)
            val bundle = bundleOf("book" to book)
            bookPref!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                mainActivity.navController.navigate(R.id.navigation_book_stats, bundle)
                false
            }
        }
    }
}
