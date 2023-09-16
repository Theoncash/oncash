package `in`.oncash.oncash.View

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import `in`.oncash.oncash.DataType.OfferList
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.R
import `in`.oncash.oncash.RoomDb.User
import `in`.oncash.oncash.RoomDb.userDb
import `in`.oncash.oncash.ViewModel.home_viewModel
import `in`.oncash.oncash.databinding.ActivityHomeBinding
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Home : AppCompatActivity() {
     lateinit var binding: ActivityHomeBinding

    val homeViewmodel: home_viewModel by viewModels()
    lateinit var OfferList : OfferList
    private  var userData: userData = userData("",0)
    lateinit var roomDb:userDb



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val REQUEST_SMS_PERMISSION = 734973 // Use any unique integer value

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), REQUEST_SMS_PERMISSION)
            }
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
                    userData.userRecordId = roomDb.userQuery().getUserId()
                    homeViewmodel.setUserData(userData)
                    homeViewmodel.withdrawalTransaction(userData.userNumber)
                    homeViewmodel.getOffersHistory(userData.userRecordId)
                    homeViewmodel.getWallet(userData.userRecordId)
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
                ) .putExtra("userNumber", userData.userNumber.toString()).putExtra("userRecordId", userData.userRecordId))

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

    private fun getUserData() {
        homeViewmodel.getUserData(this)
        homeViewmodel.getuserData().observe(this, Observer { data ->
            userData = data!!
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    roomDb.userQuery().addUser(user = User(userData.userNumber , userData.userRecordId))
                }
            }
            homeViewmodel.withdrawalTransaction(data.userNumber)
            homeViewmodel.getOffersHistory(data.userRecordId)
            homeViewmodel.getWallet(userData.userRecordId)

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
