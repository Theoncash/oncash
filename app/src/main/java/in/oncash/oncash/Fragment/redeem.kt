package `in`.oncash.oncash.Fragment

import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.oncash.oncash.Component.withdrawalTransaction_RecylerViewAdapter
import `in`.oncash.oncash.DataType.withdrawalTransaction
import `in`.oncash.oncash.ViewModel.home_viewModel
import `in`.oncash.oncash.ViewModel.wallet_viewModel
import `in`.oncash.oncash.databinding.FragmentRedeemBinding
import com.google.android.material.snackbar.Snackbar
import `in`.oncash.oncash.Component.customLoadingDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [redeem.newInstance] factory method to
 * create an instance of this fragment.
 */
class redeem : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var  binding : FragmentRedeemBinding
    private var userNumber: Long = 0
    private var userRecordId: String? = null
    var walletBalance = 0
    private val viewModel: wallet_viewModel by viewModels()
    lateinit var homeViewmodel :home_viewModel
    private val adapter = withdrawalTransaction_RecylerViewAdapter()
    private var withdrawalList :ArrayList<withdrawalTransaction> = ArrayList()
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
        binding = FragmentRedeemBinding.inflate(inflater , container , false )
        return  binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lateinit var userRecordId :String
        homeViewmodel = activity.run{
            ViewModelProvider(this!!).get(home_viewModel::class.java)
        }

        homeViewmodel.getWalletPrice().observe(viewLifecycleOwner) { walletInfo ->
            val walletBalance = walletInfo.currentBal

            val formattedBalance = NumberFormat.getCurrencyInstance().apply {
                currency = Currency.getInstance("INR")
            }.format(walletBalance)

            binding.walletBala.text = formattedBalance
        }


        homeViewmodel.getuserData().observe(viewLifecycleOwner){
                userRecordId = it.userRecordId
                userNumber = it.userNumber
            }

            binding.withdrawalTransaction.adapter = adapter
            binding.withdrawalTransaction.layoutManager = LinearLayoutManager(view.context , LinearLayoutManager.VERTICAL ,false)

            lifecycleScope.launch { getTransaction() }

            binding.withdrawButton.setOnClickListener {
                val requestAmount = binding.walletBala.text.toString()
                if (requestAmount.isNotEmpty()) {
                    if (walletBalance.toInt() >= requestAmount.toInt()) {
                        if (requestAmount.toInt() > 20) {
                                val loadingDialog = customLoadingDialog(view.context)

// To show the dialog
                                loadingDialog.show()
                                loadingDialog.setMessage("Loading data...")

// Simulate some background work (replace this with your actual work)
                                Handler().postDelayed({
                                    // Dismiss the dialog when the work is done
                                    loadingDialog.dismiss()
                                }, 3000) // Replace 3000 with the actual duration of your background work



                            viewModel.withdrawRequest(
                                userNumber,
                                requestAmount.toInt(),
                                walletBalance,
                                userRecordId  ,
                            )
                            viewModel.getWithdrawalRequest().observe(viewLifecycleOwner) { status ->
                                if (status.response.contains("200")) {
                                    // viewModel.getWallet(userRecordId)
                                    //viewModel.getWalletPrice().observe(this, Observer { wallet ->

                                    //    walletBalance = wallet
                                    walletBalance -= status.withdrawalTransaction.WithdrawalAmount.toInt()
                                    binding.walletBala.text = walletBalance.toString()
                                    binding.walletBala.editableText.clear()

                                    withdrawalList.add(status.withdrawalTransaction)
                                    adapter.updateList(withdrawalList)

                                    Snackbar.make(
                                        binding.root,
                                        "Withdraw Successful",
                                        Snackbar.LENGTH_LONG
                                    ).show()

                                }

                            }

                        } else {
                            Snackbar.make(
                                binding.root,
                                "Requested Amount Should Be More Then 20 Rs",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Insufficient balance",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Snackbar.make(binding.root, "Please enter withdraw amount", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }


        private  fun getTransaction(){
            homeViewmodel.getWithdrawalTransaction().observe(viewLifecycleOwner , Observer { withdrawalTransaction ->
                withdrawalList = withdrawalTransaction
                adapter.updateList(withdrawalTransaction)
            })
        }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment redeem.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            redeem().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}