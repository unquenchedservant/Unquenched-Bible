package com.theunquenchedservant.granthornersbiblereadingsystem

import android.content.Context
import android.content.SharedPreferences
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import java.io.File
import java.nio.file.Files

object sharedPref {
    fun statisticsRead(context: Context?, name: String): Int {
        log("Reading Statistic $name")
        val pref = context!!.getSharedPreferences("statistics", Context.MODE_PRIVATE)
        val x = pref.getInt(name, 0)
        log("returning $name value = $x")
        return x
    }

    fun statisticsEdit(context: Context?, name: String, value: Int) {
        log("Editing statistic $name")
        val pref = context!!.getSharedPreferences("statistics", Context.MODE_PRIVATE).edit()
        pref.putInt(name, value)
        log("$name value now $value")
        pref.apply()
    }

    fun listNumbersReset(context: Context?) {
        context!!.getSharedPreferences("listNumbers", Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun listNumberReadInt(context: Context?, name: String): Int {
        log("listNumberReadInt started")
        val pref = context!!.getSharedPreferences(
                "listNumbers", Context.MODE_PRIVATE)
        val x = pref.getInt(name, 0)
        log("Returning value for $name - $x")
        return x
    }

    fun listNumberEditInt(context: Context?, name: String, value: Int) {
        log("listNumberEditInt started")
        val pref = context!!.getSharedPreferences(
                "listNumbers", Context.MODE_PRIVATE).edit()
        context.let {
            pref.putInt(name, value)
            log("$name int changed to $value")
            pref.apply()
        }
    }


    fun readEdit(context: Context?, chapter: String?, value: Int) {
        log("Marking $chapter")
        val pref = context!!.getSharedPreferences("hasRead", Context.MODE_PRIVATE).edit()
        pref.putInt(chapter, value)
        log("Done marking $chapter")
        pref.apply()
    }

    fun resetRead(context: Context?) {
        context!!.getSharedPreferences("hasRead", Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun readRead(context: Context?, chapter: String?): Int {
        log("Getting $chapter")
        val pref = context!!.getSharedPreferences("hasRead", Context.MODE_PRIVATE)
        val x = pref.getInt(chapter, 0)
        log("Returning $chapter - value $x")
        return x

    }

    fun prefReadInt(context: Context?, intName: String): Int {
        log("Start prefReadInt")
        context?.let {
            val pref = getPrefRead(context)
            log("Getting and returning $intName")
            return pref.getInt(intName, 0)
        }
        return 0
    }
    fun clearOldPref(context: Context){
        log("Clearing old preference file")
        getPrefRead(context).edit().clear().commit()
        val file = File("${context.filesDir.parent}/shared_prefs/com.theunquenchedservant.granthornersbiblereadingsystem.xml")
        file.delete()
        log("${context.filesDir.parent}/shared_prefs/com.theunquenchedservant.granthornersbiblereadingsystem.xml DELETED")
    }
    fun getPrefRead(context: Context): SharedPreferences {
        log("return sharedpreference")
        return context.getSharedPreferences(
                "com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE)
    }

    fun getPrefEdit(context: Context): SharedPreferences.Editor {
        log("return sharedpreference.edit()")
        return context.getSharedPreferences(
                "com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE).edit()
    }
}
