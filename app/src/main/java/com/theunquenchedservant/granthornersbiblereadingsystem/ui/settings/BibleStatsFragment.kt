package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractIntPref
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
        Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    val currentData = it.data
                    val oldPercent = if(extractIntPref(currentData, "oldChaptersRead") != 0){
                        val oldPercent1 = extractIntPref(currentData,"oldChaptersRead").toDouble()/929
                        (oldPercent1 * 100).roundToInt()
                    }else{
                        0
                    }
                    val newPercent = if(extractIntPref(currentData, "newChaptersRead") != 0){
                        val newPercent1 = extractIntPref(currentData, "newChaptersRead").toDouble() / 260
                        (newPercent1 * 100).roundToInt()
                    }else{
                        0
                    }
                    val newAmountRead = extractIntPref(currentData, "newAmountRead")
                    val oldAmountRead = extractIntPref(currentData, "oldAmountRead")
                    val bibleReadInt = extractIntPref(currentData, "bibleAmountRead")
                    oldTestament.summary = "$oldPercent % (${extractIntPref(currentData,"oldChaptersRead")}/929) | Times Read: $oldAmountRead"
                    newTestament.summary = "$newPercent % (${extractIntPref(currentData, "newChaptersRead")}/260) | Times Read: $newAmountRead"
                    bibleRead!!.summary = "$bibleReadInt"
                }
                .addOnFailureListener { error->
                    MainActivity.log("Error getting dataa $error")
                    oldTestament.summary = "Error loading data"
                    newTestament.summary = "Error loading data"
                    bibleRead!!.summary = "Error loading data"
                }
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