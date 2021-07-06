package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Preferences
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPreferences

private const val NUM_PAGES = 4

class OnboardingActivity : FragmentActivity() {
    lateinit var viewPager: ViewPager2
    lateinit var preferences: Preferences
    override fun onCreate(savedInstanceState: Bundle?){
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                preferences = Preferences(it.data!!, resources, this)
                setContentView(R.layout.activity_onboarding_slider)
                viewPager = this.findViewById(R.id.pager)
                val pagerAdapter = ScreenSlidePagerAdapter(this)
                viewPager.adapter = pagerAdapter
                viewPager.isUserInputEnabled = false
            }
    }

    override fun onBackPressed() {
        if(viewPager.currentItem == 0){
            super.onBackPressed()
        }else{
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    private inner class ScreenSlidePagerAdapter(fa:FragmentActivity): FragmentStateAdapter(fa){
        override fun getItemCount(): Int = NUM_PAGES
        override fun createFragment(position: Int): Fragment {
            return when(position){
                0-> OnboardingFragmentOne()
                1-> OnboardingFragmentTwo()
                2-> OnboardingFragmentThree()
                3-> OnboardingFragmentFour()
                else->{
                    OnboardingFragmentOne()
                }
            }
        }
    }
}
class OnboardingFragmentOne : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainActivity = activity as OnboardingActivity
        val preferences = (activity as OnboardingActivity).preferences
        val view = inflater.inflate(R.layout.fragment_onboarding_page_one, container, false)
        val title = view.findViewById<TextView>(R.id.title)
        val nextButton = view.findViewById<Button>(R.id.next_button)
        val image = view.findViewById<ImageView>(R.id.image)
        val colors = preferences.colors
        view.setBackgroundColor(colors.background)
        title.setTextColor(colors.textColor2)
        nextButton.backgroundTintList = ColorStateList.valueOf(colors.buttonBackground)
        nextButton.backgroundTintMode = PorterDuff.Mode.ADD
        nextButton.setTextColor(colors.emphColor)
        nextButton.setOnClickListener {
            mainActivity.viewPager.currentItem = 1
        }
        image.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.logo2, mainActivity.theme))
        return view
    }
}

class OnboardingFragmentTwo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_onboarding_page_two, container, false)
        val mainActivity = activity as OnboardingActivity
        val preferences = (activity as OnboardingActivity).preferences
        val title = view.findViewById<TextView>(R.id.title)
        val titlePgh = view.findViewById<TextView>(R.id.title_pgh)
        val summaryPgh = view.findViewById<TextView>(R.id.summary_pgh)
        val checkboxPgh = view.findViewById<CheckBox>(R.id.checkbox_pgh)
        val titleMcheyne = view.findViewById<TextView>(R.id.title_mcheyne)
        val summaryMcheyne = view.findViewById<TextView>(R.id.summary_mcheyne)
        val checkboxMcheyne = view.findViewById<CheckBox>(R.id.checkbox_mcheyne)
        val moreInfoBtn = view.findViewById<MaterialButton>(R.id.moreInfoBtn)
        val nextBtn = view.findViewById<MaterialButton>(R.id.next_button)
        val backBtn = view.findViewById<MaterialButton>(R.id.back_button)
        nextBtn.isVisible = false
        moreInfoBtn.isEnabled = true
        val color = preferences.colors
        view.setBackgroundColor(color.background)
        title.setTextColor(color.textColor2)
        titlePgh.setTextColor(color.textColor2)
        summaryPgh.setTextColor(color.textColor)
        titleMcheyne.setTextColor(color.textColor2)
        summaryMcheyne.setTextColor(color.textColor)
        moreInfoBtn.backgroundTintList = ColorStateList.valueOf(color.buttonBackground)
        moreInfoBtn.backgroundTintMode = PorterDuff.Mode.ADD
        moreInfoBtn.setTextColor(color.textColor2)
        backBtn.backgroundTintList = ColorStateList.valueOf(color.buttonBackground)
        backBtn.backgroundTintMode = PorterDuff.Mode.ADD
        backBtn.setTextColor(color.textColor2)
        nextBtn.backgroundTintList = ColorStateList.valueOf(color.buttonBackground)
        nextBtn.backgroundTintMode = PorterDuff.Mode.ADD
        nextBtn.setTextColor(color.textColor2)
        checkboxPgh.buttonTintList = ColorStateList.valueOf(color.textColor2)
        checkboxMcheyne.buttonTintList = ColorStateList.valueOf(color.textColor2)
        moreInfoBtn.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.unquenched.bible/the-reading-plans/"))
            try {
                startActivity(i)
            }catch(e: ActivityNotFoundException){
                Toast.makeText(context, "No browser installed", Toast.LENGTH_LONG).show()
            }
        }
        nextBtn.setOnClickListener {
            mainActivity.viewPager.currentItem += 1
        }
        backBtn.setOnClickListener {
            mainActivity.viewPager.currentItem -= 1
        }
        checkboxPgh.setOnClickListener {
            setChecked(true, it as CheckBox, nextBtn)
            checkboxMcheyne.isChecked = false
        }
        checkboxMcheyne.setOnClickListener {
            setChecked(false, it as CheckBox, nextBtn)
            checkboxPgh.isChecked = false
        }
        return view
    }
    private fun setChecked(isPGH:Boolean, check:CheckBox, nextBtn:MaterialButton){
        val sharedPref = SharedPreferences(context)
        val preferences = (activity as OnboardingActivity).preferences
        if(check.isChecked){
            preferences.settings.planSystem = if(isPGH) "pgh" else "mcheyne"
            sharedPref.setBoolean(name = "pghSystem", value = isPGH)
            sharedPref.setBoolean(name = "mcheyneSystem", value = !isPGH)
            sharedPref.setBoolean(name = "onboardOneDone", value = true)
            nextBtn.isVisible = true
        }else{
            preferences.settings.planSystem = ""
            SharedPreferences(context).setBoolean(name = "pghSystem", value = false)
            SharedPreferences(context).setBoolean(name = "mcheyneSystem", value=false)
            SharedPreferences(context).setBoolean(name = "onboardOneDone", value=false)
            nextBtn.isVisible = false
        }
    }
}

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
        val title = vieww.findViewById<TextView>(R.id.title)
        val mainActivity = activity as OnboardingActivity
        val preferences = (activity as OnboardingActivity).preferences
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
        val colors = preferences.colors
        vieww.setBackgroundColor(colors.background)
        title.setTextColor(colors.textColor2)
        titleHorner.setTextColor(colors.textColor2)
        titleNumerical.setTextColor(colors.textColor2)
        titleCalendar.setTextColor(colors.textColor2)
        summaryHorner.setTextColor(colors.textColor)
        summaryNumerical.setTextColor(colors.textColor)
        summaryCalendar.setTextColor(colors.textColor)
        backBtn.backgroundTintList = ColorStateList.valueOf(colors.buttonBackground)
        backBtn.backgroundTintMode = PorterDuff.Mode.ADD
        backBtn.setTextColor(colors.textColor2)
        nextBtn.backgroundTintList = ColorStateList.valueOf(colors.buttonBackground)
        nextBtn.backgroundTintMode = PorterDuff.Mode.ADD
        nextBtn.setTextColor(colors.textColor2)
        checkboxHorner.buttonTintList = ColorStateList.valueOf(colors.textColor2)
        checkboxCalendar.buttonTintList = ColorStateList.valueOf(colors.textColor2)
        checkboxNumerical.buttonTintList = ColorStateList.valueOf(colors.textColor2)
        nextBtn.setOnClickListener {
            mainActivity.viewPager.currentItem += 1
        }
        backBtn.setOnClickListener {
            mainActivity.viewPager.currentItem -= 1
        }
        checkboxHorner.setOnClickListener {
            setChecked(isHorner = true, isNumerical = false, isCalendar = false, check = it as CheckBox, nextBtn = nextBtn)
        }
        checkboxNumerical.setOnClickListener {
            setChecked(isHorner = false, isNumerical = true, isCalendar = false, check = it as CheckBox, nextBtn = nextBtn)
        }
        checkboxCalendar.setOnClickListener {
            setChecked(isHorner = false, isNumerical = false, isCalendar = true, check = it as CheckBox, nextBtn = nextBtn)
        }
        return vieww
    }
    private fun setChecked(isHorner:Boolean, isNumerical:Boolean, isCalendar:Boolean, check:CheckBox, nextBtn:MaterialButton){
        val sharedPref = SharedPreferences(context)
        val preferences = (activity as OnboardingActivity).preferences
        if(check.isChecked){
            preferences.settings.planType = when{
                isHorner    -> "horner"
                isNumerical -> "numerical"
                isCalendar  -> "calendar"
                else -> "horner"
            }
            sharedPref.setBoolean(name="horner", value=isHorner)
            sharedPref.setBoolean(name="numericalDay", value=isNumerical)
            sharedPref.setBoolean(name="calendarDay", value=isCalendar)
            sharedPref.setBoolean(name="onboardingTwoDone", value=true)
            checkboxHorner.isChecked = isHorner
            checkboxNumerical.isChecked = isNumerical
            checkboxCalendar.isChecked = isCalendar
            nextBtn.isVisible = true
        }else{
            preferences.settings.planType = ""
            sharedPref.setBoolean(name="horner", value=false)
            sharedPref.setBoolean(name="numericalDay", value=false)
            sharedPref.setBoolean(name="calendarDay", value=false)
            sharedPref.setBoolean(name="onboardingTwoDone", value=false)
            nextBtn.isVisible = false
        }
    }
    override fun onResume(){
        val preferences = (activity as OnboardingActivity).preferences
        val planSystem = preferences.settings.planSystem
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

class OnboardingFragmentFour : Fragment() {

    lateinit var vieww: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        vieww = inflater.inflate(R.layout.fragment_onboarding_page_four, container, false)
        val mainActivity = activity as OnboardingActivity
        val preferences = (activity as OnboardingActivity).preferences
        val colors = preferences.colors
        val title = vieww.findViewById<TextView>(R.id.title)
        val helper = vieww.findViewById<TextView>(R.id.helper)
        val backBtn = vieww.findViewById<MaterialButton>(R.id.back_button)
        val doneBtn = vieww.findViewById<MaterialButton>(R.id.doneBtn)
        vieww.setBackgroundColor(colors.background)
        title.setTextColor(colors.textColor2)
        helper.setTextColor(colors.textColor)
        doneBtn.backgroundTintList = ColorStateList.valueOf(colors.buttonBackground)
        doneBtn.backgroundTintMode = PorterDuff.Mode.ADD
        doneBtn.setTextColor(colors.emphColor)
        backBtn.backgroundTintList = ColorStateList.valueOf(colors.buttonBackground)
        backBtn.backgroundTintMode = PorterDuff.Mode.ADD
        backBtn.setTextColor(colors.emphColor)

        backBtn.setOnClickListener {
            mainActivity.viewPager.currentItem -= 1
        }
        doneBtn.setOnClickListener {
            preferences.settings.hasCompletedOnboarding = true
            val updateData = preferences.getMap()
            Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).update(updateData).addOnSuccessListener {
                startActivity(Intent(mainActivity, MainActivity::class.java))
            }
        }
        return vieww
    }
    override fun onResume(){
        val onboardOne = SharedPreferences(context).getBoolean(name = "onboardOneDone", defaultValue = false)
        val onboardTwo = SharedPreferences(context).getBoolean(name = "onboardingTwoDone", defaultValue = false)
        val title = vieww.findViewById<TextView>(R.id.title)!!
        val helper = vieww.findViewById<TextView>(R.id.helper)!!
        val doneBtn = vieww.findViewById<MaterialButton>(R.id.doneBtn)!!
        if(onboardOne && onboardTwo){
            title.text = resources.getString(R.string.onboarding_p4_title_a)
            helper.isVisible = true
            doneBtn.isVisible = true
        }else if(!onboardOne){
            title.text = resources.getString(R.string.onboarding_p4_title_b)
            helper.isVisible = false
            doneBtn.isVisible = false
        }else if(!onboardTwo){
            title.text = resources.getString(R.string.onboarding_p4_title_c)
            helper.isVisible = false
            doneBtn.isVisible = false
        }
        super.onResume()
    }
}