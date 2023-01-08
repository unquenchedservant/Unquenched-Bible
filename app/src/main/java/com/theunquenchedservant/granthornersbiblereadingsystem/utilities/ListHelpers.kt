package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
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
import com.theunquenchedservant.granthornersbiblereadingsystem.BuildConfig
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.CardviewsBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.isLeapDay
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import java.util.*

object ListHelpers {

    fun createUpdateAlert(context: Context){
        val context = App.applicationContext()
        traceLog(file="ListHelpers.kt", function="createUpdateAlert()")
        val currentVersion = BuildConfig.VERSION_CODE
        debugLog("currentVersion $currentVersion")
        if(getIntPref("versionNumber") < currentVersion){
            val builder = AlertDialog.Builder(context)
            builder.setPositiveButton(R.string.ok) { dialog, _ ->
                setIntPref(name = "versionNumber", value = currentVersion, updateFS = true)
                dialog.dismiss()
            }
            builder.setNeutralButton(context.resources.getString(R.string.moreInfo)) { dialog, _ ->
                setIntPref(name = "versionNumber", value = currentVersion, updateFS = true)
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://changelog.unquenched.bible/"))
                try {
                    startActivity(context, i, null)
                }catch(e: ActivityNotFoundException){
                    Toast.makeText(context, "No browser installed", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
            builder.setTitle(App.applicationContext().resources.getString(R.string.title_new_update, BuildConfig.VERSION_NAME))
            builder.setMessage(
                    "* Added some various things along the backend to make the app run smoother \n" +
                    "* Finally fixed an issue with the reading lists not syncing well between multiple devices. \n"+
                            "Fixed an issue where the reading lists were not getting fully reset each night \n"+
                            "For more information go to https://changelog.unquenched.bible/ or click 'More Info' below!"
            )
            builder.create().show()
        }
        else if (getIntPref(name = "versionNumber") < 70) {
            val builder = AlertDialog.Builder(context)
            builder.setPositiveButton(R.string.ok) { dialog, _ ->
                setIntPref(name = "versionNumber", value = currentVersion, updateFS = true)
                dialog.dismiss()
            }
            builder.setNeutralButton(context.resources.getString(R.string.moreInfo)) { dialog, _ ->
                setIntPref(name = "versionNumber", value = currentVersion, updateFS = true)
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.unquenched.bible/2021/01/23/announcing-unquenched-bible-or-the-professor-grant-horner-bible-reading-system-app-version-2-0/"))
                try {
                    startActivity(context, i, null)
                }catch(e: ActivityNotFoundException){
                    Toast.makeText(context, "No browser installed", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
            builder.setTitle(context.resources.getString(R.string.title_new_update, BuildConfig.VERSION_NAME))
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
    fun setVisibilities(binding: FragmentHomeBinding? = null, isMcheyne: Boolean=false){
        traceLog(file="ListHelpers.kt", function="setVisibilities()")
        if(!isMcheyne) {
            changeVisibility(binding!!.pgh1, isCardView=false)
            changeVisibility(binding.pgh2, isCardView=false)
            changeVisibility(binding.pgh3, isCardView=false)
            changeVisibility(binding.pgh4, isCardView=false)
            changeVisibility(binding.pgh5, isCardView=false)
            changeVisibility(binding.pgh6, isCardView=false)
            changeVisibility(binding.pgh7, isCardView=false)
            changeVisibility(binding.pgh8, isCardView=false)
            changeVisibility(binding.pgh9, isCardView=false)
            changeVisibility(binding.pgh10, isCardView=false)
        }else{
            changeVisibility(binding!!.mcheyneList1, isCardView=false)
            changeVisibility(binding.mcheyneList2, isCardView=false)
            changeVisibility(binding.mcheyneList3, isCardView=false)
            changeVisibility(binding.mcheyneList4, isCardView=false)
        }
    }
    fun initList(cardList: CardviewsBinding, listTitle:String){
        traceLog(file="ListHelpers.kt", function="initList()")
        val context = App.applicationContext()
        val backgroundColor: Int
        val emphColor:Int
        if(getBoolPref("darkMode")){
            backgroundColor = getColor(context, R.color.buttonBackgroundDark)
            emphColor = getColor(context, R.color.unquenchedEmphDark)
        }else{
            backgroundColor = getColor(context, R.color.buttonBackgroundDark)
            emphColor = getColor(context, R.color.unquenchedOrange)
        }
        cardList.listTitle.text = listTitle
        //cardList.listReading.text = context.resources.getText(R.string.loading)
        cardList.root.isClickable=false
        cardList.root.setCardBackgroundColor(backgroundColor)
        cardList.listReading.setTextColor(emphColor)
        cardList.lineSeparator.setBackgroundColor(emphColor)
    }
    fun updateButton(listsDone:Int, materialButton:Button, maxDone: Int, limitNumber:Int){
        traceLog(file="ListHelpers.kt", function="updateButton()")
        val context = App.applicationContext()
        val allDoneBackgroundColor: String
        val backgroundColor:String
        if (getBoolPref(name = "darkMode", defaultValue = true)) {
            allDoneBackgroundColor = context.resources.getString(R.string.done_btn_background_color_dark)
            backgroundColor = context.resources.getString(R.string.btn_background_color_dark)
        }else{
            allDoneBackgroundColor = context.resources.getString(R.string.done_btn_background_color)
            backgroundColor = context.resources.getString(R.string.btn_background_color)
        }
        debugLog("THIS IS THE LISTS DONE ${listsDone} this is limitNumber ${limitNumber}")
        when (listsDone) {
            maxDone -> {
                materialButton.setText(R.string.done)
                materialButton.isEnabled = true
                materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#$allDoneBackgroundColor"))
                materialButton.backgroundTintMode = PorterDuff.Mode.ADD
            }
            in 1..limitNumber -> {
                debugLog("when is working properly")
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
            0 -> {
                materialButton.setText(R.string.not_done)
                materialButton.isEnabled = true
                materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#$backgroundColor"))
            }

        }
    }
    fun hideOthers(cardList: CardView?, binding: FragmentHomeBinding? = null, isMcheyne:Boolean=false){
        traceLog(file="ListHelpers.kt", function="hideOthers()")
        if(!isMcheyne){
            changeVisibility(binding!!.pgh1, isCardView=cardList == binding.pgh1.root)
            changeVisibility(binding.pgh2, isCardView=cardList == binding.pgh2.root)
            changeVisibility(binding.pgh3, isCardView=cardList == binding.pgh3.root)
            changeVisibility(binding.pgh4, isCardView=cardList == binding.pgh4.root)
            changeVisibility(binding.pgh5, isCardView=cardList == binding.pgh5.root)
            changeVisibility(binding.pgh6, isCardView=cardList == binding.pgh6.root)
            changeVisibility(binding.pgh7, isCardView=cardList == binding.pgh7.root)
            changeVisibility(binding.pgh8, isCardView=cardList == binding.pgh8.root)
            changeVisibility(binding.pgh9, isCardView=cardList == binding.pgh9.root)
            changeVisibility(binding.pgh10, isCardView=cardList == binding.pgh10.root)
        }else{
            changeVisibility(binding!!.mcheyneList1, isCardView=cardList == binding.mcheyneList1.root)
            changeVisibility(binding.mcheyneList2, isCardView=cardList == binding.mcheyneList2.root)
            changeVisibility(binding.mcheyneList3, isCardView=cardList == binding.mcheyneList3.root)
            changeVisibility(binding.mcheyneList4, isCardView=cardList == binding.mcheyneList4.root)
        }
    }

    fun listSwitcher(cardList: View, listDone: Boolean, material_button: Button){
        traceLog(file="ListHelpers.kt", function="listSwitcher()")
        val enabled = if(getBoolPref(name="darkMode", defaultValue=true)){
            getColor(App.applicationContext(), android.R.color.background_dark)
        }else{
            getColor(App.applicationContext(), android.R.color.background_light)
        }
        val disabled = Color.parseColor("#00383838")
        cardList as CardView
        when(listDone){
            false -> { cardList.isEnabled = true; cardList.setCardBackgroundColor(enabled) }
            true -> { material_button.setText(R.string.btn_mark_remaining); cardList.isEnabled = false; cardList.setCardBackgroundColor(disabled) }
        }
    }
    fun resetDaily(context:Context): Task<DocumentSnapshot> {
        traceLog(file="ListHelpers.kt", function="resetDaily()")
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
                    val listStart = if(planSystem=="pgh") "pgh" else "mcheyne"
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
                                true->{}
                            }
                        }
                    }
                    for(i in 1..doneMax){
                        if(planType == "horner") {
                            when (getBoolPref(name = "$listStart${i}Done")) {
                                true -> {
                                    data["${listStart}${i}Index"] = setIntPref("${listStart}${i}Index", extractIntPref(currentData, "$listStart${i}Index") + 1)
                                    data["$listStart${i}Done"] = setBoolPref("$listStart${i}Done", false)
                                }
                                false -> {}

                            }
                        }else if(planType == "numerical"){
                            data["${listStart}${i}Done"] = setBoolPref("$listStart${i}Done", false)
                        }
                    }
                    if(planType== "numerical" && resetStreak) {
                        if(planSystem=="pgh") {
                            data["pghIndex"] = setIntPref("pghIndex", extractIntPref(currentData,"pghIndex") + 1)
                        }else{
                            data["mcheyneIndex"] = setIntPref("mcheyneIndex", extractIntPref(currentData,"mcheyneIndex") + 1)
                        }
                    }
                    data["pghDone"] = setIntPref("pghDone", 0)
                    data["mcheyneDone"] = setIntPref("mcheyneDone", 0)
                        Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).update(data)
                                .addOnSuccessListener {
                                    debugLog("Lists reset successfully")
                                }
                                .addOnFailureListener { error->
                                    debugLog("FAILURE WRITING TO FIRESTORE $error")
                                }
                }
                .addOnFailureListener {
                    val error = it
                    debugLog("FAILURE WRITING TO FIRESTORE $error")
                    Toast.makeText(context, "Unable to reset lists, please try again", Toast.LENGTH_LONG).show()
                }

    }
    fun isAdvanceable(maxDone:Int):Boolean{
        traceLog(file="ListHelpers.kt", function="isAdvanceable()")
        val planType = getStringPref(name="planType", defaultValue="horner")
        val listsDone = if(getStringPref("planSystem") == "pgh") "pghDone" else "mcheyneDone"
        return getIntPref(listsDone) == maxDone && (planType == "horner" || planType == "numerical")
    }
    fun isHorner():Boolean{
        traceLog(file="ListHelpers.kt", function="isHorner()")
        return getStringPref(name="planType", defaultValue = "horner") == "horner"
    }

    fun isLoggedIn():Boolean{
        traceLog(file="ListHelpers.kt", function="isLoggedIn()")
        return Firebase.auth.currentUser != null
    }
    fun isPsalm(cardView: CardviewsBinding, binding:FragmentHomeBinding, psalms:Boolean):Boolean{
        traceLog(file="ListHelpers.kt", function="isPsalm()")
        return cardView.root == binding.pgh6.root && psalms
    }
    fun isDayOff():Boolean{
        traceLog(file="ListHelpers.kt", function="isDayOff()")
        return getStringPref(name="planType", defaultValue="horner") == "calendar" && isLeapDay()
    }
    fun getChapter(list:Array<String>, listName:String):String{
        traceLog(file="ListHelpers.kt", function="getChapter()")
        return when (getStringPref(name = "planType", defaultValue = "horner")) {
            "horner" -> list[getIntPref("${listName}Index")]
            "numerical" -> {
                var index = getIntPref(name = "pghIndex", defaultValue = 0)
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
        traceLog(file="ListHelpers.kt", function="changeVisibility()")
        if(isCardView){
            cardList.listButtons.visibility = View.VISIBLE
            cardList.listReading.visibility = View.GONE
        }else {
            cardList.listButtons.visibility = View.GONE
            cardList.listReading.visibility = View.VISIBLE
        }
    }
}