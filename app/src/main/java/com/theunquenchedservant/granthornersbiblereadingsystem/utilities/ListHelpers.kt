package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.BuildConfig
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.CardviewsBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeMcheyneBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import java.util.*

class SingleList(val cardView:CardviewsBinding, val list:ListItem?, val title:String, val readingLists:ReadingLists, val activity:MainActivity, val isLoading:Boolean){

    init{
        if(isLoading){
            cardView.listTitle.text = title
            cardView.listReading.text = "Loading..."
            cardView.root.isClickable = false
            cardView.listButtons.isVisible = false
            cardView.root.setBackgroundColor(Color.parseColor("#00383838"))
            cardView.listReading.setTextColor(Color.parseColor("#9CB9D3"))
            cardView.root.isEnabled = false
            cardView.listButtons.setBackgroundColor(Color.parseColor("#00383838"))
        }else {
            val preferences = activity.preferences
            if (readingLists.isDayOff()) {
                cardView.listTitle.text = title
                cardView.listReading.text = "Day Off"
                cardView.root.isClickable = false
                cardView.root.setCardBackgroundColor(Color.parseColor("#00383838"))
                cardView.listReading.setTextColor(preferences.colors.emphColor)
                cardView.lineSeparator.setBackgroundColor(preferences.colors.emphColor)
                cardView.root.isEnabled = false
                cardView.listButtons.setBackgroundColor(Color.parseColor("#00383838"))
            } else {
                val backgroundColor = preferences.colors.buttonBackground
                val disabled = Color.parseColor("#00383838")
                cardView.listTitle.text = title
                cardView.listReading.text =
                    App.applicationContext().resources.getText(R.string.loading)
                cardView.root.isClickable = false
                cardView.root.setCardBackgroundColor(backgroundColor)
                cardView.listRead.setBackgroundColor(backgroundColor)
                cardView.listDone.setBackgroundColor(backgroundColor)
                cardView.listButtons.setBackgroundColor(backgroundColor)
                cardView.listRead.setTextColor(preferences.colors.emphColor)
                cardView.listDone.setTextColor(preferences.colors.emphColor)
                cardView.listReading.setTextColor(preferences.colors.textColor2)
                cardView.lineSeparator.setBackgroundColor(preferences.colors.textColor2)
                cardView.buttonSeparator.setBackgroundColor(preferences.colors.textColor2)
                cardView.listReading.text = getReading(list!!.listIndex, preferences)
                if (cardView.listReading.text == "Day Off") {
                    cardView.root.isEnabled = false
                    cardView.root.setCardBackgroundColor(disabled)
                    cardView.listButtons.setBackgroundColor(disabled)
                    preferences.list.addListsDone(1)
                } else {
                    createCardListener(preferences)
                }
            }
        }
    }
    fun createCardListener(preferences: Preferences){
        val listArray = activity.resources.getStringArray(list!!.listId)
        val context = activity
        val enabled = preferences.colors.buttonBackground
        if(!list.listDone){
            cardView.root.setOnClickListener {
                if(cardView.listButtons.isVisible) readingLists.listSwitcher(this) else{
                    readingLists.hideOthers(cardView)
                    cardView.listDone.setOnClickListener {
                        setVisibility(false)
                        list.markDone(single=true)
                        preferences.list.addListsDone(1)
                        cardView.root.setCardBackgroundColor(Color.parseColor("#00383838"))
                    }
                    cardView.listRead.setOnClickListener {
                        val bundle = if(list.listName == "list6" && preferences.settings.psalms){
                            bundleOf("chapter" to "no", "psalms" to true, "iteration" to 1)
                        }else{
                            bundleOf("chapter" to getChapter(listArray, list, preferences), "psalms" to false, "iteration" to 0)
                        }
                        activity.navController.navigate(R.id.navigation_scripture, bundle)
                    }
                }
            }
        }else if(preferences.settings.planType == "horner"){
            cardView.root.setOnLongClickListener{
                val builder = AlertDialog.Builder(context)
                builder.setPositiveButton(R.string.yes){ diag, _->
                    list.resetList(true)
                    preferences.list.subtractListsDone(1)
                    cardView.root.isEnabled = true
                    cardView.root.setBackgroundColor(enabled)
                    cardView.listButtons.setBackgroundColor(enabled)
                    diag.dismiss()
                    activity.navController.navigate(R.id.navigation_home)
                }
                builder.setNegativeButton(R.string.no){diag, _ -> diag.dismiss()}
                builder.setMessage(R.string.msg_reset_one)
                builder.setTitle(R.string.title_reset_list)
                builder.show()
                true
            }
        }
    }
    fun getChapter(list:Array<String>, listItem:ListItem, preferences:Preferences):String {
        traceLog(file = "ListHelpers.kt", function = "getChapter()")
        return when (preferences.settings.planType) {
            "horner" -> list[listItem.listIndex]
            "numerical" -> {
                var index = preferences.list.currentIndex
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
            else -> list[listItem.listIndex]
        }
    }

    fun getReading(listIndex: Int, preferences:Preferences):String{
        val listArray = App.applicationContext().resources.getStringArray(list!!.listId)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        if (list.listName == "list6") {
            if (preferences.settings.psalms) {
                return when (day) {
                    in 1..30 -> "$day, ${day + 30}, ${day + 60}, ${day + 90}, ${day + 120}"
                    else -> "Day Off"
                }
            }
        }
        when (preferences.settings.planType) {
            "horner" -> {
                return when (listIndex) {
                    listArray.size -> {
                        list.listIndex = 0
                        listArray[0]
                    }
                    else -> {
                        list.listIndex = listIndex
                        listArray[listIndex]
                    }
                }
            }
            "numerical" -> {
                var newIndex = preferences.list.currentIndex
                while(newIndex>=listArray.size){
                    newIndex -= listArray.size
                }
                return listArray[newIndex]
            }
            "calendar" -> {
                var newIndex = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 3
                val cal = Calendar.getInstance();
                if(cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365 && cal.get(Calendar.DAY_OF_YEAR) > 60){
                    newIndex -= 1
                }
                while (newIndex >= listArray.size) {
                    newIndex -= listArray.size
                }
                return listArray[newIndex]
            }
            else -> return listArray[listIndex]
        }
    }
    fun setVisibility(isVisible:Boolean){
        if(isVisible){
            cardView.listButtons.visibility = View.VISIBLE
            cardView.listReading.visibility = View.GONE
        }else{
            cardView.listButtons.visibility = View.GONE
            cardView.listReading.visibility = View.VISIBLE
        }
    }
}
class LoadingDone(val binding: FragmentHomeBinding?=null, val mcheyneBinding:FragmentHomeMcheyneBinding?=null, val readingLists: ReadingLists){
    val button = if (readingLists.isPGH) binding!!.materialButton else mcheyneBinding!!.materialButton
    init{
        button.setBackgroundColor(Color.parseColor("#00383838"))
        button.setTextColor(Color.parseColor("#9cb9d3"))
        button.text="Loading"

    }
}
class DoneButton(val binding:FragmentHomeBinding? = null, val mcheyneBinding:FragmentHomeMcheyneBinding? = null, val resources:Resources, val activity: MainActivity, val readingLists:ReadingLists){
    val preferences = activity.preferences
    val button = if(readingLists.isPGH) binding!!.materialButton else mcheyneBinding!!.materialButton
    init{
        if(readingLists.isDayOff()){
            button.setBackgroundColor(Color.parseColor("#00383838"))
            button.setTextColor(preferences.colors.textColor)
            button.text = "Day Off"
        }else {
            button.setBackgroundColor(preferences.colors.buttonBackground)
            button.setTextColor(preferences.colors.textColor)
            updateButton()
            createButtonListener()
        }
    }
    fun updateButton(){
        val allDoneBackgroundColor = preferences.colors.sAllDoneBackgroundColor
        val backgroundColor = preferences.colors.sBackgroundColor
        val limitNumber = preferences.list.maxDone - 1
        when (preferences.list.listsDone) {
            preferences.list.maxDone -> {
                button.setText(R.string.done)
                button.isEnabled = true
                button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#$allDoneBackgroundColor"))
                button.backgroundTintMode = PorterDuff.Mode.ADD
            }
            0 -> {
                button.setText(R.string.not_done)
                button.isEnabled = true
                button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#$backgroundColor"))
            }
            in 1..limitNumber -> {
                button.setText(R.string.btn_mark_remaining)
                button.isEnabled = true
                val opacity = if (preferences.list.listsDone < 5) {
                    100 - (preferences.list.listsDone * 5)
                } else {
                    100 - ((preferences.list.listsDone * 5) - 5)
                }
                button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#${opacity}$backgroundColor"))
                button.backgroundTintMode = PorterDuff.Mode.ADD
            }
        }
    }
    fun createButtonListener(){
        button.setOnClickListener{
            readingLists.hideOthers()
            preferences.list.markAll()
            val mNotificationManager = App.applicationContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(1)
            mNotificationManager.cancel(2)
            activity.navController.navigate(R.id.navigation_home)
        }
        if(readingLists.isAdvanceable(10)){
            button.setOnLongClickListener{
                val builder = AlertDialog.Builder(activity)
                builder.setPositiveButton(R.string.yes) { _, _ ->
                    preferences.list.resetList()
                    activity.navController.navigate(R.id.navigation_home)
                }
                builder.setNegativeButton(R.string.no) { diag, _ ->
                    diag.dismiss()
                }
                builder.setMessage(R.string.msg_reset_all)
                builder.setTitle(R.string.title_reset_lists)
                builder.show()
                true
            }
        }
    }
}
class ReadingLists (val homeBinding:FragmentHomeBinding?=null, val mcheyneBinding:FragmentHomeMcheyneBinding?=null, val resources: Resources, val activity: MainActivity, val isPGH:Boolean, val displayLoading:Boolean){
    val preferences = if(displayLoading) null else activity.preferences
    var list1:SingleList = if(isPGH)
        if(displayLoading) SingleList(homeBinding!!.cardList1, null,resources.getString(R.string.title_pgh_list1), this, activity, true) else  SingleList(homeBinding!!.cardList1, preferences!!.list.list1, resources.getString(R.string.title_pgh_list1), this, activity, false)
    else
        if(displayLoading) SingleList(mcheyneBinding!!.cardList1, null, resources.getString(R.string.title_mcheyne_list1), this, activity, true) else SingleList(mcheyneBinding!!.cardList1, preferences!!.list.list1, resources.getString(R.string.title_mcheyne_list1), this, activity, false)
    var list2:SingleList = if(isPGH)
        if(displayLoading) SingleList(homeBinding!!.cardList2, null,resources.getString(R.string.title_pgh_list2), this, activity, true) else SingleList(homeBinding!!.cardList2, preferences!!.list.list2, resources.getString(R.string.title_pgh_list2), this, activity, false)
    else
        if(displayLoading) SingleList(mcheyneBinding!!.cardList2, null,resources.getString(R.string.title_mcheyne_list2), this, activity, true) else SingleList(mcheyneBinding!!.cardList2, preferences!!.list.list2, resources.getString(R.string.title_mcheyne_list3), this, activity, false)
    var list3:SingleList = if(isPGH)
        if(displayLoading) SingleList(homeBinding!!.cardList3, null,resources.getString(R.string.title_pgh_list3), this, activity, true) else SingleList(homeBinding!!.cardList3, preferences!!.list.list3, resources.getString(R.string.title_pgh_list3), this, activity, false)
    else
        if(displayLoading) SingleList(mcheyneBinding!!.cardList3, null,resources.getString(R.string.title_mcheyne_list3), this, activity, true) else SingleList(mcheyneBinding!!.cardList3, preferences!!.list.list3, resources.getString(R.string.title_mcheyne_list3), this, activity, false)
    var list4:SingleList = if(isPGH)
        if(displayLoading) SingleList(homeBinding!!.cardList4, null,resources.getString(R.string.title_pgh_list4), this, activity, true) else SingleList(homeBinding!!.cardList4, preferences!!.list.list4, resources.getString(R.string.title_pgh_list4), this, activity, false)
    else
        if(displayLoading) SingleList(mcheyneBinding!!.cardList4, null,resources.getString(R.string.title_mcheyne_list4), this, activity, true) else SingleList(mcheyneBinding!!.cardList4, preferences!!.list.list4, resources.getString(R.string.title_mcheyne_list4), this, activity, false)
    var list5:SingleList? = if(isPGH) if(displayLoading) SingleList(homeBinding!!.cardList5, null,resources.getString(R.string.title_pgh_list5), this, activity, true) else SingleList(homeBinding!!.cardList5, preferences!!.list.list5, resources.getString(R.string.title_pgh_list5), this, activity, false) else null
    var list6:SingleList? = if(isPGH) if(displayLoading) SingleList(homeBinding!!.cardList6, null,resources.getString(R.string.title_pgh_list6), this, activity, true) else SingleList(homeBinding!!.cardList6, preferences!!.list.list6, resources.getString(R.string.title_pgh_list6), this, activity, false) else null
    var list7:SingleList? = if(isPGH) if(displayLoading) SingleList(homeBinding!!.cardList7, null,resources.getString(R.string.title_pgh_list7), this, activity, true) else SingleList(homeBinding!!.cardList7, preferences!!.list.list7, resources.getString(R.string.title_pgh_list7), this, activity, false) else null
    var list8:SingleList? = if(isPGH) if(displayLoading) SingleList(homeBinding!!.cardList8, null,resources.getString(R.string.title_pgh_list8), this, activity, true) else SingleList(homeBinding!!.cardList8, preferences!!.list.list8, resources.getString(R.string.title_pgh_list8), this, activity, false) else null
    var list9:SingleList? = if(isPGH) if(displayLoading) SingleList(homeBinding!!.cardList9, null,resources.getString(R.string.title_pgh_list9), this, activity, true) else SingleList(homeBinding!!.cardList9, preferences!!.list.list9, resources.getString(R.string.title_pgh_list9), this, activity, false) else null
    var list10:SingleList? = if(isPGH) if(displayLoading) SingleList(homeBinding!!.cardList10, null,resources.getString(R.string.title_pgh_list10), this, activity, true) else SingleList(homeBinding!!.cardList10, preferences!!.list.list10, resources.getString(R.string.title_pgh_list10), this, activity, false) else null
    var button:DoneButton? = if(isPGH) if(displayLoading) null else DoneButton(homeBinding!!, mcheyneBinding = null, resources, activity, this) else if(displayLoading) null else DoneButton(binding=null, mcheyneBinding!!, resources, activity, this)
    var loadingButton: LoadingDone? = if(isPGH) if(displayLoading) LoadingDone(homeBinding!!,mcheyneBinding=null, this) else null else if (displayLoading) LoadingDone(binding=null, mcheyneBinding=mcheyneBinding, this) else null
    init{
        if(!displayLoading) {
            setVisibilities()
            if (this.isDayOff()) {
                preferences!!.list.setListDone(preferences.list.maxDone)
                preferences.streak.dailyStreak = 1
            }
        }
    }
    fun isAdvanceable(maxDone:Int):Boolean{
        return preferences!!.list.listsDone == maxDone && (preferences.settings.planType == "horner" || preferences.settings.planType == "numerical")
    }
    fun isDayOff():Boolean{
        return Calendar.getInstance().get(Calendar.MONTH) == Calendar.FEBRUARY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 29 && preferences!!.settings.planType == "calendar" && preferences.settings.planSystem == "mcheyne"
    }
    fun listSwitcher(singleList: SingleList){
        val enabled = preferences!!.colors.background
        val disabled = Color.parseColor("#00383838")
        val cardList = singleList.cardView
        when(singleList.list!!.listDone){
            false -> { cardList.root.isEnabled = true; cardList.root.setCardBackgroundColor(enabled) }
            true -> {
                if(isPGH)
                    homeBinding!!.materialButton.setText(R.string.btn_mark_remaining)
                else
                    mcheyneBinding!!.materialButton.setText(R.string.btn_mark_remaining)
                cardList.root.isEnabled = false
                cardList.root.setCardBackgroundColor(disabled)
            }
        }
    }

    fun setVisibilities(){
        list1.setVisibility(isVisible=false)
        list2.setVisibility(isVisible=false)
        list3.setVisibility(isVisible=false)
        list4.setVisibility(isVisible=false)
        if(isPGH){
            list5!!.setVisibility(isVisible=false)
            list6!!.setVisibility(isVisible=false)
            list7!!.setVisibility(isVisible=false)
            list8!!.setVisibility(isVisible=false)
            list9!!.setVisibility(isVisible=false)
            list10!!.setVisibility(isVisible=false)
        }


    }

    fun hideOthers(currentList: CardviewsBinding?=null) {
        list1.setVisibility(isVisible = currentList == list1.cardView)
        list2.setVisibility(isVisible = currentList == list2.cardView)
        list3.setVisibility(isVisible = currentList == list3.cardView)
        list4.setVisibility(isVisible = currentList == list4.cardView)
        if (isPGH) {
            list5!!.setVisibility(isVisible = currentList == list5!!.cardView)
            list6!!.setVisibility(isVisible = currentList == list6!!.cardView)
            list7!!.setVisibility(isVisible = currentList == list7!!.cardView)
            list8!!.setVisibility(isVisible = currentList == list8!!.cardView)
            list9!!.setVisibility(isVisible = currentList == list9!!.cardView)
            list10!!.setVisibility(isVisible = currentList == list10!!.cardView)
        }
    }

}

object ListHelpers {
    fun createUpdateAlert(context: Context, preferences: Preferences){
        traceLog(file="ListHelpers.kt", function="createUpdateAlert()")
        val versionNumber = BuildConfig.VERSION_CODE
        if(preferences.settings.versionNumber < versionNumber){
            val builder = AlertDialog.Builder(context)
            builder.setPositiveButton(R.string.ok) { dialog, _ ->
                preferences.settings.versionNumber = versionNumber
                dialog.dismiss()
            }
            builder.setNeutralButton(context.resources.getString(R.string.moreInfo)) { dialog, _ ->
                preferences.settings.versionNumber = versionNumber
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
    }
}