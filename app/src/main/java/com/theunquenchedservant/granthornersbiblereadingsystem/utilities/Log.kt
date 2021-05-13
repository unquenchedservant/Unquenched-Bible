package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.util.Log

object Log {
    fun debugLog(message:String,file:String="", function:String=""){
        val logString:String = if(file != "" && function != ""){
            "$function - $file : $message"
        }else if(file != "" && function == ""){
            "$file : $message"
        }else if(file == "" && function != ""){
            "$function : $message"
        }else{
            message
        }
        Log.d("PROFGRANT", logString)
    }
    fun traceLog(file:String="", function:String="", message:String=""){
        val logString = if(file != "" && function != "" && message != ""){
            "TRACE $function - $file : $message"
        }else if(file != "" && function != "" && message == ""){
            "TRACE $function - $file"
        }else if(file != "" && function == "" && message != ""){
            "TRACE $file : $message"
        }else if(file != "" && function == "" && message == ""){
            "TRACE $file"
        }else if(file == "" && function != "" && message != ""){
            "TRACE $function : $message"
        }else if(file == "" && function != "" && message == ""){
            "TRACE $function"
        }else if(file == "" && function == "" && message != ""){
            "TRACE unknown location : $message"
        }else if(file == "" && function == "" && message == ""){
            "TRACE unknown location : no message"
        }else{
            "TRACE you done messed up a-aron"
        }
        Log.d("PROFGRANT", logString)
    }
}