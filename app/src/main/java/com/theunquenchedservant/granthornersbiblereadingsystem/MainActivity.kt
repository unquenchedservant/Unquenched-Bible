package com.theunquenchedservant.granthornersbiblereadingsystem

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
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.ActivityMainBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings.Onboarding.OnboardingPagerActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.firestoreToPreference
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.preferenceToFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updatePrefNames
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.getDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.isWeekend
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getLongPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.newUser
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFirestoreAndPrefs
import timber.log.Timber

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var _rcSignIn = 96
    private var user: FirebaseUser? = null
    private var globalmenu : Menu? = null
    lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    lateinit var binding: ActivityMainBinding
    var darkMode: Boolean = false
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        WebView(this@MainActivity)
        super.onCreate(savedInstanceState)
        traceLog(file="MainActivity.kt", function="onCreate()", "beginning")
        val stateList = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        val colorList: IntArray
        val toolbarColor: Int
        if(getBoolPref("darkMode", defaultValue=true)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            toolbarColor = ContextCompat.getColor(this@MainActivity, R.color.buttonBackgroundDark)
            colorList = intArrayOf(
                ContextCompat.getColor(this@MainActivity, R.color.unquenchedEmphDark),
                ContextCompat.getColor(this@MainActivity, R.color.unquenchedTextDark)
            )
            binding.navHostFragment.backgroundTintList = ColorStateList.valueOf(getColor(R.color.backg_night))
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            toolbarColor = ContextCompat.getColor(this@MainActivity, R.color.buttonBackground)
            colorList = intArrayOf(
                ContextCompat.getColor(this@MainActivity, R.color.unquenchedOrange),
                ContextCompat.getColor(this@MainActivity, R.color.unquenchedText)
            )
            binding.navHostFragment.backgroundTintList = ColorStateList.valueOf(getColor(R.color.backg))
        }
        val colorStateList = ColorStateList(stateList, colorList)
        setContentView(binding.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val toolbar = findViewById<MaterialToolbar>(R.id.my_toolbar)
        toolbar.setBackgroundColor(toolbarColor)
        toolbar.title = getDate(option=0, fullMonth = true)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.setBackgroundColor(toolbarColor)
        binding.bottomNav.itemIconTintList = colorStateList
        binding.bottomNav.itemTextColor = colorStateList
        binding.translationSelector.isVisible = false
        setupBottomNavigationBar()

        if(Firebase.auth.currentUser == null) {
            startFirebaseAuth()
        }else if(!getBoolPref(name="hasCompletedOnboarding", defaultValue=false)){
            traceLog(file="MainActivity.kt", function="onCreate()", "has not completed onboarding")
            startActivity(Intent(this@MainActivity, OnboardingPagerActivity::class.java))
        }else if (!getBoolPref(name="updatedPref", defaultValue=false)) {
            updatePrefNames()
        }else{
            traceLog(file="MainActivity.kt", function="onCreate()", "normal operation")
            Firebase.auth.currentUser
            if (Firebase.auth.currentUser != null) {
                Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                if(it.result?.data == null){
                                    newUser()
                                }else {
                                    val data = it.result!!.data!!
                                    if ((data["lastUpdated"]) == null) {
                                        data["lastUpdated"] = 0.toLong()
                                    }
                                    if ((data["lastUpdated"] as Long) > getLongPref("lastUpdated") && data["lastUpdated"] != 0) {
                                        debugLog("firestore to preference - FOR SCIENCE")
                                        firestoreToPreference(data)
                                    } else {
                                        debugLog("preference to firestore - FOR SCIENCE")
                                        preferenceToFirestore()
                                    }
                                    if (data["updatedPreferences"] == null || !(data["updatedPreferences"] as Boolean)) {
                                        updateFirestoreAndPrefs().addOnSuccessListener {
                                            checkReadingDate()
                                        }
                                    } else {
                                        checkReadingDate()
                                    }
                                }
                            } else {
                                com.google.firebase.ktx.Firebase.crashlytics.log("Error getting user info")
                                com.google.firebase.ktx.Firebase.crashlytics.recordException(it.exception?.cause!!)
                                com.google.firebase.ktx.Firebase.crashlytics.setCustomKey("userId", com.google.firebase.ktx.Firebase.auth.currentUser?.uid!!)
                            }

                        }
            }

        }
    }
    override fun onResume() {
        super.onResume()
        traceLog(file="MainActivity.kt", function="onResume()")
        if (Firebase.auth.currentUser != null && getBoolPref("hasCompletedOnboarding")) {
            checkReadingDate()
        }
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

    private fun setupNavigation(navId:Int, displayHome1:Boolean, translationVisible:Boolean){
        traceLog(file="MainActivity.kt", function="setupNavigation()")
        binding.myToolbar.setNavigationOnClickListener {
            navController.navigate(navId)
            binding.bottomNav.isVisible = true
            supportActionBar?.setDisplayHomeAsUpEnabled(displayHome1)
        }
        binding.translationSelector.isVisible = translationVisible
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun setupBottomNavigationBar() {
        traceLog(file="MainActivity.kt", function="setupBottomNavigationBar()")
        switchEnabled(current="home")
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            val homeId = R.id.navigation_home
            when (destination.id) {
                R.id.navigation_scripture ->{
                    setupNavigation(homeId, displayHome1 = false, translationVisible = true)
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
                    setupNavigation(R.id.navigation_settings, displayHome1 = false, translationVisible = false)
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
                    setupNavigation(R.id.navigation_settings, displayHome1 = false, translationVisible = false)
                }
                R.id.navigation_overrides->{
                    setupNavigation(R.id.navigation_settings, displayHome1 = false, translationVisible = false)
                    supportActionBar?.title="Overrides"
                }
                R.id.navigation_manual->{
                    setupNavigation(R.id.navigation_overrides, displayHome1 = true, translationVisible = false)
                    supportActionBar?.title="Manual Override"
                }
                R.id.navigation_manual_numerical->{
                    setupNavigation(R.id.navigation_overrides, displayHome1 = true, translationVisible = false)
                    supportActionBar?.title="Manual Override"
                }
                R.id.navigation_information->{
                    setupNavigation(R.id.navigation_settings, displayHome1 = false, translationVisible = false)
                }
                R.id.navigation_account_settings->{
                    setupNavigation(R.id.navigation_settings, displayHome1 = false, translationVisible = false)
                    supportActionBar?.title = "Account Settings"
                }
                R.id.navigation_update_email->{
                    setupNavigation(R.id.navigation_account_settings, displayHome1 = false, translationVisible = false)
                    supportActionBar?.title = "Update Email"
                }
                R.id.navigation_update_password->{
                    setupNavigation(R.id.navigation_account_settings, displayHome1 = false, translationVisible = false)
                    supportActionBar?.title = "Update Password"
                }
                R.id.navigation_confirm_delete->{
                    setupNavigation(R.id.navigation_account_settings, displayHome1 = false, translationVisible = false)
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
        val navControl = findNavController(this, R.id.nav_host_fragment)
        when(item.itemId){
            R.id.navigation_home ->{
                supportActionBar?.title = getDate(option=0, fullMonth=true)
                switchEnabled(current="home")
                navControl.navigate(R.id.navigation_home)
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
        debugLog("checkreadingdate - FOR SCIENCE")
        Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result == null) {
                        newUser()
                    } else {
                        val currentData = it.result!!.data!!
                        if (!checkDate(
                                extractStringPref(currentData, "dateReset"),
                                "current",
                                false
                            )
                        ) {
                            val dateChecked = extractStringPref(currentData, "dateChecked")
                            val listsDone = extractIntPref(currentData, "pghDone")
                            val mcheyneListsDone = extractIntPref(currentData, "mcheyneDone")
                            debugLog("This is the date checked $dateChecked")
                            if (!checkDate(dateChecked, "current", false)) {
                                val allowPartial: Boolean =
                                    extractBoolPref(currentData, "allowPartial")
                                val planType: String =
                                    extractStringPref(currentData, "planType", "horner")
                                var pghDone = 0
                                var mcheyneDone = 0
                                val holdPlan: Boolean = extractBoolPref(currentData, "holdPlan")

                                if ((holdPlan && listsDone == 10) || !holdPlan) {
                                    for (i in 1..10) {
                                        if (extractBoolPref(currentData, "pgh${i}Done")) {
                                            pghDone += 1
                                            if (planType == "horner") currentData["pgh${i}Index"] =
                                                setIntPref(
                                                    "pgh${i}Index",
                                                    extractIntPref(currentData, "pgh${i}Index") + 1
                                                )
                                        }
                                        currentData["pgh${i}Done"] =
                                            setBoolPref(name = "pgh${i}Done", value = false)
                                        currentData["pgh${i}DoneDaily"] =
                                            setBoolPref(name = "pgh${i}DoneDaily", value = false)
                                    }
                                    currentData["pghDone"] = setIntPref(name = "pghDone", value = 0)
                                }
                                if ((holdPlan && mcheyneListsDone == 4) || !holdPlan) {
                                    for (i in 1..4) {
                                        if (extractBoolPref(currentData, "mcheyne${i}Done")) {
                                            mcheyneDone += 1
                                            if (planType == "horner") currentData["mcheyne${i}Index"] =
                                                setIntPref(
                                                    "mcheyne${i}Index",
                                                    extractIntPref(
                                                        currentData,
                                                        "mcheyne${i}Index"
                                                    ) + 1
                                                )
                                        }
                                        currentData["mcheyne${i}Done"] =
                                            setBoolPref(name = "mcheyne${i}Done", value = false)
                                        currentData["mcheyne${i}DoneDaily"] =
                                            setBoolPref(
                                                name = "mcheyne${i}DoneDaily",
                                                value = false
                                            )
                                    }
                                    currentData["mcheyneDone"] = setIntPref("mcheyneDone", 0)
                                }
                                if (planType == "numerical" && ((allowPartial && pghDone > 0) || pghDone == 10)) {
                                    currentData["pghIndex"] =
                                        setIntPref(
                                            "pghIndex",
                                            extractIntPref(currentData, "pghIndex") + 1
                                        )
                                }
                                if (planType == "numerical" && ((allowPartial && mcheyneDone > 0) || mcheyneDone == 4)) {
                                    currentData["mcheyneIndex"] =
                                        setIntPref(
                                            "mcheyneIndex",
                                            extractIntPref(currentData, "mcheyneIndex") + 1
                                        )
                                }
                                if (pghDone == 0 && mcheyneDone == 0 && (!extractBoolPref(
                                        currentData,
                                        "vacationMode"
                                    ) || !(extractBoolPref(
                                        currentData,
                                        "weekendMode"
                                    ) && isWeekend()))
                                ) {
                                    if (!checkDate(dateChecked, "two", false)) {
                                        if (!extractBoolPref(currentData, "isGrace")) {
                                            currentData["isGrace"] = setBoolPref("isGrace", true)
                                            currentData["holdStreak"] =
                                                extractIntPref(currentData, "currentStreak")
                                        } else {
                                            currentData["graceTime"] = 1
                                            currentData["isGrace"] = setBoolPref("isGrace", false)
                                            currentData["holdStreak"] = setIntPref("holdStreak", 0)
                                        }
                                    }
                                    currentData["currentStreak"] = 0
                                    debugLog("The current streak has been reset")
                                }
                                currentData["dailyStreak"] = 0
                                currentData["dateReset"] =
                                    setStringPref("dateReset", getDate(0, false))
                                updateFirestore(currentData)
                                    .addOnSuccessListener {
                                        navController.navigate(R.id.navigation_home)
                                        debugLog("updated firestore data accurately")
                                    }
                                    .addOnFailureListener { error ->
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Error updating lists",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        debugLog("Error updating lists $error")
                                    }
                            } else {
                                navController.navigate(R.id.navigation_home)
                            }
                        } else {
                            navController.navigate(R.id.navigation_home)
                        }
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
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setLogo(R.drawable.logo2)
            .setTheme(R.style.AppTheme)
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }
    private fun onSignInResult(result:FirebaseAuthUIAuthenticationResult){
        val response = result.idpResponse
        if(result.resultCode == RESULT_OK){
            val user = Firebase.auth.currentUser
            Toast.makeText(this@MainActivity, "Signed In!", Toast.LENGTH_LONG).show()
            val db = Firebase.firestore
            db.collection("main").document(user!!.uid).get()
                .addOnSuccessListener { doc ->
                    if(doc.data == null){
                        newUser().addOnSuccessListener {
                            finish()
                            val i = Intent(this@MainActivity, MainActivity::class.java)
                            startActivity(i)
                        }
                    }
                    else if(Firebase.auth.currentUser!!.uid == getStringPref("uid") && (doc.data!!["lastUpdated"] == null || (doc.data!!["lastUpdated"] as Long) < getLongPref("lastUpdated"))){
                        preferenceToFirestore().addOnSuccessListener {
                            finish()
                            val i = Intent(this@MainActivity, MainActivity::class.java)
                            startActivity(i)
                        }
                    }else if(doc.data!!["lastUpdated"] != null && doc.data!!["lastUpdated"] as Long > getLongPref("lastUpdated")){
                        firestoreToPreference(doc.data!!)
                        setStringPref("uid", user.uid)
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
