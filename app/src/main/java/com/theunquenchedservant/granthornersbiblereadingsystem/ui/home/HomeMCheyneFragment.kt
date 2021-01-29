package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
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
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeMcheyneBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarms
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createNotificationChannel
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Dates.isLeapDay
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

class HomeMCheyneFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var user: FirebaseUser? = null
    private var allowResume = true
    private var skipped = false
    private lateinit var binding: FragmentHomeMcheyneBinding
    private var darkMode = false
    private val viewModel: HomeMCheyneView by viewModels(
            factoryProducer =  { SavedStateViewModelFactory((activity as MainActivity).application, this) }
    )

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeMcheyneBinding.inflate(inflater,  container, false)
        darkMode = getBoolPref(name="darkMode", defaultValue = true)
        val backgroundColor: Int
        val emphColor: Int
        binding.cardList1.listTitle.text = resources.getString(R.string.title_mcheyne_list1)
        binding.cardList2.listTitle.text = resources.getString(R.string.title_mcheyne_list2)
        binding.cardList3.listTitle.text = resources.getString(R.string.title_mcheyne_list3)
        binding.cardList4.listTitle.text = resources.getString(R.string.title_mcheyne_list4)
        binding.cardList1.listReading.text = resources.getString(R.string.loading)
        binding.cardList2.listReading.text = resources.getString(R.string.loading)
        binding.cardList3.listReading.text = resources.getString(R.string.loading)
        binding.cardList4.listReading.text = resources.getString(R.string.loading)
        if(darkMode){
            backgroundColor = getColor(App.applicationContext(), R.color.buttonBackgroundDark)
            emphColor = getColor(App.applicationContext(), R.color.unquenchedEmphDark)
        }else{
            backgroundColor = getColor(App.applicationContext(), R.color.buttonBackground)
            emphColor = getColor(App.applicationContext(), R.color.unquenchedOrange)
        }
        binding.cardList1.root.isClickable = false
        binding.cardList2.root.isClickable = false
        binding.cardList3.root.isClickable = false
        binding.cardList4.root.isClickable = false
        binding.cardList1.root.setCardBackgroundColor(backgroundColor)
        binding.cardList1.listReading.setTextColor(emphColor)
        binding.cardList1.lineSeparator.setBackgroundColor(emphColor)
        binding.cardList2.root.setCardBackgroundColor(backgroundColor)
        binding.cardList2.listReading.setTextColor(emphColor)
        binding.cardList2.lineSeparator.setBackgroundColor(emphColor)
        binding.cardList3.root.setCardBackgroundColor(backgroundColor)
        binding.cardList3.listReading.setTextColor(emphColor)
        binding.cardList3.lineSeparator.setBackgroundColor(emphColor)
        binding.cardList4.root.setCardBackgroundColor(backgroundColor)
        binding.cardList4.listReading.setTextColor(emphColor)
        binding.cardList4.lineSeparator.setBackgroundColor(emphColor)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(allowResume){
            (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
            allowResume = false
        }
    }

    override fun onPause() {
        super.onPause()
        if(!allowResume){
            allowResume = true
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (getIntPref(name = "versionNumber")) {
            in 0..60 -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setPositiveButton(R.string.ok) { dialog, _ ->
                    setIntPref(name = "versionNumber", value = 61)
                    dialog.dismiss()
                }
                builder.setNeutralButton(resources.getString(R.string.moreInfo)){ dialog, _ ->
                    setIntPref(name = "versionNumber", value = 61)
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.unquenched.bible/2021/01/23/announcing-unquenched-bible-or-the-professor-grant-horner-bible-reading-system-app-version-2-0/"))
                    startActivity(i)
                    dialog.dismiss()
                }
                builder.setTitle(R.string.title_new_update)
                builder.setMessage(
                        "[ADDED] M'Cheyne Bible Reading Calendar\n\n" +
                                "[ADDED] Weekend Mode. Take Saturday and Sunday off.\n\n" +
                                "[ADDED] Three different methods for your reading plan: Horner, Numerical, and Calendar.\n\n" +
                                "[ADDED] Grace period for your streak. If you forgot to check your reading as done, you have one day before permanently losing your streak!\n\n"+
                                "[ADDED] New Statistics for amount of the Bible read\n\n" +
                                "[UPGRADED] NEW NAME! The Professor Grant Horner Bible Reading App is now Unquenched Bible\n\n"+
                                "[UPDATED] New sign in screen with the option to log in with your email and password\n\n"+
                                "Thank you for your continued use of the app! To find out more about these changes, press 'More Info' below!"
                )
                builder.create().show()
            }
        }
        viewModel.list1.observe(viewLifecycleOwner){ readingList ->
            createCard(binding.cardList1, readingList, R.string.title_mcheyne_list1, listName="mcheyneList1", R.array.mcheyne_list1)
        }
        viewModel.list2.observe(viewLifecycleOwner){ readingList ->
            createCard(binding.cardList2, readingList, R.string.title_mcheyne_list2, listName="mcheyneList2", R.array.mcheyne_list2)
        }
        viewModel.list3.observe(viewLifecycleOwner){ readingList ->
            createCard(binding.cardList3, readingList, R.string.title_mcheyne_list3, listName="mcheyneList3", R.array.mcheyne_list3)
        }
        viewModel.list4.observe(viewLifecycleOwner){ readingList ->
            createCard(binding.cardList4, readingList, R.string.title_mcheyne_list4, listName="mcheyneList4", R.array.mcheyne_list4)
        }
        viewModel.listsDone.observe(viewLifecycleOwner){ listsDone ->
            val backgroundColor: String
            val allDoneBackgroundColor: String
            when(getBoolPref(name="darkMode", defaultValue=true)){
                true-> {
                    val color = getColor(App.applicationContext(), R.color.unquenchedTextDark)
                    backgroundColor = getString(R.string.btn_background_color_dark)
                    allDoneBackgroundColor = getString(R.string.done_btn_background_color_dark)
                    binding.materialButton.setTextColor(color)
                }
                false->{
                    val color = getColor(App.applicationContext(), R.color.unquenchedText)
                    backgroundColor = getString(R.string.btn_background_color)
                    allDoneBackgroundColor = getString(R.string.done_btn_background_color)
                    binding.materialButton.setTextColor(color)
                }
            }
            when(listsDone.listsDone){
                4 -> {
                    binding.materialButton.setText(R.string.done)
                    binding.materialButton.isEnabled = true
                    binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#$allDoneBackgroundColor"))
                    binding.materialButton.backgroundTintMode= PorterDuff.Mode.ADD
                }
                0 -> {
                    binding.materialButton.setText(R.string.not_done)
                    binding.materialButton.isEnabled = true
                    binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#$backgroundColor"))
                }
                in 1..3 -> {
                    binding.materialButton.setText(R.string.btn_mark_remaining)
                    binding.materialButton.isEnabled = true
                    val opacity = when (listsDone.listsDone){
                        in 0..2 -> 100 - (listsDone.listsDone * 5)
                        else -> 100 - ((listsDone.listsDone * 5) - 5)
                    }
                    binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#${opacity}$backgroundColor"))
                    binding.materialButton.backgroundTintMode = PorterDuff.Mode.ADD
                }
            }
        }
        when(getStringPref(name="planType", defaultValue="horner") == "calendar" && isLeapDay()){
            true-> setIntPref(name="dailyStreak", value=1, updateFS=true)
            else -> createButtonListener()
        }
        createNotificationChannel()
        createAlarm(alarmType="dailyCheck")
        setVisibilities(binding=null, binding, isMcheyne=true)
        allowResume = false
        when(savedInstanceState != null) {
            true->createAlarms()
        }
    }

    private fun createCard(cardList: CardviewsBinding, readingLists: ReadingLists, readingString: Int, listName: String, listArray: Int){
        val cardListRoot = cardList.root
        val enabled: Int
        val lineColor: Int
        cardListRoot.isClickable = true
        when(getBoolPref(name="darkMode", defaultValue=true)) {
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
        when(readingLists.listDone){
            0 -> {
                cardListRoot.isEnabled = true
                cardListRoot.setCardBackgroundColor(enabled)
                cardList.listButtons.setBackgroundColor(enabled)
            }
            1-> {
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
        when(getStringPref(name="planType", defaultValue="horner") == "calendar" && isLeapDay()) {
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
        val ctx = App.applicationContext()
        binding.materialButton.setOnClickListener {
            hideOthers(cardList=null, binding=null, binding, isMcheyne=true)
            markAll(planType="mcheyne")
            val mNotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(1)
            mNotificationManager.cancel(2)
            (activity as MainActivity).navController.navigate(R.id.navigation_home)
        }
        when (getIntPref(name="listsDone")) {
            4 -> {
                when (getStringPref(name = "planType", defaultValue = "horner") != "calendar") {
                    true -> {
                        binding.materialButton.setOnLongClickListener {
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                                resetDaily()
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
            }
        }
    }

    private fun createCardListener(cardView: CardviewsBinding, arrayId: Int, listDone: String, listName: String){
        val list = resources.getStringArray(arrayId)
        when(getIntPref(listDone)) {
            0 -> {
                cardView.root.setOnClickListener { cView ->
                    when (cardView.listButtons.isVisible) {
                        true -> listSwitcher(cView, getIntPref(listDone), binding.materialButton)
                        false -> {
                            hideOthers(cardView.root, binding = null, binding, isMcheyne = true)
                            cardView.listDone.setOnClickListener {
                                changeVisibility(cardView, isCardView = false)
                                markSingle(listDone)
                                cardView.root.setCardBackgroundColor(Color.parseColor("#00383838"))
                                (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
                            }
                            cardView.listRead.setOnClickListener {
                                lateinit var bundle: Bundle
                                val chapter: String = when (getStringPref(name = "planType", defaultValue = "horner")) {
                                    "horner" -> list[getIntPref(listName)]
                                    "numerical" -> {
                                        var index = getIntPref(name = "mcheyneCurrentDayIndex", defaultValue = 0)
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

                                (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                            }

                        }
                    }
                }
            }
            else -> {
                when (getStringPref(name = "planType", defaultValue = "horner")){
                    "horner" -> {
                        val enabled: Int = when (getBoolPref(name = "darkMode", defaultValue = true)) {
                            true->getColor(App.applicationContext(), R.color.buttonBackgroundDark)
                            false->getColor(App.applicationContext(), R.color.buttonBackground)
                        }
                        cardView.root.setOnLongClickListener {
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setPositiveButton(getString(R.string.yes)) { diag, _ ->
                                setIntPref(name = listDone, value = 0)
                                increaseIntPref(name = listName, value = 1)
                                val isLogged = FirebaseAuth.getInstance().currentUser
                                when(isLogged != null) {
                                    true -> {
                                        val data = mutableMapOf<String, Any>()
                                        data[listDone] = 0
                                        data[listName] = getIntPref(listName)
                                        db.collection("main").document(isLogged.uid).update(data)
                                    }
                                }
                                cardView.root.isEnabled = true
                                cardView.root.setCardBackgroundColor(enabled)
                                cardView.listButtons.setBackgroundColor(enabled)
                                diag.dismiss()
                                (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
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