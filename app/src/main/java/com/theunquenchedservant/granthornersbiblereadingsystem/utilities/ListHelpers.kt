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
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS

object ListHelpers {
    fun setVisibilities(binding: FragmentHomeBinding){
        changeVisibility(binding.cardList1, false)
        changeVisibility(binding.cardList2, false)
        changeVisibility(binding.cardList3, false)
        changeVisibility(binding.cardList4, false)
        changeVisibility(binding.cardList5, false)
        changeVisibility(binding.cardList6, false)
        changeVisibility(binding.cardList7, false)
        changeVisibility(binding.cardList8, false)
        changeVisibility(binding.cardList9, false)
        changeVisibility(binding.cardList10, false)
    }

    fun hideOthers(cardList: CardView?, binding: FragmentHomeBinding){
        changeVisibility(binding.cardList1, cardList == binding.cardList1.root)
        changeVisibility(binding.cardList2, cardList == binding.cardList2.root)
        changeVisibility(binding.cardList3, cardList == binding.cardList3.root)
        changeVisibility(binding.cardList4, cardList == binding.cardList4.root)
        changeVisibility(binding.cardList5, cardList == binding.cardList5.root)
        changeVisibility(binding.cardList6, cardList == binding.cardList6.root)
        changeVisibility(binding.cardList7, cardList == binding.cardList7.root)
        changeVisibility(binding.cardList8, cardList == binding.cardList8.root)
        changeVisibility(binding.cardList9, cardList == binding.cardList9.root)
        changeVisibility(binding.cardList10, cardList == binding.cardList10.root)
    }

    fun listSwitcher(cardList: View, listDone: Int, material_button: Button){
        val enabled = if(SharedPref.getBoolPref("darkMode")){
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
    fun resetDaily(){
        val isLogged = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        var resetStreak  = false
        var resetCurrent = false
        val vacation = SharedPref.getBoolPref("vacationMode")
        when (getIntPref("dailyStreak")) {
            1 -> {
                MainActivity.log("DAILY CHECK - daily streak set to 0")
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
        for(i in 1..10){
            when(getIntPref("lists${i}DoneDaily")){
                1->{
                    setIntPref("lists${i}DoneDaily", 0)
                }
            }
            when(getIntPref("list${i}Done")){
                1 -> {
                    resetList("list$i", "list${i}Done")
                }
            }
        }
        setIntPref("listsDone", 0)
        if(isLogged != null) {
            val data = mutableMapOf<String, Any>()
            for (i in 1..10){
                data["list$i"] = getIntPref("list$i")
                data["list${i}Done"] = getIntPref("list${i}Done")
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
        SharedPref.increaseIntPref(listName, 1)
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
                if(fromFirebase){
                    updateFS(listName, 0)
                }
                setIntPref(listName, 0)
                list[0]
            }
            else -> {
                setIntPref(listName, number)
                list[number]
            }
        }
    }
}