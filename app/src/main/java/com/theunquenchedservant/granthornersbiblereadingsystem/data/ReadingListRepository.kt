package com.theunquenchedservant.granthornersbiblereadingsystem.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import java.util.*

class ReadingListRepository {
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    fun getList(listName:String): LiveData<ReadingLists> {
        val data = MutableLiveData<ReadingLists>()
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("main").document(user.uid).get()
                    .addOnCompleteListener {
                        if (it.result != null) {
                            val psalmChecked = it.result!!.data!!["psalms"] as Boolean
                            val listId = getListId(listName)
                            val reading = getReading((it.result!!.data!![listName] as Long).toInt(), listId, listName, true, psalmChecked)
                            val resultObject = ReadingLists(listName, (it.result!!.data!!["${listName}Done"] as Long).toInt(), (it.result!!.data!![listName] as Long).toInt(), reading)
                            data.value = resultObject
                        }else{
                            log("RESULT WAS NULL")
                        }
                    }
                    .addOnFailureListener {
                        log("Failed to get data. Error: $it")
                    }
        }else{
            val listId = getListId(listName)
            val psalmChecked = getBoolPref("psalms")
            val reading = getReading(getIntPref(listName), listId, listName, false, psalmChecked)
            val resultObject: ReadingLists = ReadingLists(listName, getIntPref("${listName}Done"), getIntPref(listName), reading)
            data.value = resultObject
        }
        return data
    }
    fun getListId(listName: String) : Int{
        return when(listName){
            "list1"-> R.array.list_1
            "list2"-> R.array.list_2
            "list3"-> R.array.list_3
            "list4"-> R.array.list_4
            "list5"-> R.array.list_5
            "list6"-> R.array.list_6
            "list7"-> R.array.list_7
            "list8"-> R.array.list_8
            "list9"-> R.array.list_9
            "list10"-> R.array.list_10
            else-> 0
        }
    }
    fun getReading(index:Int, listId: Int, listName: String, fromFirebase: Boolean, psalmChecked: Boolean): String {
        val list = App.applicationContext().resources.getStringArray(listId)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        if (listName == "list6") {
            if (psalmChecked) {
                return if (day != 31) {
                    "$day, ${day + 30}, ${day + 60}, ${day + 90}, ${day + 120}"
                } else {
                    "Day Off"
                }
            }
        }
        return when(index){
            list.size -> {
                if(fromFirebase){
                    SharedPref.updateFS(listName, 0)
                }
                SharedPref.setIntPref(listName, 0)
                list[0]
            }
            else -> {
                SharedPref.setIntPref(listName, index)
                list[index]
            }
        }
    }
}