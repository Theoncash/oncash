package com.example.oncash.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oncash.Component.Offer_RecylerViewAdapter
import com.example.oncash.DataType.OfferList
import com.example.oncash.DataType.userData
import com.example.oncash.R
import com.example.oncash.ViewModel.home_viewModel
import com.example.oncash.ViewModel.offer_viewmodel
import com.example.oncash.databinding.FragmentWeeklyOffersBinding
import com.google.android.gms.common.util.DataUtils
import com.google.android.material.snackbar.Snackbar

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

        val homeViewmodel = activity.run{
            this?.let { ViewModelProvider(it).get(home_viewModel::class.java) }
        }
        lateinit var adapter:Offer_RecylerViewAdapter

        homeViewmodel!!.getuserData().observe(viewLifecycleOwner){
            userData = it
            val offerRecylerview: RecyclerView = view.findViewById(R.id.weeklyOffer_recylerview)
             adapter = Offer_RecylerViewAdapter(userData)
            offerRecylerview.adapter = adapter
            offerRecylerview.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        }

        homeViewmodel!!.getOfferList().observe(viewLifecycleOwner, Observer { OfferList ->
            if (OfferList.weeklyOffersList.isNotEmpty()) {
                this.OfferList = OfferList
                adapter.updateList(OfferList.weeklyOffersList)
            }
        })

        view.findViewById<Button>(R.id.weeklyButton).setOnClickListener {
            if (OfferList.weeklyOffersList.isNotEmpty()) {
                adapter.updateList(OfferList.weeklyOffersList)
            }
        }

        view.findViewById<Button>(R.id.monthlyButton).setOnClickListener {
            if (OfferList.monthlyOfferList.isNotEmpty()) {
                adapter.updateList(OfferList.monthlyOfferList)
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