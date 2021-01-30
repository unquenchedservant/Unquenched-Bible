package com.theunquenchedservant.granthornersbiblereadingsystem

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.ActivityMainBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings.OnboardingPagerActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.firestoreToPreference
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.preferenceToFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updatePrefNames
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.getDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref

class MainActivity : AppCompatActivity(),  BottomNavigationView.OnNavigationItemSelectedListener{

    private var _rcSignIn = 96
    private var user: FirebaseUser? = null
    private var globalmenu : Menu? = null
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    lateinit var binding: ActivityMainBinding
    var darkMode: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        WebView(applicationContext)
        super.onCreate(savedInstanceState)
        val stateList = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )

        val colorList: IntArray
        val toolbarColor: Int
        darkMode = getBoolPref(name="darkMode", defaultValue=true)
        if(!getBoolPref(name="updatedPref", defaultValue=false)) updatePrefNames()
        if(Firebase.auth.currentUser == null) {
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
        }else if(!getBoolPref(name="hasCompletedOnboarding", defaultValue=false)){
            startActivity(Intent(this, OnboardingPagerActivity::class.java))
        }else {
            Firebase.auth.currentUser
            if (Firebase.auth.currentUser != null) {
                Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                firestoreToPreference(it.result!!)
                                checkReadingDate()
                            } else {
                                Firebase.crashlytics.log("Error getting user info")
                                Firebase.crashlytics.recordException(it.exception?.cause!!)
                                Firebase.crashlytics.setCustomKey("userId", Firebase.auth.currentUser?.uid!!)
                            }
                        }
            }

            if (darkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                toolbarColor = ContextCompat.getColor(App.applicationContext(), R.color.buttonBackgroundDark)
                colorList = intArrayOf(
                        ContextCompat.getColor(this, R.color.unquenchedEmphDark),
                        ContextCompat.getColor(this, R.color.unquenchedTextDark)
                )
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                toolbarColor = ContextCompat.getColor(App.applicationContext(), R.color.buttonBackground)
                colorList = intArrayOf(
                        ContextCompat.getColor(this, R.color.unquenchedOrange),
                        ContextCompat.getColor(this, R.color.unquenchedText)
                )
            }
            val colorStateList = ColorStateList(stateList, colorList)
            binding = ActivityMainBinding.inflate(layoutInflater)
            user = Firebase.auth.currentUser
            setContentView(binding.root)
            val toolbar = findViewById<MaterialToolbar>(R.id.my_toolbar)
            toolbar.setBackgroundColor(toolbarColor)
            setSupportActionBar(findViewById(R.id.my_toolbar))
            supportActionBar?.title = getDate(option = 0, fullMonth = true)
                navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navController = navHostFragment.navController
                binding.bottomNav.setupWithNavController(navController)
                binding.bottomNav.setBackgroundColor(toolbarColor)
                binding.bottomNav.itemIconTintList = colorStateList
                binding.bottomNav.itemTextColor = colorStateList

                when (getStringPref(name = "planSystem", defaultValue = "pgh")) {
                    "mcheyne" -> navController.navigate(R.id.navigation_home_mcheyne)
                }
                binding.translationSelector.isVisible = false
                setupBottomNavigationBar()
            }
    }

    private fun setupBottomNavigationBar() {
        switchEnabled(current="home")
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            val planSystem = getStringPref(name="planSystem", defaultValue="pgh")
            when (destination.id) {
                R.id.navigation_scripture ->{
                    binding.myToolbar.setNavigationOnClickListener{
                        if(planSystem == "pgh"){
                            navController.navigate(R.id.navigation_home)
                        }else{
                            navController.navigate(R.id.navigation_home_mcheyne)
                        }
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                    if (getStringPref(name="bibleVersion", defaultValue="ESV") == "NASB"){
                        setStringPref(name="bibleVersion", value="NASB20", updateFS=true)
                    }
                    when (getStringPref(name="bibleVersion", defaultValue="ESV")){
                        "AMP" -> binding.translationSelector.setSelection(1)
                        "CSB" -> binding.translationSelector.setSelection(2)
                        "ESV" -> binding.translationSelector.setSelection(3)
                        "KJV" -> binding.translationSelector.setSelection(4)
                        "NASB95" -> binding.translationSelector.setSelection(5)
                        "NASB20" -> binding.translationSelector.setSelection(6)
                    }
                    binding.translationSelector.isVisible = true
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                R.id.navigation_plan_settings ->{
                    binding.myToolbar.setNavigationOnClickListener{
                        navController.navigate(R.id.navigation_settings)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                    binding.translationSelector.isVisible = false
                }
                R.id.navigation_bible_stats_main ->{
                    binding.myToolbar.setNavigationOnClickListener{
                        navController.navigate(R.id.navigation_stats)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                    supportActionBar?.title = "Bible Statistics"
                    binding.translationSelector.isVisible = false
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                R.id.navigation_bible_testament_stats ->{
                    binding.myToolbar.setNavigationOnClickListener{
                        navController.navigate(R.id.navigation_bible_stats_main)
                        binding.bottomNav.isVisible = true
                    }
                    binding.translationSelector.isVisible = false
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                R.id.navigation_book_stats->{
                    binding.myToolbar.setNavigationOnClickListener{
                        navController.popBackStack()
                        binding.bottomNav.isVisible = true
                    }
                    binding.translationSelector.isVisible = false
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                R.id.navigation_bible_reset_menu->{
                    binding.myToolbar.setNavigationOnClickListener{
                        navController.navigate(R.id.navigation_stats)
                        binding.bottomNav.isVisible = true
                    }
                    supportActionBar?.title = "Reset Bible Stats"
                    binding.translationSelector.isVisible = false
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                R.id.navigation_books_reset_menu->{
                    binding.myToolbar.setNavigationOnClickListener{
                        navController.popBackStack()
                        binding.bottomNav.isVisible = true
                    }
                    binding.translationSelector.isVisible = false
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
                    binding.myToolbar.setNavigationOnClickListener{
                        navController.navigate(R.id.navigation_settings)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                    binding.translationSelector.isVisible = false
                }
                R.id.navigation_overrides->{
                    binding.myToolbar.setNavigationOnClickListener{
                        navController.navigate(R.id.navigation_settings)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                    binding.translationSelector.isVisible = false
                    supportActionBar?.title="Overrides"
                }
                R.id.navigation_manual->{
                    binding.myToolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.navigation_overrides)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    }
                    binding.translationSelector.isVisible = false
                    supportActionBar?.title="Manual Override"
                }
                R.id.navigation_manual_numerical->{
                    binding.myToolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.navigation_overrides)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    }
                    binding.translationSelector.isVisible = false
                    supportActionBar?.title="Manual Override"
                }
                R.id.navigation_information->{
                    binding.myToolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.navigation_settings)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                    binding.translationSelector.isVisible = false
                }
                R.id.navigation_account_settings->{
                    binding.myToolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.navigation_settings)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                    binding.translationSelector.isVisible = false
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.title = "Account Settings"
                }
                R.id.navigation_update_email->{
                    binding.myToolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.navigation_account_settings)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                    binding.translationSelector.isVisible = false
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.title = "Update Email"
                }
                R.id.navigation_update_password->{
                    binding.myToolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.navigation_account_settings)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                    binding.translationSelector.isVisible = false
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.title = "Update Password"
                }
                R.id.navigation_confirm_delete->{
                    binding.myToolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.navigation_account_settings)
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                    binding.translationSelector.isVisible = false
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.title = "Delete Account"
                }
                R.id.navigation_home -> {
                    switchEnabled(current="home")
                    when(darkMode){
                        true->binding.navHostFragment.setBackgroundColor(Color.parseColor("#121212"))
                        false->binding.navHostFragment.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                    supportActionBar?.title = getDate(option=0, fullMonth=true)
                    supportActionBar?.show()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    binding.translationSelector.isVisible = false
                }
                R.id.navigation_home_mcheyne -> {
                    switchEnabled(current="home")
                    when(darkMode){
                        true->binding.navHostFragment.setBackgroundColor(Color.parseColor("#121212"))
                        false->binding.navHostFragment.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
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
        val homeId = when(getStringPref(name="planSystem", defaultValue="pgh")){
            "pgh"->R.id.navigation_home
            "mcheyne"->R.id.navigation_home_mcheyne
            else->R.id.navigation_home
        }
        val navControl = findNavController(this, R.id.nav_host_fragment)
        when(item.itemId){
            R.id.navigation_home ->{
                log("THIS IS A TEST FROM HOME FRAGMENT ONNAVSELECTED")
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

    private fun checkReadingDate(){
        if(!checkDate("current", false)){
            val data:MutableMap<String, Any> = mutableMapOf()
            val allowPartial = getBoolPref(name="allowPartial")
            val planType = getStringPref("planType", "horner")
            var pghDone = 0
            var mcheyneDone = 0
            for(i in 1..10){
                if(getIntPref("list${i}Done") == 1){
                    pghDone += 1
                    if(planType=="horner") data["list$i"] = increaseIntPref("list$i", 1)
                    data["list${i}Done"] = setIntPref("list${i}Done", 0)
                    data["list${i}DoneDaily"] = setIntPref("list${i}DoneDaily", 0)
                }
            }
            for(i in 1..4){
                if(getIntPref("mcheyneList${i}Done") == 1){
                    mcheyneDone += 1
                    if(planType=="horner") data["mcheyneList${i}"] = increaseIntPref("list$i", 1)
                    data["mcheyneList${i}Done"] = setIntPref("mcheyneList${i}Done", 0)
                    data["mcheyneList${i}DoneDaily"] = setIntPref("mcheyneList${i}DoneDaily", 0)
                }
            }
            if(planType == "numerical" && ((allowPartial && pghDone > 0) || pghDone == 10)){
                data["currentDayIndex"] = increaseIntPref("currentDayIndex", 1)
            }
            if(planType == "numerical" && ((allowPartial && mcheyneDone > 0) || mcheyneDone == 4)){
                data["mcheyneCurrentDayIndex"] = increaseIntPref("mcheyneCurrentDayIndex", 1)
            }
            if((pghDone == 10 || (allowPartial && pghDone > 0)) || (mcheyneDone == 4 || (allowPartial && mcheyneDone > 0))){
                data["currentStreak"] = increaseIntPref("currentStreak", 1)
                if(getIntPref("currentStreak") > getIntPref("maxStreak")){
                    data["maxStreak"] = setIntPref("maxStreak", getIntPref("currentStreak"))
                }
            }
            if(pghDone == 0 && mcheyneDone == 0 && !getBoolPref("vacationMode")){
                if(checkDate("yesterday", false)){
                    data["isGrace"] = setBoolPref("isGrace", true)
                    data["graceTime"] = setIntPref("graceTime", 0)
                    data["holdStreak"] = getIntPref("currentStreak")
                }else{
                    data["graceTime"] = setIntPref("graceTime", 0)
                    data["isGrace"] = setBoolPref("isGrace", false)
                    data["holdStreak"] = setIntPref("holdStreak",0)
                }
                data["currentStreak"] = setIntPref("currentStreak", 0)
            }
            if(Firebase.auth.currentUser != null){
                Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).update(data)
            }
        }
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
        when {
            Firebase.auth.currentUser == null -> {
                finish()
                val i = Intent(App.applicationContext(), MainActivity::class.java)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == _rcSignIn){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                val user = Firebase.auth.currentUser
                Toast.makeText(App.applicationContext(), "Signed In!", Toast.LENGTH_LONG).show()
                val db = Firebase.firestore
                db.collection("main").document(user!!.uid).get()
                        .addOnSuccessListener { doc ->
                            if (doc["list1"] != null) {
                                firestoreToPreference(doc)
                            } else {
                                preferenceToFirestore()
                                val i = Intent(applicationContext, MainActivity::class.java)
                                finish()
                                startActivity(i)
                            }
                        }
            }else{
                if(response == null){
                    finish()
                }
            }
        }
    }

    companion object{

        fun log(logString:String){
            Log.d("PROFGRANT", logString)
        }


    }
}
