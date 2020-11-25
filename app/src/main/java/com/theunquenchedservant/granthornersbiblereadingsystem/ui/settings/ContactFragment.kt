package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R

class ContactFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.contact_preferences, rootKey)
        val contact: Preference? = findPreference("contact")
        val twitter: Preference? = findPreference("twitter")
        val currentVersion: Preference? = findPreference("currentVersion")


        currentVersion?.title = "Current Version - 1.2"
        currentVersion!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://theunquenchedservant.com/changelog/"))
            if(i.resolveActivity(App.applicationContext().packageManager)!= null){
                startActivity(i)
            }
            false
        }
        twitter!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/unquenchedbible"))
            if(i.resolveActivity(App.applicationContext().packageManager)!= null){
                startActivity(i)
            }
            false
        }
        contact!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_SENDTO)
                    .setType("text/plain")
                    .setData(Uri.parse("mailto:"))
                    .putExtra(Intent.EXTRA_EMAIL, arrayOf("contact@unquenched.tech"))
                    .putExtra(Intent.EXTRA_SUBJECT, "COMMENT/QUESTION - PGH APP")
            if(i.resolveActivity(App.applicationContext().packageManager) != null){
                MainActivity.log("STARTING ACTIVITY SOON")
                startActivity(i)
            }else{
                MainActivity.log("PACKAGE MANAGER ${i.resolveActivity(App.applicationContext().packageManager)}")
            }
            false
        }
    }
}