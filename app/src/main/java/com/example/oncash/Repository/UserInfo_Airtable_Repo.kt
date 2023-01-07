package com.example.oncash.Repository



import android.util.Log
import com.example.oncash.DataType.Fields
import com.example.oncash.DataType.Records
import com.example.oncash.DataType.UserwalletData
import com.example.oncash.View.Wallet
import io.ktor.client.*

import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*

import io.ktor.http.*
import io.ktor.util.Identity.encode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import org.json.JSONArray
import org.json.JSONObject

class UserInfo_Airtable_Repo {
    private val apiKey = "keyCQq6gmGFzeqDCX"
    private val base = "appK86XkkYn9dx2vu"
    private val tableId = "tblOwifipGGANDJPN"

   suspend fun getWallet( userRecordId :String ): Int = withContext(Dispatchers.IO){

           val client = HttpClient(){

           }
           val url = "https://api.airtable.com/v0/$base/$tableId/$userRecordId/"
           val response = client.get(url) {
               parameter(
                   "api_key", apiKey
               )
           }


          val fields :JSONObject = JSONObject(response.body<String>()).getJSONObject("fields")
          val wallet = JSONObject(fields.toString()).getString("Wallet")

       return@withContext  wallet.toInt()
    }

   suspend fun getUserInfo() :JSONArray = withContext(Dispatchers.IO) {
        val client = HttpClient(){

        }
        val url = "https://api.airtable.com/v0/$base/$tableId/"
        val response = client.get(url) {
            parameter(
                "api_key", apiKey
            )
        }


       val users : JSONArray =  JSONArray (JSONObject(response.body<String>()).getString("records") )
        return@withContext users

    }

   suspend fun createUser(number: Long , wallet :Int) : String = withContext(Dispatchers.IO){
        val client = HttpClient(CIO){
            install(ContentNegotiation){
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
        }
        }
        val url = "https://api.airtable.com/v0/$base/$tableId/"
        val userInfo  = Records(Fields(number,wallet))
       lateinit var  userRecordId :String
       try{
           val response = client.post{
               url(url)
               header("Authorization" , "Bearer $apiKey")
               contentType(ContentType.Application.Json)
               setBody(userInfo)
           }
            userRecordId =JSONObject( JSONObject(response.toString() ).getString("records") ).getString("id")
           Log.i("airtable" , response.toString())
       }catch (e:Exception)
       {
           Log.i("airtable",e.toString())
       }

    return@withContext (userRecordId)

    }

}