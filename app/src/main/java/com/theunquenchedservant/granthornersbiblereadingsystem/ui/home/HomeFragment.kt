package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Marker.markAll
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Marker.markSingle
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
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

class HomeFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var user: FirebaseUser? = null
    private var allowResume = true
    private var skipped = false
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeView by viewModels(
            factoryProducer =  { SavedStateViewModelFactory((activity as MainActivity).application, this) }
    )

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,  container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(getIntPref("versionNumber") < 54){
            val builder = AlertDialog.Builder(requireContext())
            builder.setPositiveButton(R.string.ok) { something ,_ ->
                setIntPref("versionNumber", 56)
                something.dismiss()
            }
            builder.setTitle(R.string.title_new_update)
            builder.setMessage(
                    "[ADDED] Once you're done with a list, holding the list card will allow you to advance just that list\n\n"+
                            "[ADDED] If you've finished all 10 lists, holding the 'Already Done For Today' button will advance all the lists. (Thank you Byard for the suggestion). \n\n" +
                            "[FIXED] Changed the button in the Scripture page to better reflect what it does (goes to the home screen) (Thank you Sheila for bringing this up)\n\n"+
                            "[FIXED] Advancing a single list now works for users who are logged in to google in the app without doubling.\n\n"+
                            "[UPDATED] Dark Mode is now the default\n\n\n"+
                            "IMPORTANT: If you have a minute, fill out the Google Form survey (under the Settings tab).\n\nI am trying to gauge interest in an app name change as well as adding other reading plans/systems such as the M'Cheyne system."
                            
            )
            builder.create().show()
        }
        if(getIntPref("versionNumber") == 54 || getIntPref("versionNumber") == 55){
            val builder = AlertDialog.Builder(requireContext())
            builder.setPositiveButton(R.string.ok) { something ,_ ->
                setIntPref("versionNumber", 55)
                something.dismiss()
            }
            builder.setTitle(R.string.title_new_update)
            builder.setMessage(
                    "[FIXED] Advancing a single list now works for users who are logged in to google in the app without doubling."
            )
            builder.create().show()
        }
        viewModel.list1.observe(viewLifecycleOwner){
            createCard(binding.cardList1, it, R.string.title_pgh_list1, "list1", R.array.list_1, false)
        }
        viewModel.list2.observe(viewLifecycleOwner){
            createCard(binding.cardList2, it, R.string.title_pgh_list2, "list2", R.array.list_2, false)
        }
        viewModel.list3.observe(viewLifecycleOwner){
            createCard(binding.cardList3, it, R.string.title_pgh_list3, "list3", R.array.list_3, false)
        }
        viewModel.list4.observe(viewLifecycleOwner){
            createCard(binding.cardList4, it, R.string.title_pgh_list4, "list4", R.array.list_4, false)
        }
        viewModel.list5.observe(viewLifecycleOwner){
            createCard(binding.cardList5, it, R.string.title_pgh_list5, "list5", R.array.list_5, false)
        }
        viewModel.list6.observe(viewLifecycleOwner){
            val psalms = getBoolPref("psalms")
            createCard(binding.cardList6, it, R.string.title_pgh_list6, "list6", R.array.list_6, psalms)
        }
        viewModel.list7.observe(viewLifecycleOwner){
            createCard(binding.cardList7, it, R.string.title_pgh_list7, "list7", R.array.list_7, false)
        }
        viewModel.list8.observe(viewLifecycleOwner){
            createCard(binding.cardList8, it, R.string.title_pgh_list8, "list8", R.array.list_8, false)
        }
        viewModel.list9.observe(viewLifecycleOwner){
            createCard(binding.cardList9, it, R.string.title_pgh_list9, "list9", R.array.list_9, false)
        }
        viewModel.list10.observe(viewLifecycleOwner){
            createCard(binding.cardList10, it, R.string.title_pgh_list10, "list10", R.array.list_10, false)
        }
        viewModel.listsDone.observe(viewLifecycleOwner){
            val backgroundColor: String
            val allDoneBackgroundColor: String
            if(getBoolPref("darkMode", true)){
                val color = getColor(App.applicationContext(), R.color.unquenchedTextDark)
                backgroundColor = getString(R.string.btn_background_color_dark)
                allDoneBackgroundColor = getString(R.string.done_btn_background_color_dark)
                binding.materialButton.setTextColor(color)
            }else{
                val color = getColor(App.applicationContext(), R.color.unquenchedText)
                backgroundColor = getString(R.string.btn_background_color)
                allDoneBackgroundColor = getString(R.string.done_btn_background_color)
                binding.materialButton.setTextColor(color)
            }
            when(it.listsDone){
                10 -> {
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
                in 1..9 -> {
                    binding.materialButton.setText(R.string.btn_mark_remaining)
                    binding.materialButton.isEnabled = true
                    val opacity = if (it.listsDone < 5){
                        100 - (it.listsDone * 5)
                    }else{
                        100 - ((it.listsDone * 5) - 5)
                    }
                    log("testing testing $backgroundColor")
                    log("testing testing #${opacity}$backgroundColor")
                    binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#${opacity}$backgroundColor"))
                    binding.materialButton.backgroundTintMode = PorterDuff.Mode.ADD
                }
            }
        }

        createButtonListener()
        createNotificationChannel()
        createAlarm("dailyCheck")
        setVisibilities(binding)
        allowResume = false
        if(savedInstanceState != null) {
            when (getIntPref("firstRun")) {
                0 -> {
                    googleSignIn()
                    setIntPref("firstRun", 1)
                }
                1 -> {
                    createAlarms()
                }
            }
        }
    }

    private fun createCard(cardList: CardviewsBinding, readingLists: ReadingLists, readingString: Int, listName: String, listArray: Int, psalms:Boolean){
        val cardListRoot = cardList.root
        val enabled: Int
        val lineColor: Int
        if(getBoolPref("darkMode", true)){
            enabled = getColor(App.applicationContext(), R.color.buttonBackgroundDark)
            lineColor = getColor(App.applicationContext(), R.color.unquenchedEmphDark)
        }else{
            enabled = getColor(App.applicationContext(), R.color.buttonBackground)
            lineColor = getColor(App.applicationContext(), R.color.unquenchedOrange)
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
        cardList.listReading.text = readingLists.listReading
        cardList.listTitle.text = resources.getString(readingString)
        createCardListener(cardList, listArray, psalms, "${listName}Done", listName)
    }
    private fun createButtonListener(){
        val ctx = App.applicationContext()
        binding.materialButton.setOnClickListener {
            hideOthers(null, binding)
            markAll()
            val mNotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(1)
            mNotificationManager.cancel(2)
            (activity as MainActivity).navController.navigate(R.id.navigation_home)
        }
        if(getIntPref("listsDone") == 10){
            binding.materialButton.setOnLongClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setPositiveButton(getString(R.string.yes)){diag,_->
                    resetDaily()
                    (activity as MainActivity).navController.navigate(R.id.navigation_home)
                }
                builder.setNegativeButton(getString(R.string.no)){diag,_->
                    diag.dismiss()
                }
                builder.setMessage(getString(R.string.msg_reset_all))
                builder.setTitle(getString(R.string.title_reset_lists))
                builder.show()
                true
            }
        }
    }

    private fun createCardListener(cardView: CardviewsBinding, arrayId: Int, psalms: Boolean, listDone: String, listName: String){
        val list = resources.getStringArray(arrayId)
        if (getIntPref(listDone) == 0){
            cardView.root.setOnClickListener {
                if (cardView.listButtons.isVisible) {
                    listSwitcher(it, getIntPref(listDone), binding.materialButton)
                } else {
                    hideOthers(cardView.root, binding)
                    cardView.listDone.setOnClickListener {
                        changeVisibility(cardView, false)
                        markSingle(listDone)
                        cardView.root.setCardBackgroundColor(Color.parseColor("#00383838"))
                        (activity as MainActivity).navController.navigate(R.id.navigation_home)
                    }
                    cardView.listRead.setOnClickListener {
                        lateinit var bundle: Bundle
                        if (cardView.root != binding.cardList6.root || cardView.root == binding.cardList6.root && !psalms) {
                            val chapter = list[getIntPref(listName)]
                            bundle = bundleOf("chapter" to chapter, "psalms" to false, "iteration" to 0)

                        } else if (cardView.root == binding.cardList6.root && psalms) {
                            bundle = bundleOf("chapter" to "no", "psalms" to true, "iteration" to 1)
                        }
                        (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                    }
                }
            }
        }else{
            val enabled: Int
            if(getBoolPref("darkMode", true)){
                enabled = getColor(App.applicationContext(), R.color.buttonBackgroundDark)
            }else{
                enabled = getColor(App.applicationContext(), R.color.buttonBackground)
            }
            cardView.root.setOnLongClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setPositiveButton(getString(R.string.yes)){diag, _ ->
                    setIntPref(listDone, 0)
                    setIntPref(listName, getIntPref(listName) + 1)
                    val isLogged = FirebaseAuth.getInstance().currentUser
                    if(isLogged != null){
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
                builder.setNegativeButton(getString(R.string.no)){diag, _ ->
                    diag.dismiss()
                }
                builder.setMessage(R.string.msg_reset_one)
                builder.setTitle(R.string.title_reset_list)
                builder.show()
                true
            }
        }
    }



    private fun googleSignIn(){
        val ctx = App.applicationContext()
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null){
            val builder = android.app.AlertDialog.Builder(context)
            builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                val mGoogleSignInClient = GoogleSignIn.getClient(ctx, gso)
                val signInIntent = mGoogleSignInClient.signInIntent

                setIntPref("needNotif", 1)
                startActivityForResult(signInIntent, 96)
            }
            builder.setNeutralButton("No") { dialogInterface, _ -> setIntPref("firstRun", 1); dialogInterface.cancel() }
            builder.setMessage(R.string.msg_google).setTitle(R.string.title_sign_in)
            builder.create().show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 96){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            }catch(e: ApiException){
                Toast.makeText(activity?.applicationContext, R.string.msg_google_failed, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, R.string.msg_signed_in, Toast.LENGTH_LONG).show()
                val db = FirebaseFirestore.getInstance()
                val user = FirebaseAuth.getInstance().currentUser
                db.collection("main").document(user!!.uid).get()
                        .addOnSuccessListener { doc ->
                            if (doc.get("Doc") != null) {
                                val builder = AlertDialog.Builder(requireContext())
                                builder.setPositiveButton(R.string.btn_use_cloud) { _,_ ->
                                    SharedPref.firestoneToPreference(doc)
                                    setIntPref("firstRun", 1)
                                    (activity as MainActivity).navController.navigate(R.id.navigation_home)
                                }
                                builder.setNeutralButton(R.string.btn_use_device) { _,_->
                                    SharedPref.preferenceToFireStone()
                                    setIntPref("firstRun", 1)
                                    (activity as MainActivity).navController.navigate(R.id.navigation_home)
                                }
                                builder.setTitle(R.string.title_account_found)
                                builder.setMessage("Found ${FirebaseAuth.getInstance().currentUser?.email}. Would you like to TRANSFER from your account or OVERWRITE your account with this device?")
                                builder.create().show()
                            } else {
                                setIntPref("firstRun", 1)
                                SharedPref.preferenceToFireStone()
                                (activity as MainActivity).navController.navigate(R.id.navigation_home)
                            }
                        }

            } else {
                setIntPref("firstRun", 1)
                Toast.makeText(context, R.string.msg_google_failed, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun welcome4(){
        val alert = AlertDialog.Builder(requireContext())
        alert.setPositiveButton("Yes") { diag, _ ->
            setIntPref("firstRun", 1)
            diag.dismiss()
        }
        alert.setNeutralButton("No"){dialogInterface, _ ->
            dialogInterface.dismiss()
            setIntPref("firstRun", 1)
            (activity as MainActivity).navController.navigate(R.id.navigation_home)
        }
        alert.setTitle("Notifications")
        alert.setMessage("Would you like to change your notification settings now?")
        alert.create().show()
    }

}