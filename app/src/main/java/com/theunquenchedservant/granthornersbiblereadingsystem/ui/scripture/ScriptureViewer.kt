package com.theunquenchedservant.granthornersbiblereadingsystem.ui.scripture

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.ScriptureViewerBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.home.HomeFragment
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
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
        val url = getESVReference(chapter, psalms, iteration)

        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_nav).isVisible = false
        if(getBoolPref("darkMode")){
            act.binding.navHostFragment.setBackgroundColor(Color.parseColor("#121212"))
        }else{
            act.binding.navHostFragment.setBackgroundColor(Color.parseColor("#fffaf0"))
        }
        getESV(url)
        return view
    }
    fun getESVReference(chapter: String, psalms: Boolean, iteration: Int) : String {
        val title : String
        val url: String
        if(getBoolPref("darkMode")){
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
                    binding.psalmsNext.text=getText(R.string.scripture_done)
                    day += 30 * (iteration - 1)
                }
            }
            title = "Psalm $day"
            url = "https://api.esv.org/v3/passage/html/?q=Psalm$day&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
        }else{
            binding.psalmsBack.visibility = View.INVISIBLE
            binding.psalmsNext.visibility = View.VISIBLE
            binding.psalmsNext.text=getText(R.string.scripture_done)
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
                    if(getBoolPref("darkMode")){
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