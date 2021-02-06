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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.CardviewsBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeMcheyneBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.isLeapDay
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import java.util.*

object ListHelpers {

    fun createUpdateAlert(context: Context){
        if(getIntPref(name="versionNumber") in 70..74){
            val builder = AlertDialog.Builder(context)
            builder.setPositiveButton(R.string.ok) { dialog, _ ->
                setIntPref(name = "versionNumber", value = 74, updateFS = true)
                dialog.dismiss()
            }
            builder.setTitle(R.string.title_new_update)
            builder.setMessage(
                    "[FIXED] Issue where when pressing individual list \"Done\" button the screen wouldn't change\n\n"+
                            "[FIXED] Issue where the app wasn't advancing the lists correctly.\n\n"
            )
        }
        if (getIntPref(name = "versionNumber") < 70) {
            val builder = AlertDialog.Builder(context)
            builder.setPositiveButton(R.string.ok) { dialog, _ ->
                setIntPref(name = "versionNumber", value = 73, updateFS = true)
                dialog.dismiss()
            }
            builder.setNeutralButton(context.resources.getString(R.string.moreInfo)) { dialog, _ ->
                setIntPref(name = "versionNumber", value = 73, updateFS = true)
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
    fun resetDaily(context:Context): Task<DocumentSnapshot> {
       return Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    val currentData = it.data
                    val data = mutableMapOf<String, Any>()
                    val planSystem  = extractStringPref(currentData,"planSystem", defaultValue="pgh")
                    val doneMax = when(planSystem){
                        "pgh"->10
                        "mcheyne"->4
                        else->10
                    }
                    val planType = extractStringPref(currentData,"planType", defaultValue="horner")
                    val listStart = if(planSystem=="pgh") "list" else "mcheyneList"
                    var resetStreak  = false
                    val vacation = extractBoolPref(currentData,"vacationMode")
                    when (extractIntPref(currentData,"dailyStreak")) {
                        1 -> {
                            data["dailyStreak"] = setIntPref("dailyStreak", 0)
                            resetStreak = true
                        }
                        0 -> {
                            when (vacation) {
                                false -> {
                                    if (!Dates.checkDate(extractStringPref(currentData, "dateChecked"), option="both", fullMonth=false))
                                        data["currentStreak"] = setIntPref("currentStreak", 0)
                                }
                            }
                        }
                    }
                    for(i in 1..doneMax){
                        if(planType == "horner") {
                            when (extractIntPref(currentData, "${listStart}${i}DoneDaily")) {
                                1 -> data["${listStart}${i}DoneDaily"] = setIntPref("$listStart${i}DoneDaily", 0)
                            }
                            when (getIntPref(name = "$listStart${i}Done")) {
                                1 -> {
                                    data["${listStart}$i"] = setIntPref("$listStart$i", extractIntPref(currentData, "$listStart$i") + 1)
                                    data["$listStart${i}Done"] = setIntPref("$listStart${i}Done", 0)
                                }

                            }
                        }else if(planType == "numerical"){
                            data["${listStart}${i}Done"] = setIntPref("$listStart${i}Done", 0)
                        }
                    }
                    if(planType== "numerical" && resetStreak) {
                        if(planSystem=="pgh") {
                            data["currentDayIndex"] = setIntPref("currentDayIndex", extractIntPref(currentData,"currentDayIndex") + 1)
                        }else{
                            data["mcheyneCurrentDayIndex"] = setIntPref("mcheyneCurrentDayIndex", extractIntPref(currentData,"mcheyneCurrentDayIndex") + 1)
                        }
                    }
                    data["${listStart}sDone"] = setIntPref("${listStart}sDone", 0)
                        Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).update(data)
                                .addOnSuccessListener {
                                    log("Lists reset successfully")
                                }
                                .addOnFailureListener { error->
                                    log("FAILURE WRITING TO FIRESTORE $error")
                                }
                }
                .addOnFailureListener {
                    val error = it
                    log("FAILURE WRITING TO FIRESTORE $error")
                    Toast.makeText(context, "Unable to reset lists, please try again", Toast.LENGTH_LONG).show()
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