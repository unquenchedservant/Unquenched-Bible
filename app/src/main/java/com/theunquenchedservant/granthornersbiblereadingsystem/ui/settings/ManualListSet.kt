package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.home.HomeView
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS

class ManualListSet: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manual, container, false)
    }

    override fun onResume() {
        super.onResume()
        val root = requireView()
        val listSelector = root.findViewById<Spinner>(R.id.listSelector)

        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.listNames,
                android.R.layout.simple_spinner_item).also{
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            listSelector.adapter = it
        }

        listSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedItem      = parent?.getItemAtPosition(position).toString()
                val list              = root.findViewById<Spinner>(R.id.listSpinner1)
                val verseSelector     = root.findViewById<LinearLayout>(R.id.verseSelector)
                val button            = root.findViewById<Button>(R.id.set_button)
                verseSelector.visibility     = View.INVISIBLE
                when(selectedItem){
                    "----"             -> {

                        button.isEnabled             = false
                        button.setBackgroundColor(Color.parseColor("#00383838"))

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

                    "The Gospels"      -> {
                             val cInfo                    = getCurrentInfo(1)
                             val cBook                    = cInfo[0] as String
                             val cVerse                   = cInfo[1] as Int
                             verseSelector.visibility     = View.VISIBLE

                             button.isEnabled             = true
                             button.setBackgroundColor(Color.parseColor("#383838"))

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
                                       verse.visibility = View.VISIBLE

                                     when(book){
                                            "Matthew"                       -> { verse.maxValue = 28; verse.value = cVerse }
                                            "Mark"                          -> { verse.maxValue = 16; verse.value = cVerse }
                                            "Luke"                          -> { verse.maxValue = 24; verse.value = cVerse }
                                            "John"                          -> { verse.maxValue = 21; verse.value = cVerse }
                                       } }

                            override fun onNothingSelected(parent: AdapterView<*>?) {} } }

                    "The Pentateuch"   -> {
                        val cInfo                    = getCurrentInfo(2)
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.setBackgroundColor(Color.parseColor("#383838"))

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
                        val cInfo                    = getCurrentInfo(3)
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.setBackgroundColor(Color.parseColor("#383838"))

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
                        val cInfo                    = getCurrentInfo(4)
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.setBackgroundColor(Color.parseColor("#383838"))

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
                                    "Revelations"                   -> { verse.maxValue = 22; verse.value = cVerse; verse.visibility = View.VISIBLE   } } }

                            override fun onNothingSelected(parent: AdapterView<*>?) {} }}


                    "Poetry"           ->{
                        val cInfo                    = getCurrentInfo(5)
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.setBackgroundColor(Color.parseColor("#383838"))

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
                        val cInfo                  = getCurrentInfo(6)
                        val cBook                  = cInfo[0] as String
                        val cVerse                 = cInfo[1] as Int
                        verseSelector.visibility   = View.VISIBLE

                        button.isEnabled           = true
                        button.setBackgroundColor(Color.parseColor("#383838"))

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
                        val cInfo                    = getCurrentInfo(7)
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.setBackgroundColor(Color.parseColor("#383838"))

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
                        val cInfo                    = getCurrentInfo(8)
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.setBackgroundColor(Color.parseColor("#383838"))

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
                        val cInfo                    = getCurrentInfo(9)
                        val cBook                    = cInfo[0] as String
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.setBackgroundColor(Color.parseColor("#383838"))

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
                        val cInfo                    = getCurrentInfo(10)
                        val cVerse                   = cInfo[1] as Int
                        verseSelector.visibility     = View.VISIBLE

                        button.isEnabled             = true
                        button.setBackgroundColor(Color.parseColor("#383838"))

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


        val button      = root.findViewById<Button>(R.id.set_button)


        button.setOnClickListener {
            val model             = ViewModelProviders.of(this).get(HomeView::class.java)
            val alert             = AlertDialog.Builder(requireContext())
            val listSelected      = root.findViewById<Spinner>(R.id.listSelector)
            val selectedList      = listSelected.selectedItem
            val bookSpinner       = root.findViewById<Spinner>(R.id.listSpinner1)
            val selectedBook      = bookSpinner.selectedItem
            val verseSpinner      = root.findViewById<NumberPicker>(R.id.listSpinner2)
            val selectedVerse     = verseSpinner.value

            alert.setTitle("Set $selectedList?")
            alert.setMessage("Are you sure you want to set $selectedList to $selectedBook $selectedVerse")
            alert.setPositiveButton("Yes") { dialogInterface, _ ->
                when(selectedList) {

                    "The Gospels" -> {
                        val array = resources.getStringArray(R.array.list_1)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref("list1", num)
                        model.list1.value = array[num]
                        updateFS( "list1", num)
                    }


                    "The Pentateuch" -> {
                        val array = resources.getStringArray(R.array.list_2)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref("list2", num)
                        model.list2.value = array[num]
                        updateFS("list2", num)}


                    "Epistles I" -> {
                        val array = resources.getStringArray(R.array.list_3)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref("list3", num)
                        model.list3.value = array[num]
                        updateFS("list3", num)}


                    "Epistles II" -> {
                        val array = resources.getStringArray(R.array.list_4)
                        val num = if (selectedBook == "Philemon" || selectedBook == "Jude" || selectedBook == "2 John" || selectedBook == "3 John") {
                            array.indexOf("$selectedBook")
                        } else {
                            array.indexOf("$selectedBook $selectedVerse")
                        }
                        setIntPref("list4", num)
                        model.list4.value = array[num]
                        updateFS("list4", num)}


                    "Poetry" -> {
                        val array = resources.getStringArray(R.array.list_5)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref("list5", num)
                        model.list5.value = array[num]
                        updateFS("list5", num)}


                    "Psalms" -> {
                        val array = resources.getStringArray(R.array.list_6)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref("list6", num)
                        model.list6.value = array[num]
                        updateFS("list6", num)}


                    "Proverbs" -> {
                        val array = resources.getStringArray(R.array.list_7)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref("list7", num)
                        model.list7.value = array[num]
                        updateFS( "list7", num)}


                    "History" -> {
                        val array = resources.getStringArray(R.array.list_8)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref("list8", num)
                        model.list8.value = array[num]
                        updateFS( "list8", num)}


                    "Prophets" -> {
                        val array = resources.getStringArray(R.array.list_9)
                        val num = if(selectedBook == "Obadiah") {
                            array.indexOf("$selectedBook")
                        }else{
                            array.indexOf("$selectedBook $selectedVerse")
                        }
                        setIntPref("list9", num)
                        model.list9.value = array[num]
                        updateFS("list9", num)}


                    "Acts" -> {
                        val array = resources.getStringArray(R.array.list_10)
                        val num = array.indexOf("$selectedBook $selectedVerse")
                        setIntPref("list10", num)
                        model.list10.value = array[num]
                        updateFS( "list10", num)}}


                dialogInterface.dismiss()
                Toast.makeText(context, "Changed $selectedList", Toast.LENGTH_LONG).show() }


            alert.setNeutralButton("Cancel"){dialogInterface, _ ->
                dialogInterface.dismiss() }


            alert.create().show() } }


    fun getCurrentInfo(listNum:Int): Array<Any> {

        val data : Array<Any> = when(listNum){
            1  ->  arrayOf(getIntPref("list1"), R.array.list_1)
            2  ->  arrayOf(getIntPref("list2"), R.array.list_2)
            3  ->  arrayOf(getIntPref("list3"), R.array.list_3)
            4  ->  arrayOf(getIntPref("list4"), R.array.list_4)
            5  ->  arrayOf(getIntPref("list5"), R.array.list_5)
            6  ->  arrayOf(getIntPref("list6"), R.array.list_6)
            7  ->  arrayOf(getIntPref("list7"), R.array.list_7)
            8  ->  arrayOf(getIntPref("list8"), R.array.list_8)
            9  ->  arrayOf(getIntPref("list9"), R.array.list_9)
            10 ->  arrayOf(getIntPref("list10"), R.array.list_10)
            else -> arrayOf(0, 0) }

        val number = data[0] as Int
        val listId = data[1] as Int

        val list = resources.getStringArray(listId)
        val fullValue = list[number]
        val afterSplit = fullValue.split(" ")

        return when (afterSplit.size){
            1 -> arrayOf(fullValue, 1)
            3 -> arrayOf("${afterSplit[0]} ${afterSplit[1]}", afterSplit[2].toInt())
            4 -> arrayOf("${afterSplit[0]} ${afterSplit[1]} ${afterSplit[2]}", afterSplit[3].toInt())
            else -> arrayOf(afterSplit[0], afterSplit[1].toInt()) } }
}