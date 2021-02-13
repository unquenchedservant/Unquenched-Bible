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
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref

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
        currentVersion?.title = resources.getString(R.string.title_version, BuildConfig.VERSION_NAME)
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
            var emailText = "\n\n(Please leave the following)\n\n"
            emailText += "App Version: ${BuildConfig.VERSION_CODE} | ${BuildConfig.VERSION_NAME}\n"
            emailText += "Plan System: ${getStringPref("planSystem")}\n"
            emailText += "Plan Method: ${getStringPref("planType")}\n"
            val psText = if(getBoolPref("psalms")) "5Psalms" else getIntPref("list6").toString()
            emailText += "PGH Lists: (${getIntPref("list1")}, ${getIntPref("list2")}, ${getIntPref("list3")}, ${getIntPref("list4")}, ${getIntPref("list5")}, $psText, ${getIntPref("list7")}, ${getIntPref("list8")}, ${getIntPref("list9")}, ${getIntPref("list10")})\n"
            emailText += "MCheyne Lists: (${getIntPref("mcheyneList1")}, ${getIntPref("mcheyneList2")}, ${getIntPref("mcheyneList3")}, ${getIntPref("mcheyneList4")})\n"
            emailText += "Indexes: (${getIntPref("currentDayIndex")}, ${getIntPref("mcheyneCurrentDayIndex")})\n"
            emailText += "PGH Lists Done: ${getIntPref("listsDone")}\n"
            emailText += "Mcheyne Lists Done: ${getIntPref("mcheyneListsDone")}\n"
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
