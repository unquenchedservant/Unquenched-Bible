package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_NAMES
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.BibleStatsReset.resetBook

class BookResetMenuFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val ctx = App.applicationContext()
        val screen: PreferenceScreen = preferenceManager.createPreferenceScreen(ctx)
        val b = arguments
        val book = b?.getString("book")
        val testament = b?.getString("testament")
        val bookName = BOOK_NAMES[book]
        val mainActivity = activity as MainActivity
        mainActivity.supportActionBar?.title = "Reset $bookName Stats"
        val hardReset = Preference(ctx)
        val softReset = Preference(ctx)
        hardReset.title = "Hard Reset"
        hardReset.summary = "Reset all the chapters in $bookName to Not Read and Read 0 Times."
        softReset.title = "Soft Reset"
        softReset.summary = "Reset the percentage of $bookName read to 0%, but keep the amount of times read."
        hardReset.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val alertDialog = AlertDialog.Builder(this.context)
            alertDialog.setPositiveButton("Yes"){dialog, _->
                resetBook(book!!, testament!!, hardReset = true)
                dialog.dismiss()
            }
            alertDialog.setNeutralButton("Nevermind"){dialog, _->
                dialog.dismiss()
            }
            alertDialog.setTitle("Are you sure?")
            alertDialog.setMessage("This reset will set $bookName and all it's chapters to 0% read and will set the amount of times read to 0 for $bookName and all it's chapters.\n\nThis is a permanent action that can not be undone.\n\nYOU DO NOT HAVE TO RESET THE BIBLE STATS IF YOU WANT IT TO GO FROM 100% to 0%, it will adjust when you next complete a list!")
            alertDialog.show()
            true
        }
        softReset.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val alertDialog = AlertDialog.Builder(this.context)
            alertDialog.setPositiveButton("Yes"){dialog, _->
                resetBook(book!!, testament!!, false)
                dialog.dismiss()
            }
            alertDialog.setNeutralButton("Nevermind"){dialog, _->
                dialog.dismiss()
            }
            alertDialog.setTitle("Are you sure?")
            alertDialog.setMessage("This reset will set $bookName and all it's chapters to 0% read. The amount of times read $bookName will remain the same.\n\nThis is a permanent action that can not be undone.\n\nYOU DO NOT HAVE TO RESET THE BIBLE STATS IF YOU WANT IT TO GO FROM 100% to 0%, it will adjust when you next complete a list!")
            alertDialog.show()
            true
        }
        screen.addPreference(hardReset)
        screen.addPreference(softReset)
        preferenceScreen = screen
    }
}