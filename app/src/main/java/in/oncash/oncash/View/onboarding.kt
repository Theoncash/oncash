package `in`.oncash.oncash.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import `in`.oncash.oncash.Component.UserDataStoreUseCase
import `in`.oncash.oncash.R
import `in`.oncash.oncash.ViewModel.loginViewModel
import kotlinx.coroutines.launch

class onboarding : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var isUserLogin: Boolean? = null
        val viewModel: loginViewModel by viewModels()
        lifecycleScope.launch {
            isUserLogin = UserDataStoreUseCase().retrieveUser(this@onboarding)
            if (isUserLogin == true) {
                startActivity(Intent(this@onboarding, Home::class.java))
                finish()

            } else {
                setContentView(R.layout.onboarding_motion_layout)

                val button = findViewById<Button>(R.id.button_get_started)

                button.setOnClickListener {
                    val intent = Intent(this@onboarding, Login::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    }
}}