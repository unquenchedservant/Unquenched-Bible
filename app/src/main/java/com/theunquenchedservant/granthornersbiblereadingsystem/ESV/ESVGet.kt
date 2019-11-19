package com.theunquenchedservant.granthornersbiblereadingsystem.ESV

import android.content.Context
import android.util.Base64
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.BuildConfig
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import java.util.*
import kotlin.collections.HashMap

object ESVGet {
    init{
        System.loadLibrary("native-lib")
    }
    private external fun getESVKey() : String

    fun getESVReference(chapter: String, ctx: Context?, psalms: Boolean, iteration: Int){
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
        val wv = getESV(ctx, url)
        chapterShower(wv, title, psalms, iteration, ctx)
    }

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

    private fun chapterShower(wv : WebView, title: String, psalms: Boolean, iteration: Int, ctx: Context?){
        val alert = AlertDialog.Builder(ctx!!)
        alert.setTitle(title)
        alert.setView(wv)
        if(psalms){
            when(iteration){
                in 1..4 -> {
                    alert.setPositiveButton("Next"){_,_ ->
                        getESVReference("no", ctx, psalms, iteration+1)
                    }
                }
                5 -> {
                    alert.setNeutralButton("Close"){dialog, _ ->
                        dialog.dismiss()
                    }
                }
            }
        }else{
            alert.setNeutralButton("Close"){ dialog, _ ->
                dialog.dismiss()
            }
        }
        alert.show()
    }

}