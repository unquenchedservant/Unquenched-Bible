package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.Marker.markAll
import com.theunquenchedservant.granthornersbiblereadingsystem.Marker.markSingle
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.DailyCheck
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.ActivityMainBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarms
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createNotificationChannel
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.changeVisibility
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.getListNumber
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.hideOthers
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.listSwitcher
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.setTitles
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.setVisibilities
import java.util.Calendar

class HomeFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var user: FirebaseUser? = null
    private var allowResume = true
    private var skipped = false
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeView by viewModels()

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
        viewModel.list1.observe(viewLifecycleOwner, {binding.cardList1.listReading.text = it})
        viewModel.list2.observe(viewLifecycleOwner, {binding.cardList2.listReading.text = it})
        viewModel.list3.observe(viewLifecycleOwner, {binding.cardList3.listReading.text = it})
        viewModel.list4.observe(viewLifecycleOwner, {binding.cardList4.listReading.text = it})
        viewModel.list5.observe(viewLifecycleOwner, {binding.cardList5.listReading.text = it})
        viewModel.list6.observe(viewLifecycleOwner, {binding.cardList6.listReading.text = it})
        viewModel.list7.observe(viewLifecycleOwner, {binding.cardList7.listReading.text = it})
        viewModel.list8.observe(viewLifecycleOwner, {binding.cardList8.listReading.text = it})
        viewModel.list9.observe(viewLifecycleOwner, {binding.cardList9.listReading.text = it})
        viewModel.list10.observe(viewLifecycleOwner, {binding.cardList10.listReading.text = it})

        user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            getData()
        }else{
            setLists(null)
        }
        setVisibilities(binding)
        setTitles(binding)
        val psalms = getBoolPref("psalms")
        createCardListeners(psalms)
        createButtonListener()
        createNotificationChannel()
        createAlarm("dailyCheck")
        allowResume = false
        if(savedInstanceState != null) {
            when (getIntPref("firstRun")) {
                0 -> {
                    googleSignIn()
                }
                1 -> {
                    createAlarms()
                }
            }
        }
    }
    private fun getData() {
        if(user != null) {
            db.collection("main").document(user!!.uid).get()
                    .addOnSuccessListener {
                        if (it.data != null) {
                            val result = it.data!!
                            if (!checkDate("both", false)) {
                                setStringPref("dateChecked", result["dateChecked"] as String)
                                setIntPref("maxStreak", (result["maxStreak"] as Long).toInt())
                                setIntPref("currentStreak", (result["currentStreak"] as Long).toInt())
                            }
                            setLists(result)
                        }
                    }
                    .addOnFailureListener {
                        setLists(null)
                    }
        }
    }

    private fun setLists(result: Map<String, Any>?){
        val ctx = App.applicationContext()
        if (!checkDate("current", false)) {
            val intent = Intent(ctx, DailyCheck::class.java)
            ctx.sendBroadcast(intent)
        }
        val fromFirebase : Boolean = (result != null)
        val psalmChecked = if(fromFirebase) (result!!["psalms"] as Boolean) else getBoolPref("psalms")
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        binding.cardList1.listReading.text = getListNumber(result, "list1", R.array.list_1, fromFirebase)
        binding.cardList2.listReading.text = getListNumber(result, "list2", R.array.list_2, fromFirebase)
        binding.cardList3.listReading.text = getListNumber(result, "list3", R.array.list_3, fromFirebase)
        binding.cardList4.listReading.text = getListNumber(result, "list4", R.array.list_4, fromFirebase)
        binding.cardList5.listReading.text = getListNumber(result, "list5", R.array.list_5, fromFirebase)
        if (psalmChecked) {
            if(day != 31) { val pal = "$day, ${day + 30}, ${day + 60}, ${day + 90}, ${day + 120}"; binding.cardList6.listReading.text = pal }
            else{ val pal = "Day Off";binding.cardList6.listReading.text = pal }
        } else binding.cardList6.listReading.text = getListNumber(result, "list6", R.array.list_6, fromFirebase)
        binding.cardList7.listReading.text = getListNumber(result, "list7", R.array.list_7, fromFirebase)
        binding.cardList8.listReading.text = getListNumber(result, "list8", R.array.list_8, fromFirebase)
        binding.cardList9.listReading.text = getListNumber(result, "list9", R.array.list_9, fromFirebase)
        binding.cardList10.listReading.text = getListNumber(result, "list10", R.array.list_10, fromFirebase)
        if(user != null) checkLists(result) else checkLists(null)
    }

    private fun checkLists(result: Map<String, Any>?) {
        val cardViewList = arrayOf(binding.cardList1.root, binding.cardList2.root, binding.cardList3.root, binding.cardList4.root, binding.cardList5.root, binding.cardList6.root, binding.cardList7.root, binding.cardList8.root, binding.cardList9.root, binding.cardList10.root)
        for (i in 1..10) {
            val listDone = if (result != null) (result["list${i}Done"] as Long).toInt() else getIntPref("list${i}Done")
            listSwitcher(cardViewList[i - 1], listDone, binding.materialButton)
        }
        when (if (result != null) (result["listsDone"] as Long).toInt() else getIntPref("listsDone")) {
            10 -> {
                binding.materialButton.setText(R.string.done)
                binding.materialButton.isEnabled = false
                binding.materialButton.backgroundTintList = null
                binding.materialButton.backgroundTintMode = null
            }
            0 -> {
                binding.materialButton.setText(R.string.not_done)
                binding.materialButton.isEnabled = true
                binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383838"))
            }
            in 1..9 -> {
                binding.materialButton.setText(R.string.btn_mark_remaining)
                binding.materialButton.isEnabled = true
                val opacity = if (getIntPref("listsDone") < 5){
                    100 - (getIntPref("listsDone") * 5)
                }else{
                    100 - ((getIntPref("listsDone") * 5) - 5)
                }
                binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#${opacity}383838"))
            }
        }
    }


    private fun createButtonListener(){
        val ctx = App.applicationContext()
        val mainBinding = (activity as MainActivity).binding
        val navView = mainBinding.bottomNav
        val disabled = Color.parseColor("#00383838")
        binding.materialButton.setOnClickListener {
            hideOthers(null, binding)
            markAll()
            binding.materialButton.isEnabled = false
            binding.materialButton.text = resources.getString(R.string.done)
            binding.materialButton.backgroundTintList = null
            binding.materialButton.backgroundTintMode = null
            binding.cardList1.root.isEnabled = false; binding.cardList2.root.isEnabled = false
            binding.cardList1.root.setBackgroundColor(disabled);binding.cardList2.root.setBackgroundColor(disabled)
            binding.cardList3.root.isEnabled = false; binding.cardList4.root.isEnabled = false
            binding.cardList3.root.setBackgroundColor(disabled);binding.cardList4.root.setBackgroundColor(disabled)
            binding.cardList5.root.isEnabled = false; binding.cardList6.root.isEnabled = false
            binding.cardList5.root.setBackgroundColor(disabled);binding.cardList6.root.setBackgroundColor(disabled)
            binding.cardList7.root.isEnabled = false; binding.cardList8.root.isEnabled = false
            binding.cardList7.root.setBackgroundColor(disabled);binding.cardList8.root.setBackgroundColor(disabled)
            binding.cardList9.root.isEnabled = false; binding.cardList10.root.isEnabled = false
            binding.cardList9.root.setBackgroundColor(disabled);binding.cardList10.root.setBackgroundColor(disabled)
            val mNotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(1)
            mNotificationManager.cancel(2)
            val stats = navView.menu.findItem(R.id.navigation_stats)
            stats.title = "Current Streak: ${getIntPref("currentStreak")}"
        }
    }



    private fun createCardListeners(psalms: Boolean) {
        val cardViewList = arrayOf(binding.cardList1,binding.cardList2, binding.cardList3,  binding.cardList4, binding.cardList5, binding.cardList6, binding.cardList7, binding.cardList8, binding.cardList9, binding.cardList10)
        val arrayIdList = arrayOf(R.array.list_1, R.array.list_2, R.array.list_3, R.array.list_4, R.array.list_5, R.array.list_6, R.array.list_7, R.array.list_8,
                R.array.list_9, R.array.list_10)
        for(i in 1..10){
            val cardList = cardViewList[i-1]
            val list = resources.getStringArray(arrayIdList[i-1])
            cardList.root.setOnClickListener{
                if(it.findViewById<LinearLayout>(R.id.list_buttons).isVisible){
                    listSwitcher(it, getIntPref("list${i}Done"), binding.materialButton)
                }else{
                    hideOthers(cardList.root, binding)
                    it.findViewById<MaterialTextView>(R.id.list_done).setOnClickListener{
                        changeVisibility(cardList, false)
                        markSingle("list${i}Done")
                        cardList.root.isEnabled = false
                        cardList.root.setCardBackgroundColor(Color.parseColor("#00383838"))
                        if(getIntPref("listsDone") == 10){
                            val mNotification = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            mNotification.cancel(1)
                            mNotification.cancel(2)
                            binding.materialButton.text = resources.getString(R.string.done)
                            binding.materialButton.isEnabled = false
                            binding.materialButton.backgroundTintList = null
                            binding.materialButton.backgroundTintMode = null
                        }else{
                            binding.materialButton.isEnabled = true
                            val opacity = if(getIntPref("listsDone") < 5){
                                 100 - (getIntPref("listsDone") * 5)
                            }else{
                                 100 - ((getIntPref("listsDone") * 5) + 5)
                            }
                            binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#${opacity}383838"))
                            binding.materialButton.text = resources.getString(R.string.btn_mark_remaining)
                        }
                    }
                    it.findViewById<MaterialTextView>(R.id.list_read).setOnClickListener{
                        if(cardList.root != binding.cardList6.root || cardList.root == binding.cardList6.root && !psalms){
                            val chapter = list[getIntPref("list$i")]
                            val bundle = bundleOf("chapter" to chapter, "psalms" to false, "iteration" to 0)
                            val navControl = findNavController(activity as MainActivity, R.id.nav_host_fragment)
                            navControl.navigate(R.id.navigation_scripture, bundle)
                        }else if(cardList.root == binding.cardList6.root && psalms){
                            val bundle = Bundle()
                            bundle.putString("chapter", "no")
                            bundle.putBoolean("psalms", true)
                            bundle.putInt("iteration", 1)
                            (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                        }
                    }
                }
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
                val mainBinding = ActivityMainBinding.inflate(layoutInflater)
                db.collection("main").document(user!!.uid).get()
                        .addOnSuccessListener { doc ->
                            if (doc.get("Doc") != null) {
                                val builder = AlertDialog.Builder(requireContext())
                                builder.setPositiveButton(R.string.btn_use_cloud) { _,_ ->
                                    SharedPref.firestoneToPreference(doc)
                                    setIntPref("firstRun", 1)
                                    requireFragmentManager().beginTransaction().detach(HomeFragment()).attach(HomeFragment()).commit()
                                }
                                builder.setNeutralButton(R.string.btn_use_device) { _,_->
                                    SharedPref.preferenceToFireStone()
                                    setIntPref("firstRun", 1)
                                    requireFragmentManager().beginTransaction().detach(HomeFragment()).attach(HomeFragment()).commit()
                                }
                                builder.setTitle(R.string.title_account_found)
                                builder.setMessage("Found ${FirebaseAuth.getInstance().currentUser?.email}. Would you like to TRANSFER from your account or OVERWRITE your account with this device?")
                                builder.create().show()
                            } else {
                                setIntPref("firstRun", 1)
                                SharedPref.preferenceToFireStone()
                                requireFragmentManager().beginTransaction().detach(HomeFragment()).attach(HomeFragment()).commit()
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
            requireFragmentManager().beginTransaction().detach(HomeFragment()).attach(HomeFragment()).commit()
        }
        alert.setTitle("Notifications")
        alert.setMessage("Would you like to change your notification settings now?")
        alert.create().show()
    }

}