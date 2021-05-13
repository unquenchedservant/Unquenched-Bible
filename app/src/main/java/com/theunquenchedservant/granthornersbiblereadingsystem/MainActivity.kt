package com.theunquenchedservant.granthornersbiblereadingsystem

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.Toast
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
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref

class MainActivity : AppCompatActivity(),  BottomNavigationView.OnNavigationItemSelectedListener{

    private var _rcSignIn = 96
    private var user: FirebaseUser? = null
    private var globalmenu : Menu? = null
    lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    lateinit var binding: ActivityMainBinding
    var darkMode: Boolean = false
    val context = this


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        WebView(applicationContext)
        super.onCreate(savedInstanceState)
        traceLog(file="MainActivity.kt", function="onCreate()", "beginning")
        val stateList = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )

        val colorList: IntArray
        val toolbarColor: Int
        darkMode = getBoolPref(name="darkMode", defaultValue=true)
        if(!getBoolPref(name="updatedPref", defaultValue=false)) updatePrefNames()
        if(Firebase.auth.currentUser == null) {
            traceLog(file="MainActivity.kt", function="onCreate()", "firebase current user == null")
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
            traceLog(file="MainActivity.kt", function="onCreate()", "has not completed onboarding")
        }else {
            traceLog(file="MainActivity.kt", function="onCreate()", "normal operation")
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            Firebase.auth.currentUser
            if (Firebase.auth.currentUser != null) {
                Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                firestoreToPreference(it.result!!)
                                checkReadingDate()
                                firestoreToPreference(it.result!!)
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
            user = Firebase.auth.currentUser
            val toolbar = findViewById<MaterialToolbar>(R.id.my_toolbar)
            toolbar.setBackgroundColor(toolbarColor)
            setSupportActionBar(findViewById(R.id.my_toolbar))
            supportActionBar?.title = getDate(option = 0, fullMonth = true)
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

    override fun onResume() {
        super.onResume()
        traceLog(file="MainActivity.kt", function="onResume()")
    }

    override fun onPause() {
        super.onPause()
        traceLog(file="MainActivity.kt", function="onPause()")
    }
    override fun onStart(){
        super.onStart()
        traceLog(file="MainActivity.kt", function="onStart()")
    }
    override fun onDestroy(){
        super.onDestroy()
        traceLog(file="MainActivity.kt", function="onDestroy()")
    }
    override fun onStop(){
        super.onStop()
        traceLog(file="MainActivity.kt", function="onStop()")
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
            val planSystem = getStringPref(name="planSystem", defaultValue="pgh")
            val homeId = if (planSystem == "pgh") R.id.navigation_home else R.id.navigation_home_mcheyne
            when (destination.id) {
                R.id.navigation_scripture ->{
                    setupNavigation(homeId, bottomNavVisible = true, displayHome1 = false, displayHome2 = true, translationVisible = true)
                    if (getStringPref(name="bibleVersion", defaultValue="NIV") == "NASB"){
                        setStringPref(name="bibleVersion", value="NASB20", updateFS=true)
                    }
                    when (getStringPref(name="bibleVersion", defaultValue="NIV")){
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
                R.id.navigation_bible_stats_main ->{
                    setupNavigation(R.id.navigation_stats, bottomNavVisible = true, displayHome1 = false, displayHome2 = true, translationVisible = false)
                    supportActionBar?.title = "Bible Statistics"
                }
                R.id.navigation_bible_testament_stats ->{
                    setupNavigation(R.id.navigation_bible_stats_main, bottomNavVisible = true, displayHome1 = true, displayHome2 = true, translationVisible = false)
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
                    setupNavigation(R.id.navigation_stats, bottomNavVisible = true, displayHome1 = true, displayHome2 = true, translationVisible = false)
                    supportActionBar?.title = "Reset Bible Stats"
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
                    if(planSystem == "mcheyne"){
                        navController.navigate(R.id.navigation_home_mcheyne)
                    }
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
        traceLog(file="MainActivity.kt", function="onNavigationItemSelected()")
        val homeId = when(getStringPref(name="planSystem", defaultValue="pgh")){
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
        Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    val currentData = it.data
                    val dateChecked = extractStringPref(currentData, "dateChecked")
                    if (!checkDate(dateChecked, "current", false) && (extractIntPref(currentData,"listsDone") != 0 || extractIntPref(currentData, "mcheyneListsDone") != 0)) {
                        val data: MutableMap<String, Any> = mutableMapOf()
                        val allowPartial = extractBoolPref(currentData, "allowPartial")
                        val planType = extractStringPref(currentData, "planType", "horner")
                        var pghDone = 0
                        var mcheyneDone = 0
                        val pghDoneAlready = extractIntPref(currentData, "listsDone")
                        val mcheyneDoneAlready = extractIntPref(currentData, "mcheyneListsDone")
                        val holdPlan = extractBoolPref(currentData, "holdPlan", false)
                        if ((holdPlan && pghDoneAlready == 10) || !holdPlan) {
                            for (i in 1..10) {
                                if (extractIntPref(currentData, "list${i}Done") == 1) {
                                    pghDone += 1
                                    if (planType == "horner") data["list$i"] = extractIntPref(currentData, "list$i") + 1
                                    data["list${i}Done"] = 0
                                    data["list${i}DoneDaily"] = 0
                                }
                            }
                            data["listsDone"] = 0
                        }
                        if((holdPlan && mcheyneDoneAlready == 4) || !holdPlan) {
                            for (i in 1..4) {
                                if (extractIntPref(currentData, "mcheyneList${i}Done") == 1) {
                                    mcheyneDone += 1
                                    if (planType == "horner") data["mcheyneList${i}"] = extractIntPref(currentData, "mcheyneList$i") + 1
                                    data["mcheyneList${i}Done"] = 0
                                    data["mcheyneList${i}DoneDaily"] = 0
                                }
                            }
                            data["mcheyneListsDone"] = 0
                        }
                        if (planType == "numerical" && ((allowPartial && pghDone > 0) || pghDone == 10)) {
                            data["currentDayIndex"] = extractIntPref(currentData, "currentDayIndex") + 1
                        }
                        if (planType == "numerical" && ((allowPartial && mcheyneDone > 0) || mcheyneDone == 4)) {
                            data["mcheyneCurrentDayIndex"] = extractIntPref(currentData, "mcheyneCurrentDayIndex") + 1
                        }
                        if ((pghDone == 10 || (allowPartial && pghDone > 0)) || (mcheyneDone == 4 || (allowPartial && mcheyneDone > 0))) {
                            data["currentStreak"] = extractIntPref(currentData, "currentStreak") + 1
                            if (extractIntPref(currentData, "currentStreak") > extractIntPref(currentData, "maxStreak")) {
                                data["maxStreak"] = extractIntPref(currentData, "currentStreak")
                            }
                        }
                        if (pghDone == 0 && mcheyneDone == 0 && !extractBoolPref(currentData, "vacationMode")) {
                            if (checkDate(dateChecked, "yesterday", false)) {
                                data["isGrace"] = true
                                data["graceTime"] = 0
                                data["holdStreak"] = extractIntPref(currentData, "currentStreak")
                            } else {
                                data["graceTime"] = 0
                                data["isGrace"] = false
                                data["holdStreak"] = 0
                            }
                            data["currentStreak"] = 0
                        }
                        val homeID = if(extractStringPref(currentData, "planSystem")== "pgh") R.id.navigation_home else R.id.navigation_home_mcheyne
                        Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).update(data)
                                .addOnSuccessListener {
                                    navController.navigate(homeID)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error updating lists", Toast.LENGTH_LONG).show()
                                }
                    }
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
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        traceLog(file="MainActivity.kt", function="onActivityResult()")
        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(context, "Loading...", Toast.LENGTH_LONG).show()
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
                                finish()
                                val i = Intent(this@MainActivity, MainActivity::class.java)
                                startActivity(i)
                            } else {
                                preferenceToFirestore()
                                finish()
                                val i = Intent(this@MainActivity, MainActivity::class.java)
                                startActivity(i)
                            }
                        }
            }else{
                if(response == null){
                    preferenceToFirestore()
                    finish()
                    val i = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(i)
                }
            }
        }
    }
}
