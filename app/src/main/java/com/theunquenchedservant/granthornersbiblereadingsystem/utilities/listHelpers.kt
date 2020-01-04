package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.graphics.Color
import android.view.View
import android.widget.Button
import androidx.cardview.widget.CardView
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.intPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS
import kotlinx.android.synthetic.main.cardviews.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*

object listHelpers {
    fun setVisibilities(view: View){
        changeVisibility(view.cardList1, false)
        changeVisibility(view.cardList2, false)
        changeVisibility(view.cardList3, false)
        changeVisibility(view.cardList4, false)
        changeVisibility(view.cardList5, false)
        changeVisibility(view.cardList6, false)
        changeVisibility(view.cardList7, false)
        changeVisibility(view.cardList8, false)
        changeVisibility(view.cardList9, false)
        changeVisibility(view.cardList10, false)
    }
    fun hideOthers(cardList: CardView?, view: View){
        changeVisibility(view.cardList1, cardList == view.cardList1)
        changeVisibility(view.cardList2, cardList == view.cardList2)
        changeVisibility(view.cardList3, cardList == view.cardList3)
        changeVisibility(view.cardList4, cardList == view.cardList4)
        changeVisibility(view.cardList5, cardList == view.cardList5)
        changeVisibility(view.cardList6, cardList == view.cardList6)
        changeVisibility(view.cardList7, cardList == view.cardList7)
        changeVisibility(view.cardList8, cardList == view.cardList8)
        changeVisibility(view.cardList9, cardList == view.cardList9)
        changeVisibility(view.cardList10, cardList == view.cardList10)
    }

    fun listSwitcher(cardList: View, listDone: Int, material_button: Button){
        val enabled = Color.parseColor("#383838")
        val disabled = Color.parseColor("#00383838")
        cardList as CardView
        when(listDone){
            0 -> { cardList.isEnabled = true; cardList.setCardBackgroundColor(enabled) }
            1-> { material_button.setText(R.string.markRemaining); cardList.isEnabled = false; cardList.setCardBackgroundColor(disabled) }
        }
    }

    fun setTitles(view: View){
        val resources = App.applicationContext().resources
        view.cardList1.list_title.text = resources.getString(R.string.l1)
        view.cardList2.list_title.text = resources.getString(R.string.l2)
        view.cardList3.list_title.text = resources.getString(R.string.l3)
        view.cardList4.list_title.text = resources.getString(R.string.l4)
        view.cardList5.list_title.text = resources.getString(R.string.l5)
        view.cardList6.list_title.text = resources.getString(R.string.l6)
        view.cardList7.list_title.text = resources.getString(R.string.l7)
        view.cardList8.list_title.text = resources.getString(R.string.l8)
        view.cardList9.list_title.text = resources.getString(R.string.l9)
        view.cardList10.list_title.text = resources.getString(R.string.l10)
    }
     fun changeVisibility(cardList: View, isCardView: Boolean){
        if(isCardView){
            cardList.list_buttons.visibility = View.VISIBLE
            cardList.list_reading.visibility = View.GONE
        }else {
            cardList.list_buttons.visibility = View.GONE
            cardList.list_reading.visibility = View.VISIBLE
        }
    }
    fun getListNumber(result: Map<String, Any>?, listName: String, listId: Int, fromFirebase: Boolean): String{
        val number = if(result != null){
            (result[listName] as Long).toInt()
        }else{
            log("NUMBER = ${intPref(listName, null)}")
            intPref(listName, null)
        }
        val list = App.applicationContext().resources.getStringArray(listId)
        return when(number){
            list.size -> {
                if(fromFirebase){
                    updateFS(listName, 0)
                }
                intPref(listName, 0)
                list[0]
            }
            else -> {
                log("THIS IS THE NUMBER $number")
                intPref(listName, number)
                list[number]
            }
        }
    }
}