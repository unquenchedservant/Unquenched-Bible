package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS

class PlanSystemFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.plan_system_menu, rootKey)
        val mainActivity = activity as MainActivity
        val pgh: Preference? = findPreference("pgh_system")
        val mcheyne: Preference? = findPreference("mcheyne_system")
        val moreInfo: Preference? = findPreference("planMoreInfo")

        when(SharedPref.getStringPref("planSystem", "pgh")){
            "pgh"-> {pgh!!.setDefaultValue(true); mcheyne!!.setDefaultValue(false)}
            "mcheyne" -> {pgh!!.setDefaultValue(false); mcheyne!!.setDefaultValue(true)}
        }

        pgh!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            setStringPref(name="planSystem", value="pgh", updateFS=true)
            setBoolPref(name="mcheyne_system", value=false)
            mainActivity.navController.navigate(R.id.navigation_plan_system)
            true
        }
        mcheyne!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            setStringPref(name="planSystem", value="mcheyne", updateFS=true)
            updateFS("planSystem", "mcheyne")
            setBoolPref(name="pgh_system", value=false)
            mainActivity.navController.navigate(R.id.navigation_plan_system)
            true
        }
        moreInfo!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.unquenched.bible/the-reading-plans/"))
            startActivity(i)
            false
        }
    }
}