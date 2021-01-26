package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
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
        titleHorner = vieww.findViewById(R.id.title_horner)
        val summaryHorner = vieww.findViewById<TextView>(R.id.summary_horner)
        checkboxHorner = vieww.findViewById(R.id.checkbox_horner)
        val titleNumerical = vieww.findViewById<TextView>(R.id.title_numerical)
        val summaryNumerical = vieww.findViewById<TextView>(R.id.summary_numerical)
        checkboxNumerical = vieww.findViewById(R.id.checkbox_numerical)
        titleCalendar = vieww.findViewById(R.id.title_calendar)
        val summaryCalendar = vieww.findViewById<TextView>(R.id.summary_calendar)
        checkboxCalendar = vieww.findViewById(R.id.checkbox_calendar)
        if(dark){
            vieww.setBackgroundColor(Color.parseColor("#121212"))
            title.setTextColor(Color.parseColor("#9cb9d3"))
            titleHorner.setTextColor(Color.parseColor("#9cb9d3"))
            titleNumerical.setTextColor(Color.parseColor("#9cb9d3"))
            titleCalendar.setTextColor(Color.parseColor("#9cb9d3"))
            summaryHorner.setTextColor(Color.parseColor("#e1e2e6"))
            summaryNumerical.setTextColor(Color.parseColor("#e1e2e6"))
            summaryCalendar.setTextColor(Color.parseColor("#e1e2e6"))
        }else{
            vieww.setBackgroundColor(Color.parseColor("#e1e2e6"))
            title.setTextColor(Color.parseColor("#b36c38"))
            titleHorner.setTextColor(Color.parseColor("#b36c38"))
            titleNumerical.setTextColor(Color.parseColor("#b36c38"))
            titleCalendar.setTextColor(Color.parseColor("#b36c38"))
            summaryHorner.setTextColor(Color.parseColor("#121212"))
            summaryNumerical.setTextColor(Color.parseColor("#121212"))
            summaryCalendar.setTextColor(Color.parseColor("#121212"))
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
            }else{
                setBoolPref(name="onboardingTwoDone", value=false)
                setStringPref(name="planType", value="")
                setBoolPref(name="grantHorner", value=false)
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
            }else{
                setBoolPref(name="onboardingTwoDone", value=false)
                setStringPref(name="planType", value="")
                setBoolPref(name="numericalDay", value=false)
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
            }else{
                setBoolPref(name="onboardingTwoDone", value=false)
                setStringPref(name="planType", value="")
                setBoolPref(name="calendarDay", value=false)
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