package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.bookChapters
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.getBooks
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref

object BibleStatsReset {
    private val isLogged = FirebaseAuth.getInstance().currentUser

    fun resetBook(bookName: String, testament: String, hardReset: Boolean = false, internal: Boolean = false, updateValues: MutableMap<String, Any> = mutableMapOf<String, Any>()):MutableMap<String, Any>{
        val chapters = bookChapters[bookName]!!
        for(chapter in 1..chapters){
            updateValues["${bookName}_${chapter}_read"] = false
            setBoolPref("${bookName}_${chapter}_read", false)
            if(hardReset) {
                updateValues["${bookName}_${chapter}_amount_read"] = 0
                setIntPref("${bookName}_${chapter}_amount_read", 0)
            }
        }
        val testament_chapters_done = getIntPref("${testament}_chapters_read")
        val whole_chapters_done = getIntPref("total_chapters_read")
        val chapters_done = getIntPref("${bookName}_chapters_read")
        if(!getBoolPref("${bookName}_done_testament")){
            val new_testament_chapters_done = testament_chapters_done - chapters_done
            setIntPref("${testament}_chapters_read", new_testament_chapters_done)
            updateValues["${testament}_chapters_read"] = new_testament_chapters_done
        }else{
            val new_testament_chapters_done = testament_chapters_done - chapters
            setIntPref("${testament}_chapters_read", new_testament_chapters_done)
            updateValues["${testament}_chapters_read"] = new_testament_chapters_done
        }
        if(!getBoolPref("${bookName}_done_whole")){
            val new_whole_chapters_done = whole_chapters_done - chapters_done
            setIntPref("total_chapters_read", new_whole_chapters_done)
            updateValues["total_chapters_read"] = new_whole_chapters_done
        }else{
            val new_whole_chapters_done = whole_chapters_done - chapters
            setIntPref("total_chapters_read", new_whole_chapters_done)
            updateValues["total_chapters_read"] = new_whole_chapters_done
        }
        setIntPref("${bookName}_chapters_read", 0)
        updateValues["${bookName}_chapters_read"] = 0
        if (hardReset){
            setIntPref("${bookName}_amount_read", 0)
        }
        if (isLogged != null && !internal) {
            val db = FirebaseFirestore.getInstance()
            db.collection("main").document(isLogged.uid).update(updateValues)
                    .addOnSuccessListener {
                        MainActivity.log("Successful update")
                    }
                    .addOnFailureListener {
                        val error = it
                        Log.w("PROFGRANT", "Failure writing to firestore", error)
                    }
        }
        if(internal){
            return updateValues
        }
        return updateValues
    }

    fun resetTestament(testament: String, hardReset: Boolean=false, internal: Boolean=false, updateValues: MutableMap<String, Any> = mutableMapOf<String, Any>()) :MutableMap<String, Any>{
        val books = getBooks(testament)!!
        var updateValue_updated = updateValues
        for(book in books){
            updateValue_updated = resetBook(book, testament, hardReset, true, updateValue_updated)
        }
        setIntPref("${testament}_chapters_read", 0)
        updateValue_updated["${testament}_chapters_read"] = 0
        if(hardReset){
            setIntPref("${testament}_amount_read", 0)
            updateValue_updated["${testament}_amount_read"] = 0
        }
        if (isLogged != null && !internal) {
            val db = FirebaseFirestore.getInstance()
            db.collection("main").document(isLogged.uid).update(updateValue_updated)
                    .addOnSuccessListener {
                        MainActivity.log("Successful update")
                    }
                    .addOnFailureListener {
                        val error = it
                        Log.w("PROFGRANT", "Failure writing to firestore", error)
                    }
        }
        return updateValue_updated
    }

    fun resetBible(hardReset: Boolean=false){
        var updateValues = mutableMapOf<String, Any>()
        updateValues = resetTestament("new", hardReset, true, updateValues)
        updateValues = resetTestament("old", hardReset, true, updateValues)
        if(hardReset){
            updateValues["bible_amount_read"] = 0
            setIntPref("bible_amount_read", 0)
        }
        setIntPref("total_chapters_read", 0)
        updateValues["total_chapters_read"] = 0
        if (isLogged != null){
            val db = FirebaseFirestore.getInstance()
            db.collection("main").document(isLogged.uid).update(updateValues)
                    .addOnSuccessListener {
                        MainActivity.log("Successful update")
                    }
                    .addOnFailureListener {
                        val error = it
                        Log.w("PROFGRANT", "Failure writing to firestore", error)
                    }
        }
    }
}