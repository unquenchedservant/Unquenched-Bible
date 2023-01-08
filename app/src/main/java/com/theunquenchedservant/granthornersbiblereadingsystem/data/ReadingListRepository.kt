package com.theunquenchedservant.granthornersbiblereadingsystem.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import java.util.*

class ReadingListRepository {
    private val user: FirebaseUser? = Firebase.auth.currentUser

    fun getList(listName:String): LiveData<ReadingLists> {
        val data = MutableLiveData<ReadingLists>()
        when (user != null) {
            true -> {
                val db = Firebase.firestore
                db.collection("main").document(Firebase.auth.currentUser!!.uid).get()
                        .addOnCompleteListener { task ->
                            when (task.result != null) {
                                true -> {
                                    val psalmChecked = if(task.result!!.data!!["psalms"] != null) task.result!!.data!!["psalms"] as Boolean else false
                                    val listId = getListId(listName)
                                    val reading: String
                                    val resultObject: ReadingLists
                                    val listIndex = if(task.result!!.data!!["${listName}Index"] != null && task.result!!.data!!["${listName}Index"] is Long) (task.result!!.data!!["${listName}Index"] as Long).toInt() else 0
                                    val listDone = if(task.result!!.data!!["${listName}Done"] != null) (task.result!!.data!!["${listName}Done"] as Boolean) else false
                                    reading = getReading(listIndex, listId, listName, psalmChecked)
                                    resultObject = ReadingLists(listName, listDone, listIndex, reading)
                                    data.value = resultObject
                                }
                                else -> debugLog(message="RESULT WAS NULL")
                            }
                        }
                        .addOnFailureListener { exception ->
                            debugLog(message="Failed to get data. Error: $exception")
                        }
            }
            else -> {
                val listId = getListId(listName)
                val psalmChecked = getBoolPref(name = "psalms")
                val reading = getReading(getIntPref(listName), listId, listName, psalmChecked)
                val resultObject = ReadingLists(listName, getBoolPref(name = "${listName}Done"), getIntPref(listName), reading)
                data.value = resultObject
            }
        }
        return data
    }
    private fun getListId(listName: String) : Int{
        return when(listName){
            "pgh1"-> R.array.pgh_list1
            "pgh2"-> R.array.pgh_list2
            "pgh3"-> R.array.pgh_list3
            "pgh4"-> R.array.pgh_list4
            "pgh5"-> R.array.pgh_list5
            "pgh6"-> R.array.pgh_list6
            "pgh7"-> R.array.pgh_list7
            "pgh8"-> R.array.pgh_list8
            "pgh9"-> R.array.pgh_list9
            "pgh10"-> R.array.pgh_list10
            "mcheyne1"->R.array.mcheyne_list1
            "mcheyne2"->R.array.mcheyne_list2
            "mcheyne3"->R.array.mcheyne_list3
            "mcheyne4"->R.array.mcheyne_list4
            else-> 0
        }
    }
    private fun getReading(index:Int, listId: Int, listName: String, psalmChecked: Boolean): String {
        val list = App.applicationContext().resources.getStringArray(listId)
        val planSystem = getStringPref(name="planSystem", defaultValue="pgh")
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        when (listName) {
            "pgh6" -> {
                when (psalmChecked) {
                    true-> {
                        return when(day){
                            in 1..30-> "$day, ${day + 30}, ${day + 60}, ${day + 90}, ${day + 120}"
                            else-> "Day Off"
                        }
                    }
                    false->{}
                }
            }
        }
        when (getStringPref(name="planType", defaultValue="horner")) {
            "horner" -> {
                return when (index) {
                    !in 0..list.size - 1 -> {
                        setIntPref(name="${listName}Index", value=0, updateFS=true)
                        list[0]
                    }
                    else -> {
                        list[index]
                    }
                }
            }
            "numerical" -> {
                var newIndex = when(planSystem){
                    "pgh"->getIntPref(name="pghIndex", defaultValue=0)
                    else->getIntPref(name="mcheyneIndex", defaultValue=0)
                }
                while(newIndex>=list.size){
                    newIndex -= list.size
                }
                return list[newIndex]
            }
            "calendar" -> {
                var newIndex = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 3
                val cal = Calendar.getInstance();
                if(cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365 && cal.get(Calendar.DAY_OF_YEAR) > 60){
                    newIndex -= 1
                }
                while (newIndex >= list.size) {
                    newIndex -= list.size
                }
                return list[newIndex]
            }
            else -> return list[index]
        }
    }
}