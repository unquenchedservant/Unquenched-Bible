package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.Context
import android.graphics.Color
import androidx.preference.PreferenceCategory
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.preference.PreferenceViewHolder
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref


class UnquenchedPreferenceCategory : PreferenceCategory {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private lateinit var holder_: PreferenceViewHolder

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        holder_ = holder!!
        val view: View = holder.itemView
        val titleView = view.findViewById<TextView>(android.R.id.title)
        if (getBoolPref("darkMode", true)) {
            titleView?.setTextColor(Color.parseColor("#9CB9D3"))
        } else {
            titleView?.setTextColor(Color.parseColor("#000000"))
        }
    }

    fun setTitleColors() {
        val view: View = holder_.itemView
        val titleView = view.findViewById<TextView>(android.R.id.title)
        if (getBoolPref("darkMode", true)) {
            titleView.setTextColor(Color.parseColor("#9CB9D3"))
        } else {
            titleView.setTextColor(Color.parseColor("#000000"))
        }
    }
}