package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_CHAPTERS
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_NAMES
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractIntPref

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
            Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        val currentData = it.data
                        val hasBeenRead = if(extractBoolPref(currentData, "${book}${i}Read")) "Yes" else "No"
                        val amountRead = extractIntPref(currentData, "${book}${i}AmountRead")
                        chapterPref.summary = "Read: $hasBeenRead | Times Read: $amountRead"
                    }
                    .addOnFailureListener { error->
                        MainActivity.log("Error getting dataa $error")
                        chapterPref.summary = "Error loading data"
                    }
            screen.addPreference(chapterPref)
        }
        preferenceScreen = screen
    }
}