package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.theunquenchedservant.granthornersbiblereadingsystem.R

class TimePreference : DialogPreference {
    private var mTime: Int = 0
    private val mDialogLayoutResId = R.layout.dialog_time
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getInt(index, 0)
    }
    fun setTime(time:Int){
        mTime = time
        persistInt(time)
    }
    fun getTime():Int{
        return mTime
    }
    override  fun onSetInitialValue(restorePersistedValue: Boolean,
                                    defaultValue: Any?) {
        setTime(if (restorePersistedValue)
            getPersistedInt(mTime)
        else
            defaultValue as Int)
    }
    override fun getDialogLayoutResource(): Int {
        return mDialogLayoutResId
    }
}