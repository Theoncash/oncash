package `in`.oncash.oncash.View

import AppInstallReceiver
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager

import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import `in`.oncash.oncash.Component.Instructions_RecylerViewAdapter
import `in`.oncash.oncash.Component.Instructions_detail_RecylerViewAdapter
import `in`.oncash.oncash.Component.TimerService
import `in`.oncash.oncash.Component.customLoadingDialog
import `in`.oncash.oncash.Component.offerQueries_adapter
import `in`.oncash.oncash.Component.step_Adapter
import `in`.oncash.oncash.DataType.Instruction
import `in`.oncash.oncash.DataType.Offer
import `in`.oncash.oncash.DataType.Step
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.R
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import `in`.oncash.oncash.Repository.offer_AirtableDatabase
import `in`.oncash.oncash.RoomDb.notification_checker
import `in`.oncash.oncash.RoomDb.userDb
import `in`.oncash.oncash.ViewModel.home_viewModel
import `in`.oncash.oncash.ViewModel.info_viewModel
import `in`.oncash.oncash.databinding.ActivityInfoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Array
import java.util.Calendar
import java.util.concurrent.TimeUnit


class Info : AppCompatActivity() {
     lateinit var binding : ActivityInfoBinding
    val home_viewModel: home_viewModel by viewModels()
    val info_viewModel: info_viewModel by viewModels()

    @SuppressLint("SetTextI18n", "InvalidPeriodicWorkRequestInterval")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val loadingDialog = customLoadingDialog(this)

        var isClicked = false
        //Getting Data from the intent form home(Activity)
        val offerId: String? = intent.getStringExtra("OfferId")
        val offerName = intent.getStringExtra("OfferName")
        binding.offernameInfo.text = offerName
        var offerPrice = intent.getStringExtra("OfferPrice")
        Glide.with(this).load(intent.getStringExtra("OfferImage")).into(binding.offerImageInfo)
        var offer: String? = intent.getStringExtra("OfferLink")
        val subid: String? = intent.getStringExtra("subid")
        val subid2: String? = intent.getStringExtra("subid2")
        val number: String? = intent.getStringExtra("number")
        val appName: String? = intent.getStringExtra("appName")
        val viewUri: String? = intent.getStringExtra("videoId")
        val offerLink = "$offer?subid2=$number/"
        val wallet : Int = intent.getIntExtra("wallet" , 0)
        val noOfSteps: String? = intent.getStringExtra("noOfSteps")
        val regSMS = intent.getStringExtra("regSMS")
        val offerDate = intent.getStringExtra("offerDate")
        //Initilizing the recylerview adapter
        val adapter = step_Adapter()
        val Documentsadapter = Instructions_RecylerViewAdapter()
        val OfferQueriesAdapter = offerQueries_adapter()


        binding.offerDate.text = "Offer Ends in $offerDate"

            binding.offerPrice.text = "₹" + offerPrice!!

        binding.instructionListInfo.adapter = adapter
        binding.instructionListInfo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.documentsListInfo.adapter = Documentsadapter
        binding.documentsListInfo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.offerQueriesListInfo.adapter = OfferQueriesAdapter
        binding.offerQueriesListInfo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.backButtonInfo.setOnClickListener {
finish()
        }
        //videoVIew
        /* val videoview= binding.videoView
        lifecycle.addObserver(videoview)

        videoview.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(viewUri!! , 0F)
            }
        })*/

// To show the dialog
        loadingDialog.show()
        loadingDialog.setMessage("Good things take time...")

// Simulate some background work (replace this with your actual work)


        var list: ArrayList<Step> = ArrayList()
        var Instruction: ArrayList<Instruction> = ArrayList()
        var Documents: ArrayList<Instruction> = ArrayList()

        Documents.addAll(
            arrayListOf(
                Instruction("Aadhar card Photo" , "1"),
                Instruction("Pan card Photo" , "2"),
                Instruction("Bank Account Info" , "1")
            )
        )
        Documentsadapter.updateList(Documents)
        var ClosingInstruction: ArrayList<Instruction> = ArrayList()
        Log.i("closingInstructions" , "it.toString()")


        adapter.updateList(list , Instruction , ClosingInstruction)
        binding.offerLinkButtonInfo.visibility = View.VISIBLE
        //Observing the getInstructionList() in info_viewmodel (ie which gets the data from info_FirebaseRepo)
        info_viewModel.getIsWeb(offerId!!.toInt())

        info_viewModel.getOfferQueries(offerId!!).observe(this@Info){
            OfferQueriesAdapter.updateList(it)
        }
        lifecycleScope.launch {
            info_viewModel.isCompleted(number!!.toLong(), offerId!!.toInt())
            info_viewModel.getBlacklist( number!!.toLong() ,  offerId!!.toInt())
            info_viewModel.getInstrutionList(offerId!!)

//               isOfferCompleted(
//                    appName!! ,
//                    regSMS!! ,
//                    offerId!! .toInt(),
//                    number!!.toLong() ,
//                    offerPrice!!.toInt(),
//                    offerName!!,
//                    loadingDialog
//                )

            info_viewModel.getBlackListData().observe(this@Info) { it ->
                if (!it) {
                    lifecycleScope.launch {
                        isBeing(
                                info_viewModel,
                                number!!.toLong(),
                                appName!!,
                                offerId!!.toInt(),
                                this@Info,
                                number!!.toLong(),
                                this@Info
                            ) {
                            if (!it) {
                                Log.i("closingInstructionss" , "bool" +  it!!.toString())


                                    info_viewModel.getisOfferCompleted().observe(this@Info) {
                                        Log.i("closingInstructionss" , "bool" +  it!!.toString())

                                        if (it == false) {
                                              info_viewModel.getInstructionListData()  .observe(this@Info, Observer {
                                                    if (it.isNotEmpty()) {
                                                        Instruction.clear()
                                                        Log.i("instructionData" , it.size.toString())
                                                        Instruction.addAll(it )

                                                        info_viewModel.getIsWebData().observe(this@Info){
                                                            if(it){
                                                                Log.i("instructionData" , noOfSteps.toString())

                                                                for (i in 0 until noOfSteps!!.toInt()) {

                                                                    if (i == 1) {
                                                                        list.add(
                                                                            Step(
                                                                                false,
                                                                                "Register in the App"
                                                                            )
                                                                        )

                                                                    }
                                                                    if (i == 2) {
                                                                        list.add(
                                                                            Step(
                                                                                false,
                                                                                "Completed 1st Trade "
                                                                            )
                                                                        )

                                                                    }
                                                                }
                                                                loadingDialog.dismiss()
                                                                adapter.updateList(list , Instruction , ClosingInstruction)


                                                            }else{
                                                                for (i in 0 until noOfSteps!!.toInt()) {
                                                                    if (i == 0) {
                                                                        list.add(
                                                                            Step(
                                                                                false,
                                                                                "Install the App"
                                                                            )
                                                                        )
                                                                    }
                                                                    if (i == 1) {
                                                                        list.add(
                                                                            Step(
                                                                                false,
                                                                                "Register in the App"
                                                                            )
                                                                        )

                                                                    }
                                                                    if (i == 2) {
                                                                        list.add(
                                                                            Step(
                                                                                false,
                                                                                "Completed 1st Trade "
                                                                            )
                                                                        )

                                                                    }
                                                                }
                                                                loadingDialog.dismiss()
                                                                adapter.updateList(list , Instruction , ClosingInstruction)

                                                            }
                                                        }




                                                    }
                                                    binding.offerLinkButtonInfo.visibility =
                                                        View.VISIBLE

                                                    lifecycleScope.launch {
                                                        info_viewModel.getIsWebData().observe(this@Info) {
                                                            if (!it) {
                                                                lifecycleScope.launch {
                                                                    if (isAppInstalled(
                                                                            this@Info,
                                                                            appName!!
                                                                        )
                                                                    ) {
                                                                        list[0] =
                                                                            Step(
                                                                                true,
                                                                                list[0].instruction
                                                                            )
                                                                        Toast.makeText(
                                                                            this@Info,
                                                                            list[0].instruction.toString(),
                                                                            Toast.LENGTH_LONG
                                                                        ).show()

                                                                        adapter.updateList(
                                                                            list,
                                                                            Instruction,
                                                                            ClosingInstruction
                                                                        )

                                                                    }

                                                                    if (isRegistered(
                                                                            this@Info,
                                                                            appName,
                                                                            regSMS!!
                                                                        )
                                                                    ) {

                                                                        list[1] =
                                                                            Step(
                                                                                true,
                                                                                list[1].instruction
                                                                            )
                                                                        adapter.updateList(
                                                                            list,
                                                                            Instruction,
                                                                            ClosingInstruction
                                                                        )

                                                                    }
                                                                }

                                                            } else {
                                                                lifecycleScope.launch {

                                                                    try {
                                                                        if (isRegistered(
                                                                                this@Info,
                                                                                appName,
                                                                                regSMS!!
                                                                            )
                                                                        ) {
                                                                            list[0] =
                                                                                Step(
                                                                                    true,
                                                                                    list[0].instruction
                                                                                )
                                                                            adapter.updateList(
                                                                                list,
                                                                                Instruction,
                                                                                ClosingInstruction
                                                                            )
                                                                        }
                                                                        loadingDialog.dismiss()


                                                                    } catch (e: ArrayIndexOutOfBoundsException) {
                                                                        Toast.makeText(
                                                                           this@Info ,
                                                                            "Something Went Wrong.....",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()

                                                                    }
                                                                }
                                                            }
                                                        }

                                                    }
                                                })
                                        } else {


                                            binding.offerLinkButtonInfo.text = "Completed"
                                            binding.offerLinkButtonInfo.visibility = View.VISIBLE
                                            Log.i("closingInstructions" , "working")
                                            loadingDialog.dismiss()


                                            info_viewModel.getClosingInstrutionList(offerId!!).observe(this@Info){
                                                ClosingInstruction.clear()
                                                ClosingInstruction.addAll(it)
                                                Log.i("closingInstructions" , it.toString())
                                                adapter.updateList(list , Instruction , ClosingInstruction)
                                                binding.offerLinkButtonInfo.text = "Completed"
                                                binding.offerLinkButtonInfo.visibility = View.VISIBLE
                                            }
                                        if(list.size <= 2){
                                            list.add(
                                                Step(
                                                    false,
                                                    "Close your account "
                                                )
                                            )
                                        }

                                        }


                                    }
                            } else {
                                loadingDialog.dismiss()

                                binding.offerLinkButtonInfo.text = "Not Eligible"
                                binding.offerLinkButtonInfo.visibility = View.VISIBLE
                            }
                        }
                    }
                }else{
                    loadingDialog.dismiss()

                    binding.offerLinkButtonInfo.text = "Not Eligible"
                    binding.offerLinkButtonInfo.visibility = View.VISIBLE
                }
            }
        }

        //Redirecting user to chrome after the event of button accurs
        binding.offerLinkButtonInfo.setOnClickListener{

            if(binding.offerLinkButtonInfo.text.contains( "0000000") && !isClicked){
                isClicked  = true
                val total_bal =  home_viewModel().totalOffers.value
                val loadingDialog = customLoadingDialog(this)

// To show the dialog
                loadingDialog.show()
                loadingDialog.setMessage("Good things take time...")

// Simulate some background work (replace this with your actual work)
                Handler().postDelayed({
                    // Dismiss the dialog when the work is done
                    loadingDialog.dismiss()
                    startActivity(Intent(this ,  Home::class.java))
                }, 4000)

                lifecycleScope.launch {
                    try{
                        UserInfo_Airtable_Repo().updateCompletedOffer( number!!.toLong()  , total_bal!! ,
                            wallet ,
                            userData( number!!.toLong()),
                            offerId!!.toInt(), offerPrice.toString()  , "Completed")
                }catch(e:Exception){
                    Toast.makeText( this@Info, "Some Error Occurred" , Toast.LENGTH_SHORT).show()
                    }                }
            }else {
                if (binding.offerLinkButtonInfo.text == "Completed") {
                    Toast.makeText(this, "Already Completed", Toast.LENGTH_LONG).show()
                }
                else {
                    if(binding.offerLinkButtonInfo.text.contains( "Not")) {
                        Toast.makeText(this, "Not Eligible for Offer", Toast.LENGTH_LONG).show()
                    }else{

                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(offerLink))

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.setPackage("com.android.chrome")
                        info_viewModel.updateOfferHistory(
                            userData(number!!.toLong()),
                            offerId!!.toInt(), offerPrice.toString(),  "Being Reviewed")
                        val inputData = Data.Builder()
                            .putString("appName", appName!!)
                            .putString("name", offerName!!)
                            .putString("offerId" , offerId!!)
                            .build()
                        val periodicWorkRequest = PeriodicWorkRequest.Builder(
                            AppInstallReceiver::class.java,
                            15, TimeUnit.SECONDS
                        )       .setInputData(inputData)


                            .build()

                        lifecycleScope.launch {
                            withContext(Dispatchers.Default)

                            {
                              var  roomDb = Room.databaseBuilder(
                                    applicationContext,
                                    notification_checker::class.java,
                                    "notification"
                                )
                                    .fallbackToDestructiveMigration() // Add this line for destructive migration
                                    .build()
                               var bool = roomDb.notificationCheckerDao().getNotificationCheckerById(offerId!!.toInt())
                                if(bool == null){
                                   val worker = WorkManager .getInstance(this@Info).enqueue(periodicWorkRequest)

                                }
                            }
                        }

                        val serviceIntent = Intent(this, TimerService::class.java)
                        startService(serviceIntent)
                        try {
                            this.startActivity(intent)
                        } catch (ex: ActivityNotFoundException) {
                            // Chrome browser presumably not installed so allow user to choose instead
                            intent.setPackage(null)
                            this.startActivity(intent)
                        }
                    }

                }
            }

        }

        offer_AirtableDatabase().getData()

       /* binding.YoutubeViewExpandable.setOnClickListener {
            val hiddenView = binding.videoView
            val cardView =binding.YoutubeViewExpandable
            if (hiddenView.visibility == View.VISIBLE){

                hiddenView.visibility = View.GONE

                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            }else {
                hiddenView.visibility = View.VISIBLE

                TransitionManager.beginDelayedTransition(cardView, AutoTransition())

            }
        }*/
    }

    @SuppressLint("SetTextI18n")
     fun isOfferCompleted(appName: String, regSMS: String, offerId: Int, userNumber: Long, appPrice :Int, Name :String , loadingDialog : customLoadingDialog){


            home_viewModel.getIsCompleted(offerId, userNumber)

            home_viewModel.getIsCompletedData().observe(this@Info) {
                if (it == false) {
                    CoroutineScope(Dispatchers.Main).launch {
                        info_viewModel.getIsWebData().observe(this@Info) {
                            CoroutineScope(Dispatchers.Main).launch {

                                if (it) {
                                    loadingDialog.dismiss()

                                    if (isRegistered(this@Info, appName, regSMS)) {

                                        showRewardCollectionDialog(
                                            offerId, userNumber, appPrice, Name
                                        )

                                    }
                                } else {
                                    loadingDialog.dismiss()

                                    if (isAppInstalled(this@Info, appName)) {
                                        if (isRegistered(this@Info, appName, regSMS)) {
                                            if (getTimeSpent(appName) >= 7) {
                                                showRewardCollectionDialog(
                                                    offerId, userNumber, appPrice, Name
                                                )
                                            }
                                        }
                                    }
                                }


                            }
                        }



                }
            }else{
                    loadingDialog.dismiss()

                    binding.offerLinkButtonInfo.text = "Completed"
                    binding.offerLinkButtonInfo.visibility = View.VISIBLE
                }

        }

        }


     fun getTimeSpent(targetPackageName : String):Int{
        val targetPackageName = targetPackageName // Replace with your app's package name
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_MONTH, -1) // Subtract 1 month from the current time
        val startTime = calendar.timeInMillis
        var timeSpentInMin = 0
        val stats: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            currentTime
        )

        for (usageStats in stats) {
            if (usageStats.packageName == targetPackageName) {
                Toast.makeText(this@Info , usageStats.packageName , Toast.LENGTH_LONG).show()
                val timeInMilis  = usageStats.totalTimeInForeground
                timeSpentInMin = (timeInMilis / 60000).toInt()
                Toast.makeText(this@Info , timeSpentInMin.toString() , Toast.LENGTH_LONG).show()
                // Convert timeSpentInMillis to hours, minutes, seconds, etc. as needed.
                break
            }
        }
        return timeSpentInMin
    }


    @SuppressLint("MissingInflatedId", "SetTextI18n")
    fun showRewardCollectionDialog( offerId: Int , userNumber: Long ,  price:Int ,  Name :String ) {
        val builder = AlertDialog.Builder(this@Info)
        val dialogView = layoutInflater.inflate(R.layout.reward_collection_dialog, null)
        builder.setView(dialogView)
        val rewardButton = dialogView.findViewById<Button>(R.id.btnCollectReward)
        val rewardText = dialogView.findViewById<TextView>(R.id.textCollectReward)
        val alertDialog = builder.create()
        rewardText.text = "Congratulations! on completing ${Name} . You've won a reward! of Rs.${price} "
        alertDialog.show()

        rewardButton.setOnClickListener {
            // Handle the reward collection logic here
            // For example, grant the reward and dismiss the dialog
            // You can also dismiss the dialog without granting the reward if the user cancels

            val soundPool = SoundPool.Builder().setMaxStreams(1).build()
            val soundID = soundPool.load(this, R.raw.water_drop, 1)
            soundPool.play(soundID, 1f, 1f, 1, 0, 1f)
            val total_bal = home_viewModel().totalOffers.value
            binding.offerLinkButtonInfo.text = "Completed"
            binding.offerLinkButtonInfo.visibility = View.VISIBLE
            lifecycleScope.launch {
                alertDialog.dismiss()
                try {
                    home_viewModel.getIsCompleted(offerId, userNumber)
                    home_viewModel.getIsCompletedData().observe(this@Info){
                        if(!it){
                            lifecycleScope.launch {
                                UserInfo_Airtable_Repo().updateCompletedOffer(
                                    userNumber!!.toLong(), total_bal!!,
                                    userNumber.toInt(),
                                    userData(userNumber.toLong()),
                                    offerId!!.toInt(), price.toString(), "Completed"
                                )
                            }

                        }else{
                            Toast.makeText(
                                this@Info,
                                "Offer is Claimed Already",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }


                } catch (e: Exception) {
                    Toast.makeText(this@Info, "Some Error Occurred Contact Customer Service", Toast.LENGTH_LONG)
                        .show()
                }
            }

        }
    }
}

@SuppressLint("SuspiciousIndentation")
 fun isBeing(
    infoViewModel: info_viewModel,
    userId: Long,
    appName: String,
    offerId: Int,
    lifecycleOwner: LifecycleOwner,
    userNumber: Long,
    context: Context,
    callback: (Boolean) -> Unit

){

    var isB : Boolean = false


    infoViewModel.isOfferBeign(userId, offerId)
     infoViewModel.getisOfferCompleted().observe(lifecycleOwner){ bool ->
         Log.i("blacklisttt", bool.toString())
        if (!bool) {
            lifecycleOwner.lifecycleScope.launch {
                val regSMS = isRegistered(context , appName , appName)
                infoViewModel.getIsWebData().observe(lifecycleOwner){
                    if(!it) {
                        lifecycleOwner.lifecycleScope.launch {
                            val appInstalled: Boolean = isAppInstalled(context, appName)
                            isB = appInstalled

                            if (appInstalled || regSMS) {
                                isB = true
                                infoViewModel.addBlacklist(userData(userNumber), offerId)
                            }
                        }
                    }else{
                        if (regSMS) {
                            isB = true
                            infoViewModel.addBlacklist(userData(userNumber), offerId)
                        }
                    }
                }

            }
        }


         Log.i("blacklisttt", "returning $isB")

         // You may need to handle the case where `isOfferBeing` is not observed or not applicable.
         // For now, returning false as a placeholder.
         callback(isB)
    }

}

  suspend fun isAppInstalled(context: Context , appName: String): Boolean = withContext(Dispatchers.Default) {

         // get list of all the apps installed
         // get list of all the apps installed
         val pm: PackageManager = context.packageManager
         try {
             val packageInfo = pm.getInstalledApplications(0)

             for (app in packageInfo) {
                 if (app.packageName == appName) {
                     return@withContext true
                 }
             }

         } catch (e: PackageManager.NameNotFoundException) {
             // The app is not installed.
             Toast.makeText(context, "packageInfo.applicationInfo.packageName", Toast.LENGTH_LONG)
                 .show()

             return@withContext false
         }
         return@withContext false
     }



 suspend fun isRegistered(context: Context, appName : String, regSMS :String) : Boolean = withContext(Dispatchers.Default) {

         val inboxSms = ArrayList<String>()
         val uri = Uri.parse("content://sms/inbox")
         val cursor = context.contentResolver.query(uri, null, null, null, null)

         if (cursor != null && cursor.moveToFirst()) {
             val bodyIndex = cursor.getColumnIndex("body")
             do {
                 val smsBody = cursor.getString(bodyIndex)
                 inboxSms.add(smsBody)
             } while (cursor.moveToNext())
             cursor.close()
         }
         // Assuming you have already retrieved SMS messages and stored them in the 'inboxSms' list
         var messageFound = false

         for (smsBody in inboxSms) {

             if (smsBody.lowercase().contains(regSMS.toString().lowercase(), ignoreCase = true)) {
                 // The message contains the search string
                 messageFound = true
                 break  // Exit the loop once a matching message is found
             }
         }

         return@withContext messageFound


// Now, 'inboxSms' contains a list of SMS message bodies from the inbox.
     }

