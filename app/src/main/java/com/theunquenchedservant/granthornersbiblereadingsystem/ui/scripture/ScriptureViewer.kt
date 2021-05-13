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
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.ScriptureViewerBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import org.json.JSONObject
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
    private var versionId:String? = ""
    private lateinit var act: MainActivity

    private external fun getESVKey() : String

    private external fun getBibleApiKey() : String

    private var BIBLE_IDS : Map<String, String> = mapOf("KJV" to "55212e3cf5d04d49-01","CSB" to "a556c5305ee15c3f-01","NASB95" to "b8ee27bcd1cae43a-01", "NASB20" to "a761ca71e0b3ddcf-01", "AMP" to "a81b73293d3080c9-01", "NIV" to "78a9f6124f344018-01", "ESV" to "ksdjfldjlkfjeiiethisdoesntmatteranyway")
    private val BOOK_IDS : Map<String, String> = mapOf(
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
        act = activity as MainActivity
        view.findViewById<WebView>(R.id.scripture_web).setBackgroundColor(Color.parseColor("#121212"))
        act.supportActionBar?.title = chapter
        var bibleVersion = getStringPref("bibleVersion", "NIV")
        bibleVersion = when (bibleVersion){
            "---"->setStringPref("bibleVersion", "NIV", updateFS=true)
            "NASB"->setStringPref(name="bibleVersion", value="NASB20", updateFS=true)
            else->bibleVersion
        }
        act.binding.translationSelector.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var version = parent?.getItemAtPosition(position).toString()
                version = when(version){
                    "NASB2020" -> "NASB20"
                    "---"->"NIV"
                    else -> version
                }
                setStringPref(name = "bibleVersion", value = version, updateFS = true)
                if(version != bibleVersion) {
                    val bundle = bundleOf("chapter" to chapter, "psalms" to psalms, "iteration" to iteration)
                    act.navController.navigate(R.id.navigation_scripture, bundle)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        versionId = BIBLE_IDS[bibleVersion]
        act.findViewById<BottomNavigationView>(R.id.bottom_nav).isVisible = false
        val buttonColor: Int
        val textColor: Int
        val context = act.applicationContext
        if(getBoolPref("darkMode")) {
            act.binding.navHostFragment.setBackgroundColor(Color.parseColor("#121212"))
            buttonColor = getColor(App.applicationContext(), R.color.buttonBackgroundDark)
            textColor = getColor(App.applicationContext(), R.color.unquenchedTextDark)
        }else{
            act.binding.navHostFragment.setBackgroundColor(Color.parseColor("#fffaf0"))
            buttonColor = getColor(App.applicationContext(), R.color.buttonBackground)
            textColor = getColor(App.applicationContext(), R.color.unquenchedText)
        }
        binding.psalmsNext.setBackgroundColor(buttonColor)
        binding.psalmsNext.setTextColor(textColor)
        binding.psalmsBack.setBackgroundColor(buttonColor)
        binding.psalmsBack.setTextColor(textColor)
        val type=if(bibleVersion == "ESV"){
            "esv"
        }else{
            "apiBible"
        }
        val returnURL = when (getStringPref(name = "planSystem", defaultValue = "pgh")) {
            "pgh" -> getReference(chapter, psalms, type)
            else -> {
                val iterations = getIterations(chapter)
                getReferenceMCheyne(chapter, iterations[1], maxIteration = iterations[0] + 1, type)
            }
        }
        getScriptureView(returnURL, type)
    }

    private fun getReferenceMCheyne(chapter: String, iteration: Int, maxIteration: Int, type:String):String{
        val title : String
        val url: String
        val chapters = getBook(chapter)
        val book = chapters[chapters.lastIndex]
        chapters.removeLast()
        when(iteration){
            0->{
                prepBindings(1, R.id.navigation_home_mcheyne, chapter, false, iteration)
                title = chapter
                url = if(type=="esv"){
                    "https://api.esv.org/v3/passage/html/?q=$chapter&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
                }else{
                    getBibleApiURL(title, versionId, book)
                }
            }
            1-> {
                prepBindings(2, R.id.navigation_home_mcheyne, chapter, false, iteration)
                val currentChapter = chapters[iteration - 1]
                title = "$book $currentChapter"
                url = if (type == "esv") {
                    "https://api.esv.org/v3/passage/html/?q=${book}.$currentChapter&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
                }else{
                    getBibleApiURL(title, versionId, book)
                }
            }
            in 2 until maxIteration -> {
                prepBindings(3, R.id.navigation_home_mcheyne, chapter, false, iteration)
                val currentChapter = chapters[iteration - 1]
                title = "$book $currentChapter"
                url = if (type == "esv") {
                    "https://api.esv.org/v3/passage/html/?q=${book}.$currentChapter&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
                }else{
                    getBibleApiURL(title, versionId, book)
                }
            }
            maxIteration-> {
                prepBindings(4, R.id.navigation_home_mcheyne, chapter, false, iteration)
                val currentChapter = chapters[iteration-1]
                title = "$book $currentChapter"
                url = if(type=="esv"){
                    "https://api.esv.org/v3/passage/html/?q=${book}.$currentChapter&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
                }else{
                    getBibleApiURL(title, versionId, book)
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
    private fun getReference(chapter: String, psalms: Boolean, type:String) : String {
        val title: String
        when (getStringPref(name = "bibleVersion", defaultValue = "ESV")) {
            "NASB" -> setStringPref(name = "bibleVersion", value = "NASB20", updateFS = true)
        }
        val versionId: String? = BIBLE_IDS[getStringPref(name="bibleVersion", defaultValue="ESV")]
        return if(psalms) {
            getPsalms(type)
        }else {
            prepBindings(1, R.id.navigation_home, chapter, false, 0)
            title = if (" " !in chapter){
                "$chapter 1"
            }else{
                chapter
            }

            act.supportActionBar?.title = title
            if(type == "esv"){
                val ch = chapter.replace(" ", "")
                "https://api.esv.org/v3/passage/html/?q=$ch&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
            }else{
                val temp = title.split(" ")
                val realChapter = temp[temp.lastIndex]
                val temp2 = temp.dropLast(1)
                val realBook = temp2.joinToString(" ")
                "https://api.scripture.api.bible/v1/bibles/$versionId/chapters/${BOOK_IDS[realBook]}.$realChapter?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=true"
            }
        }
    }
    private fun getScriptureView(url: String, type:String=""){
        val esvKey = String(Base64.decode(getESVKey(), Base64.DEFAULT))
        val key = String(Base64.decode(getBibleApiKey(), Base64.DEFAULT))
        var html: String
        val context = App.applicationContext()
        val cache = DiskBasedCache(context.cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply{
            start()
        }
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
                Response.Listener { response ->
                    val css: String
                    html = if(type=="esv") {
                        response.getJSONArray("passages").getString(0)
                    }else {
                        response.getJSONObject("data").getString("content")
                    }
                    css = when (getBoolPref(name="darkMode", defaultValue=true)) {
                        true -> if(type=="esv") "https://unquenched.bible/esv-2.css" else "https://unquenched.bible/api_bible_night.css"

                        false -> if(type=="esv") "https://unquenched.bible/esv-day2.css" else "https://unquenched.bible/api_bible_day.css"
                    }
                    html = if(type=="esv"){
                        html.replace("\"http://static.esvmedia.org.s3.amazonaws.com/tmp/text.css\"", css)
                    }else{
                        prepBibleApi(html, css, response)
                    }
                    binding.scriptureWeb.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
                },
                Response.ErrorListener { error ->
                    debugLog(message="ERROR: $error")
                }){
            override fun getHeaders(): MutableMap<String, String>{
                val headers = HashMap<String, String>()
                if(type=="esv"){
                    headers["Authorization"] = esvKey
                }else{
                    headers["api-key"] = key
                }
                return headers
            }
        }
        requestQueue.add(jsonObjectRequest)
    }
    private fun getIterations(chapter:String):List<Int>{
        val maxIteration = chapter.filter { it == ',' }.count()
        val currentIteration = if(maxIteration == 0) 0 else if(iteration == 0) 1 else iteration
        return listOf(maxIteration, currentIteration)
    }
    private fun getBook(chapter:String): MutableList<String> {
        val chapters = mutableListOf<String>()
        if ("," in chapter) {
            val temp = chapter.split(", ")
            val temp2 = temp[0]
            val temp4 = temp.subList(1, temp.lastIndex + 1)
            val temp3 = temp2.split(" ")
            chapters.add(temp3.drop(1)[0])
            for (ch in temp4) {
                chapters.add(ch)
            }
            val temp5 = temp3.subList(0, temp3.lastIndex)
            chapters.add(temp5.joinToString(" "))
        } else {
            chapters.add("")
        }
        return chapters
    }
    private fun getBibleApiURL(chapter:String, versionId:String?, book:String):String{
        return when {
            (":" in chapter) -> {
                val chapterSection = chapter.split(":")[0]
                val verseSection = chapter.split(":")[1]
                val verses = verseSection.split("–")
                val startVerse = verses[0]
                val endVerse = verses[1]
                "https://api.scripture.api.bible/v1/bibles/$versionId/passages/${BOOK_IDS[book]}.${chapterSection}.${startVerse}-${BOOK_IDS[book]}.${chapterSection}.${endVerse}?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=true"
            }
            else -> {
                val temp = chapter.split(" ")
                val realChapter = temp[temp.lastIndex]
                val temp2 = temp.dropLast(1)
                val realBook = temp2.joinToString(" ")
                "https://api.scripture.api.bible/v1/bibles/$versionId/chapters/${BOOK_IDS[realBook]}.${realChapter}?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=true"
            }
        }
    }
    private fun getPsalms(type:String):String {
        var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val act = (activity as MainActivity)
        when (iteration) {
            1 -> {
                prepBindings(2, R.id.navigation_home, "no", true, iteration)
            }
            in 2..4 -> {
                prepBindings(3, R.id.navigation_home, "no", true, iteration)
                day += 30 * (iteration - 1)
            }
            5 -> {
                prepBindings(4, R.id.navigation_home, "no", true, iteration)
                day += 30 * (iteration - 1)
            }
        }
        act.supportActionBar?.title  = "Psalm $day"
        return if(type=="esv"){
            "https://api.esv.org/v3/passage/html/?q=Psalm$day&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
        }else{
            "https://api.scripture.api.bible/v1/bibles/$versionId/chapters/PSA.$day?content-type=html&include-notes=false&include-titles=true&include-chapter-numbers=false&include-verse-numbers=true&include-verse-spans=false"
        }
    }

    private fun prepBibleApi(html2:String, css:String, response: JSONObject):String{
        val fums = response.getJSONObject("meta").getString("fums")
        var html = html2
        var copyright = ""
        copyright = when (getStringPref(name="bibleVersion", defaultValue="NIV")){
            "AMP" -> copyright + "Amplified® Bible (AMP), copyright © 1954, 1958, 1962, 1964, 1965, 1987, 2015 by The Lockman Foundation, La Habra, Calif. All rights reserved.<br><br><center>For Permission to Quote Information visit <a href=\"https://www.lockman.org\">www.lockman.org</a></center>"
            "CSB" -> copyright + "<center>Christian Standard Bible® Copyright © 2017 by Holman Bible Publishers</center><br>Christian Standard Bible® and CSB® are federally registered trademarks of Holman Bible Publishers. Used by permission."
            "NASB95","NASB20"-> copyright + "New American Standard Bible Copyright 1960, 1971, 1977, 1995, 2020 by The Lockman Foundation, La Habra, Calif. All rights reserved.<br ><br><center>For Permission to Quote Information visit <a href=\"https://www.lockman.org\">www.lockman.org</a></center>"
            "NIV" -> copyright + "<center>Holy Bible, New International Version TM, NIV TM<br>Copyright © 1973, 1978, 1984, 2011 by <a href='https://www.biblica.com'>Biblica, Inc.</a><br>Used with permission. All rights reserved worldwide.</center><br>"
            else -> copyright + "PUBLIC DOMAIN"
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
        html = tagChanger(html, "<p class=\"s1\">", "<h3 class=\"s1\">")
        html = tagChanger(html, "<p class=\"s\">", "<h3 class=\"s\">")
        html = tagChanger(html, "<p class=\"ms\">", "<h3 class=\"ms\">")
        html += "<br><br><div class=\"copyright\">$copyright</div> $fums"
        return html
    }
    private fun prepBindings(id:Int, homeId:Int, passedChapter:String, passedPsalm:Boolean, passedIteration:Int){
        when(id){
            1->{
                binding.psalmsBack.visibility = View.INVISIBLE
                binding.psalmsNext.visibility = View.VISIBLE
                binding.psalmsNext.text = getText(R.string.scripture_home)
                binding.psalmsNext.setOnClickListener {
                    act.binding.bottomNav.isVisible = true
                    act.navController.navigate(homeId)
                }
            }
            2->{
                binding.psalmsBack.visibility = View.INVISIBLE
                binding.psalmsNext.visibility = View.VISIBLE
                binding.psalmsNext.text = getText(R.string.psalms_next)
                binding.psalmsNext.setOnClickListener {
                    val bundle = bundleOf("chapter" to passedChapter, "psalms" to passedPsalm, "iteration" to passedIteration + 1)
                    act.navController.navigate(R.id.navigation_scripture, bundle)
                }
            }
            3->{
                binding.psalmsBack.visibility = View.VISIBLE
                binding.psalmsNext.visibility = View.VISIBLE
                binding.psalmsBack.text = getText(R.string.psalms_back)
                binding.psalmsNext.text = getText(R.string.psalms_next)
                binding.psalmsBack.setOnClickListener {
                    val bundle = bundleOf("chapter" to passedChapter, "psalms" to passedPsalm, "iteration" to passedIteration-1)
                    act.navController.navigate(R.id.navigation_scripture, bundle)
                }
                binding.psalmsNext.setOnClickListener {
                    val bundle = bundleOf("chapter" to passedChapter, "psalms" to passedPsalm, "iteration" to passedIteration+1)
                    act.navController.navigate(R.id.navigation_scripture, bundle)
                }
            }
            4->{
                binding.psalmsBack.visibility = View.VISIBLE
                binding.psalmsNext.visibility = View.VISIBLE
                binding.psalmsNext.text = getString(R.string.scripture_home)
                binding.psalmsBack.setOnClickListener {
                    val bundle = bundleOf("chapter" to passedChapter, "psalms" to passedPsalm, "iteration" to passedIteration-1)
                    act.navController.navigate(R.id.navigation_scripture, bundle)
                }
                binding.psalmsNext.setOnClickListener {
                    act.binding.bottomNav.isVisible = true
                    act.navController.navigate(homeId)
                }
            }
        }
    }

    private fun tagChanger(html2:String, replaceTag1:String, replaceTag2:String):String{
        var x = 0
        var html = html2
        html = html.replace(replaceTag1, replaceTag2)
        while(true) {
            x = html.indexOf(replaceTag2, x + 1)
            if (x == -1) {
                break
            }
            val y = html.indexOf("</p>", x)
            if (y == -1) {
                break
            }
            html = html.replaceRange(y, y + 4, "</h3>")
        }
        return html
    }
}