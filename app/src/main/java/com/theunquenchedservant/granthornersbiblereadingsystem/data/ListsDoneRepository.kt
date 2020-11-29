package com.theunquenchedservant.granthornersbiblereadingsystem.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref

class ListsDoneRepository {
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    fun getListsDone(): LiveData<ListsDone> {
        val data = MutableLiveData<ListsDone>()
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("main").document(user.uid).get()
                    .addOnSuccessListener {
                        val listsDone = ListsDone((it.data!!["listsDone"] as Long).toInt())
                        data.value = listsDone
                    }
                    .addOnFailureListener {
                        log("Failed to get data. Error: $it")
                    }
        }else{
            data.value = ListsDone(getIntPref("listsDone"))
        }
        return data
    }
}