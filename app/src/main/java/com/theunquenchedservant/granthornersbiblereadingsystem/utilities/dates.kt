package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import java.text.SimpleDateFormat
import java.util.*

object dates {
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