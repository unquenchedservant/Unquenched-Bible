package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref

class OnboardingFragmentTwo : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
       val view = inflater.inflate(R.layout.fragment_onboarding_page_two, container, false)
        val dark = getBoolPref(name="darkMode", defaultValue=true)
        val title = view.findViewById<TextView>(R.id.title)
        val titlePgh = view.findViewById<TextView>(R.id.title_pgh)
        val summaryPgh = view.findViewById<TextView>(R.id.summary_pgh)
        val checkboxPgh = view.findViewById<CheckBox>(R.id.checkbox_pgh)
        val titleMcheyne = view.findViewById<TextView>(R.id.title_mcheyne)
        val summaryMcheyne = view.findViewById<TextView>(R.id.summary_mcheyne)
        val checkboxMcheyne = view.findViewById<CheckBox>(R.id.checkbox_mcheyne)
        val moreInfoBtn = view.findViewById<MaterialButton>(R.id.moreInfoBtn)
        moreInfoBtn.isEnabled = true
        if(dark){
            view.setBackgroundColor(Color.parseColor("#121212"))
            title.setTextColor(Color.parseColor("#9cb9d3"))
            titlePgh.setTextColor(Color.parseColor("#9cb9d3"))
            summaryPgh.setTextColor(Color.parseColor("#e1e2e6"))
            titleMcheyne.setTextColor(Color.parseColor("#9cb9d3"))
            summaryMcheyne.setTextColor(Color.parseColor("#e1e2e6"))
            moreInfoBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383838"))
            moreInfoBtn.backgroundTintMode = PorterDuff.Mode.ADD
            moreInfoBtn.setTextColor(Color.parseColor("#9cb9d3"))
        }else{
            view.setBackgroundColor(Color.parseColor("#e1e2e6"))
            title.setTextColor(Color.parseColor("#b36c38"))
            titlePgh.setTextColor(Color.parseColor("#b36c38"))
            summaryPgh.setTextColor(Color.parseColor("#121212"))
            titleMcheyne.setTextColor(Color.parseColor("#b36c38"))
            summaryMcheyne.setTextColor(Color.parseColor("#121212"))
            moreInfoBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e1e2e6"))
            moreInfoBtn.setTextColor(Color.parseColor("#121212"))
        }
        moreInfoBtn.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.unquenched.bible/the-reading-plans/"))
            startActivity(i)
        }
        checkboxPgh.setOnClickListener {
            val check = it as CheckBox
            if(check.isChecked){
                setStringPref(name="planSystem", value="pgh")
                setBoolPref(name="pghSystem", value=true)
                setBoolPref(name="mcheyneSystem", value=false)
                setBoolPref(name="onboardOneDone", value=true)
                checkboxMcheyne.isChecked = false
            }else{
                setStringPref(name="planSystem", value="")
                setBoolPref(name="pghSystem", value=false)
                setBoolPref(name="onboardOneDone", value=false)
            }
        }
        checkboxMcheyne.setOnClickListener {
            val check = it as CheckBox
            if(check.isChecked){
                setStringPref(name="planSystem", value="mcheyne")
                setBoolPref(name="mcheyneSystem", value=true)
                setBoolPref(name="pghSystem", value=false)
                setBoolPref(name="onboardOneDone", value=true)
                checkboxPgh.isChecked = false
            }else{
                setStringPref(name="planSystem", value="")
                setBoolPref(name="mcheyneSystem", value=false)
                setBoolPref(name="onboardOneDone", value=false)
            }
        }
        return view
    }
}