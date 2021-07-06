package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarms
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createNotificationChannel
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Firestore
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.jvm.isAccessible
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ReadingLists as readingList

class HomeFragment : Fragment() {

    private var allowResume = true
    private var skipped = false
    private var darkMode = false
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        traceLog(file="HomeFragment.kt", function="onCreateView()")

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        if((activity as MainActivity).isPreferenceInitialized()) {
            readingList(binding, null, resources, (activity as MainActivity), true, false)
        }else{
            readingList(binding, null, resources, (activity as MainActivity), true, true)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        traceLog(file="HomeFragment.kt", function="onResume()")
        when (allowResume) {
            true -> {
                allowResume = false
            }
        }
    }
    override fun onDestroy(){
        super.onDestroy()
        if((activity as MainActivity).isPreferenceInitialized()) {
            (activity as MainActivity).preferences.writeToFirestore()
        }
    }
    override fun onStop() {
        super.onStop()
        if((activity as MainActivity).isPreferenceInitialized()) {
            (activity as MainActivity).preferences.writeToFirestore()
        }
    }
    override fun onPause() {
        super.onPause()
        traceLog(file="HomeFragment.kt", function="onPause()")
        if((activity as MainActivity).isPreferenceInitialized()) {
            (activity as MainActivity).preferences.writeToFirestore()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        traceLog(file="HomeFragment.kt", function="onViewCreated()")
        createNotificationChannel()
        createAlarm(alarmType = "dailyCheck")
        allowResume = false
        createAlarms()
    }
}
