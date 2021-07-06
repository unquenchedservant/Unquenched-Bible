package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.BuildConfig
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R

class InformationFragment : PreferenceFragmentCompat() {
    val preferences = App().preferences!!
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
        val acknowledgements: Preference? = findPreference("acknowledgements")
        val mainActivity = activity as MainActivity
        val context = mainActivity.applicationContext

        appHelp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.unquenched.bible"))
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(context, "No browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
        acknowledgements!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.unquenched.bible/acknowledgements"))
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(context, "No browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
        systemInfo!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.unquenched.bible/the-reading-plans/"))
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(context, "No browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
        license!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            false
        }
        currentVersion?.title = resources.getString(R.string.title_version, BuildConfig.VERSION_NAME)
        currentVersion!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://headwayapp.co/unquenched-bible-changelog"))
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(context, "No browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
        twitter!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/unquenchedservant/Unquenched-Bible"))
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(context, "No browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
        contact!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            var emailText = "\n\n(Please leave the following)\n\n"
            emailText += "App Version: ${BuildConfig.VERSION_CODE} | ${BuildConfig.VERSION_NAME}\n"
            emailText += "Plan System: ${preferences.settings.planSystem}\n"
            emailText += "Plan Method: ${preferences.settings.planType}\n"
            val psText = if(preferences.settings.psalms) "5Psalms" else preferences.list.pgh.list6.toString()
            emailText += "PGH Lists: ${preferences.list.pgh}\n"
            emailText += "MCheyne Lists: ${preferences.list.mcheyne}\n"
            emailText += "Indexes: (${preferences.list.pgh.currentIndex}, ${preferences.list.mcheyne.currentIndex})\n"
            emailText += "PGH Lists Done: ${preferences.list.pgh.listsDone}\n"
            emailText += "Mcheyne Lists Done: ${preferences.list.pgh.listsDone}\n"
            emailText += "Phone: ${Build.PRODUCT}\n"
            emailText += "Android Version: ${Build.VERSION.RELEASE}\n"
            emailText += "ID: ${Firebase.auth.currentUser?.uid}"
            val i = Intent(Intent.ACTION_SENDTO)
                    .setType("text/plain")
                    .setData(Uri.parse("mailto:"))
                    .putExtra(Intent.EXTRA_EMAIL, arrayOf("contact@unquenched.tech"))
                    .putExtra(Intent.EXTRA_TEXT, emailText)
                    .putExtra(Intent.EXTRA_SUBJECT, "COMMENT/QUESTION - Unquenched Bible (Android)")
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(context, "No email app or browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
        discord!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/AKrefXRyuA"))
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(context, "No browser or Discord app installed", Toast.LENGTH_LONG).show()
            }
            false
        }
        mailchimp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://eepurl.com/g_jIob"))
            try {
                startActivity(i)
            }catch(e:ActivityNotFoundException){
                Toast.makeText(context, "No browser installed", Toast.LENGTH_LONG).show()
            }
            false
        }
    }
}
