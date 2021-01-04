package com.theunquenchedservant.granthornersbiblereadingsystem

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorStateListDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.res.ColorStateListInflaterCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.ActivityMainBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.getDate

class MainActivity : AppCompatActivity(),  BottomNavigationView.OnNavigationItemSelectedListener{

    private var _rcSignIn = 96
    private var user: FirebaseUser? = null
    private var globalmenu : Menu? = null
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment
    lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        WebView(applicationContext)
        val stateList = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )
        val colorList: IntArray
        val toolbarColor: Int
        if(getBoolPref("darkMode", true)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            toolbarColor = ContextCompat.getColor(App.applicationContext(), R.color.buttonBackgroundDark)
            colorList = intArrayOf(
                    ContextCompat.getColor(this, R.color.unquenchedEmphDark),
                    ContextCompat.getColor(this, R.color.unquenchedTextDark)
            )
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            toolbarColor = ContextCompat.getColor(App.applicationContext(), R.color.buttonBackground)
            colorList = intArrayOf(
                    ContextCompat.getColor(this, R.color.unquenchedOrange),
                    ContextCompat.getColor(this, R.color.unquenchedText)
            )
        }
        val colorStateList = ColorStateList(stateList, colorList)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        user = FirebaseAuth.getInstance().currentUser
        setContentView(binding.root)
        val toolbar = findViewById<MaterialToolbar>(R.id.my_toolbar)
        toolbar.setBackgroundColor(toolbarColor)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.title = getDate(0, true)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        log("TESTING ${binding.bottomNav.itemIconTintList?.defaultColor}")
        binding.bottomNav.setBackgroundColor(toolbarColor)
        binding.bottomNav.itemIconTintList = colorStateList
        binding.bottomNav.itemTextColor = colorStateList
        navController.navigate(R.id.navigation_home)
        setupBottomNavigationBar()
    }
    private fun setupBottomNavigationBar() {
        switchEnabled("home")
        binding.bottomNav.setOnNavigationItemSelectedListener(this)
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            when (destination.id) {
                R.id.navigation_scripture ->{
                    binding.myToolbar.setNavigationOnClickListener{
                        navController.navigate(R.id.navigation_home)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                R.id.navigation_plan_settings ->{
                    binding.myToolbar.setNavigationOnClickListener{
                        navController.navigate(R.id.navigation_settings)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                }
                R.id.navigation_notifications->{
                    binding.myToolbar.setNavigationOnClickListener{
                        navController.navigate(R.id.navigation_settings)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                }
                R.id.navigation_overrides->{
                    binding.myToolbar.setNavigationOnClickListener{
                        navController.navigate(R.id.navigation_settings)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                }
                R.id.navigation_information->{
                    binding.myToolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.navigation_settings)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                }
                R.id.navigation_home -> {
                    log("home selected")
                    switchEnabled("home")
                    if(getBoolPref("darkMode", true)) {
                        binding.navHostFragment.setBackgroundColor(Color.parseColor("#121212"))
                    }else {
                        binding.navHostFragment.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                    supportActionBar?.title = getDate(0, true)
                    supportActionBar?.show()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                R.id.navigation_stats -> {
                    log("stats selected")
                    switchEnabled("stats")
                    supportActionBar?.title = destination.label
                }
                R.id.navigation_settings -> {
                    switchEnabled("settings")
                    supportActionBar?.title = destination.label
                }
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navControl = findNavController(this, R.id.nav_host_fragment)
        when(item.itemId){
            R.id.navigation_home ->{
                supportActionBar?.title = getDate(0, true)
                switchEnabled("home")
                navControl.navigate(R.id.navigation_home)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            R.id.navigation_stats ->{
                log("Stats was pressed")
                supportActionBar?.title = "Statistics"
                switchEnabled("stats")
                navControl.navigate(R.id.navigation_stats)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            R.id.navigation_settings -> {
                switchEnabled("Settings")
                navControl.navigate(R.id.navigation_settings)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
        return true
    }

    private fun switchEnabled(current: String){
        val menu = binding.bottomNav.menu
        menu.findItem(R.id.navigation_home)?.isEnabled = current != "home"
        menu.findItem(R.id.navigation_stats)?.isEnabled = current != "stats"
        menu.findItem(R.id.navigation_settings)?.isEnabled = current != "settings"
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        try {
            toggle.syncState()
        }catch(e: UninitializedPropertyAccessException){

        }
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig) }


    override fun onBackPressed() {
        log("TESTING TESTING TESTING")
        if(navController.currentDestination?.id != R.id.navigation_home){
            navController.popBackStack()
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            binding.bottomNav.isVisible = true
            supportActionBar?.show()
        }else{
            finish()
        }

    }




    companion object{

        fun log(logString:String){
            Log.d("PROFGRANT", logString)
        }


    }
}
