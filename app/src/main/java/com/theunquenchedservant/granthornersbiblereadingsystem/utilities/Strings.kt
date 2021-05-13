package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import java.util.*

object Strings {
    fun capitalize(str:String):String{
        return str.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }
    }
}