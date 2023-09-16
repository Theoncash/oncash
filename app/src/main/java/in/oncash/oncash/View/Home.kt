package `in`.oncash.oncash.View

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.AppOpsManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import com.google.firebase.FirebaseApp

import `in`.oncash.oncash.DataType.OfferList
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.R
import `in`.oncash.oncash.RoomDb.User
import `in`.oncash.oncash.RoomDb.userDb
import `in`.oncash.oncash.ViewModel.home_viewModel
import `in`.oncash.oncash.databinding.ActivityHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Home : AppCompatActivity() {
     lateinit var binding: ActivityHomeBinding

    val homeViewmodel: home_viewModel by viewModels()
    lateinit var OfferList : OfferList
    private  var userData: userData = userData(0)
    lateinit var roomDb:userDb



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (!isNetworkConnected(this)) {
//            setContentView(R.layout.no_internet) // Load the layout for no internet
//
//        }else{
//            setContentView(R.layout.activity_home) // Load the layout for no internet

            val REQUEST_SMS_PERMISSION = 734973 // Use any unique integer value


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), REQUEST_SMS_PERMISSION)
                }
            }
            if (!hasUsageStatsPermission()) {
                requestUsageStatsPermission()
            }


            lifecycleScope.launch {
                roomDb = Room.databaseBuilder(
                    applicationContext,
                    userDb::class.java,
                    "User"
                ).build()

                withContext(Dispatchers.IO)
                {
                    if (roomDb.userQuery().getUserId().isNullOrEmpty()) {
                        withContext(Dispatchers.Main){
                            getUserData()
                        }

                    } else {
                        userData.userNumber = roomDb.userQuery().getUserNumber()
                        homeViewmodel.setUserData(userData)
                        homeViewmodel.withdrawalTransaction(userData.userNumber)
                        homeViewmodel.getOffersHistory(userData.userNumber)
                        homeViewmodel.getWallet(userData.userNumber)
                    }

                }
            }
//        lifecycleScope.launch {
//            getUserData()
//        }
            FirebaseApp.initializeApp(this)
            binding = ActivityHomeBinding.inflate(layoutInflater)
            setContentView(binding.root)



            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController
            binding.bottomNavigation.setOnItemSelectedListener {
                if(it.itemId == R.id.home)
                {
                    if(navController.currentDestination!!.id==R.id.monthlyOffers){
                        navController.navigate(R.id.action_monthlyOffers_to_weeklyOffers)
                    }
                    if(navController.currentDestination!!.id==R.id.redeem2){
                        navController.navigate(R.id.action_redeem2_to_weeklyOffers)
                    }
                    if(navController.currentDestination!!.id==R.id.profile2){
                        navController.navigate(R.id.action_profile2_to_weeklyOffers)
                    }
                }
                if (it.itemId == R.id.history) {
                    if (navController.currentDestination!!.id == R.id.weeklyOffers) {
                        navController.navigate(R.id.action_weeklyOffers_to_monthlyOffers)
                    }
                    if (navController.currentDestination!!.id == R.id.redeem2) {
                        navController.navigate(R.id.action_redeem2_to_monthlyOffers)
                    }
                    if(navController.currentDestination!!.id==R.id.profile2){
                        navController.navigate(R.id.action_profile2_to_monthlyOffers)
                    }
                }
                if(it.itemId == R.id.redeem){
                    if (navController.currentDestination!!.id == R.id.weeklyOffers) {
                        navController.navigate(R.id.action_weeklyOffers_to_redeem2)
                    }
                    if (navController.currentDestination!!.id == R.id.monthlyOffers) {
                        navController.navigate(R.id.action_monthlyOffers_to_redeem2)
                    }
                    if(navController.currentDestination!!.id == R.id.profile2){
                        navController.navigate(R.id.action_profile2_to_redeem2)
                    }
                }
                if(it.itemId == R.id.profile){
                    if (navController.currentDestination!!.id == R.id.weeklyOffers) {
                        navController.navigate(R.id.action_weeklyOffers_to_profile2)
                    }
                    if (navController.currentDestination!!.id == R.id.monthlyOffers) {
                        navController.navigate(R.id.action_monthlyOffers_to_profile2)
                    }
                    if (navController.currentDestination!!.id == R.id.redeem2) {
                        navController.navigate(R.id.action_redeem2_to_profile2)
                    }
                }
                true
            }


            binding.walletTextView.setOnClickListener {
                startActivity(
                    Intent(this, Wallet::class.java).putExtra(
                        "walletBalance",
                        binding.walletTextView.text
                    ) .putExtra("userNumber", userData.userNumber.toString()).putExtra("userRecordId", userData.userNumber))

            }
        }





    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            homeViewmodel.getOfferList()
        }


    }
    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
    private fun hasUsageStatsPermission(): Boolean {
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = AppOpsManagerCompat.noteOpNoThrow(
            this, AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }
    private fun getUserData() {
        homeViewmodel.getUserData(this)
        homeViewmodel.getuserData().observe(this, Observer { data ->
            userData = data!!
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    roomDb.userQuery().addUser(user = User(userData.userNumber))
                }
            }
            homeViewmodel.withdrawalTransaction(data.userNumber)
            homeViewmodel.getOffersHistory(data.userNumber)
            homeViewmodel.getWallet(userData.userNumber)

        })

        homeViewmodel.getWalletPrice().observe(this, Observer { wallet ->
            binding.walletTextView.text = wallet.currentBal.toString()
        })
    }
    @Deprecated("Deprecated in Java", ReplaceWith("this.finish()"))
    override fun onBackPressed() {
       this.finish()
    }
}
