package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.annotation.SuppressLint
import android.os.Build
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import java.text.SimpleDateFormat
import java.util.*

object Dates {

    fun isLeapDay():Boolean{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val today = LocalDate.now()
            today.month == Month.FEBRUARY && today.dayOfMonth == 29
        }else{
            val today = Calendar.getInstance()
            today.get(Calendar.MONTH) == Calendar.FEBRUARY && today.get(Calendar.DAY_OF_MONTH) == 29
        }
    }
    fun isWeekend():Boolean{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val today = LocalDate.now()
            when (today.dayOfWeek) {
                DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> true
                else -> false
            }
        }else{
            val today = Calendar.getInstance()
            when (today.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SATURDAY, Calendar.SUNDAY -> true
                else -> false
            }
        }

    }

    fun checkDate(date:String, option: String, fullMonth: Boolean): Boolean{
        return when(option){
            "current"-> date == getDate(0, fullMonth)
            "yesterday"-> date == getDate(1, fullMonth)
            "two" -> date == getDate(2, fullMonth)
            "both"-> date == getDate(0, fullMonth) || date == getDate(1, false)
            else -> false
        }
    }
    @SuppressLint("SimpleDateFormatDetector")
    fun getDate(option: Int, fullMonth: Boolean): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val date: LocalDate
            val now = LocalDate.now()
            date = when (option) {
                0 -> now
                1 -> now.minusDays(1)
                2 -> now.minusDays(2)
                4 -> now.minusDays(3)
                else -> now
            }
            return if (fullMonth) {
                debugLog("THIS IS THE FORMATTED DATE ${date.format(DateTimeFormatter.ofPattern("MMMM dd"))}")
                date.format(DateTimeFormatter.ofPattern("MMMM dd"))
            } else {
                debugLog("THIS IS THE FORMATTED DATE ${date.format(DateTimeFormatter.ofPattern("MMMM dd"))}")
                date.format(DateTimeFormatter.ofPattern("MMM dd"))
            }
        }else{
            val date: Date
            val now: Date = Calendar.getInstance().time
            val yesterday = Calendar.getInstance()
            val two = Calendar.getInstance()
            val test = Calendar.getInstance()
            yesterday.set(Calendar.HOUR_OF_DAY, 0)
            yesterday.add(Calendar.DATE, -1)
            two.set(Calendar.HOUR_OF_DAY, 0)
            two.add(Calendar.DATE, -2)
            test.set(Calendar.HOUR_OF_DAY, 0)
            test.add(Calendar.DATE, -3)
            date = when(option){
                0 -> now
                1 -> { yesterday.time }
                2 -> { two.time }
                4 -> { test.time }
                else -> now
            }
            return if(fullMonth){
                SimpleDateFormat("MMMM dd", Locale.US).format(date)
            }else{
                SimpleDateFormat("MMM dd", Locale.US).format(date)
            }
        }
    }
}