package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.App.Companion.applicationContext
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R

class InformationFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.information_preferences, rootKey)
        val license: Preference? = findPreference("licenses")
        val appHelp: Preference? = findPreference("appInfo")
        val systemInfo: Preference? = findPreference("systemInfo")


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
    }
}