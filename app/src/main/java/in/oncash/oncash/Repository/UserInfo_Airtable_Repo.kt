package `in`.oncash.oncash.Repository


import android.util.Log
import androidx.lifecycle.MutableLiveData
import `in`.oncash.oncash.DataType.*
import `in`.oncash.oncash.DataType.SerializedDataType.Blacklist.Blacklist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import `in`.oncash.oncash.DataType.SerializedDataType.Blacklist.BlackList_Fields
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.OfferHistoryRecord
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class UserInfo_Airtable_Repo {
    private val apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZhbWxwd2d4bXRxcHhueWt6YXJwIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTU5Njg4MTksImV4cCI6MjAxMTU0NDgxOX0.zVIW9Z1GdvEUEPZpQgxkJwIal_MkgIN-gIEhrnKPKeg"


    suspend fun getWallet(userNumber: Long): walletDatatype = withContext(Dispatchers.IO) {

        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }




        val url =  "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/UserInfo?id=eq.$userNumber&select=*"
        val response = client.get(url) {
            headers {
                append("apikey", apiKey)
                append("Authorization", "Bearer $apiKey")
            }
        }

        var current_bal : Int  = 0
         var  total_bal :Int = 0
        Log.i("userrepository" , JSONObject(response.body<String>()).toString())
        try {
            val fields: JSONObject = JSONObject(response.body<String>())
             current_bal = JSONObject(fields.toString()).getString("Wallet").toInt()
             total_bal = JSONObject(fields.toString()).getString("Total_Bal").toInt()
        }catch ( e:Exception){

        }
        return@withContext walletDatatype(current_bal ,total_bal )
    }



    suspend fun getWithdrawTransaction(userNumber : String): MutableLiveData<JSONArray> = withContext(Dispatchers.IO) {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
        val url ="https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Withdraw Request?UserNumber=eq.$userNumber&elect=*"

        val withdrawalTransaction: MutableLiveData<JSONArray> = MutableLiveData()
        try {
            val response = client.get(url) {
                headers {
                    append("apikey", apiKey)
                    append("Authorization", "Bearer $apiKey")
                }
            }


            withdrawalTransaction.postValue(JSONArray(JSONObject(response.body<String>())))
        } catch (e: Exception) {
            withdrawalTransaction.postValue(null)
        }
        return@withContext withdrawalTransaction

    }

    suspend fun createUser(number: Long, wallet: Int , total_bal :Int): Boolean =
        withContext(Dispatchers.IO) {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                    })
                }
            }
            val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/UserInfo"

            val userInfo = Fields(number, wallet , total_bal)
            val userRecordId: MutableLiveData<String> = MutableLiveData()


            try {
                val response = client.post {
                    url(url)
                    headers {
                        append("apikey", apiKey)
                        append("Authorization", "Bearer $apiKey")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(userInfo)
                }
               if( response.status.value == 201 ){
                   return@withContext true
               }

                userRecordId.postValue(JSONObject(response.body<String>()).toString())

            } catch (e: Exception) {
                Log.i("airtable", e.toString())
            }

            return@withContext false

        }

    suspend fun updateWallet(number: Long, wallet: Int,  total_bal: Int): String =
        withContext(Dispatchers.IO) {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                    })
                }
            }
            val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/UserInfo?UserPhone=eq.$number"


            val userInfo = Fields(number, wallet , total_bal)
            var status by Delegates.notNull<String>()
            try {
                val response = client.patch {
                    url(url)
                    headers {
                        append("apikey", apiKey)
                        append("Prefer", "resolution=merge-duplicates")
                        append("Authorization", "Bearer $apiKey")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(userInfo)
                }
                status = response.status.toString()
                Log.i("airtabledata", response.body<String>().toString())

            } catch (e: Exception) {
                Log.i("airtable", e.toString())
            }

            return@withContext status

        }

    suspend fun withdrawRequest(
        phone: Long,
        RequestedAmount: Int,
        WalletBalance: Int,
    ): withdrawalsuccess =
        withContext(Dispatchers.IO) {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                    })
                }
            }
            val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Withdraw Request"
            val userInfo = FieldsX( phone, WalletBalance , "Pending")
            var date : String = ""
            var status = "Pending"
            var walletstatus: Int = 0
            var responseStatus by Delegates.notNull<String>()
            try {
                val response = client.post {
                    url(url)
                    headers {
                        append("apikey", apiKey)
                        append("Authorization", "Bearer $apiKey")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(userInfo)
                }
                responseStatus = response.status.toString()
                Log.i("userdataa" , response.body<String>().toString())
                val data = JSONObject(response.body<String>()).getString("createdTime")

                date =  data.toString().split("T").get(0).toString()
                Log.i("dateee" , date.toString())
                Log.i("dateee" , status)
            } catch (e: Exception) {
                Log.i("withdrawData", e.toString())
            }

            return@withContext withdrawalsuccess(withdrawalTransaction(  date ,  RequestedAmount.toString()   , status  ) , responseStatus)
        }

   suspend fun addBlacklist(userData: userData ,offerId: String ) = withContext(Dispatchers.IO){


           val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Blacklist"
           val client = HttpClient(CIO){
               install(ContentNegotiation){
                   Gson()
                   json(
                       Json{
                           isLenient = true
                           prettyPrint = true
                       }
                   )
               }
           }

           val blacklist =  Blacklist(`in`.oncash.oncash.DataType.SerializedDataType.Blacklist.BlackList_Fields(userData.userNumber , offerId ))

           val status =  client.post {
                                   url(url)
                                   headers {
                                       append("apikey", apiKey)
                                       append("Authorization", "Bearer $apiKey")
                                   }
                                   contentType(ContentType.Application.Json)
                                   setBody(blacklist)
                               }
       }






    suspend fun updateOfferHistory(userData: userData ,offerId: Int , offerPrice:String , status :String) = withContext(Dispatchers.IO){


            val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Offers History"
            val client = HttpClient(CIO){
                install(ContentNegotiation){
                    Gson()
                    json(
                        Json{
                            isLenient = true
                            prettyPrint = true
                        }
                    )
                }
            }

            val offerHistory = `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields(userData.userNumber , offerId ,  status , offerPrice )

            Log.i("offerhistory" , "userid"+userData.userNumber)
            Log.i("offerhistory" , getOfferHistory().toString())

        val status =  client.post {
            url(url)
            headers {
                append("apikey", apiKey)
                append("Authorization", "Bearer $apiKey")
            }
            contentType(ContentType.Application.Json)
            setBody(offerHistory)
        }

            Log.i("offerhistory" , status.status.value.toString())
        }



    suspend fun updateCompletedOffer(     number :Long , total_bal: Int  , wallet: Int,     userData: userData ,offerId: Int , offerPrice:String  , status :String) = withContext(Dispatchers.IO){

            val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Offers History?UserId=eq.$number&OfferId=eq.$offerId"
            val client = HttpClient(CIO){
                install(ContentNegotiation){
                    Gson()
                    json(
                        Json{
                            isLenient = true
                            prettyPrint = true
                        }
                    )
                }
            }

            val offerHistory =  `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields(userData.userNumber , offerId ,  status , offerPrice )

            Log.i("offerhistory" , "userid"+userData.userNumber)
            Log.i("offerhistory" , getOfferHistory().toString())

        val status =  client.post {
            url(url)
            headers {
                append("apikey", apiKey)
                append("Prefer", "resolution=merge-duplicates")

                append("Authorization", "Bearer $apiKey")
            }
            contentType(ContentType.Application.Json)
            setBody(offerHistory)
        }
            val total_wallet = wallet + offerPrice.toInt()
            updateWallet(number!!.toLong() , total_wallet  , (total_bal +offerPrice.toInt()))

            Log.i("offerhistory" , status.status.value.toString())
        }





    suspend fun getOfferHistory() : ArrayList<`in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields> = withContext(Dispatchers.IO){



        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Offers History?select=*"
        val client = HttpClient(CIO){
            install(ContentNegotiation){
                Gson()
                json(
                    Json{
                        isLenient = true
                        prettyPrint = true
                    }
                )
            }
        }

        val response = client.get(url) {
            headers {
                append("apikey", apiKey)
                append("Authorization", "Bearer $apiKey")
            }
        }


        val type = object : TypeToken<ArrayList<`in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields>>(){}.type
        val jsonObject = JSONArray(JSONObject(response.body<String>()))
        return@withContext Gson().fromJson( jsonObject.toString(), type )
    }


    suspend fun getBlacklist() : ArrayList<BlackList_Fields> = withContext(Dispatchers.IO){


        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Blacklist?select=*"
        val client = HttpClient(CIO){
            install(ContentNegotiation){
                Gson()
                json(
                    Json{
                        isLenient = true
                        prettyPrint = true
                    }
                )
            }
        }

        val response = client.get(url) {
            headers {
                append("apikey", apiKey)
                append("Authorization", "Bearer $apiKey")
            }
        }

       val type  =  object : TypeToken<ArrayList<BlackList_Fields>>(){}.type
        val jsonObject = JSONArray(JSONObject(response.body<String>())).toString()

        return@withContext Gson().fromJson( jsonObject, type )
    }


    suspend fun thereExists(list:kotlin.collections.ArrayList<OfferHistoryRecord>, element :OfferHistoryRecord):Boolean = withContext(Dispatchers.IO){
        var thereExisit = false
        for (current_element in list){
            if (current_element.fields.UserId == element.fields.UserId && current_element.fields.OfferId == element.fields.OfferId ){
                thereExisit = true
            }
        }
        return@withContext thereExisit
    }



    }


