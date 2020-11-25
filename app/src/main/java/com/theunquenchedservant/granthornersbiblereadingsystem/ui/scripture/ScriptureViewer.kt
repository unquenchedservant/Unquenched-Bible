package com.theunquenchedservant.granthornersbiblereadingsystem.ui.scripture

import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.ScriptureViewerBinding
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
        getESV(url)
        return view
    }

    fun getESVReference(chapter: String, psalms: Boolean, iteration: Int) : String {
        val title : String
        val url: String
        if(psalms){
            log("CURRENT ITERATION $iteration")
            var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            when(iteration){
                in 2..5 -> day += 30 * (iteration - 1)
            }
            title = "Psalm $day"
            url = "https://api.esv.org/v3/passage/html/?q=Psalm$day&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
        }else{
            title = chapter
            url = "https://api.esv.org/v3/passage/html/?q=$chapter&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
        }
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
                    html = response.getJSONArray("passages").getString(0)
                    html = html.replace("\"http://static.esvmedia.org.s3.amazonaws.com/tmp/text.css\"", "https://unquenched.bible/esv-2.css")
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