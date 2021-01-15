package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.bookNames
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.getBooks
import java.util.*

class BibleStatsTestamentFragment : PreferenceFragmentCompat()  {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val mainActivity = activity as MainActivity
        val b = arguments
        val testament = b?.getString("testament")!!
        mainActivity.supportActionBar?.title = "${testament.capitalize(Locale.ROOT)} Testament Statistics"
        val books = getBooks(testament)!!
        val screen = preferenceManager.createPreferenceScreen(App.applicationContext())
        for (book in books){
            val bookPref = Preference(App.applicationContext())
            bookPref.title = bookNames[book]
            bookPref.summary = "0% | Times Read: 0"
            val bundle = bundleOf("book" to book)
            bookPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                mainActivity.navController.navigate(R.id.navigation_book_stats, bundle)
                false
            }
            screen.addPreference(bookPref)
        }
        preferenceScreen = screen
    }
}
