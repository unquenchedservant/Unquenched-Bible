package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_CHAPTERS
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.getBooks
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref

object BibleStatsReset {
    private val isLogged = Firebase.auth.currentUser

    fun resetBook(bookName: String, testament: String, hardReset: Boolean = false, internal: Boolean = false, updateValues: MutableMap<String, Any> = mutableMapOf()):MutableMap<String, Any>{
        val chapters = BOOK_CHAPTERS[bookName] ?: error("")
        for(chapter in 1..chapters){
            updateValues["${bookName}${chapter}Read"] = setBoolPref(name="${bookName}${chapter}Read", value=false)
            if(hardReset) {
                updateValues["${bookName}${chapter}AmountRead"] = setIntPref(name="${bookName}${chapter}AmountRead", value=0)

            }
        }
        val testamentChaptersDone = getIntPref(name="${testament}ChaptersRead")
        val wholeChaptersDone = getIntPref(name="totalChaptersRead")
        val chaptersDone = getIntPref(name="${bookName}ChaptersRead")
        if(!getBoolPref(name="${bookName}DoneTestament")){
            updateValues["${testament}ChaptersRead"] = setIntPref(name="${testament}ChaptersRead",  value=testamentChaptersDone - chaptersDone)
        }else{
            updateValues["${testament}ChaptersRead"] = setIntPref(name="${testament}ChaptersRead", value=testamentChaptersDone - chapters)
        }
        if(!getBoolPref(name="${bookName}DoneWhole")){
            updateValues["totalChaptersRead"] = setIntPref(name="totalChaptersRead", value=wholeChaptersDone - chaptersDone)
        }else{
            updateValues["totalChaptersRead"] = setIntPref(name="totalChaptersRead", value=wholeChaptersDone-chapters)
        }
        updateValues["${bookName}ChaptersRead"] = setIntPref(name="${bookName}ChaptersRead", value=0)
        if (hardReset){
            updateValues["${bookName}AmountRead"] = setIntPref(name="${bookName}AmountRead", value=0)
        }
        if (isLogged != null && !internal) {
            val db = Firebase.firestore
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

    fun resetTestament(testament: String, hardReset: Boolean=false, internal: Boolean=false, updateValues: MutableMap<String, Any> = mutableMapOf()) :MutableMap<String, Any>{
        val books = getBooks(testament)!!
        var updateValueUpdated = updateValues
        for(book in books){
            updateValueUpdated = resetBook(book, testament, hardReset, internal=true, updateValueUpdated)
        }

        updateValueUpdated["${testament}ChaptersRead"] = setIntPref(name="${testament}ChaptersRead", value=0)
        if(hardReset){

            updateValueUpdated["${testament}AmountRead"] = setIntPref(name="${testament}AmountRead", value=0)
        }
        if (isLogged != null && !internal) {
            val db = Firebase.firestore
            db.collection("main").document(isLogged.uid).update(updateValueUpdated)
                    .addOnSuccessListener {
                        MainActivity.log("Successful update")
                    }
                    .addOnFailureListener {
                        val error = it
                        Log.w("PROFGRANT", "Failure writing to firestore", error)
                    }
        }
        return updateValueUpdated
    }

    fun resetBible(hardReset: Boolean=false){
        var updateValues = mutableMapOf<String, Any>()
        updateValues = resetTestament(testament="new", hardReset, internal=true, updateValues)
        updateValues = resetTestament(testament="old", hardReset, internal=true, updateValues)
        if(hardReset){
            updateValues["bibleAmountRead"] = setIntPref(name="bibleAmountRead", value=0)
        }
        updateValues["totalChaptersRead"] = setIntPref(name="totalChaptersRead", value=0)
        if (isLogged != null){
            val db = Firebase.firestore
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