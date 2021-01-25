package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import java.text.SimpleDateFormat
import java.util.*

object Dates {

    fun isLeapDay():Boolean{
        val today = Calendar.getInstance()
        val isFebruary = when(today.get(Calendar.MONTH)){
            Calendar.FEBRUARY -> true
            else -> false
        }
        val is29th = when(today.get(Calendar.DAY_OF_MONTH)){
            29 -> true
            else -> false
        }
        return when{
            (isFebruary && is29th)->true
            else->false
        }
    }
    fun isWeekend():Boolean{
        val today = Calendar.getInstance()
        return when(today.get(Calendar.DAY_OF_WEEK)){
            Calendar.SATURDAY, Calendar.SUNDAY -> true
            else -> false
        }
    }

    fun checkDate(option: String, fullMonth: Boolean): Boolean{
        val date = getStringPref("dateChecked")
        return when(option){
            "current"-> date == getDate(0, fullMonth)
            "yesterday"-> date == getDate(1, fullMonth)
            "both"-> date == getDate(0, fullMonth) || date == getDate(1, false)
            else -> false
        }
    }
    fun getDate(option: Int, fullMonth: Boolean): String {
        var date : Date? = null
        when(option){
            0 -> date = Calendar.getInstance().time
            1 -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.add(Calendar.DATE, -1)
                date = cal.time
            }
        }
        return if (fullMonth) {
            SimpleDateFormat("MMMM dd", Locale.US).format(date!!)
        } else {
            SimpleDateFormat("MMM dd", Locale.US).format(date!!)
        }
    }
}