package com.example.oncash.View

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels

import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.oncash.Component.UserDataStoreUseCase
import com.example.oncash.Component.get_UserInfo_UseCase
import com.example.oncash.R
import com.example.oncash.ViewModel.loginViewModel
import com.example.oncash.databinding.ActivityLoginBinding
import com.google.firebase.database.core.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class Login : AppCompatActivity() {
    lateinit var binding :ActivityLoginBinding
    private val dataStore by preferencesDataStore(
        name = "UserData"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        var isUserLogin : Boolean?  = null

        val viewModel : loginViewModel by viewModels()
        GlobalScope.launch {
            isUserLogin =  UserDataStoreUseCase().retrieveUser(dataStore)
            if (isUserLogin==true)
            {
                startActivity( Intent(this@Login , Home::class.java) )
            }
        }
        setContentView(binding.root)





        binding.phoneButtonInput.setOnClickListener {
            val phone = binding.phoneInput.text.toString()
            if(phone.isNotEmpty()) {
                val isRegistered  =
                viewModel.addUser(phone.toLong())
                lifecycleScope.launch {
                    UserDataStoreUseCase().storeUser(dataStore , isRegistered , phone.toLong())
                }

                   if (isRegistered){
                          startActivity( Intent(this@Login , Home::class.java) )

                }
            }

        }




    }
}