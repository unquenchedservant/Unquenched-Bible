package com.theunquenchedservant.granthornersbiblereadingsystem

import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.firestoneToPreference
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.preferenceToFireStone
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.ActivityMainBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.checkDate
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        user = FirebaseAuth.getInstance().currentUser
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.title = getDate(0, true)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        navController.navigate(R.id.navigation_home)
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        switchEnabled("home")
        binding.bottomNav.setOnNavigationItemSelectedListener(this)
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
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
                    supportActionBar?.title = getDate(0, true)
                    supportActionBar?.show()
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
