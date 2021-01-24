package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref

class OnboardingFragmentFour : Fragment() {
    lateinit var vieww: View
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        vieww = inflater.inflate(R.layout.fragment_onboarding_page_four, container, false)
        val dark = getBoolPref("darkMode", true)
        val title = vieww.findViewById<TextView>(R.id.title)
        val helper = vieww.findViewById<TextView>(R.id.helper)
        val doneBtn = vieww.findViewById<MaterialButton>(R.id.doneBtn)
        if(dark){
            vieww.setBackgroundColor(Color.parseColor("#121212"))
            title.setTextColor(Color.parseColor("#9cb9d3"))
            helper.setTextColor(Color.parseColor("#e1e2e6"))
            doneBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383838"))
            doneBtn.backgroundTintMode = PorterDuff.Mode.ADD
            doneBtn.setTextColor(Color.parseColor("#9cb9d3"))
        }else{
            vieww.setBackgroundColor(Color.parseColor("#e1e2e6"))
            title.setTextColor(Color.parseColor("#b36c38"))
            helper.setTextColor(Color.parseColor("#121212"))
            doneBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e1e2e6"))
            doneBtn.setTextColor(Color.parseColor("#121212"))
        }
        doneBtn.setOnClickListener {
            setBoolPref("hasCompletedOnboarding", true)
            startActivity(Intent((activity as OnboardingPagerActivity), MainActivity::class.java))
        }
        return vieww
    }
    override fun onResume(){
        val onboardOne = getBoolPref("onboardOneDone", false)
        val onboardTwo = getBoolPref("onboardingTwoDone", false)
        val title = vieww.findViewById<TextView>(R.id.title)!!
        val helper = vieww.findViewById<TextView>(R.id.helper)!!
        val doneBtn = vieww.findViewById<MaterialButton>(R.id.doneBtn)!!
        if(onboardOne && onboardTwo){
            title.text = "All set to begin using the Unquenched Bible App"
            helper.isVisible = true
            doneBtn.isVisible = true
        }else if(!onboardOne){
            title.text = "Please select the reading system you would like to use"
            helper.isVisible = false
            doneBtn.isVisible = false
        }else if(!onboardTwo){
            title.text = "Please select the reading method you would like to use"
            helper.isVisible = false
            doneBtn.isVisible = false
        }
        super.onResume()
    }
}