package com.theunquenchedservant.granthornersbiblereadingsystem

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import java.io.File

object SharedPref {
    private val user = FirebaseAuth.getInstance().currentUser

    fun firstRun(context: Context?) {
        val db = FirebaseFirestore.getInstance()
        log("First run has been run")
        if (user != null) {
            log("User is logged in")
            db.collection("main").document(user.uid).get()
                    .addOnSuccessListener {
                            /**
                            val result = it.data
                            log("$result")
                            result!!.get("list1")
                            log("User document exists")
                            val list1 = (it.data!!["list1"] as Long).toInt()
                            val list1Done = (it.data!!["list1Done"] as Long).toInt()
                            val list2 = (it.data!!["list2"] as Long).toInt()
                            val list2Done = (it.data!!["list2Done"] as Long).toInt()
                            val list3 = (it.data!!["list3"] as Long).toInt()
                            val list3Done = (it.data!!["list3Done"] as Long).toInt()
                            val list4 = (it.data!!["list4"] as Long).toInt()
                            val list4Done = (it.data!!["list4Done"] as Long).toInt()
                            val list5 = (it.data!!["list5"] as Long).toInt()
                            val list5Done = (it.data!!["list5Done"] as Long).toInt()
                            val list6 = (it.data!!["list6"] as Long).toInt()
                            val list6Done = (it.data!!["list6Done"] as Long).toInt()
                            val list7 = (it.data!!["list7"] as Long).toInt()
                            val list7Done = (it.data!!["list7Done"] as Long).toInt()
                            val list8 = (it.data!!["list8"] as Long).toInt()
                            val list8Done = (it.data!!["list8Done"] as Long).toInt()
                            val list9 = (it.data!!["list9"] as Long).toInt()
                            val list9Done = (it.data!!["list9Done"] as Long).toInt()
                            val list10 = (it.data!!["list10"] as Long).toInt()
                            val list10Done = (it.data!!["list10Done"] as Long).toInt()
                            listNumberEditInt(context, "List 1", list1)
                            log("List 1 = $list1")
                            listNumberEditInt(context, "list1Done", list1Done)
                            log("list1Done = $list1Done")
                            listNumberEditInt(context, "List 2", list2)
                            log("List 2 = $list2")
                            listNumberEditInt(context, "list2Done", list2Done)
                            log("list2Done = $list2Done")
                            listNumberEditInt(context, "List 3", list3)
                            log("List 3 = $list3")
                            listNumberEditInt(context, "list3Done", list3Done)
                            log("list3Done = $list3Done")
                            listNumberEditInt(context, "List 4", list4)
                            log("List 4 = $list4")
                            listNumberEditInt(context, "list4Done", list4Done)
                            log("list4Done = $list4Done")
                            listNumberEditInt(context, "List 5", list5)
                            log("List 5 = $list5")
                            listNumberEditInt(context, "list5Done", list5Done)
                            log("list5Done = $list5Done")
                            listNumberEditInt(context, "List 6", list6)
                            log("List 6 = $list6")
                            listNumberEditInt(context, "list6Done", list6Done)
                            log("list6Done = $list6Done")
                            listNumberEditInt(context, "List 7", list7)
                            log("List 7 = $list7")
                            listNumberEditInt(context, "list7Done", list7Done)
                            log("list7Done = $list7Done")
                            listNumberEditInt(context, "List 8", list8)
                            log("List 8 = $list8")
                            listNumberEditInt(context, "list8Done", list8Done)
                            log("list8Done = $list8Done")
                            listNumberEditInt(context, "List 9", list9)
                            log("List 9 = $list9")
                            listNumberEditInt(context, "list9Done", list9Done)
                            log("list9Done = $list9Done")
                            listNumberEditInt(context, "List 10", list10)
                            log("List 10 = $list10")
                            listNumberEditInt(context, "list10Done", list10Done)
                            log("list10Done = $list10Done")
                            val dailyStreak = (it.data!!["dailyStreak"] as Long).toInt()
                            val currentStreak = (it.data!!["currentStreak"] as Long).toInt()
                            val maxStreak = (it.data!!["maxStreak"] as Long).toInt()
                            val totalRead = (it.data!!["totalRead"] as Long).toInt()
                            statisticsEdit(context, "dailyStreak", dailyStreak)
                            log("dailyStreak = $dailyStreak")
                            statisticsEdit(context, "currentStreak", currentStreak)
                            log("currentStreak = $currentStreak")
                            statisticsEdit(context, "maxStreak", maxStreak)
                            log("maxStreak = $maxStreak")
                            statisticsEdit(context, "totalRead", totalRead)
                            log("totalRead = $totalRead")
                            val psalms = it.data!!["psalms"] as Boolean
                            val partial = it.data!!["allowPartial"] as Boolean
                            val vacationMode = it.data!!["vacationMode"] as Boolean
                            val dailyNotif = (it.data!!["dailyNotif"] as Long).toInt()
                            val remindNotif = (it.data!!["remindNotif"] as Long).toInt()
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("psalms", psalms)
                            log("Psalms = $psalms")
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("allow_partial_switch", partial)
                            log("allow_partial_switch = $partial")
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("vacation_mode", vacationMode)
                            log("vacationMode = $vacationMode")
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("daily_time", dailyNotif)
                            log("daily_time = $dailyNotif")
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("remind_time", remindNotif)
                            log("remind_time = $remindNotif")
                        }*/
                        if(it.data == null){
                            log("User document doesn't exist")
                            val list1Number = listNumberReadInt(context, "List 1")
                            val list2Number = listNumberReadInt(context, "List 2")
                            val list3Number = listNumberReadInt(context, "List 3")
                            val list4Number = listNumberReadInt(context, "List 4")
                            val list5Number = listNumberReadInt(context, "List 5")
                            val list6Number = listNumberReadInt(context, "List 1")
                            val list7Number = listNumberReadInt(context, "List 2")
                            val list8Number = listNumberReadInt(context, "List 3")
                            val list9Number = listNumberReadInt(context, "List 4")
                            val list10Number = listNumberReadInt(context, "List 5")
                            val list1Done = listNumberReadInt(context, "list1Done")
                            val list2Done = listNumberReadInt(context, "list2Done")
                            val list3Done = listNumberReadInt(context, "list3Done")
                            val list4Done = listNumberReadInt(context, "list4Done")
                            val list5Done = listNumberReadInt(context, "list5Done")
                            val list6Done = listNumberReadInt(context, "list6Done")
                            val list7Done = listNumberReadInt(context, "list7Done")
                            val list8Done = listNumberReadInt(context, "list8Done")
                            val list9Done = listNumberReadInt(context, "list9Done")
                            val list10Done = listNumberReadInt(context, "list10Done")
                            val listsDone = listNumberReadInt(context, "listsDone")
                            val currentStreak = statisticsRead(context,"currentStreak")
                            val daily = statisticsRead(context, "dailyStreak")
                            val maxStreak = statisticsRead(context, "maxStreak")
                            val notifications = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notif_switch", false)
                            val dailyNotif = PreferenceManager.getDefaultSharedPreferences(context).getInt("daily_time", 300)
                            val remindNotif = PreferenceManager.getDefaultSharedPreferences(context).getInt("remind_time", 1200)
                            val psalms = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("psalms", false)
                            val vacationMode = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("vacation_mode", false)
                            val allowPartial = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("allow_partial_switch", false)
                            val data = hashMapOf(
                                    "list1" to list1Number,
                                    "list1Done" to list1Done,
                                    "list2" to list2Number,
                                    "list2Done" to list2Done,
                                    "list3" to list3Number,
                                    "list3Done" to list3Done,
                                    "list4" to list4Number,
                                    "list4Done" to list4Done,
                                    "list5" to list5Number,
                                    "list5Done" to list5Done,
                                    "list6" to list6Number,
                                    "list6Done" to list6Done,
                                    "list7" to list7Number,
                                    "list7Done" to list7Done,
                                    "list8" to list8Number,
                                    "list8Done" to list8Done,
                                    "list9" to list9Number,
                                    "list9Done" to list9Done,
                                    "list10" to list10Number,
                                    "list10Done" to list10Done,
                                    "listsDone" to listsDone,
                                    "notifications" to notifications,
                                    "currentStreak" to currentStreak,
                                    "maxStreak" to maxStreak,
                                    "dailyStreak" to daily,
                                    "dailyNotif" to dailyNotif,
                                    "remindNotif" to remindNotif,
                                    "psalms" to psalms,
                                    "vacationMode" to vacationMode,
                                    "allowPartial" to allowPartial
                            )
                            db.collection("main").document(user.uid).set(data)
                                    .addOnSuccessListener { log("Data transferred to firestore") }
                                    .addOnFailureListener {e -> Log.w("PROFGRANT", "Error writing to firestore", e) }
                        }
                    }
        }
    }
    fun preferenceToFireStone(context : Context?){
        val db = FirebaseFirestore.getInstance()
        val list1Number = listNumberReadInt(context, "List 1")
        val list2Number = listNumberReadInt(context, "List 2")
        val list3Number = listNumberReadInt(context, "List 3")
        val list4Number = listNumberReadInt(context, "List 4")
        val list5Number = listNumberReadInt(context, "List 5")
        val list6Number = listNumberReadInt(context, "List 1")
        val list7Number = listNumberReadInt(context, "List 2")
        val list8Number = listNumberReadInt(context, "List 3")
        val list9Number = listNumberReadInt(context, "List 4")
        val list10Number = listNumberReadInt(context, "List 5")
        val list1Done = listNumberReadInt(context, "list1Done")
        val list2Done = listNumberReadInt(context, "list2Done")
        val list3Done = listNumberReadInt(context, "list3Done")
        val list4Done = listNumberReadInt(context, "list4Done")
        val list5Done = listNumberReadInt(context, "list5Done")
        val list6Done = listNumberReadInt(context, "list6Done")
        val list7Done = listNumberReadInt(context, "list7Done")
        val list8Done = listNumberReadInt(context, "list8Done")
        val list9Done = listNumberReadInt(context, "list9Done")
        val list10Done = listNumberReadInt(context, "list10Done")
        val listsDone = listNumberReadInt(context, "listsDone")
        val currentStreak = statisticsRead(context,"currentStreak")
        val daily = statisticsRead(context, "dailyStreak")
        val maxStreak = statisticsRead(context, "maxStreak")
        val notifications = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notif_switch", false)
        val dailyNotif = PreferenceManager.getDefaultSharedPreferences(context).getInt("daily_time", 300)
        val remindNotif = PreferenceManager.getDefaultSharedPreferences(context).getInt("remind_time", 1200)
        val psalms = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("psalms", false)
        val vacationMode = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("vacation_mode", false)
        val allowPartial = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("allow_partial_switch", false)
        val data = hashMapOf(
                "list1" to list1Number,
                "list1Done" to list1Done,
                "list2" to list2Number,
                "list2Done" to list2Done,
                "list3" to list3Number,
                "list3Done" to list3Done,
                "list4" to list4Number,
                "list4Done" to list4Done,
                "list5" to list5Number,
                "list5Done" to list5Done,
                "list6" to list6Number,
                "list6Done" to list6Done,
                "list7" to list7Number,
                "list7Done" to list7Done,
                "list8" to list8Number,
                "list8Done" to list8Done,
                "list9" to list9Number,
                "list9Done" to list9Done,
                "list10" to list10Number,
                "list10Done" to list10Done,
                "listsDone" to listsDone,
                "notifications" to notifications,
                "currentStreak" to currentStreak,
                "maxStreak" to maxStreak,
                "dailyStreak" to daily,
                "dailyNotif" to dailyNotif,
                "remindNotif" to remindNotif,
                "psalms" to psalms,
                "vacationMode" to vacationMode,
                "allowPartial" to allowPartial
        )
        db.collection("main").document(user!!.uid).set(data)
                .addOnSuccessListener { log("Data transferred to firestore") }
                .addOnFailureListener {e -> Log.w("PROFGRANT", "Error writing to firestore", e) }
    }
    fun firestoneToPreference(database: DocumentSnapshot, context: Context?){
        val data = database.data
        log("User document exists")
        val list1 = (data!!["list1"] as Long).toInt()
        val list1Done = (data["list1Done"] as Long).toInt()
        val list2 = (data["list2"] as Long).toInt()
        val list2Done = (data["list2Done"] as Long).toInt()
        val list3 = (data["list3"] as Long).toInt()
        val list3Done = (data["list3Done"] as Long).toInt()
        val list4 = (data["list4"] as Long).toInt()
        val list4Done = (data["list4Done"] as Long).toInt()
        val list5 = (data["list5"] as Long).toInt()
        val list5Done = (data["list5Done"] as Long).toInt()
        val list6 = (data["list6"] as Long).toInt()
        val list6Done = (data["list6Done"] as Long).toInt()
        val list7 = (data["list7"] as Long).toInt()
        val list7Done = (data["list7Done"] as Long).toInt()
        val list8 = (data["list8"] as Long).toInt()
        val list8Done = (data["list8Done"] as Long).toInt()
        val list9 = (data["list9"] as Long).toInt()
        val list9Done = (data["list9Done"] as Long).toInt()
        val list10 = (data["list10"] as Long).toInt()
        val list10Done = (data["list10Done"] as Long).toInt()
        listNumberEditInt(context, "List 1", list1)
        log("List 1 = $list1")
        listNumberEditInt(context, "list1Done", list1Done)
        log("list1Done = $list1Done")
        listNumberEditInt(context, "List 2", list2)
        log("List 2 = $list2")
        listNumberEditInt(context, "list2Done", list2Done)
        log("list2Done = $list2Done")
        listNumberEditInt(context, "List 3", list3)
        log("List 3 = $list3")
        listNumberEditInt(context, "list3Done", list3Done)
        log("list3Done = $list3Done")
        listNumberEditInt(context, "List 4", list4)
        log("List 4 = $list4")
        listNumberEditInt(context, "list4Done", list4Done)
        log("list4Done = $list4Done")
        listNumberEditInt(context, "List 5", list5)
        log("List 5 = $list5")
        listNumberEditInt(context, "list5Done", list5Done)
        log("list5Done = $list5Done")
        listNumberEditInt(context, "List 6", list6)
        log("List 6 = $list6")
        listNumberEditInt(context, "list6Done", list6Done)
        log("list6Done = $list6Done")
        listNumberEditInt(context, "List 7", list7)
        log("List 7 = $list7")
        listNumberEditInt(context, "list7Done", list7Done)
        log("list7Done = $list7Done")
        listNumberEditInt(context, "List 8", list8)
        log("List 8 = $list8")
        listNumberEditInt(context, "list8Done", list8Done)
        log("list8Done = $list8Done")
        listNumberEditInt(context, "List 9", list9)
        log("List 9 = $list9")
        listNumberEditInt(context, "list9Done", list9Done)
        log("list9Done = $list9Done")
        listNumberEditInt(context, "List 10", list10)
        log("List 10 = $list10")
        listNumberEditInt(context, "list10Done", list10Done)
        log("list10Done = $list10Done")
        val dailyStreak = (data["dailyStreak"] as Long).toInt()
        val currentStreak = (data["currentStreak"] as Long).toInt()
        val maxStreak = (data["maxStreak"] as Long).toInt()
        statisticsEdit(context, "dailyStreak", dailyStreak)
        log("dailyStreak = $dailyStreak")
        statisticsEdit(context, "currentStreak", currentStreak)
        log("currentStreak = $currentStreak")
        statisticsEdit(context, "maxStreak", maxStreak)
        log("maxStreak = $maxStreak")
        val psalms = data["psalms"] as Boolean
        val partial = data["allowPartial"] as Boolean
        val vacationMode = data["vacationMode"] as Boolean
        val dailyNotif = (data["dailyNotif"] as Long).toInt()
        val remindNotif = (data["remindNotif"] as Long).toInt()
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("psalms", psalms).apply()
        log("Psalms = $psalms")
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("allow_partial_switch", partial).apply()
        log("allow_partial_switch = $partial")
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("vacation_mode", vacationMode).apply()
        log("vacationMode = $vacationMode")
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("daily_time", dailyNotif).apply()
        log("daily_time = $dailyNotif")
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("remind_time", remindNotif).apply()
        log("remind_time = $remindNotif")
    }
    fun statisticsRead(context: Context?, name: String): Int {
        //if(isLogged != null){
          //  log("Reading Statistic")
            //val pref = 0
       // }
        log("Reading Statistic $name")
        val pref = context!!.getSharedPreferences("statistics", Context.MODE_PRIVATE)
        val x = pref.getInt(name, 0)
        log("returning $name value = $x")
        return x
    }

    fun statisticsEdit(context: Context?, name: String, value: Int) {
        log("Editing statistic $name")
        val pref = context?.getSharedPreferences("statistics", Context.MODE_PRIVATE)?.edit()
        pref?.putInt(name, value)
        log("$name value now $value")
        pref?.apply()
    }

    fun listNumbersReset(context: Context?) {
        context!!.getSharedPreferences("listNumbers", Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun listNumberReadInt(context: Context?, name: String): Int {
        log("listNumberReadInt started")
        val pref = context?.getSharedPreferences(
                "listNumbers", Context.MODE_PRIVATE)
        val x = pref?.getInt(name, 0)!!
        log("Returning value for $name - $x")
        return x
    }
    fun listNumberEditString(context: Context?, name: String, value: String) {
        log("listNumberEditString started")
        val pref = context!!.getSharedPreferences(
                "listNumbers", Context.MODE_PRIVATE)?.edit()
        context.let{
            pref?.putString(name, value)
            log("$name string changed to $value")
            pref?.apply()
        }
    }
    fun listNumberReadString(context: Context?, name: String): String {
        log("listNumberReadString started")
        val pref = context?.getSharedPreferences(
                "listNumbers", Context.MODE_PRIVATE)
        val x = pref?.getString(name, "itsdeadjim")
        log("Returning value for $name - $x")
        return x!!
    }
    fun updateFS(name: String, value: Any){
        val db = FirebaseFirestore.getInstance()
        db.collection("main").document(user!!.uid).update(name, value)
    }
    fun listNumberEditInt(context: Context?, name: String, value: Int) {
        log("listNumberEditInt started")
        val pref = context?.getSharedPreferences(
                "listNumbers", Context.MODE_PRIVATE)?.edit()
        context.let {
            pref?.putInt(name, value)
            log("$name int changed to $value")
            pref?.apply()
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
        getPrefRead(context).edit().clear().apply()
        val file = File("${context.filesDir.parent}/shared_prefs/com.theunquenchedservant.granthornersbiblereadingsystem.xml")
        file.delete()
        log("${context.filesDir.parent}/shared_prefs/com.theunquenchedservant.granthornersbiblereadingsystem.xml DELETED")
    }
   private fun getPrefRead(context: Context): SharedPreferences {
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
