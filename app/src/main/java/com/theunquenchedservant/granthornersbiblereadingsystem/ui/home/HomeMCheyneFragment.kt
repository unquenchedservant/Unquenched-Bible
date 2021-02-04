package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
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
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeMcheyneBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarms
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createNotificationChannel
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.isLeapDay
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.changeVisibility
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.createUpdateAlert
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.hideOthers
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.initList
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.isAdvanceable
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.isDayOff
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.isHorner
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.isLoggedIn
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.listSwitcher
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.resetDaily
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.setVisibilities
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.updateButton
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import java.util.*

class HomeMCheyneFragment : Fragment() {

    private var allowResume = true
    private var skipped = false
    private lateinit var binding: FragmentHomeMcheyneBinding
    private var darkMode = false
    private val viewModel: HomeMCheyneView by viewModels(
            factoryProducer = { SavedStateViewModelFactory((activity as MainActivity).application, this) }
    )

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeMcheyneBinding.inflate(inflater, container, false)
        darkMode = getBoolPref(name = "darkMode", defaultValue = true)
        initList(binding.cardList1, resources.getString(R.string.title_mcheyne_list1))
        initList(binding.cardList2, resources.getString(R.string.title_mcheyne_list2))
        initList(binding.cardList3, resources.getString(R.string.title_mcheyne_list3))
        initList(binding.cardList4, resources.getString(R.string.title_mcheyne_list4))
        viewModel.list1.observe(viewLifecycleOwner) { readingList ->
            createCard(binding.cardList1, readingList, R.string.title_mcheyne_list1, listName = "mcheyneList1", R.array.mcheyne_list1)
        }
        viewModel.list2.observe(viewLifecycleOwner) { readingList ->
            createCard(binding.cardList2, readingList, R.string.title_mcheyne_list2, listName = "mcheyneList2", R.array.mcheyne_list2)
        }
        viewModel.list3.observe(viewLifecycleOwner) { readingList ->
            createCard(binding.cardList3, readingList, R.string.title_mcheyne_list3, listName = "mcheyneList3", R.array.mcheyne_list3)
        }
        viewModel.list4.observe(viewLifecycleOwner) { readingList ->
            createCard(binding.cardList4, readingList, R.string.title_mcheyne_list4, listName = "mcheyneList4", R.array.mcheyne_list4)
        }
        viewModel.listsDone.observe(viewLifecycleOwner) { listsDone ->
            when (getBoolPref(name = "darkMode", defaultValue = true)) {
                true -> binding.materialButton.setTextColor(getColor(App.applicationContext(), R.color.unquenchedTextDark))
                false -> binding.materialButton.setTextColor(getColor(App.applicationContext(), R.color.unquenchedText))
            }
            updateButton(listsDone.listsDone, binding.materialButton, 4, 3)
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

    override fun onPause() {
        super.onPause()
        if (!allowResume) {
            allowResume = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createUpdateAlert(requireContext())
        if (isDayOff()) setIntPref("dailyStreak", value = 1, updateFS = true) else createButtonListener()
        createNotificationChannel()
        createAlarm(alarmType = "dailyCheck")
        setVisibilities(binding=null, binding, isMcheyne = true)
        allowResume = false
        createAlarms()
    }

    private fun createCard(cardList: CardviewsBinding, readingLists: ReadingLists, readingString: Int, listName: String, listArray: Int) {
        val cardListRoot = cardList.root
        val enabled: Int
        val lineColor: Int
        cardListRoot.isClickable = true
        when (getBoolPref(name = "darkMode", defaultValue = true)) {
            true -> {
                enabled = getColor(App.applicationContext(), R.color.buttonBackgroundDark)
                lineColor = getColor(App.applicationContext(), R.color.unquenchedEmphDark)
            }
            false -> {
                enabled = getColor(App.applicationContext(), R.color.buttonBackground)
                lineColor = getColor(App.applicationContext(), R.color.unquenchedOrange)
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
        when (getStringPref(name = "planType", defaultValue = "horner") == "calendar" && isLeapDay()) {
            true -> {
                cardList.listReading.text = resources.getString(R.string.dayOff)
                cardList.listTitle.text = resources.getString(readingString)
            }
            false -> {
                cardList.listReading.text = readingLists.listReading
                cardList.listTitle.text = resources.getString(readingString)
                createCardListener(cardList, listArray, listDone = "${listName}Done", listName)
            }
        }
    }

    private fun createButtonListener() {
        binding.materialButton.setOnClickListener {
            hideOthers(cardList = null, binding = null, binding, isMcheyne = true)
            markAll(planType = "mcheyne", context)
            val mNotificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(1)
            mNotificationManager.cancel(2)
            (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
        }
        if (isAdvanceable(4)) {
            binding.materialButton.setOnLongClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    resetDaily(requireContext())
                    (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
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

    private fun createCardListener(cardView: CardviewsBinding, arrayId: Int, listDone: String, listName: String) {
        val enabled: Int = when (getBoolPref(name = "darkMode", defaultValue = true)) {
            true -> getColor(App.applicationContext(), R.color.buttonBackgroundDark)
            false -> getColor(App.applicationContext(), R.color.buttonBackground)
        }
        val list = resources.getStringArray(arrayId)
        if (getIntPref("mcheyneListsDone") == 0) {
            cardView.root.setOnClickListener { cView ->
                if (cardView.listButtons.isVisible) {
                    listSwitcher(cView, getIntPref(listDone), binding.materialButton)
                } else {
                    hideOthers(cardView.root, binding = null, binding, isMcheyne = true)
                    cardView.listDone.setOnClickListener {
                        changeVisibility(cardView, isCardView = false)
                        markSingle(listDone, "mcheyne", context=context)
                        cardView.root.setCardBackgroundColor(Color.parseColor("#00383838"))
                        (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
                    }
                    cardView.listRead.setOnClickListener {
                        val chapter = ListHelpers.getChapter(list, listName)
                        val bundle = bundleOf("chapter" to chapter, "psalms" to false, "iteration" to 0)
                        (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                    }

                }
            }
        } else if (isHorner()) {
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
                    (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
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