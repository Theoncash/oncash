package com.example.oncash.ViewModel

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oncash.Component.get_UserInfo_UseCase
import com.example.oncash.DataType.UserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class loginViewModel:ViewModel() {
    private var isUserLogined by Delegates.notNull<Boolean>()
    private lateinit var userData:UserData

    fun addUser(userNumber : Long) : Boolean{

        viewModelScope.launch {
             userData =  get_UserInfo_UseCase().registerUser(userNumber.toLong())
//            Application().getSharedPreferences("UserInfo" , MODE_PRIVATE).edit( ) {
//                putBoolean("IsUserLogin" , userData.isUserRegistered)
//                putLong("UserNumber" , userNumber)
//                putString("UserRowId" , userData.userRecordId)
//                apply()
//            }
        }
    return true
    }



}