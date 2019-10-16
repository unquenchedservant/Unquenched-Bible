package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.createDailyCheck
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.createNotificationChannel
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.getCurrentDate
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.Marker.markAll
import com.theunquenchedservant.granthornersbiblereadingsystem.Marker.markSingle
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberEditInt
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberEditString
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberReadInt
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.listNumberReadString
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.statisticsEdit
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.statisticsRead
import com.theunquenchedservant.granthornersbiblereadingsystem.SharedPref.updateFS
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.CreateAlarms.createAlarms
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.DailyCheck
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings.SettingsActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.ESV.ESVGet.getESV
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class HomeFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private var allowResume = true
    private var skipped = false
    private lateinit var cardList1 : CardView
    private lateinit var cardList2 : CardView
    private lateinit var cardList3 : CardView
    private lateinit var cardList4 : CardView
    private lateinit var cardList5 : CardView
    private lateinit var cardList6 : CardView
    private lateinit var cardList7 : CardView
    private lateinit var cardList8 : CardView
    private lateinit var cardList9 : CardView
    private lateinit var cardList10 : CardView
    private lateinit var model: HomeView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        model = ViewModelProviders.of(this).get(HomeView::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val list1Observer = Observer<String> { root.findViewById<TextView>(R.id.list1_reading).text = it }
        val list2Observer = Observer<String> { root.findViewById<TextView>(R.id.list2_reading).text = it }
        val list3Observer = Observer<String> { root.findViewById<TextView>(R.id.list3_reading).text = it }
        val list4Observer = Observer<String> { root.findViewById<TextView>(R.id.list4_reading).text = it }
        val list5Observer = Observer<String> { root.findViewById<TextView>(R.id.list5_reading).text = it }
        val list6Observer = Observer<String> { root.findViewById<TextView>(R.id.list6_reading).text = it }
        val list7Observer = Observer<String> { root.findViewById<TextView>(R.id.list7_reading).text = it }
        val list8Observer = Observer<String> { root.findViewById<TextView>(R.id.list8_reading).text = it }
        val list9Observer = Observer<String> { root.findViewById<TextView>(R.id.list9_reading).text = it }
        val list10Observer = Observer<String> { root.findViewById<TextView>(R.id.list10_reading).text = it }
        model.list1.observe(this, list1Observer)
        model.list2.observe(this, list2Observer)
        model.list3.observe(this, list3Observer)
        model.list4.observe(this, list4Observer)
        model.list5.observe(this, list5Observer)
        model.list6.observe(this, list6Observer)
        model.list7.observe(this, list7Observer)
        model.list8.observe(this, list8Observer)
        model.list9.observe(this, list9Observer)
        model.list10.observe(this, list10Observer)
        return root
    }

    override fun onPause(){
        super.onPause()
        log("onPause called")
        allowResume = true
    }

    override fun onResume() {
        super.onResume()
        if(allowResume) {
            log("onResume called")
            val ctx = activity?.applicationContext
            val button = view?.findViewById<Button>(R.id.material_button)
            button?.setBackgroundColor(Color.parseColor("#383838"))
            createNotificationChannel(ctx)
            createDailyCheck(ctx)
            cardList1 = view!!.findViewById(R.id.cardList1)!!
            cardList2 = view!!.findViewById(R.id.cardList2)!!
            cardList3 = view!!.findViewById(R.id.cardList3)!!
            cardList4 = view!!.findViewById(R.id.cardList4)!!
            cardList5 = view!!.findViewById(R.id.cardList5)!!
            cardList6 = view!!.findViewById(R.id.cardList6)!!
            cardList7 = view!!.findViewById(R.id.cardList7)!!
            cardList8 = view!!.findViewById(R.id.cardList8)!!
            cardList9 = view!!.findViewById(R.id.cardList9)!!
            cardList10 = view!!.findViewById(R.id.cardList10)!!
            when (statisticsRead(ctx, "firstRun")) {
                0 -> {
                    runBlocking {
                        welcome(ctx)
                    }
                }
                1 -> {
                    createAlarms(ctx)
                    }
                }
            val psalms = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("psalms", false)
            checkLogin(ctx, button)
            createCardListeners(cardList1, R.array.list_1, "List 1", "list1Done", button, ctx, false)
            createCardListeners(cardList2, R.array.list_2, "List 2", "list2Done", button, ctx, false)
            createCardListeners(cardList3, R.array.list_3, "List 3", "list3Done", button, ctx, false)
            createCardListeners(cardList4, R.array.list_4, "List 4", "list4Done", button, ctx, false)
            createCardListeners(cardList5, R.array.list_5, "List 5", "list5Done", button, ctx, false)
            createCardListeners(cardList6, R.array.list_6, "List 6", "list6Done", button, ctx, psalms)
            createCardListeners(cardList7, R.array.list_7, "List 7", "list7Done", button, ctx, false)
            createCardListeners(cardList8, R.array.list_8, "List 8", "list8Done", button, ctx, false)
            createCardListeners(cardList9, R.array.list_9, "List 9", "list9Done", button, ctx, false)
            createCardListeners(cardList10, R.array.list_10, "List 10", "list10Done", button, ctx, false)
            createButtonListener(button, ctx)
            allowResume = false
         }
    }
    private fun createButtonListener(button:Button?, ctx:Context?){
        button?.setOnClickListener {
            markAll(ctx, button, view)
            val mNotificationManager = ctx!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(1)
            mNotificationManager.cancel(2)
        }
    }

    private fun createCardListeners(cardList: CardView, listArray: Int, listName: String, listDone:String, button: Button?, ctx: Context?, psalms: Boolean) {
        cardList.setOnClickListener {
            if(cardList != cardList6 || cardList == cardList6 && !psalms) {
                val list = resources.getStringArray(listArray)
                val number = listNumberReadInt(ctx, listName)
                val chapter = list[number]
                getESVReference(ctx, chapter, cardList, listName, listDone, listArray, button)
            }else if(cardList == cardList6 && psalms){
                log("Psalms switch on, getting special psalms")
                getESVPsalms(ctx, 1, button)
            }
        }

    }

    private fun checkLogin(ctx: Context?, button:Button?) {
        log("setLists started")
        var result : Map<String, Any>
        if (user != null) {
            db.collection("main").document(user.uid).get()
                    .addOnSuccessListener {
                        result = it.data!!
                        if (result["dateChecked"] != null) {
                            listNumberEditString(ctx, "dateChecked", result["dateChecked"] as String)
                        }
                        statisticsEdit(ctx, "maxStreak", (result["maxStreak"] as Long).toInt())
                        statisticsEdit(ctx, "currentStreak", (result["currentStreak"] as Long).toInt())
                        setLists(ctx, button, result)
                    }

        }else{
            setLists(ctx, button, null)
        }
    }

    private fun setLists(ctx: Context?, button:Button?, result: Map<String, Any>?){
        if (listNumberReadString(ctx, "dateChecked") != getCurrentDate(false)) {
            val intent = Intent(ctx, DailyCheck::class.java)
            ctx!!.sendBroadcast(intent)
        }
        val sharedpref = PreferenceManager.getDefaultSharedPreferences(ctx)
        val fromFirebase : Boolean = (result != null)
        val psalmChecked = if(result != null) (result["psalms"] as Boolean) else sharedpref.getBoolean("psalms", false)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val list1Num = if(result != null) (result["list1"] as Long).toInt() else listNumberReadInt(ctx, "List 1")
        val list2Num = if(result != null) (result["list2"] as Long).toInt() else listNumberReadInt(ctx, "List 2")
        val list3Num = if(result != null) (result["list3"] as Long).toInt() else listNumberReadInt(ctx, "List 3")
        val list4Num = if(result != null) (result["list4"] as Long).toInt() else listNumberReadInt(ctx, "List 4")
        val list5Num = if(result != null) (result["list5"] as Long).toInt() else listNumberReadInt(ctx, "List 5")
        val list6Num = if(result != null) (result["list6"] as Long).toInt() else listNumberReadInt(ctx, "List 6")
        val list7Num = if(result != null) (result["list7"] as Long).toInt() else listNumberReadInt(ctx, "List 7")
        val list8Num = if(result != null) (result["list8"] as Long).toInt() else listNumberReadInt(ctx, "List 8")
        val list9Num = if(result != null) (result["list9"] as Long).toInt() else listNumberReadInt(ctx, "List 9")
        val list10Num = if(result != null) (result["list10"] as Long).toInt() else listNumberReadInt(ctx, "List 10")
        model.list1.value = checkListNumber(ctx, "List 1", R.array.list_1, list1Num, "list1", fromFirebase)
        model.list2.value = checkListNumber(ctx, "List 2", R.array.list_2, list2Num, "list2", fromFirebase)
        model.list3.value = checkListNumber(ctx, "List 3", R.array.list_3, list3Num, "list3", fromFirebase)
        model.list4.value = checkListNumber(ctx, "List 4", R.array.list_4, list4Num, "list4", fromFirebase)
        model.list5.value = checkListNumber(ctx, "List 5", R.array.list_5, list5Num, "list5", fromFirebase)
        if (psalmChecked) {
            if(day != 31) {
                val pal = "$day, ${day + 30}, ${day + 60}, ${day + 90}, ${day + 120}"
                model.list6.value = pal
            }else{
                val pal = "Day Off"
                model.list6.value = pal
            }
        } else {
            model.list6.value = checkListNumber(ctx, "List 6", R.array.list_6, list6Num, "list6", fromFirebase)
        }

        model.list7.value = checkListNumber(ctx, "List 7", R.array.list_7, list7Num, "list7", fromFirebase)
        model.list8.value = checkListNumber(ctx, "List 8", R.array.list_8,  list8Num, "list8", fromFirebase)
        model.list9.value = checkListNumber(ctx, "List 9", R.array.list_9, list9Num, "list9", fromFirebase)
        model.list10.value = checkListNumber(ctx, "List 10", R.array.list_10,  list10Num, "list10", fromFirebase)
        if(user != null) checkLists(ctx, button, result) else checkLists(ctx, button, null)

    }

    private fun checkLists(ctx: Context?, button: Button?, result: Map<String, Any>?){
        val list1Done = if (user != null && result != null)  (result["list1Done"] as Long).toInt() else listNumberReadInt(ctx, "list1Done")
        val list2Done = if (user != null && result != null)  (result["list2Done"] as Long).toInt() else listNumberReadInt(ctx, "list2Done")
        val list3Done = if (user != null && result != null)  (result["list3Done"] as Long).toInt() else listNumberReadInt(ctx, "list3Done")
        val list4Done = if (user != null && result != null)  (result["list4Done"] as Long).toInt() else listNumberReadInt(ctx, "list4Done")
        val list5Done = if (user != null && result != null)  (result["list5Done"] as Long).toInt() else listNumberReadInt(ctx, "list5Done")
        val list6Done = if (user != null && result != null)  (result["list6Done"] as Long).toInt() else listNumberReadInt(ctx, "list6Done")
        val list7Done = if (user != null && result != null)  (result["list7Done"] as Long).toInt() else listNumberReadInt(ctx, "list7Done")
        val list8Done = if (user != null && result != null)  (result["list8Done"] as Long).toInt() else listNumberReadInt(ctx, "list8Done")
        val list9Done = if (user != null && result != null)  (result["list9Done"] as Long).toInt() else listNumberReadInt(ctx, "list9Done")
        val list10Done = if (user != null && result != null)  (result["list10Done"] as Long).toInt() else listNumberReadInt(ctx, "list10Done")
        val listsDone = if(user != null && result != null) (result["listsDone"] as Long).toInt() else listNumberReadInt(ctx, "listsDone")
        when(list1Done){
            0 -> enableSingle(cardList1);
            1 -> disableSingle(cardList1, button) }
        when(list2Done){
            0 -> enableSingle(cardList2)
            1 -> disableSingle(cardList2, button) }
        when(list3Done){
            0 -> enableSingle(cardList3)
            1 -> disableSingle(cardList3, button) }
        when(list4Done){
            0 -> enableSingle(cardList4)
            1 -> disableSingle(cardList4, button) }
        when(list5Done){
            0 -> enableSingle(cardList5)
            1 -> disableSingle(cardList5, button) }
        when(list6Done){
            0 -> enableSingle(cardList6)
            1-> disableSingle(cardList6, button) }
        when(list7Done){
            0 -> enableSingle(cardList7)
            1 -> disableSingle(cardList7, button) }
        when(list8Done){
            0 -> enableSingle(cardList8)
            1 -> disableSingle(cardList8, button) }
        when(list9Done){
            0 -> enableSingle(cardList9)
            1 -> disableSingle(cardList9, button) }
        when(list10Done){
            0 -> enableSingle(cardList10)
            1 -> disableSingle(cardList10, button) }
        when(listsDone) {
            10 -> {
                button?.setText(R.string.done)
                button?.isEnabled = false
                button?.setBackgroundColor(Color.parseColor("#00383838")) }
            0 -> {
                button?.setText(R.string.notdone)
                button?.isEnabled = true
                button?.setBackgroundColor(Color.parseColor("#383838")) }
            in 1..9 -> {
                button?.setText(R.string.markRemaining)
                button?.isEnabled = true
                button?.setBackgroundColor(Color.parseColor("#383838")) } }
    }

    private fun enableSingle(cardList: CardView){
        cardList.isEnabled = true
        cardList.setCardBackgroundColor(Color.parseColor("#383838"))
    }

    private fun disableSingle(cardList:CardView, button:Button?){
        button?.setText(R.string.markRemaining)
        cardList.isEnabled = false
        cardList.setCardBackgroundColor(Color.parseColor("#00383838"))
    }

    private fun checkListNumber(ctx: Context?, list_string: String, listId: Int, listNum: Int, firebaseString: String?, fromFirebase: Boolean):String {
        val list = ctx?.resources!!.getStringArray(listId)
        return when(listNum){
            list.size ->{
                if(fromFirebase){
                    updateFS(firebaseString!!, 0)
                }
                listNumberEditInt(ctx, list_string, 0)
                list[0]
            }
            else -> {
                listNumberEditInt(ctx, list_string, listNum)
                list[listNum]
            }
        }
    }

    private fun chapterShower(ctx: Context?, cardList:CardView, listDone:String, listName:String,arrayId: Int,  wv : WebView, title: String, psalms: Boolean, iteration: Int, button: Button?){
        val alert = AlertDialog.Builder(context!!)
        alert.setTitle(title)
        alert.setView(wv)
        if(psalms){
            if(iteration != 5) {
                alert.setNeutralButton("Mark Done"){ dialogInterface, _ ->
                    dialogInterface.dismiss()
                    markSingle(ctx, cardList, listDone, listName, R.array.list_6, button, false)
                }
                alert.setPositiveButton("Next") { _, _ ->
                    getESVPsalms(ctx, iteration + 1, button)
                }
            }else if(iteration == 5){
                alert.setNeutralButton("Close"){dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                alert.setPositiveButton("Mark Done") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    markSingle(ctx, cardList, listDone, listName, arrayId, button, false)
                }
            }
        }else{
            alert.setPositiveButton("Mark Done"){dialogInterface, _->
                dialogInterface.dismiss()
                markSingle(ctx, cardList, listDone, listName, arrayId, button,false)
            }
            alert.setNeutralButton("Close"){ dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        }
        alert.show()
    }

    private fun getESVPsalms(ctx: Context?, iteration: Int, button: Button?){
        var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        if(iteration > 1){
            day += 30 * (iteration - 1)
        }
        val title = "Psalm $day"
        val url = "https://api.esv.org/v3/passage/html/?q=Psalm$day&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
        val wv = getESV(ctx, url)
        chapterShower(ctx, cardList6,"list6Done", "List 6", R.array.list_6, wv, title, true, iteration, button)
    }

    private fun getESVReference(ctx: Context?, chapter: String, cardList:CardView, listName:String, listDone : String, arrayId: Int, button: Button?){
        val title = chapter
        val url = "https://api.esv.org/v3/passage/html/?q=$chapter&include-css-link=true&inline-styles=false&wrapping-div=false&div-classes=passage&include-passage-references=false&include-footnotes=false&include-copyright=true&include-short-copyright=false"
        val wv = getESV(ctx, url)
        chapterShower(ctx, cardList,listDone, listName, arrayId, wv, title, false, 0, button)
    }

    private fun googleSignIn(ctx: Context?){
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            val builder = AlertDialog.Builder(context!!)
            builder.setPositiveButton(getString(R.string.yes)){ _, _ ->
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(ctx, "Signed Out!", Toast.LENGTH_LONG).show()
            }
            builder.setNeutralButton("No"){dialogInterface, _ ->
                dialogInterface.cancel()
            }
            builder.setMessage("Are you sure you want to sign out ${user.email}?")
            builder.setTitle("Sign Out?")
            builder.create().show()

        }else {
            val builder = android.app.AlertDialog.Builder(context)
            builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                val mGoogleSignInClient = GoogleSignIn.getClient(ctx!!, gso)
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, 96)
            }
            builder.setNeutralButton("No") { dialogInterface, _ -> log("cancel pressed"); dialogInterface.cancel(); notificationsCheck() }
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
                db.collection("main").document(user!!.uid).get()
                        .addOnSuccessListener {
                            if (it != null) {
                                val builder = AlertDialog.Builder(context!!)
                                builder.setPositiveButton("Use Cloud Data") { dialoginterface, i ->
                                    SharedPref.firestoneToPreference(it, context)
                                    statisticsEdit(context, "firstRun", 1)
                                }
                                builder.setNeutralButton("Overwrite with device") { dialoginterface, i ->
                                    SharedPref.preferenceToFireStone(context)
                                    statisticsEdit(context, "firstRun", 1)
                                }
                                builder.setTitle("Account Found")
                                builder.setMessage("Found ${FirebaseAuth.getInstance().currentUser?.email}. Would you like to TRANSFER from your account or OVERWRITE your account with this device?")
                                builder.create().show()
                            } else {
                                statisticsEdit(context, "firstRun", 1)
                                SharedPref.preferenceToFireStone(context)
                            }
                        }
            } else {
                statisticsEdit(context, "firstRun", 1)
                Toast.makeText(context, "Google Sign In Failed", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun welcome(ctx: Context?){
            log("WELCOME STARTED")
            val alert = AlertDialog.Builder(context!!)
            alert.setPositiveButton(getString(R.string.yes)) { diag, _ ->
                diag.dismiss()
                statisticsEdit(ctx, "skipFirstRun", 0)
                haventHeard(false)
            }
            alert.setNeutralButton("No") { diag, _ ->
                diag.dismiss()
                statisticsEdit(ctx, "skipFirstRun", 1)
                haventHeard(true)
            }
            alert.setTitle("Welcome to Prof Horner's Bible Reading System App")
            alert.setMessage("Would you like to know more about the system?")
            alert.create().show()
    }
    private fun haventHeard(skippedSystem: Boolean){
        val alert = AlertDialog.Builder(context!!)
        val alert2 = AlertDialog.Builder(context!!)
        alert.setPositiveButton("Yes"){ diag, _ ->
            diag.dismiss()
            val i = Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://grant-horner-bible-reading-plan-pdf.weebly.com/uploads/4/5/9/7/45977741/professor-grant-horners-bible-reading-system.pdf"))
            aboutApp()
            startActivity(i)
        }
        alert.setNeutralButton("No thank you"){diag, _ ->
            diag.dismiss()
            aboutApp()
        }
        alert.setTitle("About The System")
        alert.setMessage("The Grant Horner System is 10 lists, where you read 1 chapter from each list per day. Would you like to see the PDF from Professor Horner?")
        alert2.setPositiveButton("Ok"){diag, _ ->
            diag.dismiss()
            aboutApp()
        }
        alert2.setTitle("About The System")
        alert2.setMessage("No worries, if you go into About you can access the PDF at any time.")
        if(!skippedSystem){
            alert.create().show()
        }else{
            alert2.create().show()
        }
    }
    private fun aboutApp(){
        val alert = AlertDialog.Builder(context!!)
        alert.setPositiveButton("Yes"){ diag, _ ->
            diag.dismiss()
            startTutorial()
        }
        alert.setNeutralButton("No"){diag, _ ->
            diag.dismiss()
            skipped = true
            googleSignIn(context)
        }
        alert.setTitle("About The App")
        alert.setMessage("Would you like to learn more about the app?")
        alert.create().show()
    }
    private fun startTutorial(){
        val alert = AlertDialog.Builder(context!!)
        alert.setPositiveButton("Next"){diag, _ ->
            diag.dismiss()
            tutorial2()
        }
        alert.setTitle("In App Bible/Marking Individual Lists")
        alert.setMessage("While most may prefer a physical Bible, or may prefer changing translation (a strong suite for this app), there is a built in Bible, using ESV (more coming). Tap on a list to bring up the chapter! From here you can also mark the list as done!")
        alert.create().show()
    }
    private fun tutorial2(){
        val alert = AlertDialog.Builder(context!!)
        alert.setPositiveButton("Next"){diag, _ ->
            diag.dismiss()
            tutorial3()
        }
        alert.setTitle("Force Daily Reset")
        alert.setMessage("Did the reading yesterday but realized this morning you forgot to mark the lists as done? Press the lists you read, or the button to mark all, and then the three button icon, and press \"Force Daily Reset\"")
        alert.create().show()
    }
    private fun tutorial3(){
        val alert = AlertDialog.Builder(context!!)
        alert.setPositiveButton("Next"){diag, _->
            diag.dismiss()
            tutorial4()
        }
        alert.setTitle("Statistics/Partial/Vacation Mode")
        alert.setMessage("The app keeps track of your current streak and your highest streak, so keep doing this daily! Can't do all 10? Allow partial will increase your streak even if you don't do all 10! Vacation mode will turn off notifications and won't set your streak to 0 if you miss a day")
        alert.create().show()
    }
    private fun tutorial4() {
        val alert = AlertDialog.Builder(context!!)
        alert.setPositiveButton("1 Psalm"){diag, _ ->
            diag.dismiss()
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("psalms", false).apply()
            googleSignIn(context)
        }
        alert.setNeutralButton("5 Psalms!"){diag, _ ->
            diag.dismiss()
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("psalms", true).apply()
            googleSignIn(context)
        }
        alert.setTitle("Psalms")
        alert.setMessage("The original plan has 1 psalm a day, however I prefer doing 5 Psalms a day based on the day (so today is the 7th, I would read Psalm 7, 37, 67, 97, 127). Using this way, you'd read all the psalms 12 times a year. Which would you prefer?")
        alert.create().show()
    }
    private fun notificationsCheck(){
        val alert = AlertDialog.Builder(context!!)
        alert.setPositiveButton("Yes") { diag, _ ->
            statisticsEdit(context, "firstRun", 1)
            diag.dismiss()
            startActivity(Intent(context, SettingsActivity::class.java).putExtra("Notifications", true))
        }
        alert.setNeutralButton("No"){dialogInterface, _ ->
            dialogInterface.dismiss()
            statisticsEdit(context, "firstRun", 1)
            fragmentManager!!.beginTransaction().detach(HomeFragment()).attach(HomeFragment()).commit()
        }
        alert.setTitle("Notifications")
        alert.setMessage("Would you like to change your notification settings now?")
        alert.create().show()

    }
}