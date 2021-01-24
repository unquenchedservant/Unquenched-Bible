package com.theunquenchedservant.granthornersbiblereadingsystem.ui.scripture

import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AdapterView
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.ScriptureViewerBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import java.util.*
import kotlin.collections.HashMap

class ScriptureViewer : Fragment() {
    init{
        System.loadLibrary("native-lib")
    }
    private lateinit var chapter: String
    private var psalms: Boolean = false
    private var iteration = 0
    private var _binding: ScriptureViewerBinding? = null
    private val binding get() = _binding!!

    private external fun getESVKey() : String

    private external fun getBibleApiKey() : String

    private var bibleIDs : Map<String, String> = mapOf("KJV" to "55212e3cf5d04d49-01","CSB" to "a556c5305ee15c3f-01","NASB95" to "b8ee27bcd1cae43a-01", "NASB20" to "a761ca71e0b3ddcf-01", "AMP" to "a81b73293d3080c9-01")
    private val bookIDs : Map<String, String> = mapOf(
            "Genesis" to "GEN", "Exodus" to "EXO", "Leviticus" to "LEV",
            "Numbers" to "NUM", "Deuteronomy" to "DEU", "Joshua" to "JOS",
            "Judges" to "JDG", "Ruth" to "RUT", "1 Samuel" to "1SA",
            "2 Samuel" to "2SA", "1 Kings" to "1KI", "2 Kings" to "2KI",
            "1 Chronicles" to "1CH", "2 Chronicles" to "2CH", "Ezra" to "EZR",
            "Nehemiah" to "NEH", "Esther" to "EST", "Job" to "JOB", "Psalms" to "PSA",
            "Proverbs" to "PRO", "Ecclesiastes" to "ECC", "Song of Solomon" to "SNG",
            "Isaiah" to "ISA", "Jeremiah" to "JER", "Lamentations" to "LAM", "Ezekiel" to "EZK",
            "Daniel" to "DAN", "Hosea" to "HOS", "Joel" to "JOL", "Amos" to "AMO", "Obadiah" to "OBA",
            "Jonah" to "JON", "Micah" to "MIC", "Nahum" to "NAM", "Habakkuk" to "HAB", "Zephaniah" to "ZEP",
            "Haggai" to "HAG", "Zechariah" to "ZEC", "Malachi" to "MAL", "Matthew" to "MAT",
            "Mark" to "MRK", "Luke" to "LUK", "John" to "JHN", "Acts" to "ACT", "Romans" to "ROM",
            "1 Corinthians" to "1CO", "2 Corinthians" to "2CO", "Galatians" to "GAL", "Ephesians" to "EPH",
            "Philippians" to "PHP", "Colossians" to "COL", "1 Thessalonians" to "1TH", "2 Thessalonians" to "2TH",
            "1 Timothy" to "1TI", "2 Timothy" to "2TI", "Titus" to "TIT", "Philemon" to "PHM", "Hebrews" to "HEB",
            "James" to "JAS", "1 Peter" to "1PE", "2 Peter" to "2PE", "1 John" to "1JN",
            "2 John" to "2JN", "3 John" to "3JN", "Jude" to "JUD", "Revelation" to "REV"
    )
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        _binding = ScriptureViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = arguments
        chapter = b?.getString("chapter")!!
        psalms = b.getBoolean("psalms")
        iteration = b.getInt("iteration")
        view.findViewById<WebView>(R.id.scripture_web).setBackgroundColor(Color.parseColor("#121212"))
        val act = activity as MainActivity
        act.supportActionBar?.title = chapter
        act.binding.translationSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var version = parent?.getItemAtPosition(position).toString()
                if(version == "NASB2020"){
                    version = "NASB"
                }
                setStringPref(name="bibleVersion", value=version, updateFS=true)
                val bundle = bundleOf("chapter" to chapter, "psalms" to  psalms, "iteration" to iteration)
                act.navController.navigate(R.id.navigation_scripture, bundle)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {} }
        val url = if(getStringPref(name="bibleVersion", defaultValue="ESV") == "ESV"){
            val returnURL = if(getStringPref(name="planSystem", defaultValue="pgh") == "pgh") {
                getESVReference(chapter, psalms, iteration)
            }else{
                val maxIteration = chapter.filter { it == ',' }.count()
                val currentIteration = if(maxIteration == 0){
                    0
                }else{
                    val returnIter = if(iteration == 0) {
                        1
                    }else{
                        iteration
                    }
                    returnIter
                }
                getESVReferenceMCheyne(chapter, currentIteration, maxIteration=maxIteration + 1)
            }
            returnURL
        }else{
            val returnURL = if(getStringPref(name="planSystem", defaultValue="pgh") == "pgh"){
                getReference(chapter, psalms, iteration)
            }else{
                val maxIteration = chapter.filter{ it == ','}.count()
                val currentIteration = if(maxIteration == 0){
                    0
                }else{
                    val returnIter = if(iteration == 0){
                        1
                    }else{
                        iteration
                    }
                    returnIter
                }
                getReferenceMCheyne(chapter, currentIteration, maxIteration=maxIteration + 1)
            }
            returnURL
        }
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_nav).isVisible = false
        if(getBoolPref(name="darkMode", defaultValue=true)){
            act.binding.navHostFragment.setBackgroundColor(Color.parseColor("#121212"))
        }else{
            act.binding.navHostFragment.setBackgroundColor(Color.parseColor("#fffaf0"))
        }
        if(getStringPref(name="bibleVersion", defaultValue="ESV") == "ESV"){
            getESV(url)
        }else{
            getScriptureView(url)
        }
    }
    private fun getReferenceMCheyne(chapter: String, iteration: Int, maxIteration: Int):String{
        val title : String
        val url: String
        val chapters = mutableListOf<String>()
        if (getStringPref(name="bibleVersion", defaultValue="ESV") == "NASB"){
            setStringPref(name="bibleVersion", value="NASB20", updateFS=true)
        }
        val versionId: String? = when (getStringPref(name="bibleVersion", defaultValue="ESV")){
            "ESV" ->  bibleIDs["ESV"]
            "CSB" -> bibleIDs["CSB"]
            "AMP" -> bibleIDs["AMP"]
            "KJV" -> bibleIDs["KJV"]
            "NASB95" -> bibleIDs["NASB95"]
            "NASB20" -> bibleIDs["NASB20"]
            else -> ""
        }
        if(getBoolPref(name="darkMode", defaultValue=true)){
            binding.psalmsNext.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackgroundDark))
            binding.psalmsNext.setTextColor(getColor(App.applicationContext(), R.color.unquenchedTextDark))
            binding.psalmsBack.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackgroundDark))
            binding.psalmsBack.setTextColor(getColor(App.applicationContext(), R.color.unquenchedTextDark))
        }else{
            binding.psalmsNext.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackground))
            binding.psalmsNext.setTextColor(getColor(App.applicationContext(), R.color.unquenchedText))
            binding.psalmsBack.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackground))
            binding.psalmsBack.setTextColor(getColor(App.applicationContext(), R.color.unquenchedText))
        }
        val book: String
        book = if ("," in chapter){
            val temp = chapter.split(", ")
            val temp2 = temp[0]
            val temp4 = temp.subList(1, temp.lastIndex+1)
            val temp3 = temp2.split(" ")
            chapters.add(temp3.drop(1)[0])
            for(ch in temp4){
                chapters.add(ch)
            }
            val temp5 = temp3.subList(0, temp3.lastIndex)
            temp5.joinToString(" ")
        }else{
            ""
        }
        when(iteration){
            0->{
                binding.psalmsBack.visibility = View.INVISIBLE
                binding.psalmsNext.visibility = View.VISIBLE
                binding.psalmsNext.text=getText(R.string.scripture_home)
                binding.psalmsNext.setOnClickListener {
                    (activity as MainActivity).binding.bottomNav.isVisible = true
                    (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
                }
                title = chapter
                url = if(":" in chapter){
                    val chapterSection = chapter.split(":")[0]
                    val verseSection = chapter.split(":")[1]
                    val verses = verseSection.split("–")
                    val startVerse = verses[0]
                    val endVerse = verses[1]
                    "https://api.scripture.api.bible/v1/bibles/$versionId/passages/${bookIDs[book]}.${chapterSection}.${startVerse}-${bookIDs[book]}.${chapterSection}.${endVerse}?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=true"
                }else{
                    val temp = title.split(" ")
                    val realChapter = temp[temp.lastIndex]
                    val temp2 = temp.dropLast(1)
                    val realBook = temp2.joinToString(" ")
                    "https://api.scripture.api.bible/v1/bibles/$versionId/chapters/${bookIDs[realBook]}.${realChapter}?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=true"
                }
            }
            1->{
                binding.psalmsBack.visibility = View.INVISIBLE
                binding.psalmsNext.visibility = View.VISIBLE
                binding.psalmsNext.text=getText(R.string.psalms_next)
                binding.psalmsNext.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("chapter", chapter)
                    bundle.putBoolean("psalms", false)
                    bundle.putInt("iteration", iteration+1)
                    (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                }
                val currentChapter = chapters[iteration-1]
                title = "$book $currentChapter"
                url = if(":" in currentChapter){
                    val chapterSection = currentChapter.split(":")[0]
                    val verseSection = currentChapter.split(":")[1]
                    val verses = verseSection.split("–")
                    val startVerse = verses[0]
                    val endVerse = verses[1]
                    "https://api.scripture.api.bible/v1/bibles/$versionId/passages/${bookIDs[book]}.${chapterSection}.${startVerse}-${bookIDs[book]}.${chapterSection}.${endVerse}?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=true"
                }else{
                    "https://api.scripture.api.bible/v1/bibles/$versionId/chapters/${bookIDs[book]}.${currentChapter}?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=true"
                }
            }
            in 2 until maxIteration ->{
                binding.psalmsBack.visibility = View.VISIBLE
                binding.psalmsNext.visibility = View.VISIBLE
                binding.psalmsBack.text = getText(R.string.psalms_back)
                binding.psalmsNext.text=getText(R.string.psalms_next)
                binding.psalmsBack.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("chapter", chapter)
                    bundle.putBoolean("psalms", false)
                    bundle.putInt("iteration", iteration-1)
                    (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                }
                binding.psalmsNext.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("chapter", chapter)
                    bundle.putBoolean("psalms", false)
                    bundle.putInt("iteration", iteration+1)
                    (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                }
                val currentChapter = chapters[iteration-1]
                title = "$book $currentChapter"
                url = if(":" in currentChapter){
                    val chapterSection = currentChapter.split(":")[0]
                    val verseSection = currentChapter.split(":")[1]
                    val verses = verseSection.split("–")
                    val startVerse = verses[0]
                    val endVerse = verses[1]
                    "https://api.scripture.api.bible/v1/bibles/$versionId/passages/${bookIDs[book]}.${chapterSection}.${startVerse}-${bookIDs[book]}.${chapterSection}.${endVerse}?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=true"
                }else{
                    "https://api.scripture.api.bible/v1/bibles/$versionId/chapters/${bookIDs[book]}.${currentChapter}?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=true"
                }
            }
            maxIteration-> {
                binding.psalmsBack.visibility = View.VISIBLE
                binding.psalmsNext.visibility = View.VISIBLE
                binding.psalmsNext.text = getString(R.string.scripture_home)
                binding.psalmsBack.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("chapter", chapter)
                    bundle.putBoolean("psalms", false)
                    bundle.putInt("iteration", iteration - 1)
                    (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                }
                binding.psalmsNext.setOnClickListener {
                    (activity as MainActivity).binding.bottomNav.isVisible = true
                    (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
                }
                val currentChapter = chapters[iteration-1]
                title = "$book $currentChapter"
                url = if(":" in currentChapter){
                    val chapterSection = currentChapter.split(":")[0]
                    val verseSection = currentChapter.split(":")[1]
                    val verses = verseSection.split("–")
                    val startVerse = verses[0]
                    val endVerse = verses[1]
                    "https://api.scripture.api.bible/v1/bibles/$versionId/passages/${bookIDs[book]}.${chapterSection}.${startVerse}-${bookIDs[book]}.${chapterSection}.${endVerse}?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=true"
                }else{
                    "https://api.scripture.api.bible/v1/bibles/$versionId/chapters/${bookIDs[book]}.${currentChapter}?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=true"
                }
            }
            else -> {
                url = ""
                title = ""
            }
        }
        val act = activity as MainActivity
        act.supportActionBar?.title = title
        return url
    }
    private fun getESVReferenceMCheyne(chapter: String, iteration: Int, maxIteration: Int):String{
        val title : String
        val url: String
        val chapters = mutableListOf<String>()
        if(getBoolPref(name="darkMode", defaultValue=true)){
            binding.psalmsNext.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackgroundDark))
            binding.psalmsNext.setTextColor(getColor(App.applicationContext(), R.color.unquenchedTextDark))
            binding.psalmsBack.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackgroundDark))
            binding.psalmsBack.setTextColor(getColor(App.applicationContext(), R.color.unquenchedTextDark))
        }else{
            binding.psalmsNext.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackground))
            binding.psalmsNext.setTextColor(getColor(App.applicationContext(), R.color.unquenchedText))
            binding.psalmsBack.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackground))
            binding.psalmsBack.setTextColor(getColor(App.applicationContext(), R.color.unquenchedText))
        }
        val book: String
        book = if ("," in chapter){
            val temp = chapter.split(", ")
            val temp2 = temp[0]
            val temp4 = temp.subList(1, temp.lastIndex+1)
            val temp3 = temp2.split(" ")
            chapters.add(temp3.drop(1)[0])
            for(ch in temp4){
                chapters.add(ch)
            }
            val temp5 = temp3.subList(0, temp3.lastIndex)
            temp5.joinToString(" ")
        }else{
            ""
        }
        when(iteration){
            0->{
                binding.psalmsBack.visibility = View.INVISIBLE
                binding.psalmsNext.visibility = View.VISIBLE
                binding.psalmsNext.text=getText(R.string.scripture_home)
                binding.psalmsNext.setOnClickListener {
                    (activity as MainActivity).binding.bottomNav.isVisible = true
                    (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
                }
                title = chapter
                url = "https://api.esv.org/v3/passage/html/?q=$chapter&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
            }
            1->{
                binding.psalmsBack.visibility = View.INVISIBLE
                binding.psalmsNext.visibility = View.VISIBLE
                binding.psalmsNext.text=getText(R.string.psalms_next)
                binding.psalmsNext.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("chapter", chapter)
                    bundle.putBoolean("psalms", false)
                    bundle.putInt("iteration", iteration+1)
                    (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                }

                val currentChapter = chapters[iteration-1]
                title = "$book $currentChapter"
                url = "https://api.esv.org/v3/passage/html/?q=${book}.$currentChapter&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
            }
            in 2 until maxIteration ->{
                binding.psalmsBack.visibility = View.VISIBLE
                binding.psalmsNext.visibility = View.VISIBLE
                binding.psalmsBack.text = getText(R.string.psalms_back)
                binding.psalmsNext.text=getText(R.string.psalms_next)
                binding.psalmsBack.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("chapter", chapter)
                    bundle.putBoolean("psalms", false)
                    bundle.putInt("iteration", iteration-1)
                    (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                }
                binding.psalmsNext.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("chapter", chapter)
                    bundle.putBoolean("psalms", false)
                    bundle.putInt("iteration", iteration+1)
                    (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                }
                val currentChapter = chapters[iteration-1]
                title = "$book $currentChapter"
                url = "https://api.esv.org/v3/passage/html/?q=${book}.$currentChapter&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
            }
            maxIteration-> {
                binding.psalmsBack.visibility = View.VISIBLE
                binding.psalmsNext.visibility = View.VISIBLE
                binding.psalmsNext.text = getString(R.string.scripture_home)
                binding.psalmsBack.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("chapter", chapter)
                    bundle.putBoolean("psalms", false)
                    bundle.putInt("iteration", iteration - 1)
                    (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                }
                binding.psalmsNext.setOnClickListener {
                    (activity as MainActivity).binding.bottomNav.isVisible = true
                    (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
                }
                val currentChapter = chapters[iteration-1]
                title = "$book $currentChapter"
                url = "https://api.esv.org/v3/passage/html/?q=${book}.$currentChapter&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
            }
            else -> {
                url = ""
                title = ""
            }
        }
        val act = activity as MainActivity
        act.supportActionBar?.title = title
        return url
    }
    private fun getESVReference(chapter: String, psalms: Boolean, iteration: Int) : String {
        val title : String
        val url: String
        if(getBoolPref(name="darkMode", defaultValue=true)){
            binding.psalmsNext.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackgroundDark))
            binding.psalmsNext.setTextColor(getColor(App.applicationContext(), R.color.unquenchedTextDark))
            binding.psalmsBack.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackgroundDark))
            binding.psalmsBack.setTextColor(getColor(App.applicationContext(), R.color.unquenchedTextDark))
        }else{
            binding.psalmsNext.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackground))
            binding.psalmsNext.setTextColor(getColor(App.applicationContext(), R.color.unquenchedText))
            binding.psalmsBack.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackground))
            binding.psalmsBack.setTextColor(getColor(App.applicationContext(), R.color.unquenchedText))
        }
        if(psalms){
            var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            when(iteration){
                1 -> {
                    binding.psalmsBack.visibility = View.INVISIBLE
                    binding.psalmsNext.visibility = View.VISIBLE
                    binding.psalmsNext.text=getText(R.string.psalms_next)
                    binding.psalmsNext.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("chapter", "no")
                        bundle.putBoolean("psalms", true)
                        bundle.putInt("iteration", iteration+1)
                        (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                    }
                }
                in 2..4 ->{
                    binding.psalmsBack.visibility = View.VISIBLE
                    binding.psalmsNext.visibility = View.VISIBLE
                    binding.psalmsBack.text = getText(R.string.psalms_back)
                    binding.psalmsNext.text=getText(R.string.psalms_next)
                    binding.psalmsBack.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("chapter", "no")
                        bundle.putBoolean("psalms", true)
                        bundle.putInt("iteration", iteration - 1)
                        (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                    }
                    binding.psalmsNext.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("chapter", "no")
                        bundle.putBoolean("psalms", true)
                        bundle.putInt("iteration", iteration+1)
                        (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                    }
                    day += 30 * (iteration - 1)
                }
                5 ->{
                    binding.psalmsBack.visibility = View.VISIBLE
                    binding.psalmsNext.visibility = View.VISIBLE
                    binding.psalmsNext.text=getString(R.string.scripture_home)
                    binding.psalmsBack.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("chapter", "no")
                        bundle.putBoolean("psalms", true)
                        bundle.putInt("iteration", iteration-1)
                        (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                    }
                    binding.psalmsNext.setOnClickListener {
                        (activity as MainActivity).binding.bottomNav.isVisible = true

                        (activity as MainActivity).navController.navigate(R.id.navigation_home)
                    }
                    day += 30 * (iteration - 1)
                }
            }
            title = "Psalm $day"
            url = "https://api.esv.org/v3/passage/html/?q=Psalm$day&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
        }else{
            binding.psalmsBack.visibility = View.INVISIBLE
            binding.psalmsNext.visibility = View.VISIBLE
            binding.psalmsNext.text=getText(R.string.scripture_home)
            binding.psalmsNext.setOnClickListener {
                (activity as MainActivity).binding.bottomNav.isVisible = true
                (activity as MainActivity).navController.navigate(R.id.navigation_home)
            }
            title = chapter
            url = "https://api.esv.org/v3/passage/html/?q=$chapter&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
        }
        val act = activity as MainActivity
        act.supportActionBar?.title = title
        return url
    }
    private fun getReference(chapter: String, psalms: Boolean, iteration: Int) : String {
        val title: String
        var url: String
        if (getStringPref(name="bibleVersion", defaultValue="ESV") == "NASB") {
            setStringPref(name="bibleVersion", value="NASB20", updateFS=true)
        }
        if (getBoolPref(name="darkMode", defaultValue=true)) {
            binding.psalmsNext.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackgroundDark))
            binding.psalmsNext.setTextColor(getColor(App.applicationContext(), R.color.unquenchedTextDark))
            binding.psalmsBack.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackgroundDark))
            binding.psalmsBack.setTextColor(getColor(App.applicationContext(), R.color.unquenchedTextDark))
        } else {
            binding.psalmsNext.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackground))
            binding.psalmsNext.setTextColor(getColor(App.applicationContext(), R.color.unquenchedText))
            binding.psalmsBack.setBackgroundColor(getColor(App.applicationContext(), R.color.buttonBackground))
            binding.psalmsBack.setTextColor(getColor(App.applicationContext(), R.color.unquenchedText))
        }

        val versionId: String? = when (getStringPref(name="bibleVersion", defaultValue="ESV")) {
            "ESV" -> bibleIDs["ESV"]
            "CSB" -> bibleIDs["CSB"]
            "AMP" -> bibleIDs["AMP"]
            "KJV" -> bibleIDs["KJV"]
            "NASB95" -> bibleIDs["NASB95"]
            "NASB20" -> bibleIDs["NASB20"]
            else -> ""
        }
        url = "https://api.scripture.api.bible/v1/bibles/$versionId/chapters/"
        if (psalms) {
            var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            when (iteration) {
                1 -> {
                    binding.psalmsBack.visibility = View.INVISIBLE
                    binding.psalmsNext.visibility = View.VISIBLE
                    binding.psalmsNext.text = getText(R.string.psalms_next)
                    binding.psalmsNext.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("chapter", "no")
                        bundle.putBoolean("psalms", true)
                        bundle.putInt("iteration", iteration + 1)
                        (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                    }
                }
                in 2..4 -> {
                    binding.psalmsBack.visibility = View.VISIBLE
                    binding.psalmsNext.visibility = View.VISIBLE
                    binding.psalmsBack.text = getText(R.string.psalms_back)
                    binding.psalmsNext.text = getText(R.string.psalms_next)
                    binding.psalmsBack.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("chapter", "no")
                        bundle.putBoolean("psalms", true)
                        bundle.putInt("iteration", iteration - 1)
                        (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                    }
                    binding.psalmsNext.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("chapter", "no")
                        bundle.putBoolean("psalms", true)
                        bundle.putInt("iteration", iteration + 1)
                        (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                    }
                    day += 30 * (iteration - 1)
                }
                5 -> {
                    binding.psalmsBack.visibility = View.VISIBLE
                    binding.psalmsNext.visibility = View.VISIBLE
                    binding.psalmsNext.text = getString(R.string.scripture_home)
                    binding.psalmsBack.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("chapter", "no")
                        bundle.putBoolean("psalms", true)
                        bundle.putInt("iteration", iteration - 1)
                        (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                    }
                    binding.psalmsNext.setOnClickListener {
                        (activity as MainActivity).binding.bottomNav.isVisible = true

                        (activity as MainActivity).navController.navigate(R.id.navigation_home)
                    }
                    day += 30 * (iteration - 1)
                }
            }
            url += "PSA.$day?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=false"
            title = "Psalm $day"
        } else {
            binding.psalmsBack.visibility = View.INVISIBLE
            binding.psalmsNext.visibility = View.VISIBLE
            binding.psalmsNext.text = getText(R.string.scripture_home)
            binding.psalmsNext.setOnClickListener {
                (activity as MainActivity).binding.bottomNav.isVisible = true
                (activity as MainActivity).navController.navigate(R.id.navigation_home)
            }
            title = chapter
            val temp = title.split(" ")
            val realChapter = temp[temp.lastIndex]
            val temp2 = temp.dropLast(1)
            val realBook = temp2.joinToString(" ")
            url += "${bookIDs[realBook]}.$realChapter?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=true"
        }
        val act = activity as MainActivity
        act.supportActionBar?.title = title
        return url
    }
    private fun getScriptureView(url: String){
        val key = String(Base64.decode(getBibleApiKey(), Base64.DEFAULT))
        var html: String
        val copyright: String
        val context = App.applicationContext()
        val cache = DiskBasedCache(context.cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply{
            start()
        }
        val bibleVersion = getStringPref(name="bibleVersion", defaultValue="ESV")
        copyright = if (bibleVersion == "AMP"){
            "Amplified® Bible (AMP), copyright © 1954, 1958, 1962, 1964, 1965, 1987, 2015 by The Lockman Foundation, La Habra, Calif. All rights reserved.<br><br><center>For Permission to Quote Information visit <a href=\"https://www.lockman.org\">www.lockman.org</a></center>"
        }else if(bibleVersion == "CSB"){
            "<center>Christian Standard Bible® Copyright © 2017 by Holman Bible Publishers</center><br>Christian Standard Bible® and CSB® are federally registered trademarks of Holman Bible Publishers. Used by permission."
        }else if(bibleVersion == "NASB95" || bibleVersion == "NASB20"){
            "New American Standard Bible Copyright 1960, 1971, 1977, 1995, 2020 by The Lockman Foundation, La Habra, Calif. All rights reserved.<br ><br><center>For Permission to Quote Information visit <a href=\"https://www.lockman.org\">www.lockman.org</a></center>"
        }else{
            "PUBLIC DOMAIN"
        }
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
                Response.Listener { response ->
                    val css: String
                    html = response.getJSONObject("data").getString("content")
                    val fums = response.getJSONObject("meta").getString("fums")
                    css = if (getBoolPref(name="darkMode", defaultValue=true)) {
                        "https://unquenched.bible/api_bible_night.css"
                    } else {
                        "https://unquenched.bible/api_bible_day.css"
                    }
                    html = "<link rel=\"stylesheet\" type=\"text/css\" href=\"$css\" media=\"all\">$html"
                    html = html.replace("1</span>", "1&nbsp;</span>")
                    html = html.replace("2</span>", "2&nbsp;</span>")
                    html = html.replace("3</span>", "3&nbsp;</span>")
                    html = html.replace("4</span>", "4&nbsp;</span>")
                    html = html.replace("5</span>", "5&nbsp;</span>")
                    html = html.replace("6</span>", "6&nbsp;</span>")
                    html = html.replace("7</span>", "7&nbsp;</span>")
                    html = html.replace("8</span>", "8&nbsp;</span>")
                    html = html.replace("9</span>", "9&nbsp;</span>")
                    html = html.replace("0</span>", "0&nbsp;</span>")
                    html = html.replace("<p class=\"s1\">", "<h3 class=\"s1\">")
                    var x1 = 0
                    var s1 = true
                    while(s1){
                        x1 = html.indexOf("<h3 class=\"s1\">", x1 + 1)
                        if (x1 == -1){
                            s1 = false
                        }else {
                            val y = html.indexOf("</p>", x1)
                            if (y == -1) {
                                s1 = false
                            } else {
                                html = html.replaceRange(y, y + 4, "</h3>")
                            }
                        }
                    }
                    html = html.replace("<p class=\"s\">", "<h3 class=\"s\">")
                    var s2 = true
                    var x = 0
                    while(s2){
                        x = html.indexOf("<h3 class=\"s\">", x + 1)
                        if (x == -1){
                            s2 = false
                        }else{
                            val y = html.indexOf("</p>", x)
                            if (y == -1){
                                s2 = false
                            }else{
                                html = html.replaceRange(y, y+4, "</h3>")
                            }
                        }
                    }
                    html = html.replace("<p class=\"ms\">", "<h3 class=\"ms\">")
                    var ms = true
                    var x3 = 0
                    while(ms){
                        x3 = html.indexOf("<h3 class=\"ms\">", x3 + 1)
                        log("THIS IS THE CURRENT INDEX OF MS $x3")
                        if (x3 == -1){
                            ms = false
                        }else {
                            val y = html.indexOf("</p>", x3)
                            if (y == -1) {
                                ms = false
                            } else {
                                html = html.replaceRange(y, y + 4, "</h3>")
                            }
                        }
                    }
                    html += "<br><br><div class=\"copyright\">$copyright</div> $fums"
                    binding.scriptureWeb.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
                },
                Response.ErrorListener { error ->
                    log("ERROR: $error")
                }){
            override fun getHeaders(): MutableMap<String, String>{
                val headers = HashMap<String, String>()
                headers["api-key"] = key
                return headers
            }
        }
        requestQueue.add(jsonObjectRequest)
    }
    private fun getESV(url: String){
        val key = String(Base64.decode(getESVKey(), Base64.DEFAULT))
        var html: String
        val context = App.applicationContext()
        val cache = DiskBasedCache(context.cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
                Response.Listener { response ->
                    val css: String
                    html = response.getJSONArray("passages").getString(0)
                    css = if(getBoolPref(name="darkMode", defaultValue=true)){
                        "https://unquenched.bible/esv-2.css"
                    }else{
                        "https://unquenched.bible/esv-day2.css"
                    }
                    html = html.replace("\"http://static.esvmedia.org.s3.amazonaws.com/tmp/text.css\"", css)
                    binding.scriptureWeb.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
                },

                Response.ErrorListener { error ->
                    log("ERROR: $error")
                }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = key
                return headers
            }
        }
        requestQueue.add(jsonObjectRequest)
    }
}