package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref

class OnboardingFragmentOne : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View{
        val mainActivity = activity as OnboardingPagerActivity
        val view = inflater.inflate(R.layout.fragment_onboarding_page_one, container, false)
        val title = view.findViewById<TextView>(R.id.title)
        val helper = view.findViewById<TextView>(R.id.helper)
        val dark = getBoolPref(name="darkMode", defaultValue=true)
        val image = view.findViewById<ImageView>(R.id.image)
        if(dark){
            view.setBackgroundColor(Color.parseColor("#121212"))
            title.setTextColor(Color.parseColor("#9cb9d3"))
            helper.setTextColor(Color.parseColor("#e1e2e6"))
        }else{
            view.setBackgroundColor(Color.parseColor("#e1e2e6"))
            title.setTextColor(Color.parseColor("#b36c38"))
            helper.setTextColor(Color.parseColor("#121212"))
        }
        image.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.logo2, mainActivity.theme))
        return view
    }
}