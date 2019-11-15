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
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.boolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.firestoneToPreference
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.intPref
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.mergePref
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.preferenceToFireStone
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.stringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.updateFS
import kotlinx.android.synthetic.main.appbar.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener{

    private var _rcSignIn = 96
    private var globalmenu : Menu? = null
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        if(boolPref("has_merged", null)) {
            setContentView(R.layout.activity_main)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            if (intPref("translation", null) == 0) intPref("translation", 1)
            setSupportActionBar(my_toolbar)
            drawer = container
            toggle = ActionBarDrawerToggle(this, drawer, my_toolbar, R.string.nav_app_bar_open_drawer_description, R.string.nav_app_bar_open_drawer_description)
            drawer.addDrawerListener(toggle)
            switchEnabled("home")
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            nav_view?.getHeaderView(0)?.setOnClickListener {
                findNavController(this, R.id.nav_host_fragment).navigate(R.id.navigation_home)
                switchEnabled("home")
                supportActionBar?.title = getCurrentDate(true)
                drawer.closeDrawers()
            }
            supportActionBar?.title = getCurrentDate(true)
            val user = FirebaseAuth.getInstance().currentUser
            val googleSign = nav_view?.menu?.findItem(R.id.google_sign)
            val psalms = nav_view?.menu?.findItem(R.id.action_psalms)
            val ps = boolPref("psalms", null)
            if (ps) psalms?.title = resources.getString(R.string.psalmsnav1) else psalms?.title = resources.getString(R.string.psalmsnav5)
            if (user != null) googleSign?.title = resources.getString(R.string.signoutnav) else googleSign?.title = resources.getString(R.string.signinnav)
            nav_view?.setNavigationItemSelectedListener(this)
        }else{
            mergePref()
            recreate()
        }
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
                supportActionBar?.title = getCurrentDate(true)
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
                Toast.makeText(this, "Forced Daily Reset", Toast.LENGTH_LONG).show()
            }
            R.id.google_sign -> {
                googleSignIn(item)
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
        toggle.syncState()
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
    private fun googleSignIn(item:MenuItem){
        val user = FirebaseAuth.getInstance().currentUser
        val navControl = findNavController(this, R.id.nav_host_fragment)
        if(user != null){
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

        }else {
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
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        log("firebaseAuthWithGoogle: ${acct.id}")
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
                                builder.setMessage("Found ${FirebaseAuth.getInstance().currentUser?.email}. Would you like to TRANSFER from your account or OVERWRITE your account with this device?")
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

    companion object{

        fun log(logString:String){
            Log.d("PROFGRANT", logString)
        }
        fun createNotificationChannel(ctx: Context?) {
            log("start createNotificationChannel")

            val primaryChannelId = "primary_notification_channel"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val name = ctx?.getString(R.string.channel_name)
                log("Name of channel: $name")

                val description = ctx?.getString(R.string.channel_description)
                log("Description of Channel: $description")

                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(primaryChannelId, name, importance)

                channel.description = description
                channel.enableVibration(true)

                val notificationManager = ctx?.getSystemService(NotificationManager::class.java)!!
                notificationManager.createNotificationChannel(channel)

                log("Notification channel created")
            }
        }
        fun createDailyCheck(ctx: Context?) {
            log("Begin createDailyCheck")

            val alarmMgr: AlarmManager = ctx!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent: PendingIntent
            val calendar = Calendar.getInstance()

            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, 0)

            if (calendar.before(Calendar.getInstance())) {
                log("ADDING ONE DAY(SHOULDNT HAPPEN)")
                calendar.add(Calendar.DATE, 1)
            }

            val intent = Intent(ctx, DailyCheck::class.java)
            alarmIntent = PendingIntent.getBroadcast(ctx, 6, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)

            log("DailyCheck Alarm created/updated")
        }
        fun getCurrentDate(fullMonth: Boolean): String {
            log("Begin getCurrentDate")
            val today = Calendar.getInstance().time
            val formatter: SimpleDateFormat = if (fullMonth) {
                SimpleDateFormat("MMMM dd", Locale.US)
            } else {
                SimpleDateFormat("MMM dd", Locale.US)
            }
            log("getCurrentDate returning ${formatter.format(today)}")
            return formatter.format(today)
        }

        fun getYesterdayDate(fullMonth:Boolean): String{
            log("Begin get yesterdays date")
            val yesterday = Calendar.getInstance()
            yesterday.set(Calendar.HOUR_OF_DAY, 0)
            yesterday.add(Calendar.DATE, -1)
            val value = yesterday.time
            val formatter: SimpleDateFormat = if(fullMonth){
                SimpleDateFormat("MMMM dd", Locale.US)
            }else{
                SimpleDateFormat("MMM dd", Locale.US)
            }
            log("getYesterdayDate returning ${formatter.format(value)}")
            return formatter.format(value)
        }
        fun cancelAlarm(ctx: Context, notifyPendingIntent: PendingIntent) {
            log("Start cancelAlarm")
            val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            log("Cancelling $notifyPendingIntent")
            alarmManager.cancel(notifyPendingIntent)
        }

        fun createAlarm(ctx: Context?, notifyPendingIntent: PendingIntent, isDaily: Boolean) {
            log("Start createAlarm (dailyAlarm)")
            val sharedpref = PreferenceManager.getDefaultSharedPreferences(ctx)
            var timeInMinutes = 0
            if(isDaily) {
                timeInMinutes = sharedpref.getInt("daily_time", 0)
            }else{
                timeInMinutes = sharedpref.getInt("remind_time", 0)
            }
            log("time in minutes $timeInMinutes")
            val hour = timeInMinutes / 60
            val minute = timeInMinutes % 60
            log("dailyHour - $hour")
            log("dailyMinute - $minute")
            val alarmManager = ctx!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1)
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, notifyPendingIntent)
            log("Created daily Alarm at $hour:$minute")

        }


    }
}