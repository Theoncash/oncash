package `in`.oncash.oncash.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.oncash.oncash.Component.withdrawalTransaction_RecylerViewAdapter
import `in`.oncash.oncash.DataType.withdrawalTransaction
import `in`.oncash.oncash.ViewModel.home_viewModel
import `in`.oncash.oncash.ViewModel.wallet_viewModel
import `in`.oncash.oncash.databinding.FragmentRedeemBinding
import com.google.android.material.snackbar.Snackbar
import `in`.oncash.oncash.Component.customLoadingDialog
import `in`.oncash.oncash.R
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewmodel = activity.run{
            ViewModelProvider(this!!).get(home_viewModel::class.java)
        }



            homeViewmodel.getuserData().observe(viewLifecycleOwner){
                userNumber = it.userNumber
                homeViewmodel.withdrawalTransaction(userNumber)

            }
        lifecycleScope.launch {
            withContext(
                Dispatchers.IO
            ){
                walletBalance =   UserInfo_Airtable_Repo().getWallet(userNumber) .currentBal

                    val formattedBalance = NumberFormat.getCurrencyInstance().apply {
                        currency = Currency.getInstance("INR")
                    }.format(walletBalance)

                    binding.walletBala.text = formattedBalance
                }
            }



            binding.withdrawalTransaction.adapter = adapter
            binding.withdrawalTransaction.layoutManager = LinearLayoutManager(view.context , LinearLayoutManager.VERTICAL ,false)
        homeViewmodel.getWithdrawalTransaction().observe(viewLifecycleOwner){
            adapter.updateList(it)
        }
            lifecycleScope.launch { getTransaction() }

        binding.withdrawButton.setOnClickListener {
        binding.withdrawButton.isClickable = false
            if (walletBalance > 20 ) {
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
                                walletBalance,
                                walletBalance,
                            )
                            viewModel.getWithdrawalRequest().observe(viewLifecycleOwner) { status ->
                                if (status.response == "201") {
                                    // viewModel.getWallet(userRecordId)
                                    //viewModel.getWalletPrice().observe(this, Observer { wallet ->

                                    //    walletBalance = wallet
                                    binding.walletBala.text = 0.toString()

                                    withdrawalList.add(status.withdrawalTransaction)
                                    adapter.updateList(withdrawalList)
                                    binding.withdrawButton.isClickable = true
                                    walletBalance = 0

                                    val channelId = "ONCASH_CANNEL"
                                    val channelName = "ONCASH"
                                    val notificationManager =  NotificationManagerCompat.from(view.context)

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                                        notificationManager.createNotificationChannel(channel)
                                    }
                                    val notificationId = 1 // Unique ID for the notification

                                    val notificationBuilder = NotificationCompat.Builder(view.context, channelId)
                                        .setSmallIcon(R.drawable.oncash)
                                        .setContentTitle("Withdraw Successfully")
                                        .setContentText("Good job! You just earned ${status.withdrawalTransaction.WithdrawalAmount} with OnCash. Keep it up and watch your earnings grow. ")
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setAutoCancel(true) // Removes the notification when tapped


                                    if (ActivityCompat.checkSelfPermission(
                                            view.context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                    }
                                    notificationManager.notify(notificationId, notificationBuilder.build())


                                    Snackbar.make(
                                        binding.root,
                                        "Withdraw Successful",
                                        Snackbar.LENGTH_LONG
                                    ).show()

                                }

                            }

                        } else {
                binding.withdrawButton.isClickable = true

                Snackbar.make(
                                binding.root,
                                "Requested Amount Should Be More Then 20 Rs",
                                Snackbar.LENGTH_SHORT
                            ).show()
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