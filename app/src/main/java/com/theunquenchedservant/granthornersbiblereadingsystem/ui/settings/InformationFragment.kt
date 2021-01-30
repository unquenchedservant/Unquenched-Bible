package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.R

class InformationFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.information_preferences, rootKey)
        val license: Preference? = findPreference("licenses")
        val appHelp: Preference? = findPreference("appInfo")
        val systemInfo: Preference? = findPreference("systemInfo")
        val contact: Preference? = findPreference("contact")
        val twitter: Preference? = findPreference("twitter")
        val currentVersion: Preference? = findPreference("currentVersion")
        val discord: Preference? = findPreference("discordLink")
        val mailchimp: Preference? = findPreference("mailchimp")
        appHelp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.unquenched.bible"))
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(App.applicationContext(), "No browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
        systemInfo!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.unquenched.bible/the-reading-plans/"))
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(App.applicationContext(), "No browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
        license!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            false
        }
        currentVersion?.title = resources.getString(R.string.title_version)
        currentVersion!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://headwayapp.co/unquenched-bible-changelog"))
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(App.applicationContext(), "No browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
        twitter!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/unquenchedservant/Unquenched-Bible"))
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(App.applicationContext(), "No browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
        contact!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_SENDTO)
                    .setType("text/plain")
                    .setData(Uri.parse("mailto:"))
                    .putExtra(Intent.EXTRA_EMAIL, arrayOf("contact@unquenched.tech"))
                    .putExtra(Intent.EXTRA_SUBJECT, "COMMENT/QUESTION - PGH APP")
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(App.applicationContext(), "No email app or browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
        discord!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/AKrefXRyuA"))
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(App.applicationContext(), "No browser or Discord app installed", Toast.LENGTH_LONG).show()
            }
            false
        }
        mailchimp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://eepurl.com/g_jIob"))
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(App.applicationContext(), "No browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
    }
}