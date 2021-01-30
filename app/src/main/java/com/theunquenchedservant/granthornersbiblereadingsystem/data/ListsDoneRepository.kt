package com.theunquenchedservant.granthornersbiblereadingsystem.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref

class ListsDoneRepository {
    private val user: FirebaseUser? = Firebase.auth.currentUser

    fun getListsDone(): LiveData<ListsDone> {
        val data = MutableLiveData<ListsDone>()
        when (user != null) {
            true -> {
                val db = Firebase.firestore
                db.collection("main").document(user.uid).get()
                        .addOnSuccessListener { docSnap ->
                            val listsDone = ListsDone((docSnap.data!!["listsDone"] as Long).toInt())
                            data.value = listsDone
                        }
                        .addOnFailureListener { exception ->
                            log(logString = "Failed to get data. Error: $exception")
                        }
            }
            else -> data.value = ListsDone(getIntPref(name = "listsDone"))
        }
        return data
    }
}