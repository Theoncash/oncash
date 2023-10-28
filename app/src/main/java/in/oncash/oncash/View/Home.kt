package `in`.oncash.oncash.View

import android.Manifest
import android.app.AppOpsManager
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.SoundPool
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.AppOpsManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import `in`.oncash.oncash.Component.checkRegistration
import `in`.oncash.oncash.DataType.OfferList
import `in`.oncash.oncash.DataType.SerializedDataType.Version
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.R
import `in`.oncash.oncash.RoomDb.OfferDb
import `in`.oncash.oncash.RoomDb.userDb
import `in`.oncash.oncash.ViewModel.home_viewModel
import `in`.oncash.oncash.databinding.ActivityHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Home : AppCompatActivity() {
    private val PERMISSION_REQUEST_CODE = 123

    lateinit var binding: ActivityHomeBinding
    val homeViewmodel: home_viewModel by viewModels()
    lateinit var OfferList : OfferList
    private  var userData: userData = userData(0)
    lateinit var roomDb:userDb
    var needToUpdate = false
    private val version : Double = 1.2
    val  SMS_PERMISSION_REQUEST_CODE = 734973;

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         soundPool = SoundPool.Builder().setMaxStreams(1).build()
        val notificationPermission = Manifest.permission.POST_NOTIFICATIONS
         val REQUEST_NOTIFICATION_PERMISSION = 4343242 // Use any unique integer value
        checkAndRequestPermissions()
        if (ContextCompat.checkSelfPermission(this, notificationPermission) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,  // Pass your activity reference
                arrayOf(notificationPermission),
                REQUEST_NOTIFICATION_PERMISSION
            )
        } else {
            // Permission is already granted, you can proceed to show notifications
            // You may call your notification code here
        }
         soundID = soundPool.load(this, R.raw.water_drop, 1)
//        if (!isNetworkConnected(this)) {
//            setContentView(R.layout.no_internet) // Load the layout for no internet
//
//        }else{
//            setContentView(R.layout.activity_home) // Load the layout for no internet

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), SMS_PERMISSION_REQUEST_CODE)
        }


            homeViewmodel.getVersion().observe(this){
                if(it.id > version){
                    needToUpdate = true
                    showCustomDialog(it)
//                   startActivity( Intent(this , update::class.java) )
                }else{

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
                        withContext(Dispatchers.IO)
                        {
                            roomDb = Room.databaseBuilder(
                                applicationContext,
                                userDb::class.java,
                                "User"
                            ).fallbackToDestructiveMigration() // Add this line for destructive migration
                                .build()
                            userData.userNumber = roomDb.userQuery().getUserNumber()
                        }

                        if(userData.userNumber.toInt() == 0){
                            getUserData()
                        }else{
                            homeViewmodel.setUserData(userData)
                            homeViewmodel.withdrawalTransaction(userData.userNumber)
                            homeViewmodel.getOffersHistory(userData.userNumber)
                            homeViewmodel.getWallet(userData.userNumber)
                        }
                    }

//        lifecycleScope.launch {
//            getUserData()
//        }
                    FirebaseMessaging.getInstance().isAutoInitEnabled = true
                    FirebaseMessaging.getInstance().subscribeToTopic("Referral")
                        .addOnCompleteListener(object : OnCompleteListener<Void> {
                            override fun onComplete(task: Task<Void>) {
                                if (task.isSuccessful) {
                                    // User is successfully subscribed to the topic
                                    // You can handle success here, e.g., show a success message
                                    Log.i("FirebaseData" , "Subscribed to 'new_entries' topic successfully.")
                                } else {
                                    // Subscription failed
                                    // You can handle the failure here, e.g., show an error message
                                    Log.i("FirebaseData" ,"Failed to subscribe to 'new_entries' topic: ${task.exception?.message}")
                                }
                            }
                        })
                    FirebaseApp.initializeApp(this)
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        val token = task.result

                        // Log and toast
                    })
                    binding = ActivityHomeBinding.inflate(layoutInflater)
                    setContentView(binding.root)



                    val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                    val navController = navHostFragment.navController
                    binding.bottomNavigation.setOnItemSelectedListener {
                        // Play sound
                        soundPool.play(soundID, 1f, 1f, 1, 0, 1f)

                        // Handle item click logic here
                        if (it.itemId == R.id.home) {
                            if (navController.currentDestination!!.id == R.id.monthlyOffers) {
                                navController.navigate(R.id.action_monthlyOffers_to_weeklyOffers)
                            }
                            if (navController.currentDestination!!.id == R.id.redeem2) {
                                navController.navigate(R.id.action_redeem2_to_weeklyOffers)
                            }
                            if (navController.currentDestination!!.id == R.id.profile2) {
                                navController.navigate(R.id.action_profile2_to_weeklyOffers)
                            }
                            if (navController.currentDestination!!.id == R.id.contactFragment) {
                                navController.navigate(R.id.action_contactFragment_to_weeklyOffers)
                            }
                        }
                        if (it.itemId == R.id.history) {
                            if (navController.currentDestination!!.id == R.id.weeklyOffers) {
                                navController.navigate(R.id.action_weeklyOffers_to_monthlyOffers)
                            }
                            if (navController.currentDestination!!.id == R.id.redeem2) {
                                navController.navigate(R.id.action_redeem2_to_monthlyOffers)
                            }
                            if (navController.currentDestination!!.id == R.id.profile2) {
                                navController.navigate(R.id.action_profile2_to_monthlyOffers)
                            }
                            if (navController.currentDestination!!.id == R.id.contactFragment) {
                                navController.navigate(R.id.action_contactFragment_to_monthlyOffers)
                            }
                        }
                        if (it.itemId == R.id.redeem) {
                            if (navController.currentDestination!!.id == R.id.weeklyOffers) {
                                navController.navigate(R.id.action_weeklyOffers_to_redeem2)
                            }
                            if (navController.currentDestination!!.id == R.id.monthlyOffers) {
                                navController.navigate(R.id.action_monthlyOffers_to_redeem2)
                            }
                            if (navController.currentDestination!!.id == R.id.profile2) {
                                navController.navigate(R.id.action_profile2_to_redeem2)
                            }
                            if (navController.currentDestination!!.id == R.id.contactFragment) {
                                navController.navigate(R.id.action_contactFragment_to_redeem2)
                            }
                        }
                        if (it.itemId == R.id.profile) {
                            if (navController.currentDestination!!.id == R.id.weeklyOffers) {
                                navController.navigate(R.id.action_weeklyOffers_to_profile2)
                            }
                            if (navController.currentDestination!!.id == R.id.monthlyOffers) {
                                navController.navigate(R.id.action_monthlyOffers_to_profile2)
                            }
                            if (navController.currentDestination!!.id == R.id.redeem2) {
                                navController.navigate(R.id.action_redeem2_to_profile2)
                            }
                            if (navController.currentDestination!!.id == R.id.contactFragment) {
                                navController.navigate(R.id.action_contactFragment_to_profile2)
                            }
                        }
                        if (it.itemId == R.id.contact) {
                            if (navController.currentDestination!!.id == R.id.weeklyOffers) {
                                navController.navigate(R.id.action_weeklyOffers_to_contactFragment)
                            }
                            if (navController.currentDestination!!.id == R.id.monthlyOffers) {
                                navController.navigate(R.id.action_monthlyOffers_to_contactFragment)
                            }
                            if (navController.currentDestination!!.id == R.id.redeem2) {
                                navController.navigate(R.id.action_redeem2_to_contactFragment)
                            }
                            if (navController.currentDestination!!.id == R.id.profile2) {
                                navController.navigate(R.id.action_profile2_to_contactFragment)
                            }
                        }
                        true
                    }



                    binding.walletTextView.setOnClickListener {
                        startActivity(
                            Intent(this, Wallet::class.java).putExtra(
                                "walletBalance",
                                binding.walletTextView.text
                            ).putExtra("userNumber", userData.userNumber.toString()).putExtra("userRecordId", userData.userNumber))

                    }
                }
            }


        }







    override fun onResume() {
        super.onResume()
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
//            // Request the RECEIVE_SMS permission
//            val REQUEST_SMS_PERMISSION = 734973 // Use any unique integer value
//
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), REQUEST_SMS_PERMISSION)
//        }
//        val registrationChecker = smsReceiver( )
//        val intentFilter = IntentFilter()
//        intentFilter.addAction(packageName + "android.provider.Telephony.SMS_RECEIVED")
//        registerReceiver(registrationChecker,
//             intentFilter
//        );

//            lifecycleScope.launch {
//                withContext(Dispatchers.IO){
//                    homeViewmodel.getOffersHistory(userData.userNumber)
//                    val db = Room.databaseBuilder(
//                        applicationContext,
//                        OfferDb::class.java, "offers_database"
//                    ).build()
//
//                    homeViewmodel.getOfferList(db)
//                }
//
//            }

    }
    private fun showCustomDialog(version :Version) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.setCancelable(true) // Allow the user to dismiss the dialog by tapping outside

        val downloadButton = dialog.findViewById<Button>(R.id.download_button)
        downloadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(version.link))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            startActivity(intent)
        }

        dialog.show()
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
                homeViewmodel.withdrawalTransaction(data.userNumber)
                homeViewmodel.getOffersHistory(data.userNumber)
                homeViewmodel.getWallet(data.userNumber)

            }
        })

        homeViewmodel.getWalletPrice().observe(this, Observer { wallet ->
            binding.walletTextView.text = wallet.currentBal.toString()
        })
    }
    override fun onBackPressed() {
        super.onBackPressed()
       finish()

    }



    private lateinit var soundPool: SoundPool
    private var soundID: Int = 0

    override fun onDestroy() {
        super.onDestroy()
        val registrationChecker = checkRegistration( )
        unregisterReceiver(registrationChecker);

        soundPool.release()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkAndRequestPermissions() {
        val permissions = arrayOf<String>(
            Manifest.permission.PACKAGE_USAGE_STATS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.VIBRATE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.REQUEST_INSTALL_PACKAGES,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET
        )

        // List of permissions to request
        val permissionsToRequest: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            // Request permissions
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray<String>(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String?>,
        @NonNull grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_REQUEST_CODE.toInt()) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
                registerReceiver(checkRegistration() , intentFilter )
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        }
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        permissions,
                        PERMISSION_REQUEST_CODE
                    )
                    // Permission denied
                    // You can show a message or take action accordingly
                }
            }
        }
    }
}
