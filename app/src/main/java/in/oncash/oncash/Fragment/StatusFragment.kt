package `in`.oncash.oncash.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.oncash.oncash.Component.leaderBoard_RecylerViewAdapter
import `in`.oncash.oncash.Component.referral_RecylerViewAdapter
import `in`.oncash.oncash.DataType.userData
import `in` .oncash.oncash.R
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import `in`.oncash.oncash.ViewModel.home_viewModel
import `in`.oncash.oncash.ViewModel.referral_viewModel
import `in`.oncash.oncash.databinding.FragmentInviteBinding
import `in`.oncash.oncash.databinding.FragmentStatusBinding
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StatusFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatusFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding : FragmentStatusBinding

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
        binding = FragmentStatusBinding.inflate(inflater , container , false)
        return binding.root      }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var referralViewmodel: referral_viewModel = activity.run{
            ViewModelProvider(this!!)[referral_viewModel::class.java]
        }
        var userData:userData = userData(0)
        try {
             userData = referralViewmodel.userData.value!!

        }catch (e:Exception){

        }
        val referral: RecyclerView = view.findViewById(`in`.oncash.oncash.R.id.status_recyclerview)
        val referral_adapter = referral_RecylerViewAdapter(  )
        referral.adapter = referral_adapter
        referral.layoutManager = LinearLayoutManager( view.context , LinearLayoutManager.VERTICAL, false)

        lifecycleScope.launch {
            UserInfo_Airtable_Repo().getRefferals(userData.userNumber).observe(viewLifecycleOwner){
                if (it.Referral_users != null ){
                    referral_adapter.updateList(it.Referral_users)
                }
                binding.totalBal.text = it.Total_Referral_amt.toString()
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
         * @return A new instance of fragment StatusFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StatusFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}