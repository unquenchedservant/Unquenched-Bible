package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.preference.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log

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

class DeleteAccountFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_confirm_delete_account, container, false)
    }

    override fun onResume() {
        super.onResume()
        val b = arguments
        val errorMsg: String
        val root = requireView()
        val user = Firebase.auth.currentUser
        val providers = user?.providerData
        val provider = providers!![1].providerId
        val errorHolder = root.findViewById<MaterialTextView>(R.id.errorLabel)
        val cardHolder = root.findViewById<MaterialCardView>(R.id.cardHolder)
        val userPassLabel = root.findViewById<MaterialTextView>(R.id.currentPassLabel)
        val userPass = root.findViewById<EditText>(R.id.userPass)
        val confirmBtn = root.findViewById<Button>(R.id.deleteAccountBtn)
        val context = (activity as MainActivity).applicationContext
        val preferences = App().preferences!!
        if (b?.getString("error") != null) {
            errorHolder.isVisible = true
            errorMsg = b.getString("error")!!
            errorHolder.text = errorMsg
        } else {
            errorHolder.isVisible = false
        }
        if (provider == "password") {
            userPassLabel.setBackgroundColor(preferences.colors.emphColor)
            userPass.setTextColor(preferences.colors.textColor)
            cardHolder.setCardBackgroundColor(preferences.colors.buttonBackground)
            userPass.backgroundTintList = ColorStateList.valueOf(preferences.colors.emphColor)
            confirmBtn.backgroundTintList = ColorStateList.valueOf(preferences.colors.buttonBackground)
            confirmBtn.setOnClickListener {
                val credential = EmailAuthProvider.getCredential(user.email.toString(), userPass.text.toString())
                deleteAccount(credential, user, root)
            }
        } else {
            cardHolder.visibility = View.GONE
            confirmBtn.visibility = View.GONE
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            GoogleSignIn.getClient(context, gso).silentSignIn().addOnCompleteListener {
                val credential = GoogleAuthProvider.getCredential(it.result?.idToken, null)
                deleteAccount(credential, user, root)
            }
        }
    }

    private fun deleteAccount(credential: AuthCredential, user: FirebaseUser, root: View) {
        val mainActivity = activity as MainActivity

        user.reauthenticate(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                root.clearFocus()
                                mainActivity.navController.navigate(R.id.navigation_settings)
                                Toast.makeText(context, "Account deleted!", Toast.LENGTH_LONG).show()
                            } else {
                                Log.debugLog(message = "This is the reason the account deletion failed: ${task.exception}")
                                val bundle = bundleOf("error" to "Unknown Error")
                                mainActivity.navController.navigate(R.id.navigation_confirm_delete, bundle)
                            }
                        }
                } else {
                    if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        val bundle = bundleOf("error" to "Incorrect Password")
                        mainActivity.navController.navigate(R.id.navigation_confirm_delete, bundle)
                    } else {
                        Log.debugLog(message = "This is the reason the account authentication failed: ${it.exception}")
                        val bundle = bundleOf("error" to "Unknown Error")
                        mainActivity.navController.navigate(R.id.navigation_confirm_delete, bundle)
                    }
                }
            }
    }
}
class UpdateEmailFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_update_email, container, false)
    }
    override fun onResume() {
        super.onResume()
        val b = arguments
        val curEmail: String
        val errorMsg: String
        val root = requireView()
        val mainActivity = activity as MainActivity
        val user = Firebase.auth.currentUser
        val errorHolder = root.findViewById<MaterialTextView>(R.id.errorLabel)
        val cardHolder = root.findViewById<MaterialCardView>(R.id.cardHolder)
        val emailLabel = root.findViewById<MaterialTextView>(R.id.emailLabel)
        val passwordLabel = root.findViewById<MaterialTextView>(R.id.passwordLabel)
        val currentEmailLabel = root.findViewById<MaterialTextView>(R.id.currentEmailLabel)
        val currentEmail = root.findViewById<MaterialTextView>(R.id.currentEmail)
        val newEmail = root.findViewById<EditText>(R.id.updatedEmail)
        val userPass = root.findViewById<EditText>(R.id.userPass)
        val updateBtn = root.findViewById<Button>(R.id.updateEmailBtn)
        val context = mainActivity.applicationContext
        val preferences = App().preferences!!
        if (b?.getString("error") != null){
            errorHolder.isVisible = true
            errorMsg = b.getString("error")!!
            errorHolder.text = errorMsg
            curEmail = b.getString("email")!!
            newEmail.setText(curEmail)
        }else{
            errorHolder.isVisible = false
        }
        currentEmail.text = Firebase.auth.currentUser?.email
        currentEmailLabel.setTextColor(preferences.colors.emphColor)
        currentEmail.setTextColor(preferences.colors.textColor)
        cardHolder.setCardBackgroundColor(preferences.colors.buttonBackground)
        emailLabel.setTextColor(preferences.colors.emphColor)
        passwordLabel.setTextColor(preferences.colors.emphColor)
        updateBtn.setTextColor(preferences.colors.textColor)
        newEmail.backgroundTintList = ColorStateList.valueOf(preferences.colors.emphColor)
        userPass.backgroundTintList = ColorStateList.valueOf(preferences.colors.emphColor)
        updateBtn.backgroundTintList = ColorStateList.valueOf(preferences.colors.buttonBackground)

        updateBtn.setOnClickListener {
            val credential = EmailAuthProvider.getCredential(user!!.email.toString(), userPass.text.toString())
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        user.updateEmail(newEmail.text.toString())
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    root.clearFocus()
                                    mainActivity.navController.navigate(R.id.navigation_account_settings)
                                    Toast.makeText(context, "Email Changed!", Toast.LENGTH_LONG).show()
                                }else{
                                    if(it.exception is FirebaseAuthUserCollisionException){
                                        val bundle = bundleOf("error" to "Email already exists", "email" to newEmail.text.toString())
                                        mainActivity.navController.navigate(R.id.navigation_update_email, bundle)
                                    }else {
                                        Log.debugLog(message = "THIS IS THE ERROR FOR EMAIL CHANGE ${it.exception}")
                                        val bundle = bundleOf("error" to "Unknown Error", "email" to newEmail.text.toString())
                                        mainActivity.navController.navigate(R.id.navigation_update_email, bundle)
                                    }
                                }
                            }
                    }else{
                        if(task.exception is FirebaseAuthInvalidCredentialsException){
                            val bundle = bundleOf("error" to "Incorrect Password", "email" to newEmail.text.toString())
                            mainActivity.navController.navigate(R.id.navigation_update_email, bundle)
                        }else {
                            Log.debugLog(message = "This is the error ${task.exception}")
                            val bundle = bundleOf("error" to "Unknown Error", "email" to newEmail.text.toString())
                            mainActivity.navController.navigate(R.id.navigation_update_email, bundle)
                        }
                    }
                }
        }
    }
}

class UpdatePasswordFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_update_password, container, false)
    }
    override fun onResume() {
        super.onResume()
        val b = arguments
        val errorMsg: String
        val root = requireView()
        val mainActivity = activity as MainActivity
        val user = Firebase.auth.currentUser
        val errorHolder = root.findViewById<MaterialTextView>(R.id.errorLabel)
        val cardHolder = root.findViewById<MaterialCardView>(R.id.cardHolder)
        val currentPasswordLabel = root.findViewById<MaterialTextView>(R.id.currentPasswordLabel)
        val newPasswordLabel = root.findViewById<MaterialTextView>(R.id.newPassLabel)
        val confirmPasswordLabel = root.findViewById<MaterialTextView>(R.id.confirmPassLabel)
        val currentPassword = root.findViewById<EditText>(R.id.currentPass)
        val newPassword = root.findViewById<EditText>(R.id.newPass)
        val confirmPassword = root.findViewById<EditText>(R.id.confirmPass)
        val updateBtn = root.findViewById<Button>(R.id.updateEmailBtn)
        val preferences = App().preferences!!
        if (b?.getString("error") != null){
            errorHolder.isVisible = true
            errorMsg = b.getString("error")!!
            errorHolder.text = errorMsg
        }else{
            errorHolder.isVisible = false
        }
        currentPasswordLabel.setTextColor(preferences.colors.emphColor)
        currentPassword.setTextColor(preferences.colors.textColor)
        newPasswordLabel.setTextColor(preferences.colors.emphColor)
        currentPasswordLabel.setTextColor(ContextCompat.getColor(mainActivity, R.color.unquenchedEmphDark))
        currentPassword.setTextColor(ContextCompat.getColor(mainActivity, R.color.unquenchedTextDark))
        newPasswordLabel.setTextColor(ContextCompat.getColor(mainActivity, R.color.unquenchedEmphDark))
        newPassword.setTextColor(preferences.colors.textColor)
        confirmPasswordLabel.setTextColor(preferences.colors.emphColor)
        confirmPassword.setTextColor(preferences.colors.textColor)
        currentPassword.backgroundTintList = ColorStateList.valueOf(preferences.colors.emphColor)
        newPassword.backgroundTintList = ColorStateList.valueOf(preferences.colors.emphColor)
        confirmPassword.backgroundTintList = ColorStateList.valueOf(preferences.colors.emphColor)
        cardHolder.setCardBackgroundColor(preferences.colors.buttonBackground)
        updateBtn.backgroundTintList = ColorStateList.valueOf(preferences.colors.buttonBackground)
        updateBtn.setTextColor(preferences.colors.textColor)
        updateBtn.setOnClickListener {
            if(newPassword.text.toString() != confirmPassword.text.toString()){
                val bundle = bundleOf("error" to "Passwords don't match")
                mainActivity.navController.navigate(R.id.navigation_update_password, bundle)
            }else{
                val credential = EmailAuthProvider.getCredential(user!!.email.toString(), currentPassword.text.toString())
                user.reauthenticate(credential)
                    .addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            user.updatePassword(newPassword.text.toString())
                                .addOnCompleteListener {
                                    if(it.isSuccessful){
                                        root.clearFocus()
                                        mainActivity.navController.navigate(R.id.navigation_account_settings)
                                        Toast.makeText(context, "Password Changed!", Toast.LENGTH_LONG).show()
                                    }else{
                                        if(it.exception is FirebaseAuthWeakPasswordException){
                                            val errorReason = (it.exception as FirebaseAuthWeakPasswordException).reason
                                            val bundle = bundleOf("error" to errorReason)
                                            mainActivity.navController.navigate(R.id.navigation_update_password, bundle)
                                        }else{
                                            Log.debugLog("This is the reason the password update failed: ${it.exception}")
                                            val bundle = bundleOf("error" to "Unknown Error")
                                            mainActivity.navController.navigate(R.id.navigation_update_password, bundle)
                                        }
                                    }
                                }
                        }else{
                            if(task.exception is FirebaseAuthInvalidCredentialsException){
                                val bundle = bundleOf("error" to "Incorrect Password")
                                mainActivity.navController.navigate(R.id.navigation_update_password, bundle)
                            }else{
                                Log.debugLog("This is the reason the password update failed: ${task.exception}")
                                val bundle = bundleOf("error" to "Unknown Error")
                                mainActivity.navController.navigate(R.id.navigation_update_password, bundle)
                            }
                        }
                    }
            }
        }
    }
}