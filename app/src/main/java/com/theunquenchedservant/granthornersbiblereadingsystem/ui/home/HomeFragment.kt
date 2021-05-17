package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Marker.markAll
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Marker.markSingle
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.data.ReadingLists
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.CardviewsBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarms
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createNotificationChannel
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.changeVisibility
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.getChapter
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.hideOthers
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.initList
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.isAdvanceable
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.isHorner
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.isLoggedIn
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.isPsalm
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.listSwitcher
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.resetDaily
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.setVisibilities
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.updateButton
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ReadingLists as readinggList
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref

class HomeFragment : Fragment() {

    private var allowResume = true
    private var skipped = false
    private var darkMode = false
    private var alertTheme = 0
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeView by viewModels(
            factoryProducer = { SavedStateViewModelFactory((activity as MainActivity).application, this) }
    )

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        traceLog(file="HomeFragment.kt", function="onCreateView()")
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        darkMode = getBoolPref(name = "darkMode", defaultValue = true)
        val context = (activity as MainActivity).applicationContext
        if(darkMode){
            binding.materialButton.setBackgroundColor(getColor(context, R.color.buttonBackgroundDark))
        }else{
            binding.materialButton.setBackgroundColor(getColor(context, R.color.buttonBackground))
        }
        readinggList(binding, resources)
        viewModel.list1.observe(viewLifecycleOwner) { readingList ->
            createCard(binding.cardList1, readingList, R.string.title_pgh_list1, listName = "list1", R.array.list_1, psalms = false)
        }
        viewModel.list2.observe(viewLifecycleOwner) { readingList ->
            createCard(binding.cardList2, readingList, R.string.title_pgh_list2, listName = "list2", R.array.list_2, psalms = false)
        }
        viewModel.list3.observe(viewLifecycleOwner) { readingList ->
            createCard(binding.cardList3, readingList, R.string.title_pgh_list3, listName = "list3", R.array.list_3, psalms = false)
        }
        viewModel.list4.observe(viewLifecycleOwner) { readingList ->
            createCard(binding.cardList4, readingList, R.string.title_pgh_list4, listName = "list4", R.array.list_4, psalms = false)
        }
        viewModel.list5.observe(viewLifecycleOwner) { readingList ->
            createCard(binding.cardList5, readingList, R.string.title_pgh_list5, listName = "list5", R.array.list_5, psalms = false)
        }
        viewModel.list6.observe(viewLifecycleOwner) { readingList ->
            val psalms = getBoolPref(name = "psalms")
            createCard(binding.cardList6, readingList, R.string.title_pgh_list6, listName = "list6", R.array.list_6, psalms = psalms)
        }
        viewModel.list7.observe(viewLifecycleOwner) { readingList ->
            createCard(binding.cardList7, readingList, R.string.title_pgh_list7, listName = "list7", R.array.list_7, psalms = false)
        }
        viewModel.list8.observe(viewLifecycleOwner) { readingList ->
            createCard(binding.cardList8, readingList, R.string.title_pgh_list8, listName = "list8", R.array.list_8, psalms = false)
        }
        viewModel.list9.observe(viewLifecycleOwner) { readingList ->
            createCard(binding.cardList9, readingList, R.string.title_pgh_list9, listName = "list9", R.array.list_9, psalms = false)
        }
        viewModel.list10.observe(viewLifecycleOwner) { readingList ->
            createCard(binding.cardList10, readingList, R.string.title_pgh_list10, listName = "list10", R.array.list_10, psalms = false)
        }
        viewModel.listsDone.observe(viewLifecycleOwner) { listsDone ->
            when (getBoolPref(name = "darkMode", defaultValue = true)) {
                true -> binding.materialButton.setTextColor(getColor(context, R.color.unquenchedTextDark))
                false -> binding.materialButton.setTextColor(getColor(context, R.color.unquenchedText))
            }
            updateButton(listsDone.listsDone, binding.materialButton, 10, 9)
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

    override fun onPause() {
        super.onPause()
        traceLog(file="HomeFragment.kt", function="onPause()")
        when (allowResume) {
            false -> allowResume = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        traceLog(file="HomeFragment.kt", function="onViewCreated()")
        createButtonListener()
        createNotificationChannel()
        createAlarm(alarmType = "dailyCheck")
        setVisibilities(binding)
        allowResume = false
        createAlarms()
    }

    private fun createCard(cardList: CardviewsBinding, readingLists: ReadingLists, readingString: Int, listName: String, listArray: Int, psalms: Boolean) {
        traceLog(file="HomeFragment.kt", function="createCard()")
        val cardListRoot = cardList.root
        val enabled: Int
        val lineColor: Int
        cardListRoot.isClickable = true
        val context = (activity as MainActivity).applicationContext
        when (App().preferences!!.settings.darkMode) {
            true -> {
                enabled = getColor(context, R.color.buttonBackgroundDark)
                lineColor = getColor(context, R.color.unquenchedEmphDark)
            }
            false -> {
                enabled = getColor(context, R.color.buttonBackground)
                lineColor = getColor(context, R.color.unquenchedOrange)
            }
        }
        val disabled = Color.parseColor("#00383838")
        when (readingLists.listDone) {
            0 -> {
                cardListRoot.isEnabled = true
                cardListRoot.setCardBackgroundColor(enabled)
                cardList.listButtons.setBackgroundColor(enabled)
            }
            1 -> {
                cardListRoot.isEnabled = true
                cardListRoot.setCardBackgroundColor(disabled)
                cardList.listButtons.setBackgroundColor(disabled)
            }
        }
        cardList.listReading.setTextColor(lineColor)
        cardList.listDone.setTextColor(lineColor)
        cardList.listRead.setTextColor(lineColor)
        cardList.buttonSeparator.setBackgroundColor(lineColor)
        cardList.lineSeparator.setBackgroundColor(lineColor)
        cardList.listReading.text = readingLists.listReading
        cardList.listTitle.text = resources.getString(readingString)
        if(readingLists.listReading == "Day Off"){
            cardListRoot.isEnabled = false
            cardListRoot.setCardBackgroundColor(disabled)
            cardList.listButtons.setBackgroundColor(disabled)
            setIntPref("listsDone", 1)
        }else {
            createCardListener(cardList, listArray, psalms, listDone = "${listName}Done", listName)
        }
    }

    private fun createButtonListener() {
        traceLog(file="HomeFragment.kt", function="createButtonListener()")
        binding.materialButton.setOnClickListener {
            hideOthers(cardList = null, binding)
            val job = markAll("pgh", context)
            job.addOnSuccessListener {
                val mNotificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                mNotificationManager.cancel(1)
                mNotificationManager.cancel(2)
                (activity as MainActivity).navController.navigate(R.id.navigation_home)
            }
        }
        if (isAdvanceable(10)) {
            binding.materialButton.setOnLongClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    resetDaily(requireContext()).addOnSuccessListener {
                        (activity as MainActivity).navController.navigate(R.id.navigation_home)
                    }
                }
                builder.setNegativeButton(getString(R.string.no)) { diag, _ ->
                    diag.dismiss()
                }
                builder.setMessage(getString(R.string.msg_reset_all))
                builder.setTitle(getString(R.string.title_reset_lists))
                builder.show()
                true
            }
        }
    }

    private fun createCardListener(cardView: CardviewsBinding, arrayId: Int, psalms: Boolean, listDone: String, listName: String) {
        traceLog(file="HomeFragment.kt", function="createCardListener()")
        val list = resources.getStringArray(arrayId)
        val context = (activity as MainActivity).applicationContext
        val enabled: Int = when (getBoolPref("darkMode", defaultValue = true)) {
            true -> getColor(context, R.color.buttonBackgroundDark)
            false -> getColor(context, R.color.buttonBackground)
        }
        if(getIntPref(listDone) == 0) {
            cardView.root.setOnClickListener { view ->
                if (cardView.listButtons.isVisible) {
                    listSwitcher(view, getIntPref(listDone), binding.materialButton)
                } else {
                    hideOthers(cardView.root, binding)
                    cardView.listDone.setOnClickListener {
                        changeVisibility(cardView, isCardView = false)
                        markSingle(listDone, "pgh", context).addOnSuccessListener {
                            cardView.root.setCardBackgroundColor(Color.parseColor("#00383838"))
                            (activity as MainActivity).navController.navigate(R.id.navigation_home)
                        }
                    }
                    cardView.listRead.setOnClickListener {
                       val bundle = if(isPsalm(cardView, binding, psalms)) {
                            bundleOf("chapter" to "no", "psalms" to true, "iteration" to 1)
                        }else{
                            val chapter = getChapter(list, listName)
                            bundleOf("chapter" to chapter, "psalms" to false, "iteration" to 0)
                        }
                        (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                    }
                }
            }
        }else if(isHorner()) {
            cardView.root.setOnLongClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setPositiveButton(getString(R.string.yes)) { diag, _ ->
                    val data = mutableMapOf<String, Any>()
                    data[listDone] = setIntPref(name = listDone, value = 0)
                    data[listName] = increaseIntPref(name = listName, value = 1)
                    if (isLoggedIn()) Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).update(data)
                    cardView.root.isEnabled = true
                    cardView.root.setCardBackgroundColor(enabled)
                    cardView.listButtons.setBackgroundColor(enabled)
                    diag.dismiss()
                    (activity as MainActivity).navController.navigate(R.id.navigation_home)
                }
                builder.setNegativeButton(getString(R.string.no)) { diag, _ -> diag.dismiss() }
                builder.setMessage(R.string.msg_reset_one)
                builder.setTitle(R.string.title_reset_list)
                builder.show()
                true
            }
        }
    }
}
