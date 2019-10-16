package com.theunquenchedservant.granthornersbiblereadingsystem.ESV

import android.content.Context
import android.webkit.WebView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.theunquenchedservant.granthornersbiblereadingsystem.BuildConfig
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity

object ESVGet {
    fun getESV(ctx: Context?, url: String): WebView {
        var html : String?
        val wv = WebView(ctx!!)
        val cache = DiskBasedCache(ctx.cacheDir, 1024 * 1024)
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
                headers["Authorization"] = BuildConfig.ESV
                return headers
            }
        }
        requestQueue.add(jsonObjectRequest)
        return wv
    }
}