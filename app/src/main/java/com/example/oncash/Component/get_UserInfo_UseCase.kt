package com.example.oncash.Component

import android.util.Log
import com.example.oncash.DataType.UserData
import com.example.oncash.Repository.UserInfo_Airtable_Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class get_UserInfo_UseCase {
   private lateinit var userRecordId :String

   suspend fun isUserRegistered(userNumber :Long): Boolean = withContext(Dispatchers.Default){

        val users =  UserInfo_Airtable_Repo().getUserInfo()
        var isUserRegistered :Boolean = false
        for (i in 0 until users.length()){
            userRecordId = JSONObject(users[i].toString()).getString("id").toString()
            val user =  JSONObject(users[i].toString()).getString("fields")

            val phone = JSONObject(user).getString("UserPhone")
            Log.i("isregistered" , phone.toString())
            if(phone.toLong() == userNumber){
                isUserRegistered =  true
            }
        }
        return@withContext isUserRegistered
    }

    suspend fun registerUser(userNumber: Long) : UserData = withContext(Dispatchers.Default){

       val isUserRegistered :Boolean =  isUserRegistered(userNumber)
        var UserRegistered :Boolean ? = null
        if (!isUserRegistered)
        {
            userRecordId =  UserInfo_Airtable_Repo().createUser(userNumber , 0)
            UserRegistered  = true
        }else{
            UserRegistered = true
        }


        return@withContext UserData(UserRegistered!! , userRecordId )
    }

}