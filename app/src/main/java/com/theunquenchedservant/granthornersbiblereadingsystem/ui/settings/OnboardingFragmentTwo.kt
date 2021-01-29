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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
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
        val mainActivity = activity as OnboardingPagerActivity
        val title = view.findViewById<TextView>(R.id.title)
        val titlePgh = view.findViewById<TextView>(R.id.title_pgh)
        val summaryPgh = view.findViewById<TextView>(R.id.summary_pgh)
        val checkboxPgh = view.findViewById<CheckBox>(R.id.checkbox_pgh)
        val titleMcheyne = view.findViewById<TextView>(R.id.title_mcheyne)
        val summaryMcheyne = view.findViewById<TextView>(R.id.summary_mcheyne)
        val checkboxMcheyne = view.findViewById<CheckBox>(R.id.checkbox_mcheyne)
        val moreInfoBtn = view.findViewById<MaterialButton>(R.id.moreInfoBtn)
        val pghArea = view.findViewById<LinearLayout>(R.id.pgh_option)
        val mcheyneArea = view.findViewById<LinearLayout>(R.id.mcheyne_option)
        val nextBtn = view.findViewById<MaterialButton>(R.id.next_button)
        val backBtn = view.findViewById<MaterialButton>(R.id.back_button)
        nextBtn.isVisible = false
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
            backBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383838"))
            backBtn.backgroundTintMode = PorterDuff.Mode.ADD
            backBtn.setTextColor(Color.parseColor("#9cb9d3"))
            nextBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383838"))
            nextBtn.backgroundTintMode = PorterDuff.Mode.ADD
            nextBtn.setTextColor(Color.parseColor("#9cb9d3"))
        }else{
            view.setBackgroundColor(Color.parseColor("#e1e2e6"))
            title.setTextColor(Color.parseColor("#b36c38"))
            titlePgh.setTextColor(Color.parseColor("#b36c38"))
            summaryPgh.setTextColor(Color.parseColor("#121212"))
            titleMcheyne.setTextColor(Color.parseColor("#b36c38"))
            summaryMcheyne.setTextColor(Color.parseColor("#121212"))
            moreInfoBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e1e2e6"))
            moreInfoBtn.setTextColor(Color.parseColor("#121212"))
            nextBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e1e2e6"))
            nextBtn.setTextColor(Color.parseColor("#121212"))
            backBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e1e2e6"))
            backBtn.setTextColor(Color.parseColor("#121212"))
        }
        moreInfoBtn.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.unquenched.bible/the-reading-plans/"))
            startActivity(i)
        }
        nextBtn.setOnClickListener {
            mainActivity.viewPager.currentItem += 1
        }
        backBtn.setOnClickListener {
            mainActivity.viewPager.currentItem -= 1
        }
        pghArea.setOnClickListener {
            if(getBoolPref("pghSystem")){
                setStringPref(name="planSystem", value="")
                setBoolPref(name="pghSystem", value=false)
                setBoolPref(name="onboardOneDone", value=false)
                checkboxPgh.isChecked = false
                nextBtn.isVisible = false
            }else{
                setStringPref(name="planSystem", value="pgh")
                setBoolPref(name="pghSystem", value=true)
                setBoolPref(name="mcheyneSystem", value=false)
                setBoolPref(name="onboardOneDone", value=true)
                checkboxPgh.isChecked = true
                checkboxMcheyne.isChecked = false
                nextBtn.isVisible = true
            }
        }
        mcheyneArea.setOnClickListener {
            if(getBoolPref(name="mcheyneSystem")){
                setStringPref(name="planSystem", value="")
                setBoolPref(name="mcheyneSystem", value=false)
                setBoolPref(name="onboardOneDone", value=false)
                checkboxMcheyne.isChecked = false
                nextBtn.isVisible = false
            }else{
                setStringPref(name="planSystem", value="mcheyne")
                setBoolPref(name="pghSystem", value=false)
                setBoolPref(name="mcheyneSystem", value=true)
                setBoolPref(name="onboardOneDone", value=true)
                checkboxPgh.isChecked = false
                nextBtn.isVisible = true
                checkboxMcheyne.isChecked = true
            }
        }
        checkboxPgh.setOnClickListener {
            val check = it as CheckBox
            if(check.isChecked){
                setStringPref(name="planSystem", value="pgh")
                setBoolPref(name="pghSystem", value=true)
                setBoolPref(name="mcheyneSystem", value=false)
                setBoolPref(name="onboardOneDone", value=true)
                checkboxMcheyne.isChecked = false
                nextBtn.isVisible = true
            }else{
                setStringPref(name="planSystem", value="")
                setBoolPref(name="pghSystem", value=false)
                setBoolPref(name="onboardOneDone", value=false)
                nextBtn.isVisible = false
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
                nextBtn.isVisible = true
            }else{
                setStringPref(name="planSystem", value="")
                setBoolPref(name="mcheyneSystem", value=false)
                setBoolPref(name="onboardOneDone", value=false)
                nextBtn.isVisible = false
            }
        }
        return view
    }
}