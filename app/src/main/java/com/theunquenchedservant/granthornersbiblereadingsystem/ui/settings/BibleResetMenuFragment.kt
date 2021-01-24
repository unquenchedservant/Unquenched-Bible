package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.BibleStatsReset.resetBible
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.BibleStatsReset.resetTestament

class BibleResetMenuFragment : PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?){
        val ctx = App.applicationContext()
        val screen: PreferenceScreen = preferenceManager.createPreferenceScreen(ctx)
        val mainActivity = activity as MainActivity
        val fullHardReset = Preference(ctx)
        val fullSoftReset = Preference(ctx)
        val oldHardReset = Preference(ctx)
        val oldSoftReset = Preference(ctx)
        val oldBooksMenu = Preference(ctx)
        val newHardReset = Preference(ctx)
        val newSoftReset = Preference(ctx)
        val newBooksMenu = Preference(ctx)
        val fullCategory = PreferenceCategory(ctx)
        val oldCategory = PreferenceCategory(ctx)
        val newCategory = PreferenceCategory(ctx)
        fullHardReset.title = "Hard Full Bible Reset"
        fullHardReset.summary = "Reset every statistic of Bible reading to 0."
        fullSoftReset.title = "Soft Full Bible Reset"
        fullSoftReset.summary = "Reset the percentage of the Bible read to 0%, but keep progress for amount of times you've read chapters, books and testaments"
        oldHardReset.title = "Old Testament Hard Reset"
        oldHardReset.summary = "Reset every statistic of Old Testament readings to 0"
        oldSoftReset.title = "Old Testament Soft Reset"
        oldSoftReset.summary = "Reset the percentage of the Old Testament read to 0%, but keep progress for amount of times you've read chapters, books and the Old Testament"
        newHardReset.title = "New Testament Hard Reset"
        newHardReset.summary = "Reset every statistic of New Testament readings to 0"
        newSoftReset.title= "New Testament Soft Reset"
        newSoftReset.summary = "Reset the percentage of the New Testament read to 0%, but keep progress for amount of times you've read chapters, books and the New Testament"
        oldBooksMenu.title = "Reset Old Testament Books"
        newBooksMenu.title = "Reset New Testament Books"
        oldBooksMenu.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        newBooksMenu.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
        fullCategory.title = "Full Bible"
        oldCategory.title = "Old Testament"
        newCategory.title = "New Testament"
        fullHardReset.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val alertDialog = AlertDialog.Builder(this.context)
            alertDialog.setPositiveButton("Yes"){dialog, _->
                resetBible(hardReset=true)
                dialog.dismiss()
            }
            alertDialog.setNeutralButton("Nevermind"){dialog, _->
                dialog.dismiss()
            }
            alertDialog.setTitle("Are you sure?")
            alertDialog.setMessage("This reset will set all books and each Testament to 0% read and the amount of times that each book and both Testaments have been read to 0.\n\nThis is a permanent action that can not be undone.\n\nYOU DO NOT HAVE TO RESET THE BIBLE STATS IF YOU WANT IT TO GO FROM 100% to 0%, it will adjust when you next complete a list!")
            alertDialog.show()
            true
        }
        fullSoftReset.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val alertDialog = AlertDialog.Builder(this.context)
            alertDialog.setPositiveButton("Yes"){dialog, _->
                resetBible(hardReset=false)
                dialog.dismiss()
            }
            alertDialog.setNeutralButton("Nevermind"){dialog, _->
                dialog.dismiss()
            }
            alertDialog.setTitle("Are you sure?")
            alertDialog.setMessage("This reset will set all books to 0% read. The amount of times read for each Testament and their books will remain the same.\n\nThis is a permanent action that can not be undone.\n\nYOU DO NOT HAVE TO RESET THE BIBLE STATS IF YOU WANT IT TO GO FROM 100% to 0%, it will adjust when you next complete a list!")
            alertDialog.show()
            true
        }
        oldHardReset.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val alertDialog = AlertDialog.Builder(this.context)
            alertDialog.setPositiveButton("Yes"){dialog, _->
                resetTestament(testament="old", hardReset=true, internal=false)
                dialog.dismiss()
            }
            alertDialog.setNeutralButton("Nevermind"){dialog, _->
                dialog.dismiss()
            }
            alertDialog.setTitle("Are you sure?")
            alertDialog.setMessage("This reset will set all Old Testament books to 0% read and will set the amount of times read to 0 for all Old Testament books.\n\nThis is a permanent action that can not be undone.\n\nYOU DO NOT HAVE TO RESET THE BIBLE STATS IF YOU WANT IT TO GO FROM 100% to 0%, it will adjust when you next complete a list!")
            alertDialog.show()
            true
        }
        oldSoftReset.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val alertDialog = AlertDialog.Builder(this.context)
            alertDialog.setPositiveButton("Yes"){dialog, _->
                resetTestament(testament="old", hardReset=false, internal=false)
                dialog.dismiss()
            }
            alertDialog.setNeutralButton("Nevermind"){dialog, _->
                dialog.dismiss()
            }
            alertDialog.setTitle("Are you sure?")
            alertDialog.setMessage("This reset will set all Old Testament books to 0% read. The amount of times read will remain the same. \n\nThis is a permanent action that can not be undone.\n\nYOU DO NOT HAVE TO RESET THE BIBLE STATS IF YOU WANT IT TO GO FROM 100% to 0%, it will adjust when you next complete a list!")
            alertDialog.show()
            true
        }
        newHardReset.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val alertDialog = AlertDialog.Builder(this.context)
            alertDialog.setPositiveButton("Yes"){dialog, _->
                resetTestament(testament="new", hardReset=true, internal=false)
                dialog.dismiss()
            }
            alertDialog.setNeutralButton("Nevermind"){dialog, _->
                dialog.dismiss()
            }
            alertDialog.setTitle("Are you sure?")
            alertDialog.setMessage("This reset will set all New Testament books to 0% read and will set the amount of times read to 0 for all New Testament books.\n\nThis is a permanent action that can not be undone.\n\nYOU DO NOT HAVE TO RESET THE BIBLE STATS IF YOU WANT IT TO GO FROM 100% to 0%, it will adjust when you next complete a list!")
            alertDialog.show()
            true
        }
        newSoftReset.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val alertDialog = AlertDialog.Builder(this.context)
            alertDialog.setPositiveButton("Yes"){dialog, _->
                resetTestament(testament="new", hardReset=false, internal=false)
                dialog.dismiss()
            }
            alertDialog.setNeutralButton("Nevermind"){dialog, _->
                dialog.dismiss()
            }
            alertDialog.setTitle("Are you sure?")
            alertDialog.setMessage("This reset will set all New Testament books to 0% read. The amount of times read will remain the same.\n\nThis is a permanent action that can not be undone.\n\nYOU DO NOT HAVE TO RESET THE BIBLE STATS IF YOU WANT IT TO GO FROM 100% to 0%, it will adjust when you next complete a list!")
            alertDialog.show()
            true
        }
        oldBooksMenu.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val bundle = bundleOf("testament" to "old")
            mainActivity.navController.navigate(R.id.navigation_books_reset_menu, bundle)
            true
        }
        newBooksMenu.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val bundle = bundleOf("testament" to "new")
            mainActivity.navController.navigate(R.id.navigation_books_reset_menu, bundle)
            true
        }
        fullCategory.layoutResource = R.layout.preferences_layout
        oldCategory.layoutResource = R.layout.preferences_layout
        newCategory.layoutResource = R.layout.preferences_layout
        screen.addPreference(fullCategory)
        fullCategory.addPreference(fullHardReset)
        fullCategory.addPreference(fullSoftReset)
        screen.addPreference(oldCategory)
        oldCategory.addPreference(oldHardReset)
        oldCategory.addPreference(oldSoftReset)
        oldCategory.addPreference(oldBooksMenu)
        screen.addPreference(newCategory)
        newCategory.addPreference(newHardReset)
        newCategory.addPreference(newSoftReset)
        newCategory.addPreference(newBooksMenu)
        preferenceScreen = screen
    }
}