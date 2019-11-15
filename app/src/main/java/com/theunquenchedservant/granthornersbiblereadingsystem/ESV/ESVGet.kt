package com.theunquenchedservant.granthornersbiblereadingsystem.ESV

import android.content.Context
import android.util.Base64
import android.webkit.WebView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.BuildConfig
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity

object ESVGet {
    init{
        System.loadLibrary("native-lib")
    }
    external fun getESVKey() : String



    fun getESV(ctx: Context?, url: String): WebView {
        val key = String(Base64.decode(getESVKey(), Base64.DEFAULT))
        var html : String?
        val context = App.applicationContext()
        val wv = WebView(ctx)
        val cache = DiskBasedCache(context.cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null,
                Response.Listener { response ->
                    html = response.getJSONArray("passages").getString(0)
                    html = html!!.replace("\"http://static.esvmedia.org.s3.amazonaws.com/tmp/text.css\"", "esv.css")
                    wv.loadDataWithBaseURL("file:///android_asset/", html,"text/html", "UTF-8", null)
                },

                Response.ErrorListener { error ->
                    MainActivity.log("ERROR: $error")
                }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = key
                return headers
            }
        }
        requestQueue.add(jsonObjectRequest)
        return wv
    }
}