package com.theunquenchedservant.granthornersbiblereadingsystem

import android.app.Activity
import android.app.AlertDialog
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.ActivityMainBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings.OnboardingPagerActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.firestoneToPreference
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.preferenceToFireStone
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS
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
        super.onCreate(savedInstanceState)
        val stateList = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )
        val colorList: IntArray
        val toolbarColor: Int
        if(FirebaseAuth.getInstance().currentUser == null) {
            setBoolPref("hasCompletedOnboarding", false)
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
        }else if(!getBoolPref("hasCompletedOnboarding", false)){
            startActivity(Intent(this, OnboardingPagerActivity::class.java))
        }else{
            log("THIS IS HAS COMPLETED ONBOARDING ${getBoolPref("hasCompletedOnboarding")}")
            if (getBoolPref("darkMode", true)) {
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
            user = FirebaseAuth.getInstance().currentUser
            setContentView(binding.root)
            val toolbar = findViewById<MaterialToolbar>(R.id.my_toolbar)
            toolbar.setBackgroundColor(toolbarColor)
            setSupportActionBar(findViewById(R.id.my_toolbar))
            supportActionBar?.title = getDate(0, true)
            navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            binding.bottomNav.setupWithNavController(navController)
            binding.bottomNav.setBackgroundColor(toolbarColor)
            binding.bottomNav.itemIconTintList = colorStateList
            binding.bottomNav.itemTextColor = colorStateList
            if (getStringPref("planSystem", "pgh") == "pgh") {
                navController.navigate(R.id.navigation_home)
            } else if (getStringPref("planSystem", "pgh") == "mcheyne") {
                navController.navigate(R.id.navigation_home_mcheyne)
            }
            binding.translationSelector.isVisible = false
            setupBottomNavigationBar()
        }
    }

    private fun setupBottomNavigationBar() {
        switchEnabled("home")
        binding.bottomNav.setOnNavigationItemSelectedListener(this)
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            when (destination.id) {
                R.id.navigation_scripture ->{
                    binding.myToolbar.setNavigationOnClickListener{
                        if(getStringPref("planSystem", "pgh") == "pgh"){
                            navController.navigate(R.id.navigation_home)
                        }else{
                            navController.navigate(R.id.navigation_home_mcheyne)
                        }
                        binding.bottomNav.isVisible = true
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                    if (getStringPref("bibleVersion", "ESV") == "NASB"){
                        setStringPref("bibleVersion", "NASB20")
                        updateFS("bibleVersion", "NASB20")
                    }
                    when (getStringPref("bibleVersion", "ESV")){
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
                    switchEnabled("home")
                    if(getBoolPref("darkMode", true)) {
                        binding.navHostFragment.setBackgroundColor(Color.parseColor("#121212"))
                    }else {
                        binding.navHostFragment.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                    supportActionBar?.title = getDate(0, true)
                    supportActionBar?.show()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    binding.translationSelector.isVisible = false
                }
                R.id.navigation_home_mcheyne -> {
                    switchEnabled("home")
                    if(getBoolPref("darkMode", true)) {
                        binding.navHostFragment.setBackgroundColor(Color.parseColor("#121212"))
                    }else {
                        binding.navHostFragment.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                    supportActionBar?.title = getDate(0, true)
                    supportActionBar?.show()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    binding.translationSelector.isVisible = false
                }
                R.id.navigation_stats -> {
                    switchEnabled("stats")
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.title = destination.label
                    binding.translationSelector.isVisible = false
                }
                R.id.navigation_settings -> {
                    switchEnabled("settings")
                    supportActionBar?.title = destination.label
                    binding.translationSelector.isVisible = false
                }
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val homeId = when(getStringPref("planSystem", "pgh")){
            "pgh"->R.id.navigation_home
            "mcheyne"->R.id.navigation_home_mcheyne
            else->R.id.navigation_home
        }
        val navControl = findNavController(this, R.id.nav_host_fragment)
        when(item.itemId){
            R.id.navigation_home ->{
                supportActionBar?.title = getDate(0, true)
                switchEnabled("home")
                navControl.navigate(homeId)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            R.id.navigation_stats ->{
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
        if(FirebaseAuth.getInstance().currentUser == null){
            log("YES THIS WORKED AS EXPECTED")
            finish()
            val i = Intent(App.applicationContext(), MainActivity::class.java)
            startActivity(i)
        }else if(navController.currentDestination?.id != R.id.navigation_home){
            navController.popBackStack()
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            binding.bottomNav.isVisible = true
            supportActionBar?.show()
        }else{
            finish()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == _rcSignIn){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(App.applicationContext(), "Signed In!", Toast.LENGTH_LONG).show()
                val db = FirebaseFirestore.getInstance()
                db.collection("main").document(user!!.uid).get()
                        .addOnSuccessListener { doc ->
                            if (doc["list1"] != null) {
                                val builder = AlertDialog.Builder(this)
                                builder.setPositiveButton("Use Cloud Data") { _, _ ->
                                    firestoneToPreference(doc)
                                    val i = Intent(applicationContext, MainActivity::class.java)
                                    log("WE GOT PAST THE FIRESTONE TO PREFERENCE")
                                    finish()
                                    startActivity(i)
                                }
                                builder.setNeutralButton("Overwrite with device") { _, _ ->
                                    preferenceToFireStone()
                                    val i = Intent(applicationContext, MainActivity::class.java)
                                    finish()
                                    startActivity(i)
                                }
                                builder.setTitle("Account Found")
                                builder.setMessage("Found ${FirebaseAuth.getInstance().currentUser?.email}. Would you like to TRANSFER from the cloud or OVERWRITE the cloud with current device data?")
                                builder.create().show()
                            } else {
                                preferenceToFireStone()
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
    fun googleSignIn(){
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this.applicationContext, gso)
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, _rcSignIn)
        }
        builder.setNeutralButton(R.string.no) { dialogInterface, _ -> dialogInterface.cancel() }
        builder.setMessage(R.string.msg_google).setTitle(R.string.title_sign_in)
        builder.create().show()
    }
    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val auth = FirebaseAuth.getInstance()
        val navControl = this.navController
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(App.applicationContext(), "Signed In!", Toast.LENGTH_LONG).show()
                val db = FirebaseFirestore.getInstance()
                val user = FirebaseAuth.getInstance().currentUser
                db.collection("main").document(user!!.uid).get()
                        .addOnSuccessListener { doc ->
                            if (doc != null) {
                                val builder = AlertDialog.Builder(this)
                                builder.setPositiveButton("Use Cloud Data") { _, _ ->
                                    SharedPref.firestoneToPreference(doc)
                                    navControl.navigate(R.id.navigation_home)
                                }
                                builder.setNeutralButton("Overwrite with device") { _, _ ->
                                    SharedPref.preferenceToFireStone()
                                    navControl.navigate(R.id.navigation_home)
                                }
                                builder.setTitle("Account Found")
                                builder.setMessage("Found ${FirebaseAuth.getInstance().currentUser?.email}. Would you like to TRANSFER from the cloud or OVERWRITE the cloud with current device data?")
                                builder.create().show()
                            } else {
                                SharedPref.preferenceToFireStone()
                            }
                        }
            } else {
                Toast.makeText(App.applicationContext(), "Google Sign In Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object{

        fun log(logString:String){
            Log.d("PROFGRANT", logString)
        }


    }
}
