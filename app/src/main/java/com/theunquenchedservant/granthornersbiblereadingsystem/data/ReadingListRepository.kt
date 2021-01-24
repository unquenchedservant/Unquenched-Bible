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
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS
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
                            val reading: String
                            val resultObject: ReadingLists
                            if(it.result!!.data!![listName] != null) {
                                reading = getReading((it.result!!.data!![listName] as Long).toInt(), listId, listName, true, psalmChecked)
                                resultObject = ReadingLists(listName, (it.result!!.data!!["${listName}Done"] as Long).toInt(), (it.result!!.data!![listName] as Long).toInt(), reading)
                            }else{
                                reading = getReading(0, listId, listName, true, psalmChecked)
                                resultObject = ReadingLists(listName, 0, 0, reading)
                            }
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
            "mcheyne_list1"->R.array.mcheyne_list1
            "mcheyne_list2"->R.array.mcheyne_list2
            "mcheyne_list3"->R.array.mcheyne_list3
            "mcheyne_list4"->R.array.mcheyne_list4
            else-> 0
        }
    }
    fun getReading(index:Int, listId: Int, listName: String, fromFirebase: Boolean, psalmChecked: Boolean): String {
        val list = App.applicationContext().resources.getStringArray(listId)
        val planSystem = getStringPref("planSystem", "pgh")
        val prefix = when(planSystem){
            "pgh" -> ""
            "mcheyne"->"mcheyne_"
            else->""
        }
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
        when (getStringPref("planType", "horner")) {
            "horner" -> {
                return when (index) {
                    list.size -> {
                        setIntPref(name=listName, value=0, updateFS=true)
                        list[0]
                    }
                    else -> {
                        setIntPref(name=listName, value=index, updateFS=true)
                        list[index]
                    }
                }
            }
            "numerical" -> {
                var newIndex = getIntPref("${prefix}currentDayIndex", 0)
                if (newIndex >= list.size){
                    while(newIndex >= list.size){
                        newIndex -= list.size
                    }
                }
                return list[newIndex]
            }
            "calendar" -> {
                var newIndex = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1
                if (newIndex >= list.size){
                    while(newIndex >= list.size){
                        newIndex -= list.size
                    }
                }
                return list[newIndex]
            }
            else -> return list[index]
        }
    }
}