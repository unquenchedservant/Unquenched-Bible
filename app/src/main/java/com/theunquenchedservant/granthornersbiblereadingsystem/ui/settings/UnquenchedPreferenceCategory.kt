package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.Context
import android.graphics.Color
import androidx.preference.PreferenceCategory
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.AttributeSet
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreferenceCompat
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import org.w3c.dom.Text


class UnquenchedPreferenceCategory : PreferenceCategory {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
constructor(context: Context, attrs: AttributeSet, defStyle : Int) : super(context, attrs, defStyle)


override fun onBindViewHolder(holder: PreferenceViewHolder?) {
    super.onBindViewHolder(holder)
    val view: View = holder?.itemView!!
    val titleView = view.findViewById<TextView>(android.R.id.title)
    titleView.setTextColor(Color.parseColor("#9CB9D3"))
}
}