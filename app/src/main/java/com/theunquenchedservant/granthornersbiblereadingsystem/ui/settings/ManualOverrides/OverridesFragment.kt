package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings.ManualOverrides

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.resetDaily
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Marker
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref

class OverridesFragment:PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.override_preferences, rootKey)
        val manual: Preference? = findPreference("manualSetLists")
        val resetAll: Preference? = findPreference("resetAll")
        val dailyReset: Preference? = findPreference("resetDaily")
        val mainActivity = activity as MainActivity
        if(getStringPref(name="planType") == "calendar"){
            manual!!.isEnabled = false //Can't change the current date, can ya?
        }
        if(getStringPref(name="planType") == "numerical" && getIntPref(name="listsDone") != 0){
            manual!!.isEnabled  = false //can't make a change if you've already completed some of the lists.
        }
        resetAll!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val updateValues = mutableMapOf<String, Any>()
            val planType = if(getStringPref("planSystem") == "pgh"){
                "Grant Horner System"
            }else if(getStringPref("planSystem") == "mcheyne"){
                "M'Cheyne Reading Plan"
            }else{
                "ERROR SHOULDN'T SEE THIS"
            }
            if(getStringPref("planType") != "calendar") {
                val alert = AlertDialog.Builder(context)
                alert.setPositiveButton("Yes") { _, _ ->
                    if (getStringPref(name = "planSystem") == "pgh") {
                        if (getStringPref(name = "planType") == "horner") {
                            for (i in 1..10) {
                                updateValues["list$i"] = setIntPref("list$i", 0)
                                updateValues["list${i}Done"] = setIntPref("list${i}Done", 0)
                                updateValues["list${i}DoneDaily"] = setIntPref("list${i}DoneDaily", 0)
                            }
                        } else if (getStringPref(name = "planType") == "numerical") {
                            updateValues["currentDayIndex"] = setIntPref("currentDayIndex", 0)
                        }
                        updateValues["listsDone"] = setIntPref("listsDone", 0)
                    } else if (getStringPref(name = "planSystem") == "mcheyne") {
                        if (getStringPref(name = "planType") == "horner") {
                            for (i in 1..4) {
                                updateValues["mcheyneList$i"] = setIntPref("mcheyneList$i", 0)
                                updateValues["mcheyneList${i}Done"] = setIntPref("mcheyneList${i}Done", 0)
                                updateValues["mcheyneList${i}DoneDaily"] = setIntPref("mcheyneList${i}Done", 0)
                                updateValues["mcheyneListsDone"] = setIntPref("mcheyneListsDone", 0)
                            }
                        } else if (getStringPref(name = "planType") == "numerical") {
                            updateValues["mcheyneCurrentDayIndex"] = setIntPref("mcheyneCurrentDayIndex", 0)
                        }
                        updateValues["mcheyneListsDone"] = setIntPref("mcheyneListsDone", 0)
                    }
                    Firebase.firestore.collection("main").document(Firebase.auth.currentUser?.uid!!).update(updateValues)
                            .addOnSuccessListener {
                                val homeId = R.id.navigation_home
                                mainActivity.navController.navigate(homeId)
                            }
                            .addOnFailureListener {
                               debugLog(message="FAILED TO RESET LISTS FOR ${it.message}")
                                Toast.makeText(mainActivity.applicationContext, "Unable to reset lists. Try again later", Toast.LENGTH_LONG).show()
                            }
                }
                alert.setNegativeButton("Nevermind") { dialog, _ ->
                    dialog.dismiss()
                }
                val message = "Are you sure you want to reset your progress in the ${planType}?"
                val title = "Reset Progress?"
                alert.setTitle(title)
                alert.setMessage(message)
                alert.show()
            }else{
                val alert = AlertDialog.Builder(context)
                alert.setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
                alert.setMessage("Sorry, can't reset the reading lists while on the calendar reading type")
                alert.setTitle("Unable to Reset")
                alert.show()
            }
            false
        }
        manual!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            when(getStringPref(name="planType", defaultValue="horner")) {
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
            resetDaily(requireContext()).addOnSuccessListener {
                Toast.makeText(mainActivity.applicationContext, "Forced Daily Reset", Toast.LENGTH_LONG).show()
                val homeId = R.id.navigation_home
                mainActivity.navController.navigate(homeId)
            }
            false
        }
    }
}