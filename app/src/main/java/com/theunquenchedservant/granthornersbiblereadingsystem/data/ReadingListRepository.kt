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
                db.collection("main").document(user.uid).get()
                        .addOnCompleteListener { task ->
                            when (task.result != null) {
                                true -> {
                                    val psalmChecked = if(task.result!!.data!!["psalms"] != null) task.result!!.data!!["psalms"] as Boolean else false
                                    val listId = getListId(listName)
                                    val reading: String
                                    val resultObject: ReadingLists
                                    val listIndex = if(task.result!!.data!![listName] != null && task.result!!.data!![listName] is Long) (task.result!!.data!![listName] as Long).toInt() else 0
                                    val listDone = if(task.result!!.data!!["${listName}Done"] != null) (task.result!!.data!!["${listName}Done"] as Long).toInt() else 0
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
                val resultObject = ReadingLists(listName, getIntPref(name = "${listName}Done"), getIntPref(listName), reading)
                data.value = resultObject
            }
        }
        return data
    }
    private fun getListId(listName: String) : Int{
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
            "mcheyneList1"->R.array.mcheyne_list1
            "mcheyneList2"->R.array.mcheyne_list2
            "mcheyneList3"->R.array.mcheyne_list3
            "mcheyneList4"->R.array.mcheyne_list4
            else-> 0
        }
    }
    private fun getReading(index:Int, listId: Int, listName: String, psalmChecked: Boolean): String {
        val list = App.applicationContext().resources.getStringArray(listId)
        val planSystem = getStringPref(name="planSystem", defaultValue="pgh")
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        when (listName) {
            "list6" -> {
                when (psalmChecked) {
                    true-> {
                        return when(day){
                            in 1..30-> "$day, ${day + 30}, ${day + 60}, ${day + 90}, ${day + 120}"
                            else-> "Day Off"
                        }
                    }
                }
            }
        }
        when (getStringPref(name="planType", defaultValue="horner")) {
            "horner" -> {
                return when (index) {
                    !in 0..list.size -> {
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
                var newIndex = when(planSystem){
                    "pgh"->getIntPref(name="currentDayIndex", defaultValue=0)
                    else->getIntPref(name="mcheyneCurrentDayIndex", defaultValue=0)
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