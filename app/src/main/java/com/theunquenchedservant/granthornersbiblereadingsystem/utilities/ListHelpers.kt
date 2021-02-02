package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.provider.Settings.Global.getString
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.CardviewsBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeMcheyneBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.isLeapDay
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import java.util.*

object ListHelpers {

    fun createUpdateAlert(context: Context){
        if(getIntPref(name="versionNumber") == 70){
            val builder = AlertDialog.Builder(context)
            builder.setPositiveButton(R.string.ok) { dialog, _ ->
                setIntPref(name = "versionNumber", value = 70, updateFS = true)
                dialog.dismiss()
            }
            builder.setTitle(R.string.title_new_update)
            builder.setMessage(
                    "[FIXED] An issue with Calendar and Numerical methods.\n\n" +
                            "[FIXED] An issue with Bible statistics not being added correctly.\n\n" +
                            "[FIXED] An issue where notifications weren't getting properly cleared."
            )
        }
        if (getIntPref(name = "versionNumber") < 70) {
            val builder = AlertDialog.Builder(context)
            builder.setPositiveButton(R.string.ok) { dialog, _ ->
                setIntPref(name = "versionNumber", value = 70, updateFS = true)
                dialog.dismiss()
            }
            builder.setNeutralButton(context.resources.getString(R.string.moreInfo)) { dialog, _ ->
                setIntPref(name = "versionNumber", value = 70, updateFS = true)
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.unquenched.bible/2021/01/23/announcing-unquenched-bible-or-the-professor-grant-horner-bible-reading-system-app-version-2-0/"))
                try {
                    startActivity(context, i, null)
                }catch(e: ActivityNotFoundException){
                    Toast.makeText(App.applicationContext(), "No browser installed", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
            builder.setTitle(R.string.title_new_update)
            builder.setMessage(
                    "[ADDED] M'Cheyne Bible Reading Calendar\n\n" +
                            "[ADDED] Weekend Mode. Take Saturday and Sunday off.\n\n" +
                            "[ADDED] Three different methods for your reading plan: Horner, Numerical, and Calendar.\n\n" +
                            "[ADDED] Grace period for your streak. If you forgot to check your reading as done, you have one day before permanently losing your streak!\n\n" +
                            "[ADDED] New Statistics for amount of the Bible read\n\n" +
                            "[UPGRADED] NEW NAME! The Professor Grant Horner Bible Reading App is now Unquenched Bible\n\n" +
                            "[UPDATED] New sign in screen with the option to log in with your email and password\n\n" +
                            "Thank you for your continued use of the app! To find out more about these changes, press 'More Info' below!"
            )
            builder.create().show()
        }
    }
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
    fun initList(cardList: CardviewsBinding, listTitle:String){
        val backgroundColor: Int
        val emphColor:Int
        if(getBoolPref("darkMode")){
            backgroundColor = getColor(App.applicationContext(), R.color.buttonBackgroundDark)
            emphColor = getColor(App.applicationContext(), R.color.unquenchedEmphDark)
        }else{
            backgroundColor = getColor(App.applicationContext(), R.color.buttonBackgroundDark)
            emphColor = getColor(App.applicationContext(), R.color.unquenchedOrange)
        }
        cardList.listTitle.text = listTitle
        cardList.listReading.text = App.applicationContext().resources.getText(R.string.loading)
        cardList.root.isClickable=false
        cardList.root.setCardBackgroundColor(backgroundColor)
        cardList.listReading.setTextColor(emphColor)
        cardList.lineSeparator.setBackgroundColor(emphColor)
    }
    fun updateButton(listsDone:Int, materialButton:Button, maxDone: Int, limitNumber:Int){
        val allDoneBackgroundColor: String
        val backgroundColor:String
        if (getBoolPref(name = "darkMode", defaultValue = true)) {
            allDoneBackgroundColor = App.applicationContext().resources.getString(R.string.done_btn_background_color_dark)
            backgroundColor = App.applicationContext().resources.getString(R.string.btn_background_color_dark)
        }else{
            allDoneBackgroundColor = App.applicationContext().resources.getString(R.string.done_btn_background_color)
            backgroundColor = App.applicationContext().resources.getString(R.string.btn_background_color)
        }
        when (listsDone) {
            maxDone -> {
                materialButton.setText(R.string.done)
                materialButton.isEnabled = true
                materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#$allDoneBackgroundColor"))
                materialButton.backgroundTintMode = PorterDuff.Mode.ADD
            }
            0 -> {
                materialButton.setText(R.string.not_done)
                materialButton.isEnabled = true
                materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#$backgroundColor"))
            }
            in 1..limitNumber -> {
                materialButton.setText(R.string.btn_mark_remaining)
                materialButton.isEnabled = true
                val opacity = if (listsDone < 5) {
                    100 - (listsDone * 5)
                } else {
                    100 - ((listsDone * 5) - 5)
                }
                materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#${opacity}$backgroundColor"))
                materialButton.backgroundTintMode = PorterDuff.Mode.ADD
            }
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
        val listStart = if(planSystem=="pgh") "list" else "mcheyneList"
        val isLogged = Firebase.auth.currentUser
        val db = Firebase.firestore
        var data = mutableMapOf<String, Any>()
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
                when (getIntPref(name = "${listStart}${i}DoneDaily")) {
                    1 -> data["${listStart}${i}DoneDaily"] = setIntPref(name = "${listStart}${i}DoneDaily", value = 0)
                }
                when (getIntPref(name = "$listStart${i}Done")) {
                    1 -> data = resetList(listName = "$listStart${i}", listNameDone = "$listStart${i}Done", data)
                }
            }else if(planType == "numerical"){
                data["${listStart}${i}Done"] = setIntPref(name="${listStart}${i}Done", value=0)
            }
        }
        if(planType== "numerical" && resetStreak) {
            if(planSystem=="pgh") {
                data["currentDayIndex"] = increaseIntPref(name="currentDayIndex", value=1)
            }else{
                data["mcheyneCurrentDayIndex"] = increaseIntPref(name="mcheyneCurrentDayIndex", value=1)
            }
        }
        data["${listStart}sDone"] = setIntPref(name="${listStart}sDone", value=0)
        if(isLogged != null) {
            db.collection("main").document(isLogged.uid).update(data)
        }
    }
    fun isAdvanceable(maxDone:Int):Boolean{
        val planType = getStringPref(name="planType", defaultValue="horner")
        val listsDone = if(getStringPref("planSystem") == "pgh") "listsDone" else "mcheyneListsDone"
        return getIntPref(listsDone) == maxDone && (planType == "horner" || planType == "numerical")
    }
    fun isHorner():Boolean{
        return getStringPref(name="planType", defaultValue = "horner") == "horner"
    }

    fun isLoggedIn():Boolean{
        return Firebase.auth.currentUser != null
    }
    fun isPsalm(cardView: CardviewsBinding, binding:FragmentHomeBinding, psalms:Boolean):Boolean{
        return cardView.root == binding.cardList6.root && psalms
    }
    fun isDayOff():Boolean{
        return getStringPref(name="planType", defaultValue="horner") == "calendar" && isLeapDay()
    }
    fun getChapter(list:Array<String>, listName:String):String{
        return when (getStringPref(name = "planType", defaultValue = "horner")) {
            "horner" -> list[getIntPref(listName)]
            "numerical" -> {
                var index = getIntPref(name = "currentDayIndex", defaultValue = 0)
                while (index >= list.size) {
                    index -= list.size
                }
                list[index]
            }
            "calendar" -> {
                var index = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                while (index >= list.size) {
                    index -= list.size
                }
                list[index]
            }
            else -> list[getIntPref(listName)]
        }
    }
    private fun resetList(listName: String, listNameDone: String, data:MutableMap<String, Any>): MutableMap<String, Any>{
        data[listName] = increaseIntPref(listName, value=1)
        data[listNameDone] = setIntPref(listNameDone, value=0)
        return data
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