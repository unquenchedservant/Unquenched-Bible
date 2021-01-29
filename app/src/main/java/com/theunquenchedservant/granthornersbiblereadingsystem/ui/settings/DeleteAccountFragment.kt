package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.firestoreToPreference

class DeleteAccountFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_confirm_delete_account, container, false)
    }

    override fun onResume() {
        super.onResume()
        val dark = SharedPref.getBoolPref(name="darkMode", defaultValue=true)
        val b = arguments
        val errorMsg: String
        val root = requireView()
        val providers = FirebaseAuth.getInstance().currentUser?.providerData
        val provider = providers!![1].providerId
        val user = FirebaseAuth.getInstance().currentUser
        val errorHolder = root.findViewById<MaterialTextView>(R.id.errorLabel)
        val cardHolder = root.findViewById<MaterialCardView>(R.id.cardHolder)
        val userPassLabel = root.findViewById<MaterialTextView>(R.id.currentPassLabel)
        val userPass = root.findViewById<EditText>(R.id.userPass)
        val confirmBtn = root.findViewById<Button>(R.id.deleteAccountBtn)
        if (b?.getString("error") != null) {
            errorHolder.isVisible = true
            errorMsg = b.getString("error")!!
            errorHolder.text = errorMsg
        } else {
            errorHolder.isVisible = false
        }
        if (provider == "password") {
            if (dark) {
                userPassLabel.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedEmphDark))
                userPass.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedTextDark))
                userPass.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#9cb9d3"))
                cardHolder.setCardBackgroundColor(ContextCompat.getColor(App.applicationContext(), R.color.buttonBackgroundDark))
                confirmBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383838"))
            } else {
                userPassLabel.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedEmph))
                userPass.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedText))
                userPass.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#121212"))
                cardHolder.setCardBackgroundColor(ContextCompat.getColor(App.applicationContext(), R.color.buttonBackground))
                confirmBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e1e2e6"))
            }
            confirmBtn.setOnClickListener {
                val credential = EmailAuthProvider.getCredential(user!!.email.toString(), userPass.text.toString())
                deleteAccount(credential, user, root)
            }
        } else {
            cardHolder.visibility = View.GONE
            confirmBtn.visibility = View.GONE
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            GoogleSignIn.getClient(App.applicationContext(), gso).silentSignIn().addOnCompleteListener {
                val credential = GoogleAuthProvider.getCredential(it.result?.idToken, null)
                deleteAccount(credential, user!!, root)
            }
        }
    }

    private fun deleteAccount(credential: AuthCredential, user: FirebaseUser, root: View) {
        val mainActivity = activity as MainActivity
        val db = FirebaseFirestore.getInstance()
        db.collection("main").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    if (doc != null) {
                        firestoreToPreference(doc)
                    }
                }

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
                                        log("This is the reason the account deletion failed: ${task.exception}")
                                        val bundle = bundleOf("error" to "Unknown Error")
                                        mainActivity.navController.navigate(R.id.navigation_confirm_delete, bundle)
                                    }
                                }
                    } else {
                        if (it.exception is FirebaseAuthInvalidCredentialsException) {
                            val bundle = bundleOf("error" to "Incorrect Password")
                            mainActivity.navController.navigate(R.id.navigation_confirm_delete, bundle)
                        } else {
                            log("This is the reason the account authentication failed: ${it.exception}")
                            val bundle = bundleOf("error" to "Unknown Error")
                            mainActivity.navController.navigate(R.id.navigation_confirm_delete, bundle)
                        }
                    }
                }
    }
}
