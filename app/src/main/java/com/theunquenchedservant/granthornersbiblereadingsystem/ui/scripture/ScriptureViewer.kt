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
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFS
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

    private var bibleIDs : Map<String, String> = mapOf<String, String>("KJV" to "55212e3cf5d04d49-01","CSB" to "a556c5305ee15c3f-01","NASB" to "a761ca71e0b3ddcf-01", "AMP" to "a81b73293d3080c9-01")
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
            "Mark" to "MRK", "Luke" to "LUK", "John" to "JHN", "Acts" to "ACTS", "Romans" to "ROM",
            "1 Corinthians" to "1CO", "2 Corinthians" to "2CO", "Galatians" to "GAL", "Ephesians" to "EPH",
            "Philippians" to "PHP", "Colossians" to "COL", "1 Thessalonians" to "1TH", "2 Thessalonians" to "2TH",
            "1 Timothy" to "1TI", "2 Timothy" to "2TI", "Titus" to "TIT", "Philemon" to "PHM", "Hebrews" to "HEB",
            "James" to "JAS", "1 Peter" to "1PE", "2 Peter" to "2PE", "1 John" to "1JN",
            "2 John" to "2JN", "3 John" to "3JN", "Jude" to "JUD", "Revelation" to "REV"
    )
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        _binding = ScriptureViewerBinding.inflate(inflater, container, false)
        val view = binding.root
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
                setStringPref("bibleVersion", version)
                updateFS("bibleVersion", version)
                val bundle = bundleOf("chapter" to chapter, "psalms" to  psalms, "iteration" to iteration)
                act.navController.navigate(R.id.navigation_scripture, bundle)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {} }
        val url = if(getStringPref("bibleVersion", "ESV") == "ESV"){
            getESVReference(chapter, psalms, iteration)
        }else{
            getReference(chapter, psalms, iteration)
        }
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_nav).isVisible = false
        if(getBoolPref("darkMode", true)){
            act.binding.navHostFragment.setBackgroundColor(Color.parseColor("#121212"))
        }else{
            act.binding.navHostFragment.setBackgroundColor(Color.parseColor("#fffaf0"))
        }
        if(getStringPref("bibleVersion", "ESV") == "ESV"){
            getESV(url)
        }else{
            getScriptureView(url)
        }
        getESV(url)
        return view
    }
    fun getESVReference(chapter: String, psalms: Boolean, iteration: Int) : String {
        val title : String
        val url: String
        if(getBoolPref("darkMode", true)){
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
            log("CURRENT ITERATION $iteration")
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
    fun getReference(chapter: String, psalms: Boolean, iteration: Int) : String{
        val title : String
        var url: String
        val versionId: String? = when (getStringPref("bibleVersion", "ESV")){
            "ESV" ->  bibleIDs["ESV"]
            "CSB" -> bibleIDs["CSB"]
            "AMP" -> bibleIDs["AMP"]
            "KJV" -> bibleIDs["KJV"]
            "NASB" -> bibleIDs["NASB"]
            else -> ""
        }
        url = "https://api.scripture.api.bible/v1/bibles/$versionId/chapters/"
        if(getBoolPref("darkMode", true)){
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
        if(psalms) {
            log("CURRENT ITERATION $iteration")
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
        }else{
            binding.psalmsBack.visibility = View.INVISIBLE
            binding.psalmsNext.visibility = View.VISIBLE
            binding.psalmsNext.text=getText(R.string.scripture_home)
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
    fun getScriptureView(url: String){
        val key = String(Base64.decode(getBibleApiKey(), Base64.DEFAULT))
        var html = ""
        val context = App.applicationContext()
        val cache = DiskBasedCache(context.cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply{
            start()
        }
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
                Response.Listener { response ->
                    val css: String
                    html = response.getJSONObject("data").getString("content")
                    val copyright = response.getJSONObject("data").getString("copyright")
                    val FUMS = response.getJSONObject("meta").getString("fums")
                    if (getBoolPref("darkMode", true)) {
                        css = "https://unquenched.bible/api_bible_night.css"
                    } else {
                        css = "https://unquenched.bible/api_bible_day.css"
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
                        log("THIS IS THE CURRENT INDEX OF s1 $x1")
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
                    log("THIS IS THE CURRENT INDEX OF YES THIS WORKED OUT WELL")
                    html = html.replace("<p class=\"s\">", "<h3 class=\"s\">")
                    var s2 = true
                    var x = 0
                    while(s2){
                        x = html.indexOf("<h3 class=\"s\">", x + 1)
                        log("THIS IS THE CURRENT INDEX OF s $x")
                        if (x == -1){
                            s2 = false
                        }else{
                            val y = html.indexOf("</p>", x)
                            if (y == -1){
                                s2 = false
                            }else{
                                html = html.replaceRange(y, y+4, "</h3>")
                            }
                            log("THIS IS THE CURRENT INDEX OF s Y $y")
                            log("THIS IS THE HTML $html")
                        }
                    }
                    log("THIS IS THE CURRENT INDEX OF YES THIS WORKED OUT WELL AGAIN")
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
                    log("THIS IS THE CURRENT INDEX OF YES THIS WORKED OUT WELL AGAIN")
                    html += "<p class=\"copyright\">$copyright</p> $FUMS"
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
    fun getESV(url: String){
        val key = String(Base64.decode(getESVKey(), Base64.DEFAULT))
        var html = ""
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
                    if(getBoolPref("darkMode", true)){
                        css = "https://unquenched.bible/esv-2.css"
                    }else{
                        css = "https://unquenched.bible/esv-day2.css"
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