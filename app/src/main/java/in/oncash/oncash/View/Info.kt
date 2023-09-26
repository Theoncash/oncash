package `in`.oncash.oncash.View

import android.R
import android.R.attr.text
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ernestoyaquello.com.verticalstepperform.Step
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener
import `in`.oncash.oncash.Component.Instructions_RecylerViewAdapter
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.Repository.offer_AirtableDatabase
import `in`.oncash.oncash.Step.installStep
import `in`.oncash.oncash.Step.registerStep
import `in`.oncash.oncash.Step.tradeStep
import `in`.oncash.oncash.ViewModel.info_viewModel
import `in`.oncash.oncash.databinding.ActivityInfoBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Currency


class Info : AppCompatActivity() , StepperFormListener {
     lateinit var binding : ActivityInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Getting Data from the intent form home(Activity)
        val offerId : String? = intent.getStringExtra("OfferId")
        val offerName= intent.getStringExtra("OfferName")
        binding.offernameInfo.text = offerName



        val offerPrice = intent.getStringExtra("OfferPrice")
        val formattedPrice = NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance("INR")
        }.format(offerPrice?.toDoubleOrNull() ?: 0.0)

        binding.offerPrice.text = formattedPrice





        Glide.with(this).load(intent.getStringExtra("OfferImage")).into(binding.offerImageInfo)
        var offer :String? = intent.getStringExtra("OfferLink")
        val subid :String? = intent.getStringExtra("subid")
        val subid2 :String? = intent.getStringExtra("subid2")
        val number :String? = intent.getStringExtra("number")
        val recordId :String? = intent.getStringExtra("recordId")
        val appName : String? = intent.getStringExtra("appName")
        val viewUri : String? = intent.getStringExtra("videoId")
        val offerLink = "$offer?&$subid=$recordId&$subid2=$number/"
        val noOfSteps :String?= intent.getStringExtra("noOfSteps")
        val regSMS = intent.getStringExtra("regSMS")
        //Initilizing the recylerview adapter
        val adapter = Instructions_RecylerViewAdapter()
        binding.instructionListInfo.adapter = adapter
        binding.instructionListInfo.layoutManager = LinearLayoutManager(this , LinearLayoutManager.VERTICAL , false)

        binding.backButtonInfo.setOnClickListener{
            val intent=Intent(this,Home::class.java)
            startActivity(intent)
        }
        //videoVIew
       /* val videoview= binding.videoView
        lifecycle.addObserver(videoview)

        videoview.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(viewUri!! , 0F)
            }
        })*/

        //Observing the getInstructionList() in info_viewmodel (ie which gets the data from info_FirebaseRepo)
        val info_viewModel : info_viewModel by viewModels()
        info_viewModel.getInstrutionList(offerId!!).observe(this , Observer {
            if (it.isNotEmpty()) {
                for (i in 0..noOfSteps!!.toInt()-1) {
                    if (i == 0) {
                        binding.stepperForm
                            .setup(
                                this,
                                installStep("Install"),
                            )
                            .allowNonLinearNavigation(true)
                            .displayBottomNavigation(false)
                            .init()
                    }
                    if (i == 1){
                        binding.stepperForm
                            .addStep(
                                1,
                                registerStep("Register"),
                            )
                    }
                    if (i == 2){
                        binding.stepperForm
                            .addStep(
                                2,
                                tradeStep("Trade"),
                            )
                    }
                }
            }
            lifecycleScope.launch {
                if ( isAppInstalled(this@Info , appName!!)) {
                    binding.stepperForm.markStepAsCompleted( 0  , true)
                }
                if(isRegistered(this@Info , appName , regSMS!!) ){
                    binding.stepperForm.markStepAsCompleted(1 , true)
                }


            }
        })





        //Redirecting user to chrome after the event of button accurs
        binding.offerLinkButtonInfo.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(offerLink))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            info_viewModel.updateOfferHistory(userData(recordId.toString() , number!!.toLong()),
                offerId, offerPrice.toString() , offerName = offerName!!)
            try {
                this.startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                // Chrome browser presumably not installed so allow user to choose instead
                intent.setPackage(null)
                this.startActivity(intent)
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

    override fun onCompletedForm() {
    }

    override fun onCancelledForm() {
    }

    override fun onStepAdded(index: Int, addedStep: Step<*>?) {
    }

    override fun onStepRemoved(index: Int) {
    }
}

suspend fun isAppInstalled(context: Context , appName: String): Boolean {
    // get list of all the apps installed
    // get list of all the apps installed
    val infos: List<ApplicationInfo> =
        context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA)
    // create a list with size of total number of apps
    // create a list with size of total number of apps
    val apps = arrayOfNulls<String>(infos.size)
    var i = 0
    // add all the app name in string list
    // add all the app name in string list
    for (info in infos) {
        apps[i] = info.packageName
        i++
    }
    // set all the apps name in list view
    // set all the apps name in list view
    // write total count of apps available.
    // write total count of apps available.
    if(apps.contains(appName)){
        return true
    }

    return false
}


suspend fun isRegistered(context: Context , appName : String , regSMS :String) : Boolean{
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
        if (smsBody.lowercase() .contains(appName, ignoreCase = true)) {
            // The message contains the search string
            messageFound = true
            break  // Exit the loop once a matching message is found
        }
    }

  return messageFound


// Now, 'inboxSms' contains a list of SMS message bodies from the inbox.

}