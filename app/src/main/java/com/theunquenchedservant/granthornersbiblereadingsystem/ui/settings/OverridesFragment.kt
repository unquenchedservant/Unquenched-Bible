package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Firestore
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManualListNumericalSet: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manual_numerical, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mainActivity = activity as MainActivity
        val dayPicker = view.findViewById<NumberPicker>(R.id.dayPickerSpinner)
        val selectButton = view.findViewById<Button>(R.id.set_button)
        val preferences = App().preferences!!
        dayPicker.background = preferences.colors.listSelectorDrawable
        selectButton.setBackgroundColor(preferences.colors.background)
        selectButton.setTextColor(preferences.colors.textColor)
        dayPicker.minValue = 1
        dayPicker.maxValue = 9999
        val planSystem = preferences.settings.planSystem
        val homeId = when(planSystem){
            "pgh"->R.id.navigation_home
            "mcheyne"->R.id.navigation_home_mcheyne
            else->R.id.navigation_home
        }
        dayPicker.value = preferences.list.currentIndex + 1
        selectButton.setOnClickListener {
            val alert             = AlertDialog.Builder(requireContext())
            alert.setTitle("Set Day?")
            alert.setMessage("Are you sure you want to set the current day of reading to ${dayPicker.value}")
            alert.setPositiveButton("Yes") { dialogInterface, _ ->
                preferences.list.currentIndex = dayPicker.value - 1
                dialogInterface.dismiss()
                Toast.makeText(context, "Changed current day of reading", Toast.LENGTH_LONG).show()
                mainActivity.navController.navigate(homeId)
            }
            alert.setNeutralButton("Cancel"){dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            alert.create().show()
        }
    }
}

class ManualListSet: Fragment() {
    val preferences = App().preferences!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manual, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val root = view
        val listSelector = root.findViewById<Spinner>(R.id.listSelector)
        val listSpinner1 = root.findViewById<Spinner>(R.id.listSpinner1)
        val listSpinner2 = root.findViewById<NumberPicker>(R.id.listSpinner2)
        val button = root.findViewById<Button>(R.id.set_button)
        val context = App.applicationContext()
        val colors = preferences.colors
        val listSelectorDrawable = colors.listSelectorDrawable
        listSelector.background = listSelectorDrawable
        listSelector.setPopupBackgroundDrawable(listSelectorDrawable)
        listSpinner1.background = listSelectorDrawable
        listSpinner1.setPopupBackgroundDrawable(listSelectorDrawable)
        listSpinner2.background = listSelectorDrawable
        button.setTextColor(colors.textColor)
        val lists = preferences.list
        val listNames = ArrayList<String>()
        val planSystem = preferences.settings.planSystem
        listNames.add("----")
        when(planSystem) {
            "pgh" -> {
                listNames.add("The Gospels")
                listNames.add("The Pentateuch")
                listNames.add("Epistles I")
                listNames.add("Epistles II")
                listNames.add("Poetry")
                listNames.add("Psalms")
                listNames.add("Proverbs")
                listNames.add("History")
                listNames.add("Prophets")
                listNames.add("Acts")
                if (lists.list1.listDone) listNames.remove("The Gospels")
                if (lists.list2.listDone) listNames.remove("The Pentateuch")
                if (lists.list3.listDone) listNames.remove("Epistles I")
                if (lists.list4.listDone) listNames.remove("Epistles II")
                if (lists.list5.listDone) listNames.remove("Poetry")
                if (lists.list6.listDone) listNames.remove("Psalms")
                if (lists.list7.listDone) listNames.remove("Proverbs")
                if (lists.list8.listDone) listNames.remove("History")
                if (lists.list9.listDone) listNames.remove("Prophets")
                if (lists.list10.listDone) listNames.remove("Acts")
            }
            "mcheyne" -> {
                listNames.add("Family I")
                listNames.add("Family II")
                listNames.add("Secret I")
                listNames.add("Secret II")
                if (lists.list1.listDone) listNames.remove("Family I")
                if (lists.list2.listDone) listNames.remove("Family II")
                if (lists.list3.listDone) listNames.remove("Secret I")
                if (lists.list4.listDone) listNames.remove("Secret II")

            }
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        listSelector.adapter = adapter


        listSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val buttonColor = colors.buttonBackground
                val selectedItem = parent?.getItemAtPosition(position).toString()
                val list = root.findViewById<Spinner>(R.id.listSpinner1)
                val verseSelector = root.findViewById<LinearLayout>(R.id.verseSelector)
                verseSelector.visibility = View.INVISIBLE
                when (selectedItem) {
                    "----" -> {
                        button.isEnabled = false
                        button.isVisible = false
                        button.backgroundTintList = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                            context,
                            R.array.noneBook,
                            android.R.layout.simple_spinner_item
                        ).also {
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                        }

                        list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val verse = root.findViewById<NumberPicker>(R.id.listSpinner2)
                                verse.minValue = 0
                                verse.maxValue = 0
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                    }

                    "Family I" -> setSpinner(preferences.list.list1, verseSelector, listSpinner2, button, buttonColor, list)
                    "Family II" -> setSpinner(preferences.list.list2, verseSelector, listSpinner2, button, buttonColor, list)
                    "Secret I" -> setSpinner(preferences.list.list3, verseSelector, listSpinner2, button, buttonColor, list)
                    "Secret II" -> setSpinner(preferences.list.list4, verseSelector, listSpinner2, button, buttonColor, list)
                    "The Gospels" -> setPGHSpinner(listSpinner2, button, buttonColor, list, R.array.list1Book, preferences.list.list1)
                    "The Pentateuch" -> setPGHSpinner(listSpinner2, button, buttonColor, list, R.array.list2Book, preferences.list.list2)
                    "Epistles I" -> setPGHSpinner(listSpinner2, button, buttonColor, list, R.array.list3Book, preferences.list.list3)
                    "Epistles II" -> setPGHSpinner(listSpinner2, button, buttonColor, list, R.array.list4Book, preferences.list.list4)
                    "Poetry" -> setPGHSpinner(listSpinner2, button, buttonColor, list, R.array.list5Book, preferences.list.list5)
                    "Psalms" -> setPGHSpinner(listSpinner2, button, buttonColor, list, R.array.list5Book, preferences.list.list6)
                    "Proverbs" -> setPGHSpinner(listSpinner2, button, buttonColor, list, R.array.list6Book, preferences.list.list7)
                    "History" -> setPGHSpinner(listSpinner2, button, buttonColor, list, R.array.list8Book, preferences.list.list8)
                    "Prophets" -> setPGHSpinner(listSpinner2, button, buttonColor, list, R.array.list9Book, preferences.list.list9)
                    "Acts" -> setPGHSpinner(listSpinner2, button, buttonColor, list, R.array.list10Book, preferences.list.list10)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {} }


        button.setOnClickListener {
            val alert             = AlertDialog.Builder(requireContext())
            val listSelected      = root.findViewById<Spinner>(R.id.listSelector)
            val selectedList      = listSelected.selectedItem
            val bookSpinner       = root.findViewById<Spinner>(R.id.listSpinner1)
            val selectedBook      = bookSpinner.selectedItem
            val verseSpinner      = root.findViewById<NumberPicker>(R.id.listSpinner2)
            val selectedVerse     = verseSpinner.value

            alert.setTitle("Set $selectedList?")
            val reading:String = if(planSystem == "pgh") {
                alert.setMessage("Are you sure you want to set $selectedList to $selectedBook $selectedVerse?")
                "$selectedBook"
            }else {
                alert.setMessage("Are you sure you want to set $selectedList to $selectedBook?")
                if(selectedBook == "Philemon" || selectedBook == "Jude" || selectedBook == "2 John" || selectedBook == "3 John" || selectedBook == "Obadiah") {
                    "$selectedBook"
                }else "$selectedBook $selectedVerse"
            }
            alert.setPositiveButton("Yes") { dialogInterface, _ ->
                when(selectedList) {
                    "Family I"       -> setList(reading, R.array.mcheyne_list1, lists.list1)
                    "Family II"      -> setList(reading, R.array.mcheyne_list2, lists.list2)
                    "Secret I"       -> setList(reading, R.array.mcheyne_list3, lists.list3)
                    "Secret II"      -> setList(reading, R.array.mcheyne_list4, lists.list4)
                    "The Gospels"    -> setList(reading, R.array.list_1, lists.list1)
                    "The Pentateuch" -> setList(reading, R.array.list_2, lists.list2)
                    "Epistles I"     -> setList(reading, R.array.list_3, lists.list3)
                    "Epistles II"    -> setList(reading, R.array.list_4, lists.list4)
                    "Poetry"         -> setList(reading, R.array.list_5, lists.list5)
                    "Psalms"         -> setList(reading, R.array.list_6, lists.list6)
                    "Proverbs"       -> setList(reading, R.array.list_7, lists.list7)
                    "History"        -> setList(reading, R.array.list_8, lists.list8)
                    "Prophets"       -> setList(reading, R.array.list_9, lists.list9)
                    "Acts"           -> setList(reading, R.array.list_10, lists.list10)
                }


                dialogInterface.dismiss()
                Toast.makeText(context, "Changed $selectedList", Toast.LENGTH_LONG).show() }


            alert.setNeutralButton("Cancel"){dialogInterface, _ ->
                dialogInterface.dismiss() }


            alert.create().show() } }


    fun getCurrentInfo(listItem:ListItem, type:String="pgh"): Any {
        val data: Array<Any> = arrayOf(listItem.listIndex, listItem.listId)
        val number = data[0] as Int
        val listId = data[1] as Int

        val list = resources.getStringArray(listId)
        val fullValue = list[number]
        val afterSplit = fullValue.split(" ")
        return if(type=="pgh"){
            var lastIndex = if(afterSplit.lastIndex == 0) 1 else afterSplit.lastIndex
            lastIndex = if(afterSplit[0].toIntOrNull() != null && afterSplit[lastIndex].toIntOrNull() == null) lastIndex + 1 else lastIndex
            val book = afterSplit.subList(0, lastIndex).joinToString(" ")
            val chapter = if(afterSplit[afterSplit.lastIndex].toIntOrNull() != null) afterSplit[afterSplit.lastIndex].toInt() else 1
            arrayOf<Any>(book, chapter)
        } else{
            fullValue
        }
    }
    fun setList(reading:String, listArray:Int, listItem:ListItem) {
        val array = resources.getStringArray(listArray)
        val num = array.indexOf(reading)
        listItem.listIndex = num
        CoroutineScope(Dispatchers.IO).launch {
            Firestore().updateFirestoreData(preferences.data)
        }
    }
    fun setSpinner(listItem: ListItem, verseSelector:LinearLayout, listSpinner:NumberPicker, button:Button, buttonColor:Int, list:Spinner):Spinner{
        verseSelector.visibility    = View.VISIBLE
        listSpinner.visibility     = View.INVISIBLE
        button.isEnabled           = true
        button.isVisible            = true
        button.backgroundTintList   = ColorStateList.valueOf(buttonColor)


        ArrayAdapter.createFromResource(App().applicationContext, listItem.listId, android.R.layout.simple_spinner_item).also{
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            list.adapter = it
            list.setSelection(it.getPosition(arrayOf(listItem.listIndex, listItem.listId).toString()))
        }
        return list
    }
    fun setPGHSpinner(listSpinner:NumberPicker, button:Button, buttonColor:Int, list:Spinner, listArray:Int, listItem:ListItem){
        val cInfo                    = getCurrentInfo(listItem) as Array<*>
        val cBook                    = cInfo[0] as String
        val cVerse                   = cInfo[1] as Int

        button.isEnabled             = true
        button.isVisible             = true
        button.backgroundTintList    = ColorStateList.valueOf(buttonColor)

        ArrayAdapter.createFromResource(App().applicationContext, listArray, android.R.layout.simple_spinner_item).also{
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            list.adapter = it
            list.setSelection(it.getPosition(cBook))
        }

        list.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val book                        = parent?.getItemAtPosition(position).toString()
                listSpinner.minValue            = 1
                listSpinner.visibility          = View.VISIBLE
                
                when(book){
                    "Matthew"                       -> { listSpinner.maxValue = 28; listSpinner.value = cVerse }
                    "Mark"                          -> { listSpinner.maxValue = 16; listSpinner.value = cVerse }
                    "Luke"                          -> { listSpinner.maxValue = 24; listSpinner.value = cVerse }
                    "John"                          -> { listSpinner.maxValue = 21; listSpinner.value = cVerse }
                    "Genesis"                       -> { listSpinner.maxValue = 50; listSpinner.value = cVerse }
                    "Exodus"                        -> { listSpinner.maxValue = 40; listSpinner.value = cVerse }
                    "Leviticus"                     -> { listSpinner.maxValue = 27; listSpinner.value = cVerse }
                    "Numbers"                       -> { listSpinner.maxValue = 36; listSpinner.value = cVerse }
                    "Deuteronomy"                   -> { listSpinner.maxValue = 34; listSpinner.value = cVerse }
                    "Romans", "1 Corinthians"       -> { listSpinner.maxValue = 16; listSpinner.value = cVerse }
                    "2 Corinthians", "Hebrews"      -> { listSpinner.maxValue = 13; listSpinner.value = cVerse }
                    "Galatians", "Ephesians"        -> { listSpinner.maxValue = 6; listSpinner.value = cVerse }
                    "Philippians", "Colossians"     -> { listSpinner.maxValue = 4; listSpinner.value = cVerse }
                    "1 Thessalonians", "James",
                    "1 Peter", "1 John"             -> { listSpinner.maxValue =  5; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "2 Thessalonians", "2 Peter",
                    "Titus"                         -> { listSpinner.maxValue =  3; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "1 Timothy"                     -> { listSpinner.maxValue =  6; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "2 Timothy"                     -> { listSpinner.maxValue =  4; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "Philemon", "2 John",
                    "3 John", "Jude"                -> { listSpinner.maxValue =  1; listSpinner.value = cVerse; listSpinner.visibility = View.INVISIBLE }
                    "Revelation"                   -> { listSpinner.maxValue = 22; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "Job"                           -> { listSpinner.maxValue = 42; listSpinner.value = cVerse }
                    "Ecclesiastes"                  -> { listSpinner.maxValue = 12; listSpinner.value = cVerse }
                    "Song of Solomon"               -> { listSpinner.maxValue =  8; listSpinner.value = cVerse }
                    "Psalms"                        -> { listSpinner.maxValue = 150; listSpinner.value = cVerse }
                    "Proverbs"                      -> { listSpinner.maxValue = 31; listSpinner.value = cVerse }
                    "Joshua", "2 Samuel"            -> { listSpinner.maxValue = 24; listSpinner.value = cVerse }
                    "Judges"                        -> { listSpinner.maxValue = 21; listSpinner.value = cVerse }
                    "Ruth"                          -> { listSpinner.maxValue =  4; listSpinner.value = cVerse }
                    "1 Samuel"                      -> { listSpinner.maxValue = 31; listSpinner.value = cVerse }
                    "1 Kings"                       -> { listSpinner.maxValue = 22; listSpinner.value = cVerse }
                    "2 Kings"                       -> { listSpinner.maxValue = 25; listSpinner.value = cVerse }
                    "1 Chronicles"                  -> { listSpinner.maxValue = 29; listSpinner.value = cVerse }
                    "2 Chronicles"                  -> { listSpinner.maxValue = 36; listSpinner.value = cVerse }
                    "Ezra", "Esther"                -> { listSpinner.maxValue = 10; listSpinner.value = cVerse }
                    "Nehemiah"                      -> { listSpinner.maxValue = 13; listSpinner.value = cVerse }
                    "Isaiah"                        -> { listSpinner.maxValue = 66; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "Jeremiah"                      -> { listSpinner.maxValue = 52; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "Lamentations"                  -> { listSpinner.maxValue =  5; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "Ezekiel"                       -> { listSpinner.maxValue = 48; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "Daniel"                        -> { listSpinner.maxValue = 12; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "Hosea", "Zechariah"            -> { listSpinner.maxValue = 14; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "Joel", "Nahum",
                    "Habakkuk", "Zephaniah"         -> { listSpinner.maxValue =  3; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "Amos"                          -> { listSpinner.maxValue =  9; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "Obadiah"                       -> { listSpinner.maxValue =  1; listSpinner.value = cVerse; listSpinner.visibility = View.INVISIBLE }
                    "Jonah", "Malachi"              -> { listSpinner.maxValue =  4; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "Micah"                         -> { listSpinner.maxValue =  7; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "Haggai"                        -> { listSpinner.maxValue =  2; listSpinner.value = cVerse; listSpinner.visibility = View.VISIBLE   }
                    "Acts"                          -> { listSpinner.maxValue = 28; listSpinner.value = cVerse }
                    
                } }

            override fun onNothingSelected(parent: AdapterView<*>?) {} } }
}
class OverridesFragment:PreferenceFragmentCompat(){
    val preferences = App().preferences!!
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.override_preferences, rootKey)
        val manual: Preference? = findPreference("manualSetLists")
        val resetAll: Preference? = findPreference("resetAll")
        val dailyReset: Preference? = findPreference("resetDaily")
        val mainActivity = activity as MainActivity
        if(preferences.settings.planType == "calendar"){
            manual!!.isEnabled = false //Can't change the current date, can ya?
        }
        if(preferences.settings.planType == "numerical" && preferences.list.listsDone != 0){
            manual!!.isEnabled  = false //can't make a change if you've already completed some of the lists.
        }
        resetAll!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val planType = if(preferences.settings.planSystem == "pgh"){
                "Grant Horner System"
            }else if(preferences.settings.planSystem == "mcheyne"){
                "M'Cheyne Reading Plan"
            }else{
                "ERROR SHOULDN'T SEE THIS"
            }
            if(preferences.settings.planType != "calendar") {
                val alert = AlertDialog.Builder(context)
                alert.setPositiveButton("Yes") { _, _ ->
                    preferences.list.hardReset()
                    CoroutineScope(Dispatchers.IO).launch{
                        Firestore().updateFirestoreData(preferences.getMap())
                    }
                }
                alert.setNegativeButton("Nevermind") { dialog, _ ->
                    dialog.dismiss()
                }
                val message = "Are you sure you want to reset your progress in the ${planType}?"
                val title = "Reset Progress?"
                alert.setTitle(title)
                alert.setMessage(message)
                alert.show()
            }else{
                val alert = AlertDialog.Builder(context)
                alert.setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
                alert.setMessage("Sorry, can't reset the reading lists while on the calendar reading type")
                alert.setTitle("Unable to Reset")
                alert.show()
            }
            false
        }
        manual!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            when(preferences.settings.planType) {
                "horner" -> {
                    mainActivity.navController.navigate(R.id.navigation_manual)
                }
                "numerical"->{
                    mainActivity.navController.navigate(R.id.navigation_manual_numerical)
                }
            }
            false
        }
        dailyReset!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            preferences.list.resetList()
            CoroutineScope(Dispatchers.IO).launch {
                Firestore().updateFirestoreData(preferences.getMap())
            }
            Toast.makeText(mainActivity.applicationContext, "Forced Daily Reset", Toast.LENGTH_LONG).show()
            val homeId = if(preferences.settings.planSystem == "pgh") R.id.navigation_home else R.id.navigation_home_mcheyne
            mainActivity.navController.navigate(homeId)
            false
        }
    }

}