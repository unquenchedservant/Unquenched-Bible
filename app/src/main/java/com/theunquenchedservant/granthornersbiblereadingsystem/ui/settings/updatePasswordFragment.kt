package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.Context
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
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref

class updatePasswordFragment: Fragment() {
        override fun onCreateView(inflater: LayoutInflater,
                                  container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_update_password, container, false)
        }
        override fun onResume() {
            super.onResume()
            val dark = SharedPref.getBoolPref("darkMode", true)
            val b = arguments
            val errorMsg: String
            val root = requireView()
            val mainActivity = activity as MainActivity
            val user = FirebaseAuth.getInstance().currentUser
            val errorHolder = root.findViewById<MaterialTextView>(R.id.errorLabel)
            val cardHolder = root.findViewById<MaterialCardView>(R.id.cardHolder)
            val currentPasswordLabel = root.findViewById<MaterialTextView>(R.id.currentPasswordLabel)
            val newPasswordLabel = root.findViewById<MaterialTextView>(R.id.newPassLabel)
            val confirmPasswordLabel = root.findViewById<MaterialTextView>(R.id.confirmPassLabel)
            val currentPassword = root.findViewById<EditText>(R.id.currentPass)
            val newPassword = root.findViewById<EditText>(R.id.newPass)
            val confirmPassword = root.findViewById<EditText>(R.id.confirmPass)
            val updateBtn = root.findViewById<Button>(R.id.updateEmailBtn)
            if (b?.getString("error") != null){
                errorHolder.isVisible = true
                errorMsg = b.getString("error")!!
                errorHolder.text = errorMsg
            }else{
                errorHolder.isVisible = false
            }
            if(dark){
                currentPasswordLabel.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedEmphDark))
                currentPassword.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedTextDark))
                newPasswordLabel.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedEmphDark))
                newPassword.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedTextDark))
                confirmPasswordLabel.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedEmphDark))
                confirmPassword.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedTextDark))
                currentPassword.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#9cb9d3"))
                newPassword.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#9cb9d3"))
                confirmPassword.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#9cb9d3"))
                cardHolder.setCardBackgroundColor(ContextCompat.getColor(App.applicationContext(), R.color.buttonBackgroundDark))
                updateBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383838"))
                updateBtn.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedTextDark))
            }else{
                currentPasswordLabel.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedEmph))
                currentPassword.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedText))
                newPasswordLabel.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedEmph))
                newPassword.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedText))
                confirmPasswordLabel.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedEmph))
                confirmPassword.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedText))
                currentPassword.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#121212"))
                newPassword.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#121212"))
                confirmPassword.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#121212"))
                cardHolder.setCardBackgroundColor(ContextCompat.getColor(App.applicationContext(), R.color.buttonBackground))
                updateBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e1e2e6"))
                updateBtn.setTextColor(ContextCompat.getColor(App.applicationContext(), R.color.unquenchedText))
            }
            updateBtn.setOnClickListener {
                if(newPassword.text.toString() != confirmPassword.text.toString()){
                    val bundle = bundleOf("error" to "Passwords don't match")
                    mainActivity.navController.navigate(R.id.navigation_update_password, bundle)
                }else{
                    val credential = EmailAuthProvider.getCredential(user!!.email.toString(), currentPassword.text.toString())
                    user.reauthenticate(credential)
                            .addOnCompleteListener{
                                if(it.isSuccessful){
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
                                                        log("This is the reason the password update failed: ${it.exception}")
                                                        val bundle = bundleOf("error" to "Unknown Error")
                                                        mainActivity.navController.navigate(R.id.navigation_update_password, bundle)
                                                    }
                                                }
                                            }
                                }else{
                                    if(it.exception is FirebaseAuthInvalidCredentialsException){
                                        val bundle = bundleOf("error" to "Incorrect Password")
                                        mainActivity.navController.navigate(R.id.navigation_update_password, bundle)
                                    }else{
                                        log("This is the reason the password update failed: ${it.exception}")
                                        val bundle = bundleOf("error" to "Unknown Error")
                                        mainActivity.navController.navigate(R.id.navigation_update_password, bundle)
                                    }
                                }
                            }
                }
            }
        }
}