package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref

class ManualListSet: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manual, container, false)
    }

    override fun onResume() {
        super.onResume()
        val root = requireView()
        val listSelector = root.findViewById<Spinner>(R.id.listSelector)
        val listSpinner1 = root.findViewById<Spinner>(R.id.listSpinner1)
        val listSpinner2 = root.findViewById<NumberPicker>(R.id.listSpinner2)
        val dark = getBoolPref(name="darkMode", defaultValue=true)
        val button = root.findViewById<Button>(R.id.set_button)
        if(dark){
            listSelector.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners_dark, (activity as MainActivity).theme)
            listSelector.setPopupBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.spinners_dark, (activity as MainActivity).theme))
            listSpinner1.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners_dark, (activity as MainActivity).theme)
            listSpinner1.setPopupBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.spinners_dark, (activity as MainActivity).theme))
            listSpinner2.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners_dark, (activity as MainActivity).theme)
            button.setTextColor(getColor(App.applicationContext(), R.color.unquenchedTextDark))
        }else{
            listSelector.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners, (activity as MainActivity).theme)
            listSelector.setPopupBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.spinners, (activity as MainActivity).theme))
            listSpinner1.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners, (activity as MainActivity).theme)
            listSpinner1.setPopupBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.spinners, (activity as MainActivity).theme))
            listSpinner2.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners, (activity as MainActivity).theme)
            button.setTextColor(getColor(App.applicationContext(), R.color.unquenchedText))
        }
        val listNames = ArrayList<String>()
        val planSystem = getStringPref(name="planSystem", defaultValue="pgh")
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
                when (getIntPref(name = "list1Done")) {
                    1 -> listNames.remove("The Gospels")
                }
                when (getIntPref(name = "list2Done")) {
                    1 -> listNames.remove("The Pentateuch")
                }
                when (getIntPref(name = "list3Done")) {
                    1 -> listNames.remove("Epistles I")
                }
                when (getIntPref(name = "list4Done")) {
                    1 -> listNames.remove("Epistles II")
                }
                when (getIntPref(name = "list5Done")) {
                    1 -> listNames.remove("Poetry")
                }
                when (getIntPref(name = "list6Done")) {
                    1 -> listNames.remove("Psalms")
                }
                when (getIntPref(name = "list7Done")) {
                    1 -> listNames.remove("Proverbs")
                }
                when (getIntPref(name = "list8Done")) {
                    1 -> listNames.remove("History")
                }
                when (getIntPref(name = "list9Done")) {
                    1 -> listNames.remove("Prophets")
                }
                when (getIntPref(name = "list10Done")) {
                    1 -> listNames.remove("Acts")
                }
            }
            "mcheyne" -> {
                listNames.add("Family I")
                listNames.add("Family II")
                listNames.add("Secret I")
                listNames.add("Secret II")
                when (getIntPref(name = "mcheyneList1Done")) {
                    1 -> listNames.remove("Family I")
                }
                when (getIntPref(name = "mcheyneList2Done")) {
                    1 -> listNames.remove("Family II")
                }
                when (getIntPref(name = "mcheyneList3Done")) {
                    1 -> listNames.remove("Secret I")
                }
                when (getIntPref(name = "mcheyneList4Done")) {
                    1 -> listNames.remove("Secret II")
                }
            }
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        listSelector.adapter = adapter


        listSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val buttonColor: Int = if(dark){
                    getColor(App.applicationContext(), R.color.buttonBackgroundDark)
                }else{
                    getColor(App.applicationContext(), R.color.buttonBackground)
                }

                val selectedItem      = parent?.getItemAtPosition(position).toString()
                val list              = root.findViewById<Spinner>(R.id.listSpinner1)
                val verseSelector     = root.findViewById<LinearLayout>(R.id.verseSelector)
                verseSelector.visibility     = View.INVISIBLE
                when(selectedItem){
                    "----"             -> {

                        button.isEnabled = false
                        button.isVisible = false
                        button.backgroundTintList = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.noneBook,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                        }

                        list.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val verse = root.findViewById<NumberPicker>(R.id.listSpinner2)
                                verse.minValue = 0
                                verse.maxValue = 0
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        } }

                    "Family I"         ->{
                        val cInfo                   = getCurrentInfo(1, "mcheyne") as String
                        verseSelector.visibility    = View.INVISIBLE

                        button.isEnabled            = true
                        button.isVisible            = true
                        button.backgroundTintList   = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.mcheyne_list2,
                                android.R.layout.simple_spinner_item).also{
                                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    list.adapter = it
                                    list.setSelection(it.getPosition(cInfo)) }
                    }
                    "Family II"         ->{
                        val cInfo                   = getCurrentInfo(2, "mcheyne") as String
                        verseSelector.visibility    = View.INVISIBLE

                        button.isEnabled            = true
                        button.isVisible            = true
                        button.backgroundTintList   = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.mcheyne_list3,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cInfo)) }
                    }
                    "Secret I"         ->{
                        val cInfo                   = getCurrentInfo(3, "mcheyne") as String
                        verseSelector.visibility    = View.INVISIBLE

                        button.isEnabled            = true
                        button.isVisible            = true
                        button.backgroundTintList   = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.mcheyne_list1,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cInfo)) }
                    }
                    "Secret II"         ->{
                        val cInfo                   = getCurrentInfo(4, "mcheyne") as String
                        verseSelector.visibility    = View.INVISIBLE

                        button.isEnabled            = true
                        button.isVisible            = true
                        button.backgroundTintList   = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.mcheyne_list4,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cInfo)) }
                    }

                    "The Gospels"      -> {
                        val cInfo                    = getCurrentInfo(1) as Array<*>
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.isVisible             = true
                        button.backgroundTintList    = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.list1Book,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cBook)) }

                        list.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val book                  = parent?.getItemAtPosition(position).toString()
                                val verse                 = root.findViewById<NumberPicker>(R.id.listSpinner2)
                                verse.minValue            = 1
                                verse.visibility          = View.VISIBLE

                                when(book){
                                    "Matthew"                       -> { verse.maxValue = 28; verse.value = cVerse }
                                    "Mark"                          -> { verse.maxValue = 16; verse.value = cVerse }
                                    "Luke"                          -> { verse.maxValue = 24; verse.value = cVerse }
                                    "John"                          -> { verse.maxValue = 21; verse.value = cVerse }
                                } }

                            override fun onNothingSelected(parent: AdapterView<*>?) {} } }

                    "The Pentateuch"   -> {
                        val cInfo                    = getCurrentInfo(2) as Array<*>
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.isVisible             = true
                        button.backgroundTintList    = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.list2Book,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cBook))}

                        list.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val book                  = parent?.getItemAtPosition(position).toString()
                                val verse                 = root.findViewById<NumberPicker>(R.id.listSpinner2)
                                verse.minValue            = 1
                                verse.visibility = View.VISIBLE

                                when(book){
                                    "Genesis"                       -> { verse.maxValue = 50; verse.value = cVerse }
                                    "Exodus"                        -> { verse.maxValue = 40; verse.value = cVerse }
                                    "Leviticus"                     -> { verse.maxValue = 27; verse.value = cVerse }
                                    "Numbers"                       -> { verse.maxValue = 36; verse.value = cVerse }
                                    "Deuteronomy"                   -> { verse.maxValue = 34; verse.value = cVerse }
                                } }

                            override fun onNothingSelected(parent: AdapterView<*>?) {} } }


                    "Epistles I"       -> {
                        val cInfo                    = getCurrentInfo(3) as Array<*>
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.isVisible             = true
                        button.backgroundTintList = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.list3Book,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cBook)) }

                        list.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val book             = parent?.getItemAtPosition(position).toString()
                                val verse            = root.findViewById<NumberPicker>(R.id.listSpinner2)
                                verse.visibility = View.VISIBLE
                                verse.minValue       = 1

                                when(book){
                                    "Romans", "1 Corinthians"       -> { verse.maxValue = 16; verse.value = cVerse }
                                    "2 Corinthians", "Hebrews"      -> { verse.maxValue = 13; verse.value = cVerse }
                                    "Galatians", "Ephesians"        -> { verse.maxValue = 6; verse.value = cVerse }
                                    "Philippians", "Colossians"     -> { verse.maxValue = 4; verse.value = cVerse } } }

                            override fun onNothingSelected(parent: AdapterView<*>?) {} } }


                    "Epistles II"      -> {
                        val cInfo                    = getCurrentInfo(4) as Array<*>
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.isVisible             = true
                        button.backgroundTintList = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.list4Book,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cBook)) }

                        list.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val book                  = parent?.getItemAtPosition(position).toString()
                                val verse                 = root.findViewById<NumberPicker>(R.id.listSpinner2)
                                verse.minValue            = 1
                                verse.visibility = View.VISIBLE

                                when(book){
                                    "1 Thessalonians", "James",
                                    "1 Peter", "1 John"             -> { verse.maxValue =  5; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "2 Thessalonians", "2 Peter",
                                    "Titus"                         -> { verse.maxValue =  3; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "1 Timothy"                     -> { verse.maxValue =  6; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "2 Timothy"                     -> { verse.maxValue =  4; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "Philemon", "2 John",
                                    "3 John", "Jude"                -> { verse.maxValue =  1; verse.value = cVerse; verse.visibility = View.INVISIBLE }
                                    "Revelation"                   -> { verse.maxValue = 22; verse.value = cVerse; verse.visibility = View.VISIBLE   } } }

                            override fun onNothingSelected(parent: AdapterView<*>?) {} }}


                    "Poetry"           ->{
                        val cInfo                    = getCurrentInfo(5) as Array<*>
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.isVisible             = true
                        button.backgroundTintList = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.list5Book,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cBook)) }

                        list.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val book                  = parent?.getItemAtPosition(position).toString()
                                val verse                 = root.findViewById<NumberPicker>(R.id.listSpinner2)
                                verse.minValue            = 1
                                verse.visibility = View.VISIBLE

                                when(book) {
                                    "Job"                           -> { verse.maxValue = 42; verse.value = cVerse }
                                    "Ecclesiastes"                  -> { verse.maxValue = 12; verse.value = cVerse }
                                    "Song of Solomon"               -> { verse.maxValue =  8; verse.value = cVerse } } }

                            override fun onNothingSelected(parent: AdapterView<*>?) {} }}


                    "Psalms"           -> {
                        val cInfo                  = getCurrentInfo(6) as Array<*>
                        val cBook                  = cInfo[0] as String
                        val cVerse                 = cInfo[1] as Int
                        verseSelector.visibility   = View.VISIBLE

                        button.isEnabled           = true
                        button.isVisible           = true
                        button.backgroundTintList = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.list6Book,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cBook)) }

                        list.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val verse                 = root.findViewById<NumberPicker>(R.id.listSpinner2)
                                verse.minValue            = 1
                                verse.value               = cVerse
                                verse.maxValue            = 150
                                verse.visibility = View.VISIBLE }

                            override fun onNothingSelected(parent: AdapterView<*>?) {} }}


                    "Proverbs"         -> {
                        val cInfo                    = getCurrentInfo(7) as Array<*>
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.isVisible             = true
                        button.backgroundTintList = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.list7Book,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cBook)) }

                        list.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val verse                 = root.findViewById<NumberPicker>(R.id.listSpinner2)
                                verse.visibility = View.VISIBLE

                                verse.minValue            = 1
                                verse.value               = cVerse
                                verse.maxValue            = 31 }

                            override fun onNothingSelected(parent: AdapterView<*>?) {} }}


                    "History"          -> {
                        val cInfo                    = getCurrentInfo(8) as Array<*>
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.isVisible             = true
                        button.backgroundTintList = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.list8Book,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cBook)) }

                        list.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val book                  = parent?.getItemAtPosition(position).toString()
                                val verse                 = root.findViewById<NumberPicker>(R.id.listSpinner2)
                                verse.minValue            = 1
                                verse.visibility = View.VISIBLE

                                when(book){
                                    "Joshua", "2 Samuel"            -> { verse.maxValue = 24; verse.value = cVerse }
                                    "Judges"                        -> { verse.maxValue = 21; verse.value = cVerse }
                                    "Ruth"                          -> { verse.maxValue =  4; verse.value = cVerse }
                                    "1 Samuel"                      -> { verse.maxValue = 31; verse.value = cVerse }
                                    "1 Kings"                       -> { verse.maxValue = 22; verse.value = cVerse }
                                    "2 Kings"                       -> { verse.maxValue = 25; verse.value = cVerse }
                                    "1 Chronicles"                  -> { verse.maxValue = 29; verse.value = cVerse }
                                    "2 Chronicles"                  -> { verse.maxValue = 36; verse.value = cVerse }
                                    "Ezra", "Esther"                -> { verse.maxValue = 10; verse.value = cVerse }
                                    "Nehemiah"                      -> { verse.maxValue = 13; verse.value = cVerse } } }
                            override fun onNothingSelected(parent: AdapterView<*>?) {} }}


                    "Prophets"         -> {
                        val cInfo                    = getCurrentInfo(9) as Array<*>
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.isVisible             = true
                        button.backgroundTintList = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.list9Book,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cBook))}

                        list.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val book                  = parent?.getItemAtPosition(position).toString()
                                val verse                 = root.findViewById<NumberPicker>(R.id.listSpinner2)
                                verse.minValue            = 1
                                verse.visibility = View.VISIBLE

                                when(book){
                                    "Isaiah"                        -> { verse.maxValue = 66; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "Jeremiah"                      -> { verse.maxValue = 52; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "Lamentations"                  -> { verse.maxValue =  5; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "Ezekiel"                       -> { verse.maxValue = 48; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "Daniel"                        -> { verse.maxValue = 12; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "Hosea", "Zechariah"            -> { verse.maxValue = 14; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "Joel", "Nahum",
                                    "Habakkuk", "Zephaniah"         -> { verse.maxValue =  3; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "Amos"                          -> { verse.maxValue =  9; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "Obadiah"                       -> { verse.maxValue =  1; verse.value = cVerse; verse.visibility = View.INVISIBLE }
                                    "Jonah", "Malachi"              -> { verse.maxValue =  4; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "Micah"                         -> { verse.maxValue =  7; verse.value = cVerse; verse.visibility = View.VISIBLE   }
                                    "Haggai"                        -> { verse.maxValue =  2; verse.value = cVerse; verse.visibility = View.VISIBLE   } } }

                            override fun onNothingSelected(parent: AdapterView<*>?) {} }}


                    "Acts"             -> {
                        val cInfo                    = getCurrentInfo(10) as Array<*>
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.isVisible             = true
                        button.backgroundTintList = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context!!,
                                R.array.list10Book,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it }

                        list.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val verse                 = root.findViewById<NumberPicker>(R.id.listSpinner2)
                                verse.minValue            = 1
                                verse.maxValue            = 28
                                verse.value               = cVerse
                                verse.visibility = View.VISIBLE}

                            override fun onNothingSelected(parent: AdapterView<*>?) {} } } } }


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
            if(planSystem == "pgh") {
                alert.setMessage("Are you sure you want to set $selectedList to $selectedBook $selectedVerse?")
            }else{
                alert.setMessage("Are you sure you want to set $selectedList to $selectedBook?")
            }
            alert.setPositiveButton("Yes") { dialogInterface, _ ->
                when(selectedList) {
                    "Family I"    -> {
                        val array = resources.getStringArray(R.array.mcheyne_list1)
                        val num = array.indexOf("$selectedBook")
                        setIntPref(name="mcheyneList1", value=num, updateFS=true)
                    }
                    "Family II"   -> {
                        val array = resources.getStringArray(R.array.mcheyne_list2)
                        val num = array.indexOf("$selectedBook")
                        setIntPref(name="mcheyneList2", value=num, updateFS=true)
                    }
                    "Secret I"    -> {
                        val array = resources.getStringArray(R.array.mcheyne_list3)
                        val num = array.indexOf("$selectedBook")
                        setIntPref(name="mcheyneList3", value=num, updateFS=true)
                    }
                    "Secret II"   -> {
                        val array = resources.getStringArray(R.array.mcheyne_list4)
                        val num = array.indexOf("$selectedBook")
                        setIntPref(name="mcheyneList4", value=num, updateFS=true)
                    }
                    "The Gospels" -> {
                        val array = resources.getStringArray(R.array.list_1)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref(name = "list1", value=num, updateFS=true)
                    }


                    "The Pentateuch" -> {
                        val array = resources.getStringArray(R.array.list_2)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref(name = "list2", value = num, updateFS=true)
                    }


                    "Epistles I" -> {
                        val array = resources.getStringArray(R.array.list_3)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref(name = "list3", value = num, updateFS = true)
                    }


                    "Epistles II" -> {
                        val array = resources.getStringArray(R.array.list_4)
                        val num = if (selectedBook == "Philemon" || selectedBook == "Jude" || selectedBook == "2 John" || selectedBook == "3 John") {
                            array.indexOf("$selectedBook")
                        } else {
                            array.indexOf("$selectedBook $selectedVerse")
                        }
                        setIntPref(name = "list4", value = num, updateFS = true)
                    }


                    "Poetry" -> {
                        val array = resources.getStringArray(R.array.list_5)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref(name = "list5", value = num, updateFS = true)
                    }


                    "Psalms" -> {
                        val array = resources.getStringArray(R.array.list_6)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref(name = "list6", value = num, updateFS = true)
                    }


                    "Proverbs" -> {
                        val array = resources.getStringArray(R.array.list_7)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref(name = "list7", value = num, updateFS = true)
                    }


                    "History" -> {
                        val array = resources.getStringArray(R.array.list_8)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref(name="list8", value=num, updateFS=true)
                    }


                    "Prophets" -> {
                        val array = resources.getStringArray(R.array.list_9)
                        val num = if (selectedBook == "Obadiah") {
                            array.indexOf("$selectedBook")
                        } else {
                            array.indexOf("$selectedBook $selectedVerse")
                        }
                        setIntPref(name="list9", value=num, updateFS=true)
                    }


                    "Acts" -> {
                        val array = resources.getStringArray(R.array.list_10)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref(name="list10", value=num, updateFS=true)
                    }
                }


                dialogInterface.dismiss()
                Toast.makeText(context, "Changed $selectedList", Toast.LENGTH_LONG).show() }


            alert.setNeutralButton("Cancel"){dialogInterface, _ ->
                dialogInterface.dismiss() }


            alert.create().show() } }


    fun getCurrentInfo(listNum:Int, type:String="pgh"): Any {
        val data: Array<Any> = if(type=="pgh") {
            val returnVal: Array<Any> = when (listNum) {
                1 -> arrayOf(getIntPref("list1"), R.array.list_1)
                2 -> arrayOf(getIntPref("list2"), R.array.list_2)
                3 -> arrayOf(getIntPref("list3"), R.array.list_3)
                4 -> arrayOf(getIntPref("list4"), R.array.list_4)
                5 -> arrayOf(getIntPref("list5"), R.array.list_5)
                6 -> arrayOf(getIntPref("list6"), R.array.list_6)
                7 -> arrayOf(getIntPref("list7"), R.array.list_7)
                8 -> arrayOf(getIntPref("list8"), R.array.list_8)
                9 -> arrayOf(getIntPref("list9"), R.array.list_9)
                10 -> arrayOf(getIntPref("list10"), R.array.list_10)
                else -> arrayOf(0, 0)
            }
            returnVal
        }else{
            val returnVal: Array<Any> = when(listNum){
                1 -> arrayOf(getIntPref("mcheyneList1"), R.array.mcheyne_list1)
                2 -> arrayOf(getIntPref("mcheyneList2"), R.array.mcheyne_list2)
                3 -> arrayOf(getIntPref("mcheyneList3"), R.array.mcheyne_list3)
                4 -> arrayOf(getIntPref("mcheyneList4"), R.array.mcheyne_list4)
                else -> arrayOf(0, 0)
            }
            returnVal
        }
        val number = data[0] as Int
        val listId = data[1] as Int

        val list = resources.getStringArray(listId)
        val fullValue = list[number]
        val afterSplit = fullValue.split(" ")

        return if(type=="pgh"){
           val returnVal = when (afterSplit.size){
                1 -> arrayOf<Any>(fullValue, 1)
                3 -> arrayOf<Any>("${afterSplit[0]} ${afterSplit[1]}", afterSplit[2].toInt())
                4 -> arrayOf<Any>("${afterSplit[0]} ${afterSplit[1]} ${afterSplit[2]}", afterSplit[3].toInt())
                else -> arrayOf<Any>(afterSplit[0], afterSplit[1].toInt()) }
            returnVal
        } else{
            fullValue
        }
    }
}