package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.firestoneToPreference
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.preferenceToFireStone
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS

class MainSettings : PreferenceFragmentCompat() {

    private var _rcSignIn = 96

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_settings, rootKey)
        val plan: Preference? = findPreference("planSettings")
        val notifications: Preference? = findPreference("notifications")
        val overrides: Preference? = findPreference("overrides")
        val infoSupport: Preference? = findPreference("infoSupport")
        val account: Preference? = findPreference("googleSignIn")
        val darkMode: Preference? = findPreference("darkMode")
        val discord: Preference? = findPreference("discordLink")
        val mainActivity = activity as MainActivity

        plan!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        notifications!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        overrides!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        infoSupport!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)

        val dark = getBoolPref("darkMode", true)
        darkMode!!.setDefaultValue(true)
        if(dark){
            darkMode.setDefaultValue(true)
        }else{
            darkMode.setDefaultValue(false)
        }

        if(FirebaseAuth.getInstance().currentUser != null){
            account?.title = getString(R.string.title_account_loggedin)
            account?.summary = getString(R.string.summary_account_loggedin)
            account!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setPositiveButton(getString(R.string.yes)){_,_->
                    FirebaseAuth.getInstance().signOut()
                    account.title = getString(R.string.title_account_loggedout)
                    account.summary = getString(R.string.summary_account_loggedout)
                    Toast.makeText(context, "Signed Out!", Toast.LENGTH_LONG).show()
                    mainActivity.navController.navigate(R.id.navigation_settings)
                }
                builder.setNeutralButton(getString(R.string.no)){dialogInterface, _->
                    dialogInterface.cancel()
                }
                builder.setMessage(getString(R.string.msg_sign_out))
                builder.setTitle(getString(R.string.title_sign_out))
                builder.create().show()
                false
            }
        }else{
            account?.title = getString(R.string.title_account_loggedout)
            account?.summary = getString(R.string.summary_account_loggedout)
            account!!.onPreferenceClickListener  = Preference.OnPreferenceClickListener {
                googleSignIn()
                mainActivity.navController.navigate(R.id.navigation_settings)
                false
            }
        }
        darkMode.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if(dark){
                setBoolPref("darkMode", false)
                if(FirebaseAuth.getInstance().currentUser != null){
                    updateFS("darkMode", false)
                }
                mainActivity.navController.navigate(R.id.navigation_home)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }else{
                setBoolPref("darkMode", true)
                if(FirebaseAuth.getInstance().currentUser != null){
                    updateFS("darkMode", true)
                }
                mainActivity.navController.navigate(R.id.navigation_home)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            false
        }
        plan.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.supportActionBar?.title="Plan Settings"
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.navController.navigate(R.id.navigation_plan_settings)
            false
        }
        notifications.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.supportActionBar?.title = "Notifications"
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.navController.navigate(R.id.navigation_notifications)
            false
        }
        overrides!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.supportActionBar?.title = "Overrides"
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.navController.navigate(R.id.navigation_overrides)
            false
        }
        infoSupport!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mainActivity.supportActionBar?.title = "Information & Support"
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.navController.navigate(R.id.navigation_information)
            false
        }
        discord!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://discord.gg/AKrefXRyuA"))
            startActivity(i)
            false
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
                Toast.makeText(App.applicationContext(), "Google Sign In Failed", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun googleSignIn(){
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            val mGoogleSignInClient = GoogleSignIn.getClient((activity as MainActivity).applicationContext, gso)
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, _rcSignIn)
        }
        builder.setNeutralButton(R.string.no) { dialogInterface, _ -> dialogInterface.cancel() }
        builder.setMessage(R.string.msg_google).setTitle(R.string.title_sign_in)
        builder.create().show()
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val auth = FirebaseAuth.getInstance()
        val navControl = (activity as MainActivity).navController
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(App.applicationContext(), "Signed In!", Toast.LENGTH_LONG).show()
                val db = FirebaseFirestore.getInstance()
                val user = FirebaseAuth.getInstance().currentUser
                db.collection("main").document(user!!.uid).get()
                        .addOnSuccessListener { doc ->
                            if (doc != null) {
                                val builder = AlertDialog.Builder(context)
                                builder.setPositiveButton("Use Cloud Data") { _, _ ->
                                    firestoneToPreference(doc)
                                    navControl.navigate(R.id.navigation_home)
                                }
                                builder.setNeutralButton("Overwrite with device") { _, _ ->
                                    preferenceToFireStone()
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
                Toast.makeText(App.applicationContext(), "Google Sign In Failed", Toast.LENGTH_LONG).show()
            }
        }
    }
}