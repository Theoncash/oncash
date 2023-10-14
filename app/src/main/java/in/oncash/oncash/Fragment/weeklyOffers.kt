package `in`.oncash.oncash.Fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import `in`.oncash.oncash.Component.Offer_RecylerViewAdapter
import `in`.oncash.oncash.DataType.Offer
import `in`.oncash.oncash.DataType.OfferList
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields
import `in`.oncash.oncash.DataType.Step
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.R
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import `in`.oncash.oncash.RoomDb.TimerDb
import `in`.oncash.oncash.View.LeaderBoard
import `in`.oncash.oncash.View.ReferalActivity
import `in`.oncash.oncash.View.isAppInstalled
import `in`.oncash.oncash.View.isRegistered
import `in`.oncash.oncash.ViewModel.home_viewModel
import `in`.oncash.oncash.ViewModel.offer_viewmodel
import `in`.oncash.oncash.databinding.FragmentWeeklyOffersBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [weeklyOffers.newInstance] factory method to
 * create an instance of this fragment.
 */
class weeklyOffers : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var userData: userData  = userData(0)
    lateinit var binding : FragmentWeeklyOffersBinding
    val offerViewModel: offer_viewmodel by viewModels()
    lateinit var OfferList : OfferList
     var endTime :Long = 0L
    val calendar = Calendar.getInstance().timeInMillis
    var offer = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentWeeklyOffersBinding.inflate(inflater , container, false)
        return binding.root
    }
    fun isAppInstalled(context: Context, appName: String): Boolean {
        // get list of all the apps installed
        // get list of all the apps installed
        val pm: PackageManager = context.packageManager
        try {
            val packageInfo = pm.getInstalledApplications(0)

            for(app in packageInfo){
                if(app.packageName == appName){
                    return true
                }
            }

        } catch (e: PackageManager.NameNotFoundException) {
            // The app is not installed.
            Toast.makeText(context , "packageInfo.applicationInfo.packageName" , Toast.LENGTH_LONG).show()

            return false
        }
        return false
    }


    suspend fun isRegistered(context: Context, appName : String, regSMS :String) : Boolean{
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

            if (smsBody.lowercase() .contains(regSMS.toString().lowercase(), ignoreCase = true)) {
                // The message contains the search string
                messageFound = true
                break  // Exit the loop once a matching message is found
            }
        }

        return messageFound


// Now, 'inboxSms' contains a list of SMS message bodies from the inbox.

    }
    fun getTimeSpent(targetPackageName : String):Int{
        val targetPackageName = targetPackageName // Replace with your app's package name
        val usageStatsManager =  requireActivity().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
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
                Toast.makeText(requireContext(), usageStats.packageName , Toast.LENGTH_LONG).show()
                val timeInMilis  = usageStats.totalTimeInForeground
                timeSpentInMin = (timeInMilis / 60000).toInt()
                Toast.makeText(requireContext() , timeSpentInMin.toString() , Toast.LENGTH_LONG).show()
                // Convert timeSpentInMillis to hours, minutes, seconds, etc. as needed.
                break
            }
        }
        return timeSpentInMin
    }

    fun isOfferCompleted(appName:String , regSMS:String):Boolean{
        var isOfferCompleted = false
        lifecycleScope.launch {
            if (isAppInstalled(requireContext(), appName!!)) {
                if (isRegistered(
                        requireContext(),
                        appName,
                        regSMS!!
                    )
                ) {

                    if (getTimeSpent(appName) >= 0) {
                        isOfferCompleted = true
                    }
                }

            }

        }
        return isOfferCompleted
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeViewmodel = activity.run{
            this?.let { ViewModelProvider(it).get(home_viewModel::class.java) }
        }
         var adapter:Offer_RecylerViewAdapter = Offer_RecylerViewAdapter(userData)

        homeViewmodel!!.getUserData(view.context)
        homeViewmodel!!.getuserData().observe(viewLifecycleOwner){
            userData = it
            val offerRecylerview: RecyclerView = view.findViewById(R.id.weeklyOffer_recylerview)
             adapter = Offer_RecylerViewAdapter(userData )
            offerRecylerview.adapter = adapter
            offerRecylerview.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        }
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                homeViewmodel.getOffersHistory(userData.userNumber)
            }
        }

        @SuppressLint("MissingInflatedId")
        fun showRewardCollectionDialog(offer: Offer) {
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.reward_collection_dialog, null)
            builder.setView(dialogView)
            val rewardButton = dialogView.findViewById<Button>(R.id.btnCollectReward)
            val rewardText = dialogView.findViewById<TextView>(R.id.textCollectReward)
            val alertDialog = builder.create()
            rewardText.text = "Congratulations! on completing ${offer.Name} . You've won a reward! of Rs.${offer.Price} "
            alertDialog.show()

            rewardButton.setOnClickListener {
                // Handle the reward collection logic here
                // For example, grant the reward and dismiss the dialog
                // You can also dismiss the dialog without granting the reward if the user cancels

                val soundPool = SoundPool.Builder().setMaxStreams(1).build()
                val soundID = soundPool.load(requireContext(), R.raw.water_drop, 1)
                soundPool.play(soundID, 1f, 1f, 1, 0, 1f)
                val total_bal = home_viewModel().totalOffers.value

                lifecycleScope.launch {
                    try {
                        UserInfo_Airtable_Repo().updateCompletedOffer(
                            userData.userNumber!!.toLong(), total_bal!!,
                            userData.userNumber.toInt(),
                            userData(userData.userNumber.toLong()),
                            offer.OfferId!!.toInt(), offer.Price.toString(), "Completed"
                        )
                        alertDialog.dismiss()

                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Some Error Occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }

        val offerList : ArrayList<Offer> = ArrayList()
        homeViewmodel.getOfferList().observe(viewLifecycleOwner) { OfferList ->
            offerList.clear()
            offerList.addAll(OfferList.weeklyOffersList)
            if (OfferList.weeklyOffersList.isNotEmpty()) {
                homeViewmodel.getOfferHistoryList().observe(viewLifecycleOwner) {
                    if(!homeViewmodel.checkingCompleted){
                        for (offer in offerList) {
                            Log.i("offertesting" , "OfferName" + offer.Name.toString() )

//                            val isCompleted: Boolean = isCompleted(it, offer)
                             lifecycleScope.launch {
                                 UserInfo_Airtable_Repo().isCompleted( userData.userNumber ,  offer.OfferId!!.toInt() ).observe(viewLifecycleOwner){
                                     var isCompleted :Boolean = false
                                     if(it == "Completed"){
                                         isCompleted = true
                                     }
                                     Log.i("offertesting" , "Output" + isCompleted.toString() )

                                     if (!isCompleted) {
                                         if (isOfferCompleted(offer.appName!!, offer.regSMS!!)) {
                                             Log.i("offertesting" , "Reward Output" + true.toString())

                                             showRewardCollectionDialog(offer)
                                         }
                                     }
                                 }


                             }

                        }
                    }

                    homeViewmodel.checkingCompleted = true

                    this.OfferList = OfferList
                    adapter.updateList(OfferList.weeklyOffersList, offer, it)


                }

            }

            homeViewmodel.getOfferHistoryList().observe(viewLifecycleOwner) {
                var totalOffers = OfferList.weeklyOffersList.size + OfferList.monthlyOfferList.size
                var completedOffers = it.size
                homeViewmodel.setProgressBar(completedOffers, totalOffers)
            }
        }

        val phone = homeViewmodel.getuserData().value ?: userData(0)
        binding.continueBut.setOnClickListener{
            startActivity(Intent(requireActivity().application, ReferalActivity::class.java).putExtra("number" , userData.userNumber ))
        }






    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment weeklyOffers.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            weeklyOffers().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
private fun formatTime(millis: Long): String {
    val days = TimeUnit.MILLISECONDS.toDays(millis)
    val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

    return String.format("%02d Day : %02d H : %02d M : %02d S", days, hours, minutes, seconds)
}

 fun isCompleted(offerList :ArrayList<Fields> , offer:Offer):Boolean{
    var bool : Boolean = false
    for(offers in offerList){
        Log.i("offertesting" , offers.toString()  +  offer.OfferId.toString())
        if( offers.Status == "Completed" && offers.OfferId.toString() == offer.OfferId!!)
        {
            Log.i("offertesting" , offers.Status.toString())

            bool = true
            break;
        }
    }
    return bool
}



