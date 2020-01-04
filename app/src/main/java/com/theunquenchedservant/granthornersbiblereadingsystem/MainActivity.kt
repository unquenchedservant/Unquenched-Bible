package com.theunquenchedservant.granthornersbiblereadingsystem

import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.firestoneToPreference
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.preferenceToFireStone
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.boolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStreak
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.getDate
import kotlinx.android.synthetic.main.appbar.*

class MainActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener{

    private var _rcSignIn = 96
    private var user: FirebaseUser? = null
    private var globalmenu : Menu? = null
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        user = FirebaseAuth.getInstance().currentUser
        if(savedInstanceState == null){
            setupDrawer()
        }
    }
    private fun setupDrawer() {
        setSupportActionBar(my_toolbar)
        toggle = ActionBarDrawerToggle(this, container, my_toolbar, R.string.nav_app_bar_open_drawer_description, R.string.nav_app_bar_open_drawer_description)
        container.addDrawerListener(toggle)
        switchEnabled("home")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        nav_view?.getHeaderView(0)?.setOnClickListener {
            findNavController(this, R.id.nav_host_fragment).navigate(R.id.navigation_home)
            switchEnabled("home")
            supportActionBar?.title = getDate(0,true)
            container.closeDrawers()
        }
        setStreak()

        setDrawerTitles()

        nav_view?.setNavigationItemSelectedListener(this)
    }
    private fun setDrawerTitles(){
        val menu = nav_view?.menu
        val stats = menu?.findItem(R.id.action_statistics)
        val psalms = menu?.findItem(R.id.action_psalms)
        val googleSign = menu?.findItem(R.id.google_sign)
        val ps = boolPref("psalms", null)
        supportActionBar?.title = getDate(0, true)
        stats?.title = "Current Streak: ${getIntPref("currentStreak")}"
        if (ps) psalms?.title = resources.getString(R.string.psalmsnav1) else psalms?.title = resources.getString(R.string.psalmsnav5)
        if (user != null) googleSign?.title = resources.getString(R.string.signoutnav) else googleSign?.title = resources.getString(R.string.signinnav)
    }
    /**
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.translation, menu)
        val spinner = menu?.findItem(R.id.translation_selector)
        val spin = spinner?.actionView as Spinner
        ArrayAdapter.createFromResource(this, R.array.translationArray,
                android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spin.adapter = it
            spin.setSelection(listNumberPref("translation", null))
        }
        spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (parent?.getItemAtPosition(position).toString()) {
                    "CSB" -> listNumberPref("translation", 1)
                    "ESV" -> listNumberPref("translation", 2)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        return true
    } */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val homeFrag = nav_host_fragment.childFragmentManager.fragments[0]
        val navControl = findNavController(this, R.id.nav_host_fragment)
        when(item.itemId){
            R.id.action_home ->{
                supportActionBar?.title = getDate(0, true)
                switchEnabled("home")
                navControl.navigate(R.id.navigation_home)
            }
            R.id.action_manual ->{
                supportActionBar?.title = "Manually Set List"
                switchEnabled("manual")
                navControl.navigate(R.id.navigation_manual)
            }
            R.id.action_support -> {
                supportActionBar?.title = "Support"
                switchEnabled("support")
                navControl.navigate(R.id.navigation_support)
            }
            R.id.action_information -> {
                supportActionBar?.title = "Information"
                switchEnabled("information")
                navControl.navigate(R.id.navigation_information)
            }
            R.id.action_statistics -> {
                supportActionBar?.title = "Statistics"
                switchEnabled("stats")
                navControl.navigate(R.id.navigation_stats)
            }
            R.id.action_notifications -> {
                supportActionBar?.title = "Notifications"
                switchEnabled("notif")
                navControl.navigate(R.id.navigation_notifications)
            }
            R.id.action_daily_reset -> {
                resetDaily()
                navControl.navigate(R.id.navigation_home)
                switchEnabled("home")
                Toast.makeText(this, "Forced Daily Reset", Toast.LENGTH_LONG).show()
            }
            R.id.google_sign -> {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null){
                    val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.unquenchedAlert))
                    builder.setPositiveButton(getString(R.string.yes)){_,_ ->
                        FirebaseAuth.getInstance().signOut()
                        item.title = "Sign In"
                        navControl.navigate(R.id.navigation_home)
                        Toast.makeText(applicationContext, "Signed Out!", Toast.LENGTH_LONG).show()
                    }
                    builder.setNeutralButton(getString(R.string.no)){dialogInterface, _ ->
                        dialogInterface.cancel()
                    }
                    builder.setMessage("Are you sure you want to sign out ${user.email}?")
                    builder.setTitle("Sign Out?")
                    builder.create().show()
                }
                googleSignIn()
            }
            R.id.action_psalms -> {
                val ps = boolPref("psalms", null)
                if (ps) {
                    boolPref("psalms", false)
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        updateFS("psalms", false)
                    }
                    item.title = resources.getString(R.string.psalmsnav5)
                    nav_host_fragment.childFragmentManager.beginTransaction().detach(homeFrag).attach(homeFrag).commit()
                } else {
                    boolPref("psalms", true)
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        updateFS("psalms", true)
                    }
                    item.title = resources.getString(R.string.psalmsnav1)
                    nav_host_fragment.childFragmentManager.beginTransaction().detach(homeFrag).attach(homeFrag).commit()
                }
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
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

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == _rcSignIn){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            }catch(e: ApiException){
                Toast.makeText(applicationContext, "Google Sign In Failed", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun googleSignIn(){
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.unquenchedAlert))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, _rcSignIn)
        }
        builder.setNeutralButton(R.string.no) { dialogInterface, _ -> log("cancel pressed"); dialogInterface.cancel() }
        builder.setMessage(R.string.googleCheck).setTitle("Sign In To Google Account")
        builder.create().show()
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val auth = FirebaseAuth.getInstance()
        val navControl = findNavController(this, R.id.nav_host_fragment)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(App.applicationContext(), "Signed In!", Toast.LENGTH_LONG).show()
                val db = FirebaseFirestore.getInstance()
                val user = FirebaseAuth.getInstance().currentUser
                db.collection("main").document(user!!.uid).get()
                        .addOnSuccessListener { doc ->
                            if (doc != null) {
                                val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.unquenchedAlert))
                                builder.setPositiveButton("Use Cloud Data") { _, _ ->
                                    firestoneToPreference(doc)
                                    nav_view.menu.findItem(R.id.google_sign).title = "Sign Out"
                                    navControl.navigate(R.id.navigation_home)
                                }
                                builder.setNeutralButton("Overwrite with device") { _, _ ->
                                    preferenceToFireStone()
                                    nav_view.menu.findItem(R.id.google_sign).title = "Sign Out"
                                    navControl.navigate(R.id.navigation_home)
                                }
                                builder.setTitle("Account Found")
                                builder.setMessage("Found ${FirebaseAuth.getInstance().currentUser?.email}. Would you like to TRANSFER from the cloud or OVERWRITE the cloud with current device data?")
                                builder.create().show()
                            } else {
                                preferenceToFireStone()
                            }
                        }
            } else {
                Toast.makeText(applicationContext, "Google Sign In Failed", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun resetDaily(){
        val isLogged = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        var resetStreak  = false
        var resetCurrent = false
        val vacation = boolPref("vacationMode", null)
        when (getIntPref("dailyStreak")) {
            1 -> {
                setIntPref("dailyStreak", 0)
                log("DAILY CHECK - daily streak set to 0")
                resetStreak = true
            }
            0 -> {
                when (vacation) {
                    false -> {
                        if (!checkDate("both", false))
                            resetCurrent = true
                        log("DAILY CHECK - currentStreak set to 0")
                        setIntPref("currentStreak", 0)
                    }
                }
            }
        }
        for(i in 1..10){
            when(getIntPref("list${i}Done")){
                1 -> {
                    resetList("list$i", "list${i}Done")
                }
            }
        }
        setIntPref("listsDone", 0)
        if(isLogged != null) {
            val data = mutableMapOf<String, Any>()
            for (i in 1..10){
                data["list$i"] = getIntPref("list$i")
                data["list${i}Done"] = getIntPref("list${i}Done")
            }
            data["listsDone"] = 0
            if(resetCurrent) {
                data["currentStreak"] = 0
            }else if(resetStreak) {
                data["dailyStreak"] = 0
            }
            db.collection("main").document(isLogged.uid).update(data)
        }
    }

    private fun resetList(listName: String, listNameDone: String){
        log("$listName is now set to ${getIntPref(listName)}")
        increaseIntPref(listName, 1)
        log("$listName index is now ${getIntPref( listName)}")
        setIntPref(listNameDone, 0)
        log("$listNameDone set to 0")
    }

    private fun switchEnabled(current: String){
        val menu = nav_view?.menu
        menu?.findItem(R.id.action_home)?.isEnabled = current != "home"
        menu?.findItem(R.id.action_information)?.isEnabled = current != "information"
        menu?.findItem(R.id.action_manual)?.isEnabled = current != "manual"
        menu?.findItem(R.id.action_statistics)?.isEnabled = current != "stats"
        menu?.findItem(R.id.action_notifications)?.isEnabled = current != "notif"
        menu?.findItem(R.id.action_support)?.isEnabled = current != "support"
    }
    companion object{

        fun log(logString:String){
            Log.d("PROFGRANT", logString)
        }


    }
}
class InformationFragment : PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?){
        setPreferencesFromResource(R.xml.information_preferences, rootKey)
        val license: Preference? = findPreference("licenses")
        license!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            false
        }
    }
}