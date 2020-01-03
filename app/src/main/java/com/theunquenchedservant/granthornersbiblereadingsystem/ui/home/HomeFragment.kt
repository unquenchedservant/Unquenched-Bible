package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.getCurrentDate
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.Marker.markAll
import com.theunquenchedservant.granthornersbiblereadingsystem.Marker.markSingle
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.DailyCheck
import com.theunquenchedservant.granthornersbiblereadingsystem.ESV.ESVGet.getESVReference
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.boolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.intPref
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.stringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmCreator.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmCreator.createAlarms
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmCreator.createNotificationChannel
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.FirebaseHelper.checkLogin
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.listHelpers.changeVisibility
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.listHelpers.getListNumber
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.listHelpers.hideOthers
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.listHelpers.listSwitcher
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.listHelpers.setTitles
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.listHelpers.setVisibilities
import kotlinx.android.synthetic.main.activity_main.*
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
        model.list1.observe(this, Observer<String>{ cardList1.list_reading.text = it })
        model.list2.observe(this, Observer<String>{ cardList2.list_reading.text = it })
        model.list3.observe(this, Observer<String>{ cardList3.list_reading.text = it })
        model.list4.observe(this, Observer<String>{ cardList4.list_reading.text = it })
        model.list5.observe(this, Observer<String>{ cardList5.list_reading.text = it })
        model.list6.observe(this, Observer<String>{ cardList6.list_reading.text = it })
        model.list7.observe(this, Observer<String>{ cardList7.list_reading.text = it })
        model.list8.observe(this, Observer<String>{ cardList8.list_reading.text = it })
        model.list9.observe(this, Observer<String>{ cardList9.list_reading.text = it })
        model.list10.observe(this, Observer<String>{ cardList10.list_reading.text = it })
        user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            getData()
        }else{
            setLists(null)
        }
        setVisibilities(view)
        setTitles(view)
        val psalms = boolPref("psalms", null)
        createCardListeners(psalms)
        createButtonListener()
        createNotificationChannel()
        createAlarm("dailyCheck")
        allowResume = false
        if(savedInstanceState != null) {
            when (intPref("firstRun", null)) {
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
                            if (result["dateChecked"] != null && result["dateChecked"] != getCurrentDate(false)) {
                                stringPref("dateChecked", result["dateChecked"] as String)
                                intPref("maxStreak", (result["maxStreak"] as Long).toInt())
                                intPref("currentStreak", (result["currentStreak"] as Long).toInt())
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
        if (stringPref("dateChecked", null) != getCurrentDate(false)) {
            val intent = Intent(ctx, DailyCheck::class.java)
            ctx.sendBroadcast(intent)
        }
        val fromFirebase : Boolean = (result != null)
        val psalmChecked = if(fromFirebase) (result!!["psalms"] as Boolean) else boolPref("psalms", null)
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
                val listDone = if (result != null) (result["list${i}Done"] as Long).toInt() else intPref("list${i}Done", null)
                if (cardViewList[i - 1] != null) listSwitcher(cardViewList[i - 1], listDone, material_button)
            }

            when (if (result != null) (result["listsDone"] as Long).toInt() else intPref("listsDone", null)) {
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
                    val opacity = if (intPref("listsDone", null) < 5){
                        100 - (intPref("listsDone", null) * 5)
                    }else{
                        100 - ((intPref("listsDone", null) * 5) - 5)
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
            hideOthers(null, view!!)
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
            stats.title = "Current Streak: ${intPref("currentStreak", null)}"
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
                    listSwitcher(it, intPref("list${i}Done", null), material_button)
                }else{
                    hideOthers(cardList, view!!)
                    it.list_done.setOnClickListener{
                        changeVisibility(cardList, false)
                        markSingle("list${i}Done")
                        cardList.isEnabled = false
                        cardList.setCardBackgroundColor(Color.parseColor("#00383838"))
                        if(intPref("listsDone", null) == 10){
                            val mNotif = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            mNotif.cancel(1)
                            mNotif.cancel(2)
                            material_button.text = resources.getString(R.string.done)
                            material_button.isEnabled = false
                            material_button.setBackgroundColor(Color.parseColor("#00383838"))
                        }else{
                            material_button.isEnabled = true
                            val opacity = if(intPref("listsDone", null) < 5){
                                 100 - (intPref("listsDone", null) * 5)
                            }else{
                                 100 - ((intPref("listsDone", null) * 5) + 5)
                            }
                            log("THIS IS THE OPACITY ${opacity}383838")
                            material_button.setBackgroundColor(Color.parseColor("#${opacity}383838"))
                            material_button.text = resources.getString(R.string.markRemaining)
                        }
                    }
                    it.list_read.setOnClickListener{
                        if(cardList != cardList6 || cardList == cardList6 && !psalms){
                            val chapter = list[intPref("list$i", null)]
                            /**when(listNumberPref("translation", null)){
                                1 -> getCSBReference(chapter)
                                2 -> getESVReference(chapter)
                            }*/
                            getESVReference(chapter, context, false, 0)
                        }else if(cardList == cardList6 && psalms){
                            /**when(listNumberPref("translation", null)){
                                2 -> getESVPsalms(i)
                            }*/
                            getESVReference("no", context, true, 1)
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
                    intPref("needNotif", 1)
                }else{
                    intPref("needNotif", 0)
                }
                startActivityForResult(signInIntent, 96)
            }
            builder.setNeutralButton("No") { dialogInterface, _ -> log("cancel pressed"); intPref("firstRun", 1); dialogInterface.cancel() }
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
                                val builder = AlertDialog.Builder(context!!)
                                builder.setPositiveButton("Use Cloud Data") { _,_ ->
                                    SharedPref.firestoneToPreference(doc)
                                    intPref("firstRun", 1)
                                    log("NAV VIEW $nav_view")
                                    nav_view.menu.findItem(R.id.google_sign)?.title = "Sign Out"
                                    val psalmsItem = nav_view.menu.findItem(R.id.action_psalms)
                                    val isPsalms = doc.data!!["psalms"] as Boolean
                                    if(isPsalms) psalmsItem?.title = resources.getString(R.string.psalmsnav1)
                                        else nav_view.menu.findItem(R.id.action_psalms).title = resources.getString(R.string.psalmsnav5)
                                    fragmentManager?.beginTransaction()?.detach(fragmentManager!!.fragments[0]!!)?.attach(fragmentManager!!.fragments[0]!!)?.commit()
                                }
                                builder.setNeutralButton("Overwrite with device") { _,_->
                                    SharedPref.preferenceToFireStone()
                                    intPref("firstRun", 1)
                                    nav_view.menu.findItem(R.id.google_sign).title = "Sign Out"
                                    fragmentManager?.beginTransaction()?.detach(fragmentManager!!.fragments[0]!!)?.attach(fragmentManager!!.fragments[0]!!)?.commit()
                                }
                                builder.setTitle("Account Found")
                                builder.setMessage("Found ${FirebaseAuth.getInstance().currentUser?.email}. Would you like to TRANSFER from your account or OVERWRITE your account with this device?")
                                builder.create().show()
                            } else {
                                intPref("firstRun", 1)
                                SharedPref.preferenceToFireStone()
                                nav_view.menu.findItem(R.id.google_sign).title = "Sign Out"
                                val psalmsItem = nav_view.menu.findItem(R.id.action_psalms)
                                psalmsItem.title = resources.getString(R.string.psalmsnav1)
                                fragmentManager?.beginTransaction()?.detach(fragmentManager!!.fragments[0]!!)?.attach(fragmentManager!!.fragments[0]!!)?.commit()
                            }
                        }

            } else {
                intPref("firstRun", 1)
                Toast.makeText(context, "Google Sign In Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun welcome4(){
        val alert = AlertDialog.Builder(context!!)
        alert.setPositiveButton("Yes") { diag, _ ->
            intPref("firstRun", 1)
            diag.dismiss()
        }
        alert.setNeutralButton("No"){dialogInterface, _ ->
            dialogInterface.dismiss()
            intPref("firstRun", 1)
            fragmentManager!!.beginTransaction().detach(HomeFragment()).attach(HomeFragment()).commit()
        }
        alert.setTitle("Notifications")
        alert.setMessage("Would you like to change your notification settings now?")
        alert.create().show()
    }

}