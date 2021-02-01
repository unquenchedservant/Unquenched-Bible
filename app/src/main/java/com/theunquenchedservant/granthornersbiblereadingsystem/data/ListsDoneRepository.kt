package com.theunquenchedservant.granthornersbiblereadingsystem.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref

class ListsDoneRepository {
    private val user: FirebaseUser? = Firebase.auth.currentUser

    fun getListsDone(): LiveData<ListsDone> {
        val data = MutableLiveData<ListsDone>()
        when (user != null) {
            true -> {
                val db = Firebase.firestore
                db.collection("main").document(user.uid).get()
                        .addOnSuccessListener { docSnap ->
                            val mcheyneDone = if (docSnap.data!!["mcheyneListsDone"] == null){
                                0
                            }else{
                                (docSnap.data!!["mcheyneListsDone"] as Long).toInt()
                            }
                            val listsDone = if(docSnap.data!!["planSystem"] == "pgh") ListsDone((docSnap.data!!["listsDone"] as Long).toInt()) else ListsDone(mcheyneDone)
                            data.value = listsDone
                        }
                        .addOnFailureListener { exception ->
                            log(logString = "Failed to get data. Error: $exception")
                        }
            }
            else -> data.value = if(getStringPref(name="planSystem") == "pgh") ListsDone(getIntPref(name = "listsDone")) else ListsDone(getIntPref("mcheyneListsDone"))
        }
        return data
    }
}