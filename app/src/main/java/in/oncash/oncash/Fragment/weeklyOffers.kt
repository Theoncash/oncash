package `in`.oncash.oncash.Fragment

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import `in`.oncash.oncash.Component.Offer_RecylerViewAdapter
import `in`.oncash.oncash.DataType.OfferList
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.R
import `in`.oncash.oncash.RoomDb.TimerDb
import `in`.oncash.oncash.ViewModel.home_viewModel
import `in`.oncash.oncash.ViewModel.offer_viewmodel
import `in`.oncash.oncash.databinding.FragmentWeeklyOffersBinding
import com.google.android.material.snackbar.Snackbar
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
    var userData: userData  = userData("",0)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            var roomDb = Room.databaseBuilder(
                view.context,
                TimerDb::class.java,
                "Timer"
            ).build()

            withContext(Dispatchers.IO)
            {
                endTime = roomDb.TimerQuery().getEndTime()

                val timerTime = endTime - calendar

                withContext(Dispatchers.Main) {

                    object : CountDownTimer( timerTime, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            // Update the UI for the current item with the remaining time
                            val formattedTime = formatTime(millisUntilFinished)
                            binding.countdownTextView.text = " $formattedTime"
                        }

                        override fun onFinish() {
                            binding.countdownTextView.text = "Cashback: 30% - Offer Ended"
                            offer = 70
                        }
                    }.start()
                }
            }

        }









        val homeViewmodel = activity.run{
            this?.let { ViewModelProvider(it).get(home_viewModel::class.java) }
        }
        lateinit var adapter:Offer_RecylerViewAdapter


        homeViewmodel!!.getuserData().observe(viewLifecycleOwner){
            userData = it
            val offerRecylerview: RecyclerView = view.findViewById(R.id.weeklyOffer_recylerview)
             adapter = Offer_RecylerViewAdapter(userData )
            offerRecylerview.adapter = adapter
            offerRecylerview.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        }

        homeViewmodel.getOfferList().observe(viewLifecycleOwner, Observer { OfferList ->
            if (OfferList.weeklyOffersList.isNotEmpty()) {
                this.OfferList = OfferList
                adapter.updateList(OfferList.weeklyOffersList , offer )
            }
            homeViewmodel.getOfferHistoryList().observe(viewLifecycleOwner){
               var totalOffers =  OfferList.weeklyOffersList.size+OfferList.monthlyOfferList.size
               var completedOffers = it.size
               homeViewmodel.setProgressBar(completedOffers , totalOffers)
            }
        })

        view.findViewById<Button>(R.id.weeklyButton).setOnClickListener {
            if (OfferList.weeklyOffersList.isNotEmpty()) {
                adapter.updateList(OfferList.weeklyOffersList , offer)
            }
        }

        view.findViewById<Button>(R.id.monthlyButton).setOnClickListener {
            if (OfferList.monthlyOfferList.isNotEmpty()) {
                adapter.updateList(OfferList.monthlyOfferList , offer)
            }else{
                Snackbar.make(view , "No Monthly Offers Available " , Snackbar.LENGTH_LONG).show()
            }
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



