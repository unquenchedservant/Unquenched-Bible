package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeMcheyneBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarms
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createNotificationChannel
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Firestore
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.createUpdateAlert
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ReadingLists as readingList

class   HomeMCheyneFragment : Fragment() {

    private var allowResume = true
    private var skipped = false
    private lateinit var binding: FragmentHomeMcheyneBinding
    private lateinit var preferences:Preferences

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeMcheyneBinding.inflate(inflater, container, false)
        if((activity as MainActivity).isPreferenceInitialized()) {
            readingList(null, binding, resources, (activity as MainActivity), false, false)
        }else{
            readingList(null, binding, resources, (activity as MainActivity), false, true)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (allowResume) {
            (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
            allowResume = false
        }
    }

    override fun onDestroy(){
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }
    override fun onStop() {
        super.onStop()
        CoroutineScope(Dispatchers.IO).launch {
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }
    override fun onPause() {
        super.onPause()
        Log.traceLog(file = "HomeFragment.kt", function = "onPause()")
        CoroutineScope(Dispatchers.IO).launch{
            Firestore().updateFirestoreData(preferences.getMap())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createUpdateAlert(requireContext(), (activity as MainActivity).preferences)
        createNotificationChannel()
        createAlarm(alarmType = "dailyCheck")
        allowResume = false
        createAlarms()
    }
}