package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.bookChapters
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.bookNames
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.getBooks
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt

class BibleStatsTestamentFragment : PreferenceFragmentCompat()  {
    private lateinit var testament: String
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val mainActivity = activity as MainActivity
        val b = arguments
        testament = b?.getString("testament")!!
        mainActivity.supportActionBar?.title = "${testament.capitalize(Locale.ROOT)} Testament Statistics"
        val books = getBooks(testament)!!
        val screen = preferenceManager.createPreferenceScreen(App.applicationContext())
        for (book in books){
            val bookPref = Preference(App.applicationContext())
            bookPref.title = bookNames[book]
            bookPref.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
            val timesRead = getIntPref("${book}_amount_read")
            val percentRead = if(getIntPref("${book}_chapters_read") != 0) {
                val percentRead_1 = getIntPref("${book}_chapters_read").toDouble() / bookChapters[book]!!
                (percentRead_1 * 100).roundToInt()
            }else{
                0
            }
            bookPref.summary = "$percentRead % | Times Read: $timesRead"
            val bundle = bundleOf("book" to book)
            bookPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                mainActivity.navController.navigate(R.id.navigation_book_stats, bundle)
                false
            }
            screen.addPreference(bookPref)
        }
        preferenceScreen = screen
    }

    override fun onResume() {
        val mainActivity = activity as MainActivity
        mainActivity.supportActionBar?.title = "${testament.capitalize(Locale.ROOT)} Testament Statistics"
        super.onResume()
    }
}
