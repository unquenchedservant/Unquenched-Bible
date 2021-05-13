package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog

object Dates {

    fun isLeapDay():Boolean{
        val today = LocalDate.now()
        return today.month == Month.FEBRUARY && today.dayOfMonth == 29

    }
    fun isWeekend():Boolean{
        val today = LocalDate.now()
        return when(today.dayOfWeek){
            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> true
            else -> false
        }
    }

    fun checkDate(date:String, option: String, fullMonth: Boolean): Boolean{
        return when(option){
            "current"-> date == getDate(0, fullMonth)
            "yesterday"-> date == getDate(1, fullMonth)
            "both"-> date == getDate(0, fullMonth) || date == getDate(1, false)
            else -> false
        }
    }
    fun getDate(option: Int, fullMonth: Boolean): String {
        val date : LocalDate
        val now  = LocalDate.now()
        date = when(option){
            0 -> now
            1 -> now.minusDays(1)
            else -> now
        }
        return if (fullMonth) {
            debugLog("THIS IS THE FORMATTED DATE ${date.format(DateTimeFormatter.ofPattern("MMMM dd"))}")
            date.format(DateTimeFormatter.ofPattern("MMMM dd"))
        } else {
            debugLog("THIS IS THE FORMATTED DATE ${date.format(DateTimeFormatter.ofPattern("MMMM dd"))}")
            date.format(DateTimeFormatter.ofPattern("MMM dd"))
        }
    }
}