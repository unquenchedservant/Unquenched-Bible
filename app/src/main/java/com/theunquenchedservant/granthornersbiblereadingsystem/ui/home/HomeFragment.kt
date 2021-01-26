package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.app.NotificationManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
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
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.hideOthers
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.listSwitcher
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.resetDaily
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.setVisibilities
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import java.util.*

class HomeFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var user: FirebaseUser? = null
    private var allowResume = true
    private var skipped = false
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeView by viewModels(
            factoryProducer = { SavedStateViewModelFactory((activity as MainActivity).application, this) }
    )

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        when (allowResume) {
            true -> {
                (activity as MainActivity).navController.navigate(R.id.navigation_home)
                allowResume = false
            }
        }
    }

    override fun onPause() {
        super.onPause()
        when (allowResume) {
            false -> allowResume = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (getIntPref(name = "versionNumber")) {
            in 0..58 -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setPositiveButton(R.string.ok) { something, _ ->
                    setIntPref(name = "versionNumber", value = 59)
                    something.dismiss()
                }
                builder.setTitle(R.string.title_new_update)
                builder.setMessage(
                        "[ADDED] New Bible Versions (AMP, CSB, KJV, NASB95, and the NASB20)\n\n" +
                                "You can change the translation in the scripture window or under Plan Settings\n\n" +
                                "[UPDATED] You can no longer manually set a list you currently have marked as done.\n\n" +
                                "[FIXED] Song of Solomon in ESV dark mode now is in the right colors. (Thank you Meinhard)\n\n" +
                                "[Potentially FIXED] Issue where the home screen was updating when you opened the app after the lists should have moved forward"
                )
                builder.create().show()
            }
        }
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
            val backgroundColor: String
            val allDoneBackgroundColor: String
            when (getBoolPref(name = "darkMode", defaultValue = true)) {
                true -> {
                    val color = getColor(App.applicationContext(), R.color.unquenchedTextDark)
                    backgroundColor = getString(R.string.btn_background_color_dark)
                    allDoneBackgroundColor = getString(R.string.done_btn_background_color_dark)
                    binding.materialButton.setTextColor(color)
                }
                false -> {
                    val color = getColor(App.applicationContext(), R.color.unquenchedText)
                    backgroundColor = getString(R.string.btn_background_color)
                    allDoneBackgroundColor = getString(R.string.done_btn_background_color)
                    binding.materialButton.setTextColor(color)
                }
            }
            when (listsDone.listsDone) {
                10 -> {
                    binding.materialButton.setText(R.string.done)
                    binding.materialButton.isEnabled = true
                    binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#$allDoneBackgroundColor"))
                    binding.materialButton.backgroundTintMode = PorterDuff.Mode.ADD
                }
                0 -> {
                    binding.materialButton.setText(R.string.not_done)
                    binding.materialButton.isEnabled = true
                    binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#$backgroundColor"))
                }
                in 1..9 -> {
                    binding.materialButton.setText(R.string.btn_mark_remaining)
                    binding.materialButton.isEnabled = true
                    val opacity = if (listsDone.listsDone < 5) {
                        100 - (listsDone.listsDone * 5)
                    } else {
                        100 - ((listsDone.listsDone * 5) - 5)
                    }
                    binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#${opacity}$backgroundColor"))
                    binding.materialButton.backgroundTintMode = PorterDuff.Mode.ADD
                }
            }
        }

        createButtonListener()
        createNotificationChannel()
        createAlarm(alarmType = "dailyCheck")
        setVisibilities(binding)
        allowResume = false
        when (savedInstanceState != null) {
            true -> createAlarms()
        }
    }

    private fun createCard(cardList: CardviewsBinding, readingLists: ReadingLists, readingString: Int, listName: String, listArray: Int, psalms: Boolean) {
        val cardListRoot = cardList.root
        val enabled: Int
        val lineColor: Int
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
        cardList.listReading.text = readingLists.listReading
        cardList.listTitle.text = resources.getString(readingString)
        createCardListener(cardList, listArray, psalms, listDone = "${listName}Done", listName)
    }

    private fun createButtonListener() {
        val ctx = App.applicationContext()
        binding.materialButton.setOnClickListener {
            hideOthers(cardList = null, binding)
            markAll()
            val mNotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(1)
            mNotificationManager.cancel(2)
            (activity as MainActivity).navController.navigate(R.id.navigation_home)
        }
        when (getIntPref(name = "listsDone")) {
            10 -> {
                when (getStringPref(name = "planType", defaultValue = "horner")) {
                    "horner", "numerical" -> {
                        binding.materialButton.setOnLongClickListener {
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                                resetDaily()
                                (activity as MainActivity).navController.navigate(R.id.navigation_home)
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
            }
        }
    }

    private fun createCardListener(cardView: CardviewsBinding, arrayId: Int, psalms: Boolean, listDone: String, listName: String) {
        val list = resources.getStringArray(arrayId)
        when (getIntPref(listDone)) {
            0 -> {
                cardView.root.setOnClickListener { view ->
                    when {
                        cardView.listButtons.isVisible -> listSwitcher(view, getIntPref(listDone), binding.materialButton)
                        else -> {
                            hideOthers(cardView.root, binding)
                            cardView.listDone.setOnClickListener {
                                changeVisibility(cardView, isCardView = false)
                                markSingle(listDone)
                                cardView.root.setCardBackgroundColor(Color.parseColor("#00383838"))
                                (activity as MainActivity).navController.navigate(R.id.navigation_home)
                            }
                            cardView.listRead.setOnClickListener {
                                lateinit var bundle: Bundle
                                when {
                                    (cardView.root != binding.cardList6.root || cardView.root == binding.cardList6.root && !psalms) -> {
                                        val chapter: String = when (getStringPref(name = "planType", defaultValue = "horner")) {
                                            "horner" -> list[getIntPref(listName)]
                                            "numerical" -> {
                                                var index = getIntPref(name = "currentDayIndex", defaultValue = 0)
                                                while (index >= list.size) {
                                                    index -= list.size
                                                }
                                                list[index]
                                            }
                                            "calendar" -> {
                                                var index = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                                                while (index >= list.size) {
                                                    index -= list.size
                                                }
                                                list[index]
                                            }
                                            else -> list[getIntPref(listName)]
                                        }
                                        bundle = bundleOf("chapter" to chapter, "psalms" to false, "iteration" to 0)
                                    }
                                    (cardView.root == binding.cardList6.root && psalms) -> bundle = bundleOf("chapter" to "no", "psalms" to true, "iteration" to 1)
                                }
                                (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                            }
                        }
                    }
                }
            }
            else -> {
                when (getStringPref(name = "planType", defaultValue = "horner")) {
                    "horner" -> {
                        val enabled: Int = when (getBoolPref("darkMode", defaultValue = true)) {
                            true -> getColor(App.applicationContext(), R.color.buttonBackgroundDark)
                            false -> getColor(App.applicationContext(), R.color.buttonBackground)
                        }
                        cardView.root.setOnLongClickListener {
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setPositiveButton(getString(R.string.yes)) { diag, _ ->
                                setIntPref(name = listDone, value = 0)
                                increaseIntPref(name = listName, value = 1)
                                val isLogged = FirebaseAuth.getInstance().currentUser
                                if (isLogged != null) {
                                    val data = mutableMapOf<String, Any>()
                                    data[listDone] = 0
                                    data[listName] = getIntPref(listName)
                                    db.collection("main").document(isLogged.uid).update(data)
                                }
                                cardView.root.isEnabled = true
                                cardView.root.setCardBackgroundColor(enabled)
                                cardView.listButtons.setBackgroundColor(enabled)
                                diag.dismiss()
                                (activity as MainActivity).navController.navigate(R.id.navigation_home)
                            }
                            builder.setNegativeButton(getString(R.string.no)) { diag, _ ->
                                diag.dismiss()
                            }
                            builder.setMessage(R.string.msg_reset_one)
                            builder.setTitle(R.string.title_reset_list)
                            builder.show()
                            true
                        }
                    }
                }
            }
        }
    }
}