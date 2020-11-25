package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.renderscript.Script
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.Marker.markAll
import com.theunquenchedservant.granthornersbiblereadingsystem.Marker.markSingle
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.DailyCheck
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmCreator.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmCreator.createAlarms
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmCreator.createNotificationChannel
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.scripture.ScriptureViewer
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.getDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.listHelpers.changeVisibility
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.listHelpers.getListNumber
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.listHelpers.hideOthers
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.listHelpers.listSwitcher
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.listHelpers.setTitles
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.listHelpers.setVisibilities
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.cardviews.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.Calendar

class HomeFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var user: FirebaseUser? = null
    private var allowResume = true
    private var skipped = false
    private lateinit var model: HomeView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        model = ViewModelProviders.of(this).get(HomeView::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.list1.observe(viewLifecycleOwner, Observer<String>{ cardList1.list_reading.text = it })
        model.list2.observe(viewLifecycleOwner, Observer<String>{ cardList2.list_reading.text = it })
        model.list3.observe(viewLifecycleOwner, Observer<String>{ cardList3.list_reading.text = it })
        model.list4.observe(viewLifecycleOwner, Observer<String>{ cardList4.list_reading.text = it })
        model.list5.observe(viewLifecycleOwner, Observer<String>{ cardList5.list_reading.text = it })
        model.list6.observe(viewLifecycleOwner, Observer<String>{ cardList6.list_reading.text = it })
        model.list7.observe(viewLifecycleOwner, Observer<String>{ cardList7.list_reading.text = it })
        model.list8.observe(viewLifecycleOwner, Observer<String>{ cardList8.list_reading.text = it })
        model.list9.observe(viewLifecycleOwner, Observer<String>{ cardList9.list_reading.text = it })
        model.list10.observe(viewLifecycleOwner, Observer<String>{ cardList10.list_reading.text = it })
        user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            getData()
        }else{
            setLists(null)
        }
        setVisibilities(view)
        setTitles(view)
        val psalms = getBoolPref("psalms")
        createCardListeners(psalms)
        createButtonListener()
        createNotificationChannel()
        createAlarm("dailyCheck")
        allowResume = false
        if(savedInstanceState != null) {
            when (getIntPref("firstRun")) {
                0 -> {
                    googleSignIn(true)
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
        model.list1.value = getListNumber(result, "list1", R.array.list_1, fromFirebase)
        model.list2.value = getListNumber(result, "list2", R.array.list_2, fromFirebase)
        model.list3.value = getListNumber(result, "list3", R.array.list_3, fromFirebase)
        model.list4.value = getListNumber(result, "list4", R.array.list_4, fromFirebase)
        model.list5.value = getListNumber(result, "list5", R.array.list_5, fromFirebase)
        if (psalmChecked) {
            if(day != 31) { val pal = "$day, ${day + 30}, ${day + 60}, ${day + 90}, ${day + 120}"; model.list6.value = pal }
            else{ val pal = "Day Off";model.list6.value = pal }
        } else model.list6.value = getListNumber(result, "list6", R.array.list_6, fromFirebase)
        model.list7.value = getListNumber(result, "list7", R.array.list_7, fromFirebase)
        model.list8.value = getListNumber(result, "list8", R.array.list_8, fromFirebase)
        model.list9.value = getListNumber(result, "list9", R.array.list_9, fromFirebase)
        model.list10.value = getListNumber(result, "list10", R.array.list_10, fromFirebase)
        if(user != null) checkLists(result) else checkLists(null)
    }

    private fun checkLists(result: Map<String, Any>?) {
        if (material_button != null) {
            val cardViewList = arrayOf(cardList1, cardList2, cardList3, cardList4, cardList5, cardList6, cardList7, cardList8, cardList9, cardList10)
            for (i in 1..10) {
                val listDone = if (result != null) (result["list${i}Done"] as Long).toInt() else getIntPref("list${i}Done")
                if (cardViewList[i - 1] != null) listSwitcher(cardViewList[i - 1], listDone, material_button)
            }

            when (if (result != null) (result["listsDone"] as Long).toInt() else getIntPref("listsDone")) {
                10 -> {
                    material_button.setText(R.string.done)
                    material_button.isEnabled = false
                    material_button.setBackgroundColor(Color.parseColor("#00383838"))
                }
                0 -> {
                    material_button.setText(R.string.notdone)
                    material_button.isEnabled = true
                    material_button.setBackgroundColor(Color.parseColor("#383838"))
                }
                in 1..9 -> {
                    material_button.setText(R.string.markRemaining)
                    material_button.isEnabled = true
                    val opacity = if (getIntPref("listsDone") < 5){
                        100 - (getIntPref("listsDone") * 5)
                    }else{
                        100 - ((getIntPref("listsDone") * 5) - 5)
                    }
                    material_button.setBackgroundColor(Color.parseColor("#${opacity}383838"))
                }
            }
        }
    }


    private fun createButtonListener(){
        val ctx = App.applicationContext()
        val nav_view = activity?.nav_view!!
        val disabled = Color.parseColor("#00383838")
        material_button.setOnClickListener {
            hideOthers(null, requireView())
            markAll()
            material_button.isEnabled = false
            material_button.text = resources.getString(R.string.done)
            material_button.setBackgroundColor(Color.parseColor("#00383838"))
            cardList1.isEnabled = false; cardList2.isEnabled = false
            cardList1.setBackgroundColor(disabled);cardList2.setBackgroundColor(disabled)
            cardList3.isEnabled = false; cardList4.isEnabled = false
            cardList3.setBackgroundColor(disabled);cardList4.setBackgroundColor(disabled)
            cardList5.isEnabled = false; cardList6.isEnabled = false
            cardList5.setBackgroundColor(disabled);cardList6.setBackgroundColor(disabled)
            cardList7.isEnabled = false; cardList8.isEnabled = false
            cardList7.setBackgroundColor(disabled);cardList8.setBackgroundColor(disabled)
            cardList9.isEnabled = false; cardList10.isEnabled = false
            cardList9.setBackgroundColor(disabled);cardList10.setBackgroundColor(disabled)
            val mNotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(1)
            mNotificationManager.cancel(2)
            val stats = nav_view.menu.findItem(R.id.action_statistics)
            stats.title = "Current Streak: ${getIntPref("currentStreak")}"
        }
    }


    private fun createCardListeners(psalms: Boolean) {
        val cardViewList = arrayOf(cardList1, cardList2, cardList3,  cardList4, cardList5, cardList6, cardList7, cardList8, cardList9, cardList10)
        val arrayIdList = arrayOf(R.array.list_1, R.array.list_2, R.array.list_3, R.array.list_4, R.array.list_5, R.array.list_6, R.array.list_7, R.array.list_8,
                R.array.list_9, R.array.list_10)
        for(i in 1..10){
            val cardList = cardViewList[i-1] as CardView
            val list = resources.getStringArray(arrayIdList[i-1])
            cardList.setOnClickListener{
                if(it.list_buttons.isVisible){
                    listSwitcher(it, getIntPref("list${i}Done"), material_button)
                }else{
                    hideOthers(cardList, requireView())
                    it.list_done.setOnClickListener{
                        changeVisibility(cardList, false)
                        markSingle("list${i}Done")
                        cardList.isEnabled = false
                        cardList.setCardBackgroundColor(Color.parseColor("#00383838"))
                        if(getIntPref("listsDone") == 10){
                            val mNotif = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            mNotif.cancel(1)
                            mNotif.cancel(2)
                            material_button.text = resources.getString(R.string.done)
                            material_button.isEnabled = false
                            material_button.setBackgroundColor(Color.parseColor("#00383838"))
                        }else{
                            material_button.isEnabled = true
                            val opacity = if(getIntPref("listsDone") < 5){
                                 100 - (getIntPref("listsDone") * 5)
                            }else{
                                 100 - ((getIntPref("listsDone") * 5) + 5)
                            }
                            log("THIS IS THE OPACITY ${opacity}383838")
                            material_button.setBackgroundColor(Color.parseColor("#${opacity}383838"))
                            material_button.text = resources.getString(R.string.markRemaining)
                        }
                    }
                    it.list_read.setOnClickListener{
                        if(cardList != cardList6 || cardList == cardList6 && !psalms){
                            val chapter = list[getIntPref("list$i")]
                            /**when(listNumberPref("translation", null)){
                                1 -> getCSBReference(chapter)
                                2 -> getESVReference(chapter)
                            }*/
                            val bundle = bundleOf("chapter" to chapter, "psalms" to false, "iteration" to 0)
                            val nav_control = findNavController(activity as MainActivity, R.id.nav_host_fragment)
                            nav_control.navigate(R.id.navigation_scripture, bundle)
                        }else if(cardList == cardList6 && psalms){
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



    /**
    private fun getCSBReference(chapter: String){
        log("START CSB REFERENCE")
        val title = chapter
        val url = "https://api.scripture.api.bible/v1/bibles/a556c5305ee15c3f-01/passages/JHN.1"
        val wv = getCSB(context, url)
        chapterShower(wv, title, false, 0)
    }*/


    private fun googleSignIn(needsNotifOnSuccess: Boolean){
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
                if(needsNotifOnSuccess) {
                    setIntPref("needNotif", 1)
                }else{
                    setIntPref("needNotif", 0)
                }
                startActivityForResult(signInIntent, 96)
            }
            builder.setNeutralButton("No") { dialogInterface, _ -> log("cancel pressed"); setIntPref("firstRun", 1); dialogInterface.cancel() }
            builder.setMessage(R.string.googleCheck).setTitle("Sign In To Google Account")
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
                Toast.makeText(activity?.applicationContext, "Google Sign In Failed", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        log("firebaseAuthWithGoogle: ${acct.id}")
        val auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Signed In!", Toast.LENGTH_LONG).show()
                val db = FirebaseFirestore.getInstance()
                val user = FirebaseAuth.getInstance().currentUser
                val nav_view = activity?.nav_view!!
                db.collection("main").document(user!!.uid).get()
                        .addOnSuccessListener { doc ->
                            log("DOCUMENT SNAPSHOT ${doc.get("Doc")}")
                            if (doc.get("Doc") != null) {
                                val builder = AlertDialog.Builder(requireContext())
                                builder.setPositiveButton("Use Cloud Data") { _,_ ->
                                    SharedPref.firestoneToPreference(doc)
                                    setIntPref("firstRun", 1)
                                    log("NAV VIEW $nav_view")
                                    nav_view.menu.findItem(R.id.google_sign)?.title = "Sign Out"
                                    val psalmsItem = nav_view.menu.findItem(R.id.action_psalms)
                                    val isPsalms = doc.data!!["psalms"] as Boolean
                                    if(isPsalms) psalmsItem?.title = resources.getString(R.string.psalmsnav1)
                                        else nav_view.menu.findItem(R.id.action_psalms).title = resources.getString(R.string.psalmsnav5)
                                    fragmentManager?.beginTransaction()?.detach(requireFragmentManager().fragments[0]!!)?.attach(requireFragmentManager().fragments[0]!!)?.commit()
                                }
                                builder.setNeutralButton("Overwrite with device") { _,_->
                                    SharedPref.preferenceToFireStone()
                                    setIntPref("firstRun", 1)
                                    nav_view.menu.findItem(R.id.google_sign).title = "Sign Out"
                                    fragmentManager?.beginTransaction()?.detach(requireFragmentManager().fragments[0]!!)?.attach(requireFragmentManager().fragments[0]!!)?.commit()
                                }
                                builder.setTitle("Account Found")
                                builder.setMessage("Found ${FirebaseAuth.getInstance().currentUser?.email}. Would you like to TRANSFER from your account or OVERWRITE your account with this device?")
                                builder.create().show()
                            } else {
                                setIntPref("firstRun", 1)
                                SharedPref.preferenceToFireStone()
                                nav_view.menu.findItem(R.id.google_sign).title = "Sign Out"
                                val psalmsItem = nav_view.menu.findItem(R.id.action_psalms)
                                psalmsItem.title = resources.getString(R.string.psalmsnav1)
                                fragmentManager?.beginTransaction()?.detach(requireFragmentManager().fragments[0]!!)?.attach(requireFragmentManager().fragments[0]!!)?.commit()
                            }
                        }

            } else {
                setIntPref("firstRun", 1)
                Toast.makeText(context, "Google Sign In Failed", Toast.LENGTH_LONG).show()
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