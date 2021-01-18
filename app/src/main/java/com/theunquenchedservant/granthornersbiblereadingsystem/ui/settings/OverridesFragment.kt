package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates

class OverridesFragment:PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.override_preferences, rootKey)
        val manual: Preference? = findPreference("manualSetLists")
        val dailyReset: Preference? = findPreference("resetDaily")
        val mainActivity = activity as MainActivity
        if(getStringPref("planType") == "calendar"){
            manual!!.isEnabled = false //Can't change the current date, can ya?
        }
        if(getStringPref("planType") == "numerical" && getIntPref("listsDone") != 0){
            manual!!.isEnabled  = false //can't make a change if you've already completed some of the lists.
        }
        manual!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            when(getStringPref("planType", "horner")) {
                "horner" -> {
                    mainActivity.navController.navigate(R.id.navigation_manual)
                }
                "numerical"->{
                    mainActivity.navController.navigate(R.id.navigation_manual_numerical)
                }
            }
            false
        }
        dailyReset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            resetDaily()
            Toast.makeText(mainActivity.applicationContext, "Forced Daily Reset", Toast.LENGTH_LONG).show()
            mainActivity.navController.navigate(R.id.navigation_home)
            false
        }
    }
    private fun resetDaily(){
        val isLogged = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        var resetStreak  = false
        var resetCurrent = false
        val vacation = SharedPref.getBoolPref("vacationMode")
        when (getIntPref("dailyStreak")) {
            1 -> {
                setIntPref("dailyStreak", 0)
                log("DAILY CHECK - daily streak set to 0")
                resetStreak = true
            }
            0 -> {
                when (vacation) {
                    false -> {
                        if (!dates.checkDate("both", false))
                            resetCurrent = true
                        log("DAILY CHECK - currentStreak set to 0")
                        setIntPref("currentStreak", 0)
                    }
                }
            }
        }
        for(i in 1..10){
            when(getIntPref("list${i}DoneDaily")){
                1->{
                    setIntPref("list${i}DoneDaily", 0)
                }
            }
            when(getIntPref("list${i}Done")){
                1 -> {
                    resetList("list$i", "list${i}Done")
                }
            }
        }
        setIntPref("listsDone", 0)
        if(isLogged != null) {
            val data = mutableMapOf<String, Any>()
            for (i in 1..10){
                data["list$i"] = getIntPref("list$i")
                data["list${i}Done"] = getIntPref("list${i}Done")
            }
            data["listsDone"] = 0
            if(resetCurrent) {
                data["currentStreak"] = 0
            }else if(resetStreak) {
                data["dailyStreak"] = 0
            }
            db.collection("main").document(isLogged.uid).update(data)
        }
    }

    private fun resetList(listName: String, listNameDone: String){
        log("$listName is now set to ${SharedPref.getIntPref(listName)}")
        increaseIntPref(listName, 1)
        log("$listName index is now ${SharedPref.getIntPref(listName)}")
        setIntPref(listNameDone, 0)
        log("$listNameDone set to 0")
    }
}