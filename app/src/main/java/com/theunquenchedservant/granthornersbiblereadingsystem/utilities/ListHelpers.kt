package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.graphics.Color
import android.view.View
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.CardviewsBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeMcheyneBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref

object ListHelpers {
    fun setVisibilities(binding: FragmentHomeBinding? = null, bindingMCheyne: FragmentHomeMcheyneBinding? = null, isMcheyne: Boolean=false){
        if(!isMcheyne) {
            changeVisibility(binding!!.cardList1, isCardView=false)
            changeVisibility(binding.cardList2, isCardView=false)
            changeVisibility(binding.cardList3, isCardView=false)
            changeVisibility(binding.cardList4, isCardView=false)
            changeVisibility(binding.cardList5, isCardView=false)
            changeVisibility(binding.cardList6, isCardView=false)
            changeVisibility(binding.cardList7, isCardView=false)
            changeVisibility(binding.cardList8, isCardView=false)
            changeVisibility(binding.cardList9, isCardView=false)
            changeVisibility(binding.cardList10, isCardView=false)
        }else{
            changeVisibility(bindingMCheyne!!.cardList1, isCardView=false)
            changeVisibility(bindingMCheyne.cardList2, isCardView=false)
            changeVisibility(bindingMCheyne.cardList3, isCardView=false)
            changeVisibility(bindingMCheyne.cardList4, isCardView=false)
        }
    }

    fun hideOthers(cardList: CardView?, binding: FragmentHomeBinding? = null, bindingMCheyne: FragmentHomeMcheyneBinding? = null, isMcheyne:Boolean=false){
        if(!isMcheyne){
            changeVisibility(binding!!.cardList1, isCardView=cardList == binding.cardList1.root)
            changeVisibility(binding.cardList2, isCardView=cardList == binding.cardList2.root)
            changeVisibility(binding.cardList3, isCardView=cardList == binding.cardList3.root)
            changeVisibility(binding.cardList4, isCardView=cardList == binding.cardList4.root)
            changeVisibility(binding.cardList5, isCardView=cardList == binding.cardList5.root)
            changeVisibility(binding.cardList6, isCardView=cardList == binding.cardList6.root)
            changeVisibility(binding.cardList7, isCardView=cardList == binding.cardList7.root)
            changeVisibility(binding.cardList8, isCardView=cardList == binding.cardList8.root)
            changeVisibility(binding.cardList9, isCardView=cardList == binding.cardList9.root)
            changeVisibility(binding.cardList10, isCardView=cardList == binding.cardList10.root)
        }else{
            changeVisibility(bindingMCheyne!!.cardList1, isCardView=cardList == bindingMCheyne.cardList1.root)
            changeVisibility(bindingMCheyne.cardList2, isCardView=cardList == bindingMCheyne.cardList2.root)
            changeVisibility(bindingMCheyne.cardList3, isCardView=cardList == bindingMCheyne.cardList3.root)
            changeVisibility(bindingMCheyne.cardList4, isCardView=cardList == bindingMCheyne.cardList4.root)
        }
    }

    fun listSwitcher(cardList: View, listDone: Int, material_button: Button){
        val enabled = if(getBoolPref(name="darkMode", defaultValue=true)){
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
        val planSystem  = getStringPref(name="planSystem", defaultValue="pgh")
        val doneMax = when(planSystem){
            "pgh"->10
            "mcheyne"->4
            else->10
        }
        val planType = getStringPref(name="planType", defaultValue="horner")
        val isLogged = Firebase.auth.currentUser
        val db = Firebase.firestore
        val data = mutableMapOf<String, Any>()
        var resetStreak  = false
        val vacation = getBoolPref(name="vacationMode")
        when (getIntPref(name="dailyStreak")) {
            1 -> {
                data["dailyStreak"] = 0
                resetStreak = true
            }
            0 -> {
                when (vacation) {
                    false -> {
                        if (!Dates.checkDate(option="both", fullMonth=false))
                            data["currentStreak"] = 0
                    }
                }
            }
        }
        for(i in 1..doneMax){
            if(planType == "horner") {
                if(planSystem=="pgh"){
                    when(getIntPref(name="list${i}DoneDaily")){
                        1-> { setIntPref(name="list${i}DoneDaily", value=0); data["list${i}DoneDaily"]=0 }
                    }
                    when(getIntPref(name="list${i}Done")){
                        1-> resetList(listName="list${i}", listNameDone="list${i}Done")
                    }
                }else{
                    when(getIntPref(name="mcheyneList${i}DoneDaily")){
                        1-> { setIntPref(name="mcheyneList${i}DoneDaily", value=0); data["mcheyneList${i}DoneDaily"] = 0}
                    }
                    when(getIntPref(name="mcheyneList${i}Done")){
                        1-> resetList(listName="mcheyneList${i}", listNameDone="mcheyneList${i}Done")
                    }
                }
            }else if(planType == "numerical"){
                if(planSystem=="pgh"){
                    setIntPref(name="list${i}Done", value=0)
                    data["list${i}Done"] = 0
                }else{
                    setIntPref(name="mcheyneList${i}Done", value=0)
                    data["mcheyneList${i}Done"]
                }
            }
        }
        if(planType== "numerical" && resetStreak) {
            if(planSystem=="pgh") {
                data["currentDayIndex"] = increaseIntPref(name="currentDayIndex", value=1)
            }else{
                data["mcheyneCurrentDayIndex"] = increaseIntPref(name="mcheyneCurrentDayIndex", value=1)
            }
        }
        setIntPref(name="listsDone", value=0)
        data["listsDone"] = 0
        if(isLogged != null) {
            for (i in 1..doneMax) {
                if (planSystem == "pgh") {
                    if (planType == "horner") {
                        data["list$i"] = getIntPref(name="list$i")
                    }
                    data["list${i}Done"] = getIntPref(name="list${i}Done")
                }else{
                    if(planType == "horner"){
                        data["mcheyneList${i}"] = getIntPref(name="mcheyneList${i}")
                    }
                    data["mcheyneList${i}Done"] = getIntPref(name="mcheyneList${i}Done")
                }
            }
            db.collection("main").document(isLogged.uid).update(data)
        }
    }

    private fun resetList(listName: String, listNameDone: String){
        increaseIntPref(listName, value=1)
        setIntPref(listNameDone, value=0)
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
    fun getListNumber(result: Map<String, Any>?, listName: String, listId: Int): String{
        val number = if(result != null){
            (result[listName] as Long).toInt()
        }else{
            getIntPref(listName)
        }
        val list = App.applicationContext().resources.getStringArray(listId)
        return when(number){
            list.size -> {
                setIntPref(listName, value=0, updateFS=true)
                list[0]
            }
            else -> {
                setIntPref(listName, value=number, updateFS=true)
                list[number]
            }
        }
    }
}