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
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref

class UpdateEmailFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_update_email, container, false)
    }
    override fun onResume() {
        super.onResume()
        val dark = getBoolPref(name="darkMode", defaultValue=true)
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
        if(dark){
            currentEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.unquenchedEmphDark))
            currentEmail.setTextColor(ContextCompat.getColor(context, R.color.unquenchedTextDark))
            newEmail.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#9cb9d3"))
            userPass.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#9cb9d3"))
            cardHolder.setCardBackgroundColor(ContextCompat.getColor(context, R.color.buttonBackgroundDark))
            emailLabel.setTextColor(ContextCompat.getColor(context, R.color.unquenchedEmphDark))
            passwordLabel.setTextColor(ContextCompat.getColor(context, R.color.unquenchedEmphDark))
            updateBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383838"))
            updateBtn.setTextColor(ContextCompat.getColor(context, R.color.unquenchedTextDark))
        }else{
            currentEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.unquenchedEmph))
            currentEmail.setTextColor(ContextCompat.getColor(context, R.color.unquenchedText))
            newEmail.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#121212"))
            userPass.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#121212"))
            cardHolder.setCardBackgroundColor(ContextCompat.getColor(context, R.color.buttonBackground))
            emailLabel.setTextColor(ContextCompat.getColor(context, R.color.unquenchedEmph))
            passwordLabel.setTextColor(ContextCompat.getColor(context, R.color.unquenchedEmph))
            updateBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e1e2e6"))
            updateBtn.setTextColor(ContextCompat.getColor(context, R.color.unquenchedText))
        }
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
                                                debugLog(message="THIS IS THE ERROR FOR EMAIL CHANGE ${it.exception}")
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
                                debugLog(message="This is the error ${task.exception}")
                                val bundle = bundleOf("error" to "Unknown Error", "email" to newEmail.text.toString())
                                mainActivity.navController.navigate(R.id.navigation_update_email, bundle)
                            }
                        }
                    }
        }
    }
}