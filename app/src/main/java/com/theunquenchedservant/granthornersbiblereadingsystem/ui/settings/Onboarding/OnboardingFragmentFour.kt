package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings.Onboarding

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFirestore

class OnboardingFragmentFour : Fragment() {
    lateinit var vieww: View
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        vieww = inflater.inflate(R.layout.fragment_onboarding_page_four, container, false)
        val mainActivity = activity as OnboardingPagerActivity
        val dark = getBoolPref(name="darkMode", defaultValue=true)
        val title = vieww.findViewById<TextView>(R.id.title)
        val helper = vieww.findViewById<TextView>(R.id.helper)
        val backBtn = vieww.findViewById<MaterialButton>(R.id.back_button)
        val doneBtn = vieww.findViewById<MaterialButton>(R.id.doneBtn)
        if(dark){
            vieww.setBackgroundColor(Color.parseColor("#121212"))
            title.setTextColor(Color.parseColor("#9cb9d3"))
            helper.setTextColor(Color.parseColor("#e1e2e6"))
            doneBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383838"))
            doneBtn.backgroundTintMode = PorterDuff.Mode.ADD
            doneBtn.setTextColor(Color.parseColor("#9cb9d3"))
            backBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383838"))
            backBtn.backgroundTintMode = PorterDuff.Mode.ADD
            backBtn.setTextColor(Color.parseColor("#9cb9d3"))
        }else{
            vieww.setBackgroundColor(Color.parseColor("#e1e2e6"))
            title.setTextColor(Color.parseColor("#b36c38"))
            helper.setTextColor(Color.parseColor("#121212"))
            doneBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e1e2e6"))
            doneBtn.setTextColor(Color.parseColor("#121212"))
            backBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e1e2e6"))
            backBtn.setTextColor(Color.parseColor("#121212"))
        }
        backBtn.setOnClickListener {
            mainActivity.viewPager.currentItem -= 1
        }
        doneBtn.setOnClickListener {
            val data = mutableMapOf<String, Any>()
            data["hasCompletedOnboarding"] = setBoolPref(name="hasCompletedOnboarding", value=true)
            data["planSystem"] = getStringPref("planSystem", defaultValue="pgh")
            data["planType"] = getStringPref("planType", defaultValue="horner")
            data["darkMode"] = getBoolPref("darkMode", defaultValue=true)
            updateFirestore(data).addOnSuccessListener {
                startActivity(Intent(mainActivity, MainActivity::class.java))
            }
        }
        return vieww
    }
    override fun onResume(){
        val onboardOne = getBoolPref(name="onboardOneDone", defaultValue=false)
        val onboardTwo = getBoolPref(name="onboardingTwoDone", defaultValue=false)
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