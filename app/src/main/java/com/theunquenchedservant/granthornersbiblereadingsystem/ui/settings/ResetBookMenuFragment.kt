package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_NAMES
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.getBooks
import java.util.*

class ResetBookMenuFragment: PreferenceFragmentCompat() {
    private lateinit var testament: String
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val ctx = App.applicationContext()
        val screen: PreferenceScreen = preferenceManager.createPreferenceScreen(ctx)
        val mainActivity = activity as MainActivity
        val b = arguments
        testament = b?.getString("testament")!!
        mainActivity.supportActionBar?.title = "Reset ${testament.capitalize(Locale.ROOT)} Testament Books"
        val books = getBooks(testament)!!
        for(book in books){
            val bookPref = Preference(App.applicationContext())
            bookPref.title = "Reset ${BOOK_NAMES[book]}"
            bookPref.summary = "Reset options for ${BOOK_NAMES[book]}"
            val bundle = bundleOf("book" to book, "testament" to testament)
            bookPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                mainActivity.navController.navigate(R.id.navigation_book_reset_menu, bundle)
                true
            }
            screen.addPreference(bookPref)
        }
        preferenceScreen = screen
    }
}