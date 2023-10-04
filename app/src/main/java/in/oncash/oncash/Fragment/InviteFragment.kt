package `in`.oncash.oncash.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import `in`.oncash.oncash.R
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import `in`.oncash.oncash.ViewModel.referral_viewModel
import `in`.oncash.oncash.databinding.FragmentInviteBinding
import `in`.oncash.oncash.databinding.FragmentProfileFragmentBinding
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InviteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InviteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var binding : FragmentInviteBinding

    private var param1: String? = null
    private var param2: String? = null

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
        // Inflate the layout for this fragment
        binding = FragmentInviteBinding.inflate(inflater , container , false)
        return binding.root    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var referralViewmodel: referral_viewModel = activity.run{
            ViewModelProvider(this!!)[referral_viewModel::class.java]
        }
        val userId = referralViewmodel.userData.value

        lifecycleScope.launch {
            UserInfo_Airtable_Repo().getReferralCode(userId!!.userNumber).observe(viewLifecycleOwner){

                binding.referalCode.text =  "Referral code : $it"
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
         * @return A new instance of fragment InviteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InviteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}