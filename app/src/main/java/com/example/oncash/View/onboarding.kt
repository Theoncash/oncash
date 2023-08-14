package com.example.oncash.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.oncash.Component.UserDataStoreUseCase
import com.example.oncash.R
import com.example.oncash.ViewModel.loginViewModel
import com.example.oncash.databinding.ActivityHomeBinding
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
            } else {
                setContentView(R.layout.onboarding_motion_layout)

                val button = findViewById<Button>(R.id.button_get_started)

                button.setOnClickListener {
                    val intent = Intent(this@onboarding, Login::class.java)
                    startActivity(intent)
                }
            }
    }
}}