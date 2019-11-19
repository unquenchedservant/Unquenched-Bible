package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import com.google.firebase.auth.FirebaseAuth


object FirebaseHelper {
    fun checkLogin():Boolean{
        val user = FirebaseAuth.getInstance().currentUser
        return user != null
    }
}