package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.BuildConfig
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Marker.markAll
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Marker.markSingle
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.data.ReadingLists
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.CardviewsBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeBinding
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
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.updateFirestore

class HomeFragment : Fragment() {

    private var allowResume = true
    private var skipped = false
    private var darkMode = false
    private var alertTheme = 0
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeView by viewModels(
            factoryProducer = { SavedStateViewModelFactory((activity as MainActivity).application, this) }
    )
    private var planSystem = "pgh"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        traceLog(file="HomeFragment.kt", function="onCreateView()")
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        darkMode = getBoolPref(name = "darkMode", defaultValue = true)
        planSystem = getStringPref("planSystem")
        if (planSystem == "pgh") binding.pgh.visibility = View.VISIBLE else if(planSystem=="mcheyne") binding.mcheyne.visibility = View.VISIBLE
        val context = (activity as MainActivity).applicationContext
        if(darkMode){
            binding.materialButton.setBackgroundColor(getColor(context, R.color.buttonBackgroundDark))
            binding.root.backgroundTintList = ColorStateList.valueOf(getColor(context, R.color.backg_night))
        }else{
            binding.materialButton.setBackgroundColor(getColor(context, R.color.buttonBackground))
            binding.root.backgroundTintList = ColorStateList.valueOf(getColor(context, R.color.backg))
        }
        if(getStringPref("planSystem", defaultValue = "pgh") == "pgh") {
            initList(binding.pgh1, resources.getString(R.string.title_pgh_list1))
            initList(binding.pgh2, resources.getString(R.string.title_pgh_list2))
            initList(binding.pgh3, resources.getString(R.string.title_pgh_list3))
            initList(binding.pgh4, resources.getString(R.string.title_pgh_list4))
            initList(binding.pgh5, resources.getString(R.string.title_pgh_list5))
            initList(binding.pgh6, resources.getString(R.string.title_pgh_list6))
            initList(binding.pgh7, resources.getString(R.string.title_pgh_list7))
            initList(binding.pgh8, resources.getString(R.string.title_pgh_list8))
            initList(binding.pgh9, resources.getString(R.string.title_pgh_list9))
            initList(binding.pgh10, resources.getString(R.string.title_pgh_list10))

            viewModel.pgh1.observe(viewLifecycleOwner) { readingList ->
                createCard(
                    binding.pgh1,
                    readingList,
                    R.string.title_pgh_list1,
                    listName = "pgh1",
                    R.array.pgh_list1,
                    psalms = false
                )
            }
            viewModel.pgh2.observe(viewLifecycleOwner) { readingList ->
                createCard(
                    binding.pgh2,
                    readingList,
                    R.string.title_pgh_list2,
                    listName = "pgh2",
                    R.array.pgh_list2,
                    psalms = false
                )
            }
            viewModel.pgh3.observe(viewLifecycleOwner) { readingList ->
                createCard(
                    binding.pgh3,
                    readingList,
                    R.string.title_pgh_list3,
                    listName = "pgh3",
                    R.array.pgh_list3,
                    psalms = false
                )
            }
            viewModel.pgh4.observe(viewLifecycleOwner) { readingList ->
                createCard(
                    binding.pgh4,
                    readingList,
                    R.string.title_pgh_list4,
                    listName = "pgh4",
                    R.array.pgh_list4,
                    psalms = false
                )
            }
            viewModel.pgh5.observe(viewLifecycleOwner) { readingList ->
                createCard(
                    binding.pgh5,
                    readingList,
                    R.string.title_pgh_list5,
                    listName = "pgh5",
                    R.array.pgh_list5,
                    psalms = false
                )
            }
            viewModel.pgh6.observe(viewLifecycleOwner) { readingList ->
                val psalms = getBoolPref(name = "psalms")
                createCard(
                    binding.pgh6,
                    readingList,
                    R.string.title_pgh_list6,
                    listName = "pgh6",
                    R.array.pgh_list6,
                    psalms = psalms
                )
            }
            viewModel.pgh7.observe(viewLifecycleOwner) { readingList ->
                createCard(
                    binding.pgh7,
                    readingList,
                    R.string.title_pgh_list7,
                    listName = "pgh7",
                    R.array.pgh_list7,
                    psalms = false
                )
            }
            viewModel.pgh8.observe(viewLifecycleOwner) { readingList ->
                createCard(
                    binding.pgh8,
                    readingList,
                    R.string.title_pgh_list8,
                    listName = "pgh8",
                    R.array.pgh_list8,
                    psalms = false
                )
            }
            viewModel.pgh9.observe(viewLifecycleOwner) { readingList ->
                createCard(
                    binding.pgh9,
                    readingList,
                    R.string.title_pgh_list9,
                    listName = "pgh9",
                    R.array.pgh_list9,
                    psalms = false
                )
            }
            viewModel.pgh10.observe(viewLifecycleOwner) { readingList ->
                createCard(
                    binding.pgh10,
                    readingList,
                    R.string.title_pgh_list10,
                    listName = "pgh10",
                    R.array.pgh_list10,
                    psalms = false
                )
            }
            viewModel.listsDone.observe(viewLifecycleOwner) { listsDone ->
                when (getBoolPref(name = "darkMode", defaultValue = true)) {
                    true -> binding.materialButton.setTextColor(getColor(context, R.color.unquenchedTextDark))
                    false -> binding.materialButton.setTextColor(getColor(context, R.color.unquenchedText))
                }
                binding.materialButton.isEnabled = true

            }

        }else{
            initList(binding.mcheyneList1, resources.getString(R.string.title_mcheyne_list1))
            initList(binding.mcheyneList2, resources.getString(R.string.title_mcheyne_list2))
            initList(binding.mcheyneList3, resources.getString(R.string.title_mcheyne_list3))
            initList(binding.mcheyneList4, resources.getString(R.string.title_mcheyne_list4))

            viewModel.mcheyne1.observe(viewLifecycleOwner) { readingList ->
                createCard(
                    binding.mcheyneList1,
                    readingList,
                    R.string.title_mcheyne_list1,
                    listName = "mcheyne1",
                    R.array.mcheyne_list1,
                    psalms = false
                )
            }
            viewModel.mcheyne2.observe(viewLifecycleOwner) { readingList ->
                createCard(
                    binding.mcheyneList2,
                    readingList,
                    R.string.title_mcheyne_list2,
                    listName = "mcheyne2",
                    R.array.mcheyne_list2,
                    psalms = false
                )
            }
            viewModel.mcheyne3.observe(viewLifecycleOwner) { readingList ->
                createCard(
                    binding.mcheyneList3,
                    readingList,
                    R.string.title_mcheyne_list3,
                    listName = "mcheyne3",
                    R.array.mcheyne_list3,
                    psalms = false
                )
            }
            viewModel.mcheyne4.observe(viewLifecycleOwner) { readingList ->
                createCard(
                    binding.mcheyneList4,
                    readingList,
                    R.string.title_mcheyne_list4,
                    listName = "mcheyne4",
                    R.array.mcheyne_list4,
                    psalms = false
                )
            }
            viewModel.listsDone.observe(viewLifecycleOwner) { listsDone ->
                when (getBoolPref(name = "darkMode", defaultValue = true)) {
                    true -> binding.materialButton.setTextColor(getColor(context, R.color.unquenchedTextDark))
                    false -> binding.materialButton.setTextColor(getColor(context, R.color.unquenchedText))
                }
                binding.mcheyneButton.isEnabled = true
            }

        }
        if(planSystem == "pgh"){
            binding.pgh.visibility = View.VISIBLE
        }else if(planSystem == "mcheyne"){
            binding.mcheyne.visibility = View.VISIBLE
        }
        binding.loading.visibility = View.GONE
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
        if(planSystem == "pgh"){
            updateButton(getIntPref("pghDone"), binding.materialButton, 10, 9)
        }else{
            updateButton(getIntPref("mcheyneDone"), binding.mcheyneButton, 4, 3)
        }
        updateAlert()
        createButtonListener()
        createNotificationChannel()
        setVisibilities(binding, planSystem=="mcheyne")
        allowResume = false
        createAlarms()
    }

    private fun createCard(cardList: CardviewsBinding, readingLists: ReadingLists, readingString: Int, listName: String, listArray: Int, psalms: Boolean) {
        traceLog(file="HomeFragment.kt", function="createCard()")
        val cardListRoot = cardList.root
        val enabled: Int
        val lineColor: Int
        cardList.root.isClickable = true
        val context = (activity as MainActivity).applicationContext
        when (getBoolPref(name = "darkMode", defaultValue = true)) {
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
            false -> {
                cardListRoot.isEnabled = true
                cardListRoot.setCardBackgroundColor(enabled)
                cardList.listButtons.setBackgroundColor(enabled)
            }
            true -> {
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
            if (!getBoolPref("${listName}Done")) {
                setBoolPref("${listName}Done", true)
                setBoolPref("${listName}DoneDaily", true)
                val doneKey = if(planSystem == "pgh") {
                    "pghDone"
                }else if(planSystem == "mcheyne"){
                    "mcheyneDone"
                }else{
                    "pghDone"
                }
                setIntPref(doneKey, getIntPref(doneKey) + 1)
            }
        }else {
            createCardListener(cardList, listArray, psalms, listDone = "${listName}Done", listName)
        }
    }

    private fun createButtonListener() {
        traceLog(file="HomeFragment.kt", function="createButtonListener()")
        val maxDone: Int
        val button: Button
        val listsDone:Int
        if(planSystem == "pgh"){
            maxDone = 10
            button = binding.materialButton
        }else if(planSystem == "mcheyne"){
            maxDone = 4
            button = binding.mcheyneButton
        }else{
            maxDone = 10
            button = binding.materialButton
        }
        if(getIntPref("${planSystem}Done") < maxDone) {
            button.setOnClickListener {
                hideOthers(cardList = null, binding, isMcheyne = planSystem == "mcheyne")
                val job = markAll(planSystem, context)
                val mNotificationManager =
                    requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                mNotificationManager.cancel(1)
                mNotificationManager.cancel(2)
                (activity as MainActivity).navController.navigate(R.id.navigation_home)
            }
        }
        if (isAdvanceable(maxDone)) {
            button.setOnLongClickListener {
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
        if(!getBoolPref(listDone)) {
            cardView.root.setOnClickListener { view ->
                if (cardView.listButtons.isVisible) {
                    listSwitcher(view, getBoolPref(listDone), binding.materialButton)
                } else {
                    hideOthers(cardView.root, binding, isMcheyne = planSystem == "mcheyne")
                    cardView.listDone.setOnClickListener {
                        changeVisibility(cardView, isCardView = false)
                        markSingle(listDone, planSystem, context)
                        (activity as MainActivity).navController.navigate(R.id.navigation_home)
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
                    data[listDone] = setBoolPref(name = listDone, value = false)
                    data["${listName}Index"] = increaseIntPref(name = "${listName}Index", value = 1)
                    if(planSystem == "pgh"){
                        data["pghDone"] = setIntPref("pghDone", getIntPref("pghDone") - 1)
                    }else if(planSystem == "mcheyne"){
                        data["mcheyneDone"] = setIntPref("mcheyneDone", getIntPref("mcheyneDone") - 1)
                    }
                    if (isLoggedIn()) updateFirestore(data)
                    cardView.root.isEnabled = true
                    cardView.root.setCardBackgroundColor(enabled)
                    cardView.listButtons.setBackgroundColor(enabled)
                    diag.dismiss()
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
