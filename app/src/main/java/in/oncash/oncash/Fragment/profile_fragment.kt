package `in`.oncash.oncash.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.R
import `in`.oncash.oncash.View.LeaderBoard
import `in`.oncash.oncash.View.ReferalActivity
import `in`.oncash.oncash.ViewModel.home_viewModel
import `in`.oncash.oncash.databinding.FragmentProfileFragmentBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [profile_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class profile_fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
     lateinit var binding : FragmentProfileFragmentBinding

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
        binding = FragmentProfileFragmentBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var homeViewmodel: home_viewModel = activity.run{
            ViewModelProvider(this!!)[home_viewModel::class.java]
        }
        try {
            binding.currentBalance.text = homeViewmodel.getWalletPrice().value!!.currentBal.toString()
           /* binding.totalBalance.text = homeViewmodel.getWalletPrice().value!!.totalBa.toString()*/
        }catch (e: Exception){
        }
        val phone = homeViewmodel.getuserData().value ?: userData(0)
        binding.LeaderBoardText.setOnClickListener {
            startActivity(Intent(requireActivity().application, LeaderBoard::class.java).putExtra("userPhone" , phone.userNumber))
        }

        binding.v.setOnClickListener {
            startActivity(Intent(requireActivity().application, LeaderBoard::class.java).putExtra("userPhone" , phone.userNumber))
        }
        binding.v2.setOnClickListener {
            startActivity(Intent(requireActivity().application, ReferalActivity::class.java).putExtra("number" , phone.userNumber ))
        }
        binding.referalIcon.setOnClickListener {
            startActivity(Intent(requireActivity().application, ReferalActivity::class.java).putExtra("number" , phone.userNumber ))
        }

        binding.ReferalText.setOnClickListener {
            startActivity(Intent(requireActivity().application, ReferalActivity::class.java).putExtra("number" , phone.userNumber ))
        }

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment profile_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            profile_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}