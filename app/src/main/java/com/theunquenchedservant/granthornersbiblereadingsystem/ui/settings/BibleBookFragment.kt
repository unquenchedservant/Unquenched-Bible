package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_CHAPTERS
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_NAMES
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref

class BibleBookFragment : PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?){
        val screen: PreferenceScreen = preferenceManager.createPreferenceScreen(App.applicationContext())
        val b = arguments
        val book = b?.getString("book")
        val chapters = BOOK_CHAPTERS[book]
        val bookName = BOOK_NAMES[book]
        val mainActivity = activity as MainActivity
        mainActivity.supportActionBar?.title = "$bookName Statistics"
        for(i in 1..chapters!!){
            val chapterPref = Preference(App.applicationContext())
            chapterPref.title = "$bookName $i"
            val hasBeenRead = if(getBoolPref(name="${book}${i}Read")) "Yes " else "No "
            val amountRead = getIntPref(name="${book}${i}AmountRead")
            chapterPref.summary = "Read: $hasBeenRead| Times Read: $amountRead"
            screen.addPreference(chapterPref)
        }
        preferenceScreen = screen
    }
}