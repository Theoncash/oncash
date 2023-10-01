package `in`.oncash.oncash.View

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import `in`.oncash.oncash.Component.UserDataStoreUseCase
import `in`.oncash.oncash.RoomDb.Timer
import `in`.oncash.oncash.RoomDb.TimerDb
import `in`.oncash.oncash.ViewModel.loginViewModel
import `in`.oncash.oncash.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import `in`.oncash.oncash.Component.customLoadingDialog
import `in`.oncash.oncash.RoomDb.User
import `in`.oncash.oncash.RoomDb.userDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class Login : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)


        var isUserLogin: Boolean? = null
        val viewModel: loginViewModel by viewModels()
        lifecycleScope.launch {
            isUserLogin = UserDataStoreUseCase().retrieveUser(this@Login)
            if (isUserLogin == true) {
                startActivity(Intent(this@Login, Home::class.java))
            } else {
                setContentView(binding.root)
                binding.continueBut.setOnClickListener {

                    val loadingDialog = customLoadingDialog(this@Login)

// To show the dialog
                    loadingDialog.show()
                    loadingDialog.setMessage("Loading data...")

// Simulate some background work (replace this with your actual work)
                    Handler().postDelayed({
                        // Dismiss the dialog when the work is done
                        loadingDialog.dismiss()
                    }, 3000) // Replace 3000 with the actual duration of your background work

                    val phone = binding.phoneButtonInput.text.toString()

                    if (phone.length == 10) {

                        viewModel.addUser(phone.toLong())
                        viewModel.getUserData1().observe(this@Login, Observer { userData ->

                            if (userData) {
                                lifecycleScope.launch {
                                    UserDataStoreUseCase().storeUser(
                                        this@Login,
                                        true,
                                        phone.toLong(),
                                    )
                                    var roomDb = Room.databaseBuilder(
                                        this@Login,
                                        userDb::class.java,
                                        "Timer"
                                    ).build()
                                    roomDb.userQuery().addUser(User(phone.toLong()))
                                    val calendar = Calendar.getInstance()
                                    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 2)
                                    CoroutineScope(Dispatchers.Main).launch {
                                        var roomDb = Room.databaseBuilder(
                                            this@Login,
                                            TimerDb::class.java,
                                            "Timer"
                                        )    .fallbackToDestructiveMigration() // Add this line for destructive migration
                                            .build()

                                        withContext(Dispatchers.IO)
                                        {
                                            roomDb.TimerQuery().addTimer(Timer(calendar.timeInMillis))
                                        }

                                    }

                                    startActivity(Intent(this@Login, Home::class.java))


                                }
                            } else {
                                Snackbar.make(
                                    binding.root,
                                    "Error Registering , Please try again",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }


                        })


                    } else {
                        Snackbar.make(
                            binding.root,
                            "Please Enter Your Correct Number",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                }
            }


        }
    }
}