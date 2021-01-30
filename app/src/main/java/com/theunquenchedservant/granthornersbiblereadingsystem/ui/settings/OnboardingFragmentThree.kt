package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
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
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref

class OnboardingFragmentThree : Fragment() {
    private lateinit var checkboxHorner: CheckBox
    private lateinit var checkboxCalendar: CheckBox
    private lateinit var checkboxNumerical: CheckBox
    private lateinit var titleHorner: TextView
    private lateinit var titleCalendar: TextView
    private lateinit var vieww: View
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        vieww = inflater.inflate(R.layout.fragment_onboarding_page_three, container, false)
        val dark = getBoolPref(name="darkMode", defaultValue=true)
        val title = vieww.findViewById<TextView>(R.id.title)
        val mainActivity = activity as OnboardingPagerActivity
        titleHorner = vieww.findViewById(R.id.title_horner)
        val summaryHorner = vieww.findViewById<TextView>(R.id.summary_horner)
        checkboxHorner = vieww.findViewById(R.id.checkbox_horner)
        val titleNumerical = vieww.findViewById<TextView>(R.id.title_numerical)
        val summaryNumerical = vieww.findViewById<TextView>(R.id.summary_numerical)
        checkboxNumerical = vieww.findViewById(R.id.checkbox_numerical)
        titleCalendar = vieww.findViewById(R.id.title_calendar)
        val summaryCalendar = vieww.findViewById<TextView>(R.id.summary_calendar)
        checkboxCalendar = vieww.findViewById(R.id.checkbox_calendar)
        val nextBtn = vieww.findViewById<MaterialButton>(R.id.next_button)
        val backBtn = vieww.findViewById<MaterialButton>(R.id.back_button)
        nextBtn.isVisible = false
        val colorOne: Int
        val colorTwo: Int
        val colorThree: Int
        val colorFour: Int
        if(dark){
            colorOne = Color.parseColor("#121212")
            colorTwo = Color.parseColor("#9cb9d3")
            colorThree = Color.parseColor("#e1e2e6")
            colorFour = Color.parseColor("#383838")

        }else{
            colorOne = Color.parseColor("#e1e2e6")
            colorTwo = Color.parseColor("#b36c38")
            colorThree = Color.parseColor("#121212")
            colorFour = Color.parseColor("#e1e2e6")
        }
        vieww.setBackgroundColor(colorOne)
        title.setTextColor(colorTwo)
        titleHorner.setTextColor(colorTwo)
        titleNumerical.setTextColor(colorTwo)
        titleCalendar.setTextColor(colorTwo)
        summaryHorner.setTextColor(colorThree)
        summaryNumerical.setTextColor(colorThree)
        summaryCalendar.setTextColor(colorThree)
        backBtn.backgroundTintList = ColorStateList.valueOf(colorFour)
        backBtn.backgroundTintMode = PorterDuff.Mode.ADD
        backBtn.setTextColor(colorTwo)
        nextBtn.backgroundTintList = ColorStateList.valueOf(colorFour)
        nextBtn.backgroundTintMode = PorterDuff.Mode.ADD
        nextBtn.setTextColor(colorTwo)
        checkboxHorner.buttonTintList = ColorStateList.valueOf(colorTwo)
        checkboxCalendar.buttonTintList = ColorStateList.valueOf(colorTwo)
        checkboxNumerical.buttonTintList = ColorStateList.valueOf(colorTwo)
        nextBtn.setOnClickListener {
            mainActivity.viewPager.currentItem += 1
        }
        backBtn.setOnClickListener {
            mainActivity.viewPager.currentItem -= 1
        }
        checkboxHorner.setOnClickListener {
            val check = it as CheckBox
            if(check.isChecked){
                setBoolPref(name="grantHorner", value=true)
                setBoolPref(name="numericalDay", value=false)
                setBoolPref(name="calendarDay", value=false)
                setBoolPref(name="onboardingTwoDone", value=true)
                setStringPref(name="planType", value="horner")
                checkboxNumerical.isChecked = false
                checkboxCalendar.isChecked = false
                nextBtn.isVisible = true
            }else{
                setBoolPref(name="onboardingTwoDone", value=false)
                setStringPref(name="planType", value="")
                setBoolPref(name="grantHorner", value=false)
                nextBtn.isVisible = false
            }
        }
        checkboxNumerical.setOnClickListener {
            val check = it as CheckBox
            if(check.isChecked){
                setBoolPref(name="grantHorner", value=false)
                setBoolPref(name="numericalDay", value=true)
                setBoolPref(name="calendarDay", value=false)
                setBoolPref(name="onboardingTwoDone", value=true)
                setStringPref(name="planType", value="numerical")
                checkboxHorner.isChecked = false
                checkboxCalendar.isChecked = false
                nextBtn.isVisible = true
            }else{
                setBoolPref(name="onboardingTwoDone", value=false)
                setStringPref(name="planType", value="")
                setBoolPref(name="numericalDay", value=false)
                nextBtn.isVisible = false
            }
        }
        checkboxCalendar.setOnClickListener {
            val check = it as CheckBox
            if(check.isChecked){
                setBoolPref(name="grantHorner", value=false)
                setBoolPref(name="numericalDay", value=false)
                setBoolPref(name="calendarDay", value=true)
                setBoolPref(name="onboardingTwoDone", value=true)
                setStringPref(name="planType", value="calendar")
                checkboxNumerical.isChecked = false
                checkboxHorner.isChecked = false
                nextBtn.isVisible = true
            }else{
                setBoolPref(name="onboardingTwoDone", value=false)
                setStringPref(name="planType", value="")
                setBoolPref(name="calendarDay", value=false)
                nextBtn.isVisible = false
            }
        }
        return vieww
    }
    override fun onResume(){
        val planSystem = getStringPref(name="planSystem", defaultValue="")
        if(planSystem == "pgh"){
            titleHorner.text = resources.getString(R.string.onboarding_p3_horner_b)
            titleCalendar.text = resources.getString(R.string.onboarding_p3_calendar_a)
        }else if(planSystem == "mcheyne"){
            titleHorner.text = resources.getString(R.string.onboarding_p3_horner_a)
            titleCalendar.text = resources.getString(R.string.onboarding_p3_calendar_b)
        }
        super.onResume()
    }
}