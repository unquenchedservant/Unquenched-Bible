package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.bookChapters
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.bookNames

class BibleBookFragment : PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?){
        val screen: PreferenceScreen = preferenceManager.createPreferenceScreen(App.applicationContext())
        val b = arguments
        val book = b?.getString("book")
        val chapters = bookChapters[book]
        val bookName = bookNames[book]
        val mainActivity = activity as MainActivity
        mainActivity.supportActionBar?.title = "$bookName Statistics"
        for(i in 1..chapters!!){
            val chapterPref = Preference(App.applicationContext())
            chapterPref.title = "$bookName $i"
            chapterPref.summary = "Read: No|Times Read: 0"
            screen.addPreference(chapterPref)
        }
        preferenceScreen = screen
    }
}