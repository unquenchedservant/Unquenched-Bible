package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings.Onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.theunquenchedservant.granthornersbiblereadingsystem.R

private const val NUM_PAGES = 4

class OnboardingPagerActivity : FragmentActivity() {
    lateinit var viewPager: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?){
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_slider)
        viewPager = this.findViewById(R.id.pager)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled  = false
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