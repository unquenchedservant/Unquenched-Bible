package com.theunquenchedservant.granthornersbiblereadingsystem

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.ActivityMainBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings.OnboardingActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.getDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Firestore
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Preferences
import kotlinx.coroutines.*
import timber.log.Timber

class MainActivity : AppCompatActivity(),  BottomNavigationView.OnNavigationItemSelectedListener{

    private var _rcSignIn = 96
    private var user: FirebaseUser? = null
    private var globalmenu : Menu? = null
    lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    lateinit var binding: ActivityMainBinding
    lateinit var app:App
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        WebView(this@MainActivity)
        super.onCreate(savedInstanceState)
        app = App()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Firebase.auth.currentUser == null) {
            debugLog("NO CURRENT USER")
            startFirebaseAuth()
        } else {
            debugLog("OKAY")
            Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    preferences = Preferences(it.data!!, resources, this)
                    if (preferences.settings.darkMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                    if (!preferences.settings.hasCompletedOnboarding) {
                        startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
                    } else {
                        checkReadingDate()
                        navHostFragment =
                            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                        navController = navHostFragment.navController

                        val stateList = arrayOf(
                            intArrayOf(android.R.attr.state_checked),
                            intArrayOf(-android.R.attr.state_checked)
                        )
                        val colorList: IntArray =
                            intArrayOf(preferences.colors.textColor2, preferences.colors.textColor)
                        val colorStateList = ColorStateList(stateList, colorList)
                        user = Firebase.auth.currentUser
                        val toolbar = findViewById<MaterialToolbar>(R.id.my_toolbar)
                        toolbar.setTitleTextColor(preferences.colors.textColor)
                        toolbar.setBackgroundColor(preferences.colors.buttonBackground)
                        setSupportActionBar(findViewById(R.id.my_toolbar))
                        supportActionBar?.title = getDate(option = 0, fullMonth = true)
                        binding.bottomNav.setupWithNavController(navController)
                        binding.bottomNav.setBackgroundColor(preferences.colors.buttonBackground)
                        binding.bottomNav.itemIconTintList = colorStateList
                        binding.bottomNav.itemTextColor = colorStateList
                        setupBottomNavigationBar()
                        debugLog("IN COROUTINE GOING TO NAVIGATION HOME")
                        when (preferences.settings.planSystem) {
                            "mcheyne" -> navController.navigate(R.id.navigation_home_mcheyne)
                            "pgh" -> navController.navigate(R.id.navigation_home)
                        }
                    }
                }

        }
        traceLog(file="MainActivity.kt", function="onCreate()", "beginning")
    }

    override fun onResume() {
        super.onResume()
        if(isPreferenceInitialized()) {
            val stateList = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            )
            val colorList: IntArray =
                intArrayOf(preferences.colors.textColor2, preferences.colors.textColor)
            val colorStateList = ColorStateList(stateList, colorList)
            val toolbar = findViewById<MaterialToolbar>(R.id.my_toolbar)
            toolbar.setTitleTextColor(preferences.colors.textColor)
            toolbar.setBackgroundColor(preferences.colors.buttonBackground)
            binding.bottomNav.setupWithNavController(navController)
            binding.bottomNav.setBackgroundColor(preferences.colors.buttonBackground)
            binding.bottomNav.itemIconTintList = colorStateList
            binding.bottomNav.itemTextColor = colorStateList
        }
    }
    fun isPreferenceInitialized():Boolean{
        return this::preferences.isInitialized
    }
    fun setupNavigation(navId:Int, bottomNavVisible:Boolean, displayHome1:Boolean, displayHome2:Boolean, translationVisible:Boolean){
        traceLog(file="MainActivity.kt", function="setupNavigation()")
        binding.myToolbar.setNavigationOnClickListener {
            navController.navigate(navId)
            binding.bottomNav.isVisible = bottomNavVisible
            supportActionBar?.setDisplayHomeAsUpEnabled(displayHome1)
        }
        binding.translationSelector.isVisible = translationVisible
        supportActionBar?.setDisplayHomeAsUpEnabled(displayHome2)
    }
    private fun setupBottomNavigationBar() {
        traceLog(file="MainActivity.kt", function="setupBottomNavigationBar()")
        switchEnabled(current="home")
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            val planSystem = preferences.settings.planSystem
            val homeId = if (planSystem == "pgh") R.id.navigation_home else R.id.navigation_home_mcheyne
            when (destination.id) {
                R.id.navigation_scripture ->{
                    setupNavigation(homeId, bottomNavVisible = true, displayHome1 = false, displayHome2 = true, translationVisible = true)
                    when (preferences.settings.bibleVersion){
                        "AMP" -> binding.translationSelector.setSelection(1)
                        "CSB" -> binding.translationSelector.setSelection(2)
                        "ESV" -> binding.translationSelector.setSelection(3)
                        "KJV" -> binding.translationSelector.setSelection(4)
                        "NIV" -> binding.translationSelector.setSelection(5)
                        "NASB95" -> binding.translationSelector.setSelection(6)
                        "NASB20" -> binding.translationSelector.setSelection(7)
                    }
                }
                R.id.navigation_plan_settings ->{
                    setupNavigation(R.id.navigation_settings, bottomNavVisible = true, displayHome1 = false, displayHome2 = true, translationVisible = false)
                }
                R.id.navigation_plan_type->{
                    binding.myToolbar.setNavigationOnClickListener{
                        navController.navigate(R.id.navigation_plan_settings)
                        supportActionBar?.title = "Plan Settings"
                        binding.bottomNav.isVisible = true
                    }
                    binding.translationSelector.isVisible = false
                    supportActionBar?.title = "Plan Method"
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                R.id.navigation_plan_system->{
                    binding.myToolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.navigation_plan_settings)
                        supportActionBar?.title = "Plan Settings"
                        binding.bottomNav.isVisible = true
                    }
                    binding.translationSelector.isVisible = false
                    supportActionBar?.title = "Plan System"
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                R.id.navigation_notifications->{
                    setupNavigation(R.id.navigation_settings, bottomNavVisible = true, displayHome1 = false, displayHome2 = true, translationVisible = false)
                }
                R.id.navigation_overrides->{
                    setupNavigation(R.id.navigation_settings, bottomNavVisible = true, displayHome1 = false, displayHome2 = true, translationVisible = false)
                    supportActionBar?.title="Overrides"
                }
                R.id.navigation_manual->{
                    setupNavigation(R.id.navigation_overrides, bottomNavVisible = true, displayHome1 = true, displayHome2 = true, translationVisible = false)
                    supportActionBar?.title="Manual Override"
                }
                R.id.navigation_manual_numerical->{
                    setupNavigation(R.id.navigation_overrides, bottomNavVisible = true, displayHome1 = true, displayHome2 =true, translationVisible = false)
                    supportActionBar?.title="Manual Override"
                }
                R.id.navigation_information->{
                    setupNavigation(R.id.navigation_settings, bottomNavVisible = true, displayHome1 = false, displayHome2 = true, translationVisible = false)
                }
                R.id.navigation_account_settings->{
                    setupNavigation(R.id.navigation_settings, bottomNavVisible = true, displayHome1 = false, displayHome2 = true, translationVisible = false)
                    supportActionBar?.title = "Account Settings"
                }
                R.id.navigation_update_email->{
                    setupNavigation(R.id.navigation_account_settings, bottomNavVisible = true, displayHome1 = false, displayHome2 = true, translationVisible = false)
                    supportActionBar?.title = "Update Email"
                }
                R.id.navigation_update_password->{
                    setupNavigation(R.id.navigation_account_settings, bottomNavVisible = true, displayHome1 = false, displayHome2 = true, translationVisible = false)
                    supportActionBar?.title = "Update Password"
                }
                R.id.navigation_confirm_delete->{
                    setupNavigation(R.id.navigation_account_settings, bottomNavVisible = true, displayHome1 = false, displayHome2 = true, translationVisible = false)
                    supportActionBar?.title = "Delete Account"
                }
                R.id.navigation_home -> {
                    binding.translationSelector.isVisible = false
                    if(planSystem == "mcheyne"){
                        navController.navigate(R.id.navigation_home_mcheyne)
                    }
                    switchEnabled(current="home")
                    binding.navHostFragment.setBackgroundColor(preferences.colors.background2)
                    supportActionBar?.title = getDate(option=0, fullMonth=true)
                    supportActionBar?.show()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)

                }
                R.id.navigation_home_mcheyne -> {
                    switchEnabled(current="home")
                    binding.navHostFragment.setBackgroundColor(preferences.colors.background2)
                    supportActionBar?.title = getDate(option=0, fullMonth=true)
                    supportActionBar?.show()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    binding.translationSelector.isVisible = false
                }
                R.id.navigation_stats -> {
                    switchEnabled(current="stats")
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.title = destination.label
                    binding.translationSelector.isVisible = false
                }
                R.id.navigation_settings -> {
                    switchEnabled(current="settings")
                    supportActionBar?.title = destination.label
                    binding.translationSelector.isVisible = false
                }
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        traceLog(file="MainActivity.kt", function="onNavigationItemSelected()")
        val homeId = when(preferences.settings.planSystem){
            "pgh"->R.id.navigation_home
            "mcheyne"->R.id.navigation_home_mcheyne
            else->R.id.navigation_home
        }
        val navControl = findNavController(this, R.id.nav_host_fragment)
        when(item.itemId){
            R.id.navigation_home ->{
                supportActionBar?.title = getDate(option=0, fullMonth=true)
                switchEnabled(current="home")
                navControl.navigate(homeId)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            R.id.navigation_stats ->{
                supportActionBar?.title = "Statistics"
                switchEnabled(current="stats")
                navControl.navigate(R.id.navigation_stats)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            R.id.navigation_settings -> {
                switchEnabled(current="Settings")
                navControl.navigate(R.id.navigation_settings)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
        return true
    }
    private fun checkReadingDate() {
        traceLog(file="MainActivity.kt", function="checkReadingDate()")
        val streak = preferences.streak
        val list = preferences.list
        val dateChecked = streak.dateChecked
        val listsDone = list.listsDone
        val checkDate = checkDate(dateChecked, "current", false)
        if((!checkDate && listsDone != 0)){
            list.resetList()
        }
    }

    private fun switchEnabled(current: String){
        traceLog(file="MainActivity.kt", function="switchEnabled()")
        val menu = binding.bottomNav.menu
        menu.findItem(R.id.navigation_home)?.isEnabled = current != "home"
        menu.findItem(R.id.navigation_stats)?.isEnabled = current != "stats"
        menu.findItem(R.id.navigation_settings)?.isEnabled = current != "settings"
    }


    override fun onBackPressed() {
        traceLog(file="MainActivity.kt", function="onBackPressed()")
        if(this::navController.isInitialized) {
            when {
                Firebase.auth.currentUser == null -> {
                    finish()
                    val i = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(i)
                }
                navController.currentDestination?.id != R.id.navigation_home -> {
                    navController.popBackStack()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    binding.bottomNav.isVisible = true
                    supportActionBar?.show()
                }
                else -> {
                    finish()
                }
            }
        }
    }
    private fun startFirebaseAuth(){
        traceLog(file="MainActivity.kt", function="onActivityResult()")
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.logo2)
                .setTheme(R.style.AppTheme)
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build(), _rcSignIn)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        traceLog(file="MainActivity.kt", function="onActivityResult()")
        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(this@MainActivity, "Loading...", Toast.LENGTH_LONG).show()
        if(requestCode == _rcSignIn){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                val user = Firebase.auth.currentUser
                Toast.makeText(this@MainActivity, "Signed In!", Toast.LENGTH_LONG).show()
                val db = Firebase.firestore
                db.collection("main").document(user!!.uid).get()
                        .addOnSuccessListener { doc ->
                            if (doc["list1"] != null) {
                                finish()
                                val i = Intent(this@MainActivity, MainActivity::class.java)
                                startActivity(i)
                            } else {
                                finish()
                                val i = Intent(this@MainActivity, MainActivity::class.java)
                                startActivity(i)
                            }
                        }
            }else{
                when{
                    response == null -> {
                        Timber.d("onActivityResult(): sign_in_cancelled")
                        Toast.makeText(this@MainActivity, "Sign in was cancelled", Toast.LENGTH_LONG).show()
                    }
                    response.error?.errorCode == ErrorCodes.NO_NETWORK -> {
                        Timber.d("onActivityResult(): no_internet_connection")
                        Toast.makeText(this@MainActivity, "No internet connection", Toast.LENGTH_LONG).show()
                    }
                    response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR -> {
                        Timber.e("onActivityResult(): unknown_error")
                        Toast.makeText(this@MainActivity, "An unknown error occured", Toast.LENGTH_LONG).show()
                    }
                    else ->{
                        Timber.e("onActivityResult(): unknown_sign_in_response")
                        Toast.makeText(this@MainActivity, "Unknown sign in response", Toast.LENGTH_LONG).show()
                    }

                }
                startFirebaseAuth()
            }
        }
    }
    fun updateFSData() {
        val data = preferences.data
        CoroutineScope(Dispatchers.IO).launch {
            Firestore().updateFirestoreData(data)
        }
    }
}
