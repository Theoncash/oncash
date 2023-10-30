package `in`.oncash.oncash.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import `in`.oncash.oncash.Component.OfferHistory_RecylerViewAdapter
import `in`.oncash.oncash.R
import `in`.oncash.oncash.ViewModel.home_viewModel
import `in`.oncash.oncash.ViewModel.offer_history_viewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [monthlyOffers.newInstance] factory method to
 * create an instance of this fragment.
 */
class monthlyOffers : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val viewModel : offer_history_viewModel by viewModels()
    lateinit var homeViewmodel : home_viewModel

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
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = OfferHistory_RecylerViewAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.OfferHistory_recylerview)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(view.context , LinearLayoutManager.VERTICAL ,false)
        recyclerView.layoutManager = layoutManager

        homeViewmodel = activity?.run {
            ViewModelProvider(this)[home_viewModel::class.java]
        }!!

        homeViewmodel.getOfferHistoryList().observe(viewLifecycleOwner) { offerhistory ->
            val offer = homeViewmodel.getOffer()
            Log.i("offerHistory" , offerhistory.toString())
      try{
          adapter.updateList(offerhistory , offer!! )
      }catch (e: NullPointerException){
          Snackbar.make(view, "Loading..",Snackbar.LENGTH_LONG).show()
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
         * @return A new instance of fragment monthlyOffers.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            monthlyOffers().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}