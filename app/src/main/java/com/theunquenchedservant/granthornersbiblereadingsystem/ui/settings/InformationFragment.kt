package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.App.Companion.applicationContext
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
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
        val mailchimp: Preference? = findPreference("mailchimp")

        systemInfo!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://grant-horner-bible-reading-plan-pdf.weebly.com/uploads/4/5/9/7/45977741/professor-grant-horners-bible-reading-system.pdf"))
            startActivity(i)
            false
        }
        appHelp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://theunquenchedservant.com/bible-reading-system"))
            startActivity(i)
            false
        }
        license!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            false
        }
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
        mailchimp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://eepurl.com/g_jIob"))
            if(i.resolveActivity(App.applicationContext().packageManager)!= null){
                startActivity(i)
            }
            false
        }
    }
}