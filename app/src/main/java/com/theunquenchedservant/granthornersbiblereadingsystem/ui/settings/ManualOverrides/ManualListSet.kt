package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings.ManualOverrides

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
        val context = App.applicationContext()
        if(dark){
            listSelector.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners_dark, (activity as MainActivity).theme)
            listSelector.setPopupBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.spinners_dark, (activity as MainActivity).theme))
            listSpinner1.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners_dark, (activity as MainActivity).theme)
            listSpinner1.setPopupBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.spinners_dark, (activity as MainActivity).theme))
            listSpinner2.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners_dark, (activity as MainActivity).theme)
            button.setTextColor(getColor(context, R.color.unquenchedTextDark))
        }else{
            listSelector.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners, (activity as MainActivity).theme)
            listSelector.setPopupBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.spinners, (activity as MainActivity).theme))
            listSpinner1.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners, (activity as MainActivity).theme)
            listSpinner1.setPopupBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.spinners, (activity as MainActivity).theme))
            listSpinner2.background = ResourcesCompat.getDrawable(resources, R.drawable.spinners, (activity as MainActivity).theme)
            button.setTextColor(getColor(context, R.color.unquenchedText))
        }
        val listNames = ArrayList<String>()
        val planSystem = getStringPref(name="planSystem", defaultValue="pgh")
        listNames.add("----")
        when(planSystem) {
            "pgh" -> {
                if (!getBoolPref(name = "pgh1Done")) listNames.add("Gospels")
                if (!getBoolPref(name = "pgh2Done")) listNames.add("The Pentateuch")
                if (!getBoolPref(name = "pgh3Done")) listNames.add("Epistles I")
                if (!getBoolPref(name = "pgh4Done")) listNames.add("Epistles II")
                if (!getBoolPref(name = "pgh5Done")) listNames.add("Poetry")
                if (!getBoolPref(name = "pgh6Done") || !getBoolPref("psalms")) listNames.add("Psalms")
                if (!getBoolPref(name = "pgh7Done")) listNames.add("Proverbs")
                if (!getBoolPref(name = "pgh8Done")) listNames.add("History")
                if (!getBoolPref(name = "pgh9Done")) listNames.add("Prophets")
                if (!getBoolPref(name = "pgh10Done")) listNames.add("Acts")
            }
            "mcheyne" -> {
                if (!getBoolPref(name = "mcheyne1Done")) listNames.add("Family I")
                if (!getBoolPref(name = "mcheyne2Done")) listNames.add("Family II")
                if (!getBoolPref(name = "mcheyne3Done")) listNames.add("Secret I")
                if (!getBoolPref(name = "mcheyne4Done")) listNames.add("Secret II")
            }
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        listSelector.adapter = adapter


        listSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val buttonColor: Int = if(dark){
                    getColor(context, R.color.buttonBackgroundDark)
                }else{
                    getColor(context, R.color.buttonBackground)
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
                                context,
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
                        verseSelector.visibility    = View.VISIBLE
                        listSpinner2.visibility     = View.INVISIBLE

                        button.isEnabled            = true
                        button.isVisible            = true
                        button.backgroundTintList   = ColorStateList.valueOf(buttonColor)


                        ArrayAdapter.createFromResource(
                                context,
                                R.array.mcheyne_list1,
                                android.R.layout.simple_spinner_item).also{
                                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    list.adapter = it
                                    list.setSelection(it.getPosition(cInfo)) }
                    }
                    "Family II"         ->{
                        val cInfo                   = getCurrentInfo(2, "mcheyne") as String
                        verseSelector.visibility    = View.VISIBLE
                        listSpinner2.visibility     = View.INVISIBLE

                        button.isEnabled            = true
                        button.isVisible            = true
                        button.backgroundTintList   = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context,
                                R.array.mcheyne_list2,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cInfo)) }
                    }
                    "Secret I"         ->{
                        val cInfo                   = getCurrentInfo(3, "mcheyne") as String
                        verseSelector.visibility    = View.VISIBLE
                        listSpinner2.visibility     = View.INVISIBLE

                        button.isEnabled            = true
                        button.isVisible            = true
                        button.backgroundTintList   = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context,
                                R.array.mcheyne_list3,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cInfo)) }
                    }
                    "Secret II"         ->{
                        val cInfo                   = getCurrentInfo(4, "mcheyne") as String
                        verseSelector.visibility    = View.VISIBLE
                        listSpinner2.visibility     = View.INVISIBLE

                        button.isEnabled            = true
                        button.isVisible            = true
                        button.backgroundTintList   = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context,
                                R.array.mcheyne_list4,
                                android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cInfo)) }
                    }

                    "Gospels"      -> {
                        val cInfo                    = getCurrentInfo(1) as Array<*>
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.isVisible             = true
                        button.backgroundTintList    = ColorStateList.valueOf(buttonColor)

                        ArrayAdapter.createFromResource(
                                context,
                                R.array.pgh_list1Book,
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
                                context,
                                R.array.pgh_list2Book,
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
                                context,
                                R.array.pgh_list3Book,
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
                                context,
                                R.array.pgh_list4Book,
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
                                context,
                                R.array.pgh_list5Book,
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
                                context,
                                R.array.pgh_list6Book,
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

                        ArrayAdapter.createFromResource(context, R.array.pgh_list7Book, android.R.layout.simple_spinner_item).also{
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            list.adapter = it
                            list.setSelection(it.getPosition(cBook)) }

                        list.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val verse                 = root.findViewById<NumberPicker>(R.id.listSpinner2)
                                verse.visibility          = View.VISIBLE

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
                                context,
                                R.array.pgh_list8Book,
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
                                context,
                                R.array.pgh_list9Book,
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
                                context,
                                R.array.pgh_list10Book,
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
                        setIntPref(name="mcheyne1Index", value=num, updateFS=true)
                    }
                    "Family II"   -> {
                        val array = resources.getStringArray(R.array.mcheyne_list2)
                        val num = array.indexOf("$selectedBook")
                        setIntPref(name="mcheyne2Index", value=num, updateFS=true)
                    }
                    "Secret I"    -> {
                        val array = resources.getStringArray(R.array.mcheyne_list3)
                        val num = array.indexOf("$selectedBook")
                        setIntPref(name="mcheyne3Index", value=num, updateFS=true)
                    }
                    "Secret II"   -> {
                        val array = resources.getStringArray(R.array.mcheyne_list4)
                        val num = array.indexOf("$selectedBook")
                        setIntPref(name="mcheyne4Index", value=num, updateFS=true)
                    }
                    "The Gospels" -> setIntPref(name = "pgh1Index", value=resources.getStringArray(R.array.pgh_list1).indexOf("$selectedBook $selectedVerse"), updateFS=true)
                    "The Pentateuch" -> setIntPref(name = "pgh2Index", value = resources.getStringArray(R.array.pgh_list2).indexOf("$selectedBook $selectedVerse"), updateFS=true)
                    "Epistles I" -> setIntPref(name = "pgh3Index", value = resources.getStringArray(R.array.pgh_list3).indexOf("$selectedBook $selectedVerse"), updateFS = true)
                    "Epistles II" -> {
                        val indexOf = if (selectedBook == "Philemon" || selectedBook == "Jude" || selectedBook == "2 John" || selectedBook == "3 John"){
                            selectedBook
                        }else{
                            "$selectedBook $selectedVerse"
                        }
                        setIntPref(name = "pgh4Index", value = resources.getStringArray(R.array.pgh_list4).indexOf(indexOf), updateFS = true)
                    }
                    "Poetry" -> setIntPref(name = "pgh5Index", value = resources.getStringArray(R.array.pgh_list5).indexOf("$selectedBook $selectedVerse"), updateFS = true)
                    "Psalms" ->  setIntPref(name = "pgh6Index", value = resources.getStringArray(R.array.pgh_list6).indexOf("$selectedBook $selectedVerse"), updateFS = true)
                    "Proverbs" -> setIntPref(name = "pgh7Index", value = resources.getStringArray(R.array.pgh_list7).indexOf("$selectedBook $selectedVerse"), updateFS = true)
                    "History" -> setIntPref(name="pgh8Index", value=resources.getStringArray(R.array.pgh_list8).indexOf("$selectedBook $selectedVerse"), updateFS=true)
                    "Prophets" -> {
                        val indexOf = if (selectedBook == "Obadiah") selectedBook else "$selectedBook $selectedVerse"
                        setIntPref(name="pgh9Index", value=resources.getStringArray(R.array.pgh_list9).indexOf(indexOf), updateFS=true)
                    }
                    "Acts" -> setIntPref(name="pgh10Index", value=resources.getStringArray(R.array.pgh_list10).indexOf("$selectedBook $selectedVerse"), updateFS=true)
                }


                dialogInterface.dismiss()
                Toast.makeText(context, "Changed $selectedList", Toast.LENGTH_LONG).show() }


            alert.setNeutralButton("Cancel"){dialogInterface, _ ->
                dialogInterface.dismiss() }


            alert.create().show() } }


    fun getCurrentInfo(listNum:Int, type:String="pgh"): Any {
        val data: Array<Any> = if(type=="pgh") {
            val returnVal: Array<Any> = when (listNum) {
                1 -> arrayOf(getIntPref("pghIndex"), R.array.pgh_list1)
                2 -> arrayOf(getIntPref("pgh2Index"), R.array.pgh_list2)
                3 -> arrayOf(getIntPref("pgh3Index"), R.array.pgh_list3)
                4 -> arrayOf(getIntPref("pgh4Index"), R.array.pgh_list4)
                5 -> arrayOf(getIntPref("pgh5Index"), R.array.pgh_list5)
                6 -> arrayOf(getIntPref("pgh6Index"), R.array.pgh_list6)
                7 -> arrayOf(getIntPref("pgh7Index"), R.array.pgh_list7)
                8 -> arrayOf(getIntPref("pgh8Index"), R.array.pgh_list8)
                9 -> arrayOf(getIntPref("pgh9Index"), R.array.pgh_list9)
                10 -> arrayOf(getIntPref("pgh10Index"), R.array.pgh_list10)
                else -> arrayOf(0, 0)
            }
            returnVal
        }else{
            val returnVal: Array<Any> = when(listNum){
                1 -> arrayOf(getIntPref("mcheyne1Index"), R.array.mcheyne_list1)
                2 -> arrayOf(getIntPref("mcheyne2Index"), R.array.mcheyne_list2)
                3 -> arrayOf(getIntPref("mcheyne3Index"), R.array.mcheyne_list3)
                4 -> arrayOf(getIntPref("mcheyne4Index"), R.array.mcheyne_list4)
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
            var lastIndex = if(afterSplit.lastIndex == 0) 1 else afterSplit.lastIndex
            lastIndex = if(afterSplit[0].toIntOrNull() != null && afterSplit[lastIndex].toIntOrNull() == null) lastIndex + 1 else lastIndex
            val book = afterSplit.subList(0, lastIndex).joinToString(" ")
            val chapter = if(afterSplit[afterSplit.lastIndex].toIntOrNull() != null) afterSplit[afterSplit.lastIndex].toInt() else 1
            arrayOf<Any>(book, chapter)
        } else{
            fullValue
        }
    }
}