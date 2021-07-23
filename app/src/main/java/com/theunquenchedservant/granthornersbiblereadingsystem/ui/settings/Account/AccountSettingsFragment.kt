package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings.Account

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.preference.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R

class AccountSettingsFragment: PreferenceFragmentCompat()  {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.account_settings_preferences, rootKey)
        val mainActivity = activity as MainActivity
        val providers = Firebase.auth.currentUser?.providerData
        val provider = providers!![1].providerId
        val currentUser: Preference? = findPreference("currentAccount")
        currentUser!!.summary = "${Firebase.auth.currentUser?.email}"
        val deleteAccount: Preference? = findPreference("deleteAccount")
        val logOut: Preference? = findPreference("logOut")
        val emailUpdate: Preference? = findPreference("editEmail")
        val passwordUpdate: Preference? = findPreference("editPassword")
        emailUpdate!!.isVisible = false
        passwordUpdate!!.isVisible = false
        deleteAccount!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_delete_forever_24, mainActivity.theme)
        deleteAccount.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                mainActivity.navController.navigate(R.id.navigation_confirm_delete)
            }
            builder.setNeutralButton(getString(R.string.no)) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            builder.setMessage(getString(R.string.msg_confirm_delete))
            builder.setTitle(getString(R.string.title_confirm_delete))
            builder.create().show()
            true
        }
        logOut!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().clear().apply()
                Firebase.auth.signOut()
                Toast.makeText(context, "Signed Out!", Toast.LENGTH_LONG).show()
                (activity as MainActivity).finish()
                val i = Intent(requireContext(), MainActivity::class.java)
                startActivity(i)
            }
            builder.setNeutralButton(getString(R.string.no)) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            builder.setMessage(getString(R.string.msg_sign_out))
            builder.setTitle(getString(R.string.title_sign_out))
            builder.create().show()
            true
        }
        when (provider) {
            "password" -> {
                emailUpdate.isVisible = true
                passwordUpdate.isVisible = true
                emailUpdate.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    mainActivity.navController.navigate(R.id.navigation_update_email)
                    true
                }
                emailUpdate.title = "Update Email Address"
                passwordUpdate.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    mainActivity.navController.navigate(R.id.navigation_update_password)
                    true
                }
            }
        }
    }
}