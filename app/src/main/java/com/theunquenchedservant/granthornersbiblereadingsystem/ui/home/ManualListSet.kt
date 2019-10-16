package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberEditInt
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberReadInt

class ManualListSet: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manual, container, false)
    }
    override fun onPause(){
        super.onPause()
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            val data = mapOf(
                    "list1" to listNumberReadInt(context, "List 1"),
                    "list2" to listNumberReadInt(context, "List 2"),
                    "list3" to listNumberReadInt(context, "List 3"),
                    "list4" to listNumberReadInt(context, "List 4"),
                    "list5" to listNumberReadInt(context, "List 5"),
                    "list6" to listNumberReadInt(context, "List 6"),
                    "list7" to listNumberReadInt(context, "List 7"),
                    "list8" to listNumberReadInt(context, "List 8"),
                    "list9" to listNumberReadInt(context, "List 9"),
                    "list10" to listNumberReadInt(context, "List 10")
            )
            db.collection("main").document(user.uid).update(data)
                    .addOnSuccessListener { log("Data transferred to firestore") }
                    .addOnFailureListener {e -> Log.w("PROFGRANT", "Error writing to firestore", e) }
        }
    }
    override fun onResume() {
        super.onResume()
        val root = view!!
        val listSelector = root.findViewById<Spinner>(R.id.listSelector)
        ArrayAdapter.createFromResource(
                context!!,
                R.array.listNames,
                android.R.layout.simple_spinner_item).also{
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            listSelector.adapter = it
        }
        listSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                val list = root.findViewById<Spinner>(R.id.listSpinner1)
                val verseSelector = root.findViewById<LinearLayout>(R.id.verseSelector)
                val button = root.findViewById<Button>(R.id.set_button)
                if(selectedItem == "----"){
                    button.isEnabled = false
                    button.setBackgroundColor(Color.parseColor("#00383838"))
                    ArrayAdapter.createFromResource(
                            context!!,
                            R.array.noneBook,
                            android.R.layout.simple_spinner_item).also{
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        list.adapter = it
                    }
                    list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val verse = root.findViewById<NumberPicker>(R.id.listSpinner2)
                            verse.minValue = 0
                            verse.maxValue = 0
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                }
                if(selectedItem == "The Gospels"){

                    button.isEnabled = true
                    button.setBackgroundColor(Color.parseColor("#383838"))
                    ArrayAdapter.createFromResource(
                            context!!,
                            R.array.list1Book,
                            android.R.layout.simple_spinner_item).also{
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        list.adapter = it
                    }
                    list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val book = parent?.getItemAtPosition(position).toString()
                            val verse = root.findViewById<NumberPicker>(R.id.listSpinner2)
                            verse.minValue = 1
                            when(book){
                                "Matthew" -> verse.maxValue = 28
                                "Mark" -> verse.maxValue = 16
                                "Luke" -> verse.maxValue = 24
                                "John" -> verse.maxValue = 21
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
                }else if(selectedItem == "The Pentateuch"){
                    verseSelector.visibility = View.VISIBLE
                    button.isEnabled = true
                    button.setBackgroundColor(Color.parseColor("#383838"))
                    ArrayAdapter.createFromResource(
                            context!!,
                            R.array.list2Book,
                            android.R.layout.simple_spinner_item).also{
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        list.adapter = it
                    }
                    list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val book = parent?.getItemAtPosition(position).toString()
                            val verse = root.findViewById<NumberPicker>(R.id.listSpinner2)
                            verse.minValue = 1
                            when(book){
                                "Genesis" -> verse.maxValue = 50
                                "Exodus" -> verse.maxValue = 40
                                "Leviticus" -> verse.maxValue = 27
                                "Numbers" -> verse.maxValue = 36
                                "Deuteronomy" -> verse.maxValue = 34
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
                }else if(selectedItem == "Epistles I"){
                    verseSelector.visibility = View.VISIBLE
                    button.isEnabled = true
                    button.setBackgroundColor(Color.parseColor("#383838"))
                    ArrayAdapter.createFromResource(
                            context!!,
                            R.array.list3Book,
                            android.R.layout.simple_spinner_item).also{
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        list.adapter = it
                    }
                    list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val book = parent?.getItemAtPosition(position).toString()
                            val verse = root.findViewById<NumberPicker>(R.id.listSpinner2)
                            verse.minValue = 1
                            when(book){
                                "Romans", "1 Corinthians" -> verse.maxValue = 16
                                "2 Corinthians", "Hebrews" -> verse.maxValue = 13
                                "Galatians", "Ephesians" -> verse.maxValue = 6
                                "Philippians", "Colossians" -> verse.maxValue = 4
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                }else if(selectedItem == "Epistles II"){
                    verseSelector.visibility = View.VISIBLE
                    button.isEnabled = true
                    button.setBackgroundColor(Color.parseColor("#383838"))
                    ArrayAdapter.createFromResource(
                            context!!,
                            R.array.list4Book,
                            android.R.layout.simple_spinner_item).also{
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        list.adapter = it
                    }
                    list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val book = parent?.getItemAtPosition(position).toString()
                            val verse = root.findViewById<NumberPicker>(R.id.listSpinner2)
                            verse.minValue = 1
                            when(book){
                                "1 Thessalonians", "James", "1 Peter", "1 John" -> verse.maxValue = 5
                                "2 Thessalonians", "2 Peter", "Titus" -> verse.maxValue = 3
                                "1 Timothy" -> verse.maxValue = 6
                                "2 Timothy" -> verse.maxValue = 4
                                "Philemon", "2 John", "3 John", "Jude" -> verse.maxValue = 1
                                "Revelations" -> verse.maxValue = 22
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
                }else if(selectedItem == "Poetry"){
                    verseSelector.visibility = View.VISIBLE
                    button.isEnabled = true
                    button.setBackgroundColor(Color.parseColor("#383838"))
                    ArrayAdapter.createFromResource(
                            context!!,
                            R.array.list5Book,
                            android.R.layout.simple_spinner_item).also{
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        list.adapter = it
                    }
                    list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val book = parent?.getItemAtPosition(position).toString()
                            val verse = root.findViewById<NumberPicker>(R.id.listSpinner2)
                            verse.minValue = 1
                            when(book) {
                                "Job" -> verse.maxValue = 42
                                "Ecclesiastes" -> verse.maxValue = 12
                                "Song of Solomon" -> verse.maxValue = 8
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
                }else if(selectedItem == "Psalms"){
                    verseSelector.visibility = View.VISIBLE
                    button.isEnabled = true
                    button.setBackgroundColor(Color.parseColor("#383838"))
                    ArrayAdapter.createFromResource(
                            context!!,
                            R.array.list6Book,
                            android.R.layout.simple_spinner_item).also{
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        list.adapter = it
                    }
                    list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val verse = root.findViewById<NumberPicker>(R.id.listSpinner2)
                            verse.minValue = 1
                            verse.maxValue = 150
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                }else if(selectedItem == "Proverbs"){
                    verseSelector.visibility = View.VISIBLE
                    button.isEnabled = true
                    button.setBackgroundColor(Color.parseColor("#383838"))
                    ArrayAdapter.createFromResource(
                            context!!,
                            R.array.list7Book,
                            android.R.layout.simple_spinner_item).also{
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        list.adapter = it
                    }
                    list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val verse = root.findViewById<NumberPicker>(R.id.listSpinner2)
                            verse.minValue = 1
                            verse.maxValue = 31
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                }else if(selectedItem == "History"){
                    verseSelector.visibility = View.VISIBLE
                    button.isEnabled = true
                    button.setBackgroundColor(Color.parseColor("#383838"))
                    ArrayAdapter.createFromResource(
                            context!!,
                            R.array.list8Book,
                            android.R.layout.simple_spinner_item).also{
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        list.adapter = it
                    }
                    list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val book = parent?.getItemAtPosition(position).toString()
                            val verse = root.findViewById<NumberPicker>(R.id.listSpinner2)
                            verse.minValue = 1
                            when(book){
                                "Joshua", "2 Samuel" -> verse.maxValue = 24
                                "Judges" -> verse.maxValue = 21
                                "Ruth" -> verse.maxValue = 4
                                "1 Samuel" -> verse.maxValue = 31
                                "1 Kings" -> verse.maxValue = 22
                                "2 Kings" -> verse.maxValue = 25
                                "1 Chronicles" -> verse.maxValue = 29
                                "2 Chronicles" -> verse.maxValue = 36
                                "Ezra", "Esther" -> verse.maxValue = 10
                                "Nehemiah" -> verse.maxValue = 13
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
                }else if(selectedItem == "Prophets"){
                    verseSelector.visibility = View.VISIBLE
                    button.isEnabled = true
                    button.setBackgroundColor(Color.parseColor("#383838"))
                    ArrayAdapter.createFromResource(
                            context!!,
                            R.array.list9Book,
                            android.R.layout.simple_spinner_item).also{
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        list.adapter = it
                    }
                    list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val book = parent?.getItemAtPosition(position).toString()
                            val verse = root.findViewById<NumberPicker>(R.id.listSpinner2)
                            verse.minValue = 1
                            when(book){
                                "Isaiah" -> verse.maxValue = 66
                                "Jeremiah" -> verse.maxValue = 52
                                "Lamentations" -> verse.maxValue = 5
                                "Ezekiel" -> verse.maxValue = 48
                                "Daniel" -> verse.maxValue = 12
                                "Hosea", "Zechariah" -> verse.maxValue = 14
                                "Joel", "Nahum", "Habakkuk", "Zephaniah" -> verse.maxValue = 3
                                "Amos" -> verse.maxValue = 9
                                "Obadiah" -> verse.maxValue = 1
                                "Jonah", "Malachi" -> verse.maxValue = 4
                                "Micah" -> verse.maxValue = 7
                                "Haggai" -> verse.maxValue = 2
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
                }else if(selectedItem == "Acts"){
                    verseSelector.visibility = View.VISIBLE
                    button.isEnabled = true
                    button.setBackgroundColor(Color.parseColor("#383838"))
                    ArrayAdapter.createFromResource(
                            context!!,
                            R.array.list10Book,
                            android.R.layout.simple_spinner_item).also{
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        list.adapter = it
                    }
                    list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val verse = root.findViewById<NumberPicker>(R.id.listSpinner2)
                            verse.minValue = 1
                            verse.maxValue = 28
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        val button = root.findViewById<Button>(R.id.set_button)
        button.setOnClickListener {
            val model = ViewModelProviders.of(this).get(HomeView::class.java)
            val alert = AlertDialog.Builder(context!!)
            val listSelected = root.findViewById<Spinner>(R.id.listSelector)
            val selectedList = listSelected.selectedItem
            val bookSpinner = root.findViewById<Spinner>(R.id.listSpinner1)
            val selectedBook = bookSpinner.selectedItem
            val verseSpinner = root.findViewById<NumberPicker>(R.id.listSpinner2)
            val selectedVerse = verseSpinner.value
            alert.setTitle("Set $selectedList?")
            alert.setMessage("Are you sure you want to set $selectedList to $selectedBook $selectedVerse")
            alert.setPositiveButton("Yes") { dialogInterface, _ ->
                if (selectedList == "The Gospels") {
                    val array = resources.getStringArray(R.array.list_1)
                    val num = array.indexOf("$selectedBook $selectedVerse")
                    listNumberEditInt(context, "List 1", num)
                    model.list1.value = array[num]
                } else if (selectedList == "The Pentateuch") {
                    val array = resources.getStringArray(R.array.list_2)
                    val num = array.indexOf("$selectedBook $selectedVerse")
                    listNumberEditInt(context, "List 2", num)
                    model.list2.value = array[num]
                } else if (selectedList == "Epistles I"){
                    val array = resources.getStringArray(R.array.list_3)
                    val num = array.indexOf("$selectedBook $selectedVerse")
                    listNumberEditInt(context, "List 3", num)
                    model.list3.value = array[num]
                }else if(selectedList == "Epistles II"){
                    val array = resources.getStringArray(R.array.list_4)
                    val num : Int
                    if(selectedBook == "Philemon" || selectedBook == "Jude" || selectedBook == "2 John" || selectedBook == "3 John"){
                        num = array.indexOf("$selectedBook")
                    }else {
                        num = array.indexOf("$selectedBook $selectedVerse")
                    }
                    listNumberEditInt(context, "List 4", num)
                    model.list4.value = array[num]
                }else if(selectedList == "Poetry"){
                    val array = resources.getStringArray(R.array.list_5)
                    val num = array.indexOf("$selectedBook $selectedVerse")
                    listNumberEditInt(context, "List 5", num)
                    model.list5.value = array[num]
                }else if(selectedList == "Psalms"){
                    val array = resources.getStringArray(R.array.list_6)
                    val num = array.indexOf("$selectedBook $selectedVerse")
                    listNumberEditInt(context, "List 6", num)
                    model.list6.value = array[num]
                }else if(selectedList == "Proverbs"){
                    val array = resources.getStringArray(R.array.list_7)
                    val num = array.indexOf("$selectedBook $selectedVerse")
                    listNumberEditInt(context, "List 7", num)
                    model.list7.value = array[num]
                }else if(selectedList == "History"){
                    val array = resources.getStringArray(R.array.list_8)
                    val num = array.indexOf("$selectedBook $selectedVerse")
                    listNumberEditInt(context, "List 8", num)
                    model.list8.value = array[num]
                }else if(selectedList == "Prophets"){
                    val array = resources.getStringArray(R.array.list_9)
                    val num : Int
                    if(selectedBook == "Obadiah") {
                        num = array.indexOf("$selectedBook")
                        log("$num")
                    }else{
                        num = array.indexOf("$selectedBook $selectedVerse")
                    }
                    listNumberEditInt(context, "List 9", num)
                    model.list9.value = array[num]
                }else if(selectedList == "Acts"){
                    val array = resources.getStringArray(R.array.list_10)
                    val num = array.indexOf("$selectedBook $selectedVerse")
                    listNumberEditInt(context, "List 10", num)
                    model.list10.value = array[num]
                }
                Toast.makeText(context, "Changed $selectedList", Toast.LENGTH_LONG).show()
            }
            alert.setNeutralButton("Cancel"){dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            alert.create().show()
        }

    }
}