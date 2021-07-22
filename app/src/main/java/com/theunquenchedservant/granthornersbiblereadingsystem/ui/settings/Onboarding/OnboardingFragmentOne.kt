package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings.Onboarding

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref

class OnboardingFragmentOne : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val mainActivity = activity as OnboardingPagerActivity
        val view = inflater.inflate(R.layout.fragment_onboarding_page_one, container, false)
        val title = view.findViewById<TextView>(R.id.title)
        val nextButton = view.findViewById<Button>(R.id.next_button)
        val dark = getBoolPref(name = "darkMode", defaultValue = true)
        val image = view.findViewById<ImageView>(R.id.image)
        when (dark) {
            true -> {
                view.setBackgroundColor(Color.parseColor("#121212"))
                title.setTextColor(Color.parseColor("#9cb9d3"))
                nextButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383838"))
                nextButton.backgroundTintMode = PorterDuff.Mode.ADD
                nextButton.setTextColor(Color.parseColor("#9cb9d3"))
            }
            false -> {
                view.setBackgroundColor(Color.parseColor("#e1e2e6"))
                title.setTextColor(Color.parseColor("#b36c38"))
                nextButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e1e2e6"))
                nextButton.setTextColor(Color.parseColor("#121212"))
            }
        }
        nextButton.setOnClickListener {
            mainActivity.viewPager.currentItem = 1
        }
        image.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.logo2, mainActivity.theme))
        return view
    }
}