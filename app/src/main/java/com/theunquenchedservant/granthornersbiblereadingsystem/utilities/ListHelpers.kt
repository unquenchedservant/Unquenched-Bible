package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.graphics.Color
import android.view.View
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.CardviewsBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeMcheyneBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS

object ListHelpers {
    fun setVisibilities(binding: FragmentHomeBinding? = null, bindingMCheyne: FragmentHomeMcheyneBinding? = null, isMcheyne: Boolean=false){
        if(!isMcheyne) {
            changeVisibility(binding!!.cardList1, false)
            changeVisibility(binding.cardList2, false)
            changeVisibility(binding.cardList3, false)
            changeVisibility(binding.cardList4, false)
            changeVisibility(binding.cardList5, false)
            changeVisibility(binding.cardList6, false)
            changeVisibility(binding.cardList7, false)
            changeVisibility(binding.cardList8, false)
            changeVisibility(binding.cardList9, false)
            changeVisibility(binding.cardList10, false)
        }else{
            changeVisibility(bindingMCheyne!!.cardList1, false)
            changeVisibility(bindingMCheyne.cardList2, false)
            changeVisibility(bindingMCheyne.cardList3, false)
            changeVisibility(bindingMCheyne.cardList4, false)
        }
    }

    fun hideOthers(cardList: CardView?, binding: FragmentHomeBinding? = null, bindingMCheyne: FragmentHomeMcheyneBinding? = null, isMcheyne:Boolean=false){
        if(!isMcheyne){
            changeVisibility(binding!!.cardList1, cardList == binding.cardList1.root)
            changeVisibility(binding.cardList2, cardList == binding.cardList2.root)
            changeVisibility(binding.cardList3, cardList == binding.cardList3.root)
            changeVisibility(binding.cardList4, cardList == binding.cardList4.root)
            changeVisibility(binding.cardList5, cardList == binding.cardList5.root)
            changeVisibility(binding.cardList6, cardList == binding.cardList6.root)
            changeVisibility(binding.cardList7, cardList == binding.cardList7.root)
            changeVisibility(binding.cardList8, cardList == binding.cardList8.root)
            changeVisibility(binding.cardList9, cardList == binding.cardList9.root)
            changeVisibility(binding.cardList10, cardList == binding.cardList10.root)
        }else{
            changeVisibility(bindingMCheyne!!.cardList1, cardList == bindingMCheyne.cardList1.root)
            changeVisibility(bindingMCheyne.cardList2, cardList == bindingMCheyne.cardList2.root)
            changeVisibility(bindingMCheyne.cardList3, cardList == bindingMCheyne.cardList3.root)
            changeVisibility(bindingMCheyne.cardList4, cardList == bindingMCheyne.cardList4.root)
        }
    }

    fun listSwitcher(cardList: View, listDone: Int, material_button: Button){
        val enabled = if(SharedPref.getBoolPref("darkMode", true)){
            getColor(App.applicationContext(), android.R.color.background_dark)
        }else{
            getColor(App.applicationContext(), android.R.color.background_light)
        }
        val disabled = Color.parseColor("#00383838")
        cardList as CardView
        when(listDone){
            0 -> { cardList.isEnabled = true; cardList.setCardBackgroundColor(enabled) }
            1-> { material_button.setText(R.string.btn_mark_remaining); cardList.isEnabled = false; cardList.setCardBackgroundColor(disabled) }
        }
    }
    fun resetDaily(planSystem:String = ""){
        val doneMax = when(planSystem){
            "pgh"->10
            "mcheyne"->4
            else->10
        }
        val prefix = when(planSystem){
            "pgh"->""
            "mcheyne"->"mcheyne_"
            else->""
        }
        val planType = getStringPref("planType", "horner")
        val isLogged = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        var resetStreak  = false
        var resetCurrent = false
        val vacation = SharedPref.getBoolPref("vacationMode")
        when (getIntPref("dailyStreak")) {
            1 -> {
                resetStreak = true
            }
            0 -> {
                when (vacation) {
                    false -> {
                        if (!dates.checkDate("both", false))
                            resetCurrent = true
                    }
                }
            }
        }
        for(i in 1..doneMax){
            if(planType == "horner") {
                when (getIntPref("${prefix}list${i}DoneDaily")) {
                    1 -> {
                        setIntPref("${prefix}list${i}DoneDaily", 0)
                    }
                }
                when (getIntPref("${prefix}list${i}Done")) {
                    1 -> {
                        resetList("${prefix}list$i", "${prefix}list${i}Done")
                    }
                }
            }else if(planType == "numerical"){
                setIntPref("${prefix}list${i}Done", 0)
            }
        }
        if(planType== "numerical" && resetStreak) {
            increaseIntPref("currentDayIndex", 1)
        }
        setIntPref("listsDone", 0)
        if(isLogged != null) {
            val data = mutableMapOf<String, Any>()
            for (i in 1..10){
                if(planType == "horner") {
                    data["${prefix}list$i"] = getIntPref("${prefix}list$i")
                }
                data["${prefix}list${i}Done"] = getIntPref("${prefix}list${i}Done")
            }
            if (planType=="numerical" && resetStreak){
                data["currentDayIndex"] = getIntPref("currentDayIndex")
            }
            data["listsDone"] = 0
            if(resetCurrent) {
                data["currentStreak"] = 0
            }else if(resetStreak) {
                data["dailyStreak"] = 0
            }
            db.collection("main").document(isLogged.uid).update(data)
        }
    }

    private fun resetList(listName: String, listNameDone: String){
        MainActivity.log("$listName is now set to ${getIntPref(listName)}")
        increaseIntPref(listName, 1)
        MainActivity.log("$listName index is now ${getIntPref(listName)}")
        setIntPref(listNameDone, 0)
        MainActivity.log("$listNameDone set to 0")
    }

    fun changeVisibility(cardList: CardviewsBinding, isCardView: Boolean){
        if(isCardView){
            cardList.listButtons.visibility = View.VISIBLE
            cardList.listReading.visibility = View.GONE
        }else {
            cardList.listButtons.visibility = View.GONE
            cardList.listReading.visibility = View.VISIBLE
        }
    }
    fun getListNumber(result: Map<String, Any>?, listName: String, listId: Int, fromFirebase: Boolean): String{
        val number = if(result != null){
            (result[listName] as Long).toInt()
        }else{
            getIntPref(listName)
        }
        val list = App.applicationContext().resources.getStringArray(listId)
        return when(number){
            list.size -> {
                setIntPref(listName, 0, true)
                list[0]
            }
            else -> {
                setIntPref(listName, number, true)
                list[number]
            }
        }
    }
}