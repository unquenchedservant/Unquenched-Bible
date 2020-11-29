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
        if(getIntPref("versionNumber") != 49){
            val builder = AlertDialog.Builder(requireContext())
            builder.setPositiveButton(R.string.ok) { something ,_ ->
                setIntPref("versionNumber", 49)
                something.dismiss()
            }
            builder.setTitle(R.string.title_new_update)
            builder.setMessage(
                    "[ADDED] Mailchimp mailing list sign up(under Info and support)\n\n"+
                            "[ADDED] Remote notifications for easy contact with users for future updates\n\n"+
                            "[ADDED] Ability to hold all lists in place until all 10 are finished\n\n" +
                            "[FIXED] Current streak should not get reset every day\n\n" +
                            "[FIXED] An issue with Song of Solomon in the Manually Set List menu\n\n" +
                            "[FIXED] A lot of issues with scripture viewer\n\n" +
                            "[FIXED] Psalms once again have ability to go backwards and forwards in scripture viewer\n\n" +
                            "[FIXED] Occasional crash when opening the app \n\n" +
                            "[UPDATED] A lot of backend logic so that things should run smoother\n\n" +
                            "[UPDATED] Contact information\n\n"+
                            "[UPDATED] Removed a lot of unnecessary files, so app size should be smaller\n\n" +
                            "[UPDATED] Side drawer is now a bottom navigation layout\n\n" +
                            "[UPDATED] icons on the bottom bar"
            )
            builder.create().show()
        }
        val enabled = Color.parseColor("#383838")
        val disabled = Color.parseColor("#00383838")
        viewModel.list1.observe(viewLifecycleOwner){
            val cardList = binding.cardList1.root
            when(it.listDone){
                0 -> { cardList.isEnabled = true; cardList.setCardBackgroundColor(enabled) }
                1-> { cardList.isEnabled = false; cardList.setCardBackgroundColor(disabled) }
            }
            binding.cardList1.listReading.text = it.listReading
            binding.cardList1.listTitle.text = resources.getString(R.string.title_pgh_list1)
            createCardListener(binding.cardList1, R.array.list_1, false, "list1Done", "list1")
        }
        viewModel.list2.observe(viewLifecycleOwner){
            val cardList = binding.cardList2.root
            when(it.listDone){
                0 -> { cardList.isEnabled = true; cardList.setCardBackgroundColor(enabled) }
                1-> { cardList.isEnabled = false; cardList.setCardBackgroundColor(disabled) }
            }
            binding.cardList2.listReading.text = it.listReading
            binding.cardList2.listTitle.text = resources.getString(R.string.title_pgh_list2)
            createCardListener(binding.cardList2, R.array.list_2, false, "list2Done", "list2")
        }
        viewModel.list3.observe(viewLifecycleOwner){
            val cardList = binding.cardList3.root
            when(it.listDone){
                0 -> { cardList.isEnabled = true; cardList.setCardBackgroundColor(enabled) }
                1-> { cardList.isEnabled = false; cardList.setCardBackgroundColor(disabled) }
            }
            binding.cardList3.listReading.text = it.listReading
            binding.cardList3.listTitle.text = resources.getString(R.string.title_pgh_list3)
            createCardListener(binding.cardList3, R.array.list_3, false, "list3Done", "list3")
        }
        viewModel.list4.observe(viewLifecycleOwner){
            val cardList = binding.cardList4.root
            when(it.listDone){
                0 -> { cardList.isEnabled = true; cardList.setCardBackgroundColor(enabled) }
                1-> { cardList.isEnabled = false; cardList.setCardBackgroundColor(disabled) }
            }
            binding.cardList4.listReading.text = it.listReading
            binding.cardList4.listTitle.text = resources.getString(R.string.title_pgh_list4)
            createCardListener(binding.cardList4, R.array.list_4, false, "list4Done", "list4")
        }
        viewModel.list5.observe(viewLifecycleOwner){
            val cardList = binding.cardList5.root
            when(it.listDone){
                0 -> { cardList.isEnabled = true; cardList.setCardBackgroundColor(enabled) }
                1-> { cardList.isEnabled = false; cardList.setCardBackgroundColor(disabled) }
            }
            binding.cardList5.listReading.text = it.listReading
            binding.cardList5.listTitle.text = resources.getString(R.string.title_pgh_list5)
            createCardListener(binding.cardList5, R.array.list_5, false, "list5Done", "list5")
        }
        viewModel.list6.observe(viewLifecycleOwner){
            val cardList = binding.cardList6.root
            when(it.listDone){
                0 -> { cardList.isEnabled = true; cardList.setCardBackgroundColor(enabled) }
                1-> { cardList.isEnabled = false; cardList.setCardBackgroundColor(disabled) }
            }
            binding.cardList6.listReading.text = it.listReading
            binding.cardList6.listTitle.text = resources.getString(R.string.title_pgh_list6)
            val psalms = getBoolPref("psalms")
            createCardListener(binding.cardList6, R.array.list_6, psalms, "list6Done", "list6")
        }
        viewModel.list7.observe(viewLifecycleOwner){
            val cardList = binding.cardList7.root
            when(it.listDone){
                0 -> { cardList.isEnabled = true; cardList.setCardBackgroundColor(enabled) }
                1-> { cardList.isEnabled = false; cardList.setCardBackgroundColor(disabled) }
            }
            binding.cardList7.listReading.text = it.listReading
            binding.cardList7.listTitle.text = resources.getString(R.string.title_pgh_list7)
            createCardListener(binding.cardList7, R.array.list_7, false, "list7Done", "list7")
        }
        viewModel.list8.observe(viewLifecycleOwner){
            val cardList = binding.cardList8.root
            when(it.listDone){
                0 -> { cardList.isEnabled = true; cardList.setCardBackgroundColor(enabled) }
                1-> { cardList.isEnabled = false; cardList.setCardBackgroundColor(disabled) }
            }
            binding.cardList8.listReading.text = it.listReading
            binding.cardList8.listTitle.text = resources.getString(R.string.title_pgh_list8)
            createCardListener(binding.cardList8, R.array.list_8, false, "list8Done", "list8")
        }
        viewModel.list9.observe(viewLifecycleOwner){
            val cardList = binding.cardList9.root
            when(it.listDone){
                0 -> { cardList.isEnabled = true; cardList.setCardBackgroundColor(enabled) }
                1-> { cardList.isEnabled = false; cardList.setCardBackgroundColor(disabled) }
            }
            binding.cardList9.listReading.text = it.listReading
            binding.cardList9.listTitle.text = resources.getString(R.string.title_pgh_list9)
            createCardListener(binding.cardList9, R.array.list_9, false, "list9Done", "list2")
        }
        viewModel.list10.observe(viewLifecycleOwner){
            val cardList = binding.cardList10.root
            when(it.listDone){
                0 -> { cardList.isEnabled = true; cardList.setCardBackgroundColor(enabled) }
                1-> { cardList.isEnabled = false; cardList.setCardBackgroundColor(disabled) }
            }
            binding.cardList10.listReading.text = it.listReading
            binding.cardList10.listTitle.text = resources.getString(R.string.title_pgh_list10)
            createCardListener(binding.cardList10, R.array.list_10, false, "list10Done", "list10")
        }
        viewModel.listsDone.observe(viewLifecycleOwner){
            when(it.listsDone){
                10 -> {
                    binding.materialButton.setText(R.string.done)
                    binding.materialButton.isEnabled = false
                    binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#121212"))
                    binding.materialButton.backgroundTintMode= PorterDuff.Mode.ADD
                }
                0 -> {
                    binding.materialButton.setText(R.string.not_done)
                    binding.materialButton.isEnabled = true
                    binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#383838"))
                    binding.materialButton.backgroundTintMode = PorterDuff.Mode.ADD
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
                }
                1 -> {
                    createAlarms()
                }
            }
        }
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

    }

    private fun createCardListener(cardView: CardviewsBinding, arrayId: Int, psalms: Boolean, listDone: String, listName: String){
        val list = resources.getStringArray(arrayId)
        cardView.root.setOnClickListener {
            if(cardView.listButtons.isVisible){
                listSwitcher(it, getIntPref(listDone), binding.materialButton)
            }else{
                hideOthers(cardView.root, binding)
                cardView.listDone.setOnClickListener {
                    changeVisibility(cardView, false)
                    markSingle(listDone)
                    cardView.root.setCardBackgroundColor(Color.parseColor("#00383838"))
                    (activity as MainActivity).navController.navigate(R.id.navigation_home)
                }
                cardView.listRead.setOnClickListener {
                    lateinit var bundle: Bundle
                    if(cardView.root != binding.cardList6.root || cardView.root == binding.cardList6.root && !psalms){
                        val chapter = list[getIntPref(listName)]
                        bundle = bundleOf("chapter" to chapter, "psalms" to false, "iteration" to 0)

                    }else if(cardView.root == binding.cardList6.root && psalms){
                        bundle = bundleOf("chapter" to "no", "psalms" to true, "iteration" to 1)
                    }
                    (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
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