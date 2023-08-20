package `in`.oncash.oncash.View

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import `in`.oncash.oncash.Component.Instructions_RecylerViewAdapter
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.Repository.offer_AirtableDatabase
import `in`.oncash.oncash.ViewModel.info_viewModel
import `in`.oncash.oncash.databinding.ActivityInfoBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener


class Info : AppCompatActivity() {
     lateinit var binding : ActivityInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Getting Data from the intent form home(Activity)
        val offerId : String? = intent.getStringExtra("OfferId")
        val offerName= intent.getStringExtra("OfferName")
        binding.offernameInfo.text = offerName
        var offerPrice= intent.getStringExtra("OfferPrice")
        binding.offerPrice.text = offerPrice
        Glide.with(this).load(intent.getStringExtra("OfferImage")).into(binding.offerImageInfo)
        var offer :String? = intent.getStringExtra("OfferLink")
        val subid :String? = intent.getStringExtra("subid")
        val subid2 :String? = intent.getStringExtra("subid2")
        val number :String? = intent.getStringExtra("number")
        val recordId :String? = intent.getStringExtra("recordId")
        val viewUri : String? = intent.getStringExtra("videoId")
        val offerLink = "$offer?&$subid=$recordId&$subid2=$number/"


        //Initilizing the recylerview adapter
        val adapter = Instructions_RecylerViewAdapter()
        binding.instructionListInfo.adapter = adapter
        binding.instructionListInfo.layoutManager = LinearLayoutManager(this , LinearLayoutManager.VERTICAL , false)

        binding.backButtonInfo.setOnClickListener{
            val intent=Intent(this,Home::class.java)
            startActivity(intent)
        }
        //videoVIew
        val videoview= binding.videoView
        lifecycle.addObserver(videoview)

        videoview.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(viewUri!! , 0F)
            }
        })

        //Observing the getInstructionList() in info_viewmodel (ie which gets the data from info_FirebaseRepo)
        val info_viewModel : info_viewModel by viewModels()

        info_viewModel.getInstrutionList(offerId!!).observe(this , Observer {
            if (it.isNotEmpty()){
                Toast.makeText(this , it.size.toString() , Toast.LENGTH_LONG).show()
                adapter.updateList(it)
            }
        })


        //Redirecting user to chrome after the event of button accurs
        binding.offerLinkButtonInfo.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(offerLink))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            Toast.makeText(this , recordId.toString() + " user id", Toast.LENGTH_LONG).show()
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

        binding.YoutubeViewExpandable.setOnClickListener {
            val hiddenView = binding.videoView
            val cardView =binding.YoutubeViewExpandable
            if (hiddenView.visibility == View.VISIBLE){

                hiddenView.visibility = View.GONE

                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            }else {
                hiddenView.visibility = View.VISIBLE

                TransitionManager.beginDelayedTransition(cardView, AutoTransition())

            }
        }
    }
}