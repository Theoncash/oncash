package `in`.oncash.oncash.Repository


import android.util.Log
import androidx.lifecycle.MutableLiveData
import `in`.oncash.oncash.DataType.*
import `in`.oncash.oncash.DataType.SerializedDataType.Blacklist.Blacklist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import `in`.oncash.oncash.DataType.SerializedDataType.Blacklist.BlackList_Fields
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.OfferHistoryRecord
import `in`.oncash.oncash.DataType.SerializedDataType.Referral
import `in`.oncash.oncash.DataType.SerializedDataType.ReferralFields
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
import java.sql.Ref
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


            } catch (e: Exception) {
                Log.i("airtable", e.toString())
            }

            return@withContext false

        }

    suspend fun isUserRegistered(number: Long): Boolean =
        withContext(Dispatchers.IO) {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                    })
                }
            }
            val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/UserInfo?UserPhone=eq.$number&select=*"


            try {
                val response = client.get {
                    url(url)
                    headers {
                        append("apikey", apiKey)
                        append("Authorization", "Bearer $apiKey")
                    }
                }
                Log.i("supabase" , response.body<String>().toString())

                if( response.status.value == 200 ){

                    if(response.body<Boolean>().toString() == "[]"){
                        return@withContext false
                    }else{
                        return@withContext true

                    }
                }

            } catch (e: Exception) {
                Log.i("airtable", e.toString())
                return@withContext false

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

            } catch (e: Exception) {
                Log.i("withdrawData", e.toString())
            }

            return@withContext withdrawalsuccess(withdrawalTransaction(    RequestedAmount.toString()   , status  ) , responseStatus)
        }

   suspend fun addBlacklist(userData: userData ,offerId: Int ) = withContext(Dispatchers.IO){


           val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Blacklist"
           val client = HttpClient(CIO){
               install(ContentNegotiation){
                   json(
                       Json{
                           isLenient = true
                           prettyPrint = true
                       }
                   )
               }
           }

           val blacklist =  BlackList_Fields(userData.userNumber , offerId )

           val status =  client.post {
                                   url(url)
                                   headers {
                                       append("apikey", apiKey)
                                       append("Authorization", "Bearer $apiKey")
                                   }
                                   contentType(ContentType.Application.Json)
                                   setBody(blacklist)
                               }

       Log.i("supabasee" , status.status.toString())

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

        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        val response = client.get(url) {
            headers {
                append("apikey", apiKey)
                append("Authorization", "Bearer $apiKey")
            }
        }

        Log.i("supabase" , "oh nooo" +  response.status.toString())

        Log.i("supabase" , "oh nooo" +  response.body<String>().toString())

        val type = object : TypeToken<ArrayList<`in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields>>(){}.type

         var jsonObject :JSONArray = JSONArray()
       try {
            jsonObject = JSONArray(JSONObject(response.body<String>()))
       }catch(e:Exception){

       }

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

        Log.i("supabase" , response.body<String>().toString())
        var list :ArrayList<BlackList_Fields> = ArrayList()
        if(response.body<String>().toString() == "[]"){
            return@withContext ArrayList<BlackList_Fields>()
        }else{
           var json = JSONArray( response.body<String>().toString())
            for(i in 0 until json.length()){
                var blacklist = JSONObject(json[i].toString())
                list.add(BlackList_Fields(blacklist.getLong("UserId") , blacklist.getInt("OfferId") ))
            }
            return@withContext list
        }


    }

    suspend fun getLeaderBoard() : ArrayList<Fields>{
        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/UserInfo?order=Wallet.desc&select=*"

        val response = getData(url)
        val users :ArrayList<Fields> = ArrayList()
        var json = JSONArray( response.body<String>().toString())
        for(i in 0 until json.length()){
            var user = JSONObject(json[i].toString())
            val number = user.getInt("UserPhone")
            val wallet = user.getInt("Wallet")
            val total_bal = user.getInt("Total_Bal")
            users.add(Fields(number.toLong() , wallet , total_bal))
    }
        return users

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

    suspend fun getRefferals(userId :Long):Referral{
        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Referral?UserId=eq.$userId&select=Referral_code ,Referral_amount"
        val response = getData(url)
        var json = JSONArray( response.body<String>().toString())
        var user_referral_info = JSONObject(json[0].toString())
        var user_referral_code = user_referral_info.getInt("Referral_code")
        var user_referral_amount = user_referral_info.getInt("Referral_amount")

        val referred_url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Referred?Referred_code=eq.$user_referral_code&select=UserId"
        val referrals = getData(referred_url)
        val  users = JSONArray( referrals.body<String>().toString())
        val referred_users = ArrayList<Long>()

        for (i in 0 until users.length()){
            val user = JSONObject(users[i].toString()).getInt("UserId")
            referred_users.add(user.toLong())
        }
        val referral:Referral=  getReferralAmt(referred_users , user_referral_amount , userId , user_referral_code )

        return referral

        }

    suspend fun getReferralAmt(referred_users :ArrayList<Long> ,redeemed_referred_amt :Int , userId: Long , referral_code: Int):Referral{

        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/UserInfo?select=*"
        val users_info = getData(url)
        val  users = JSONArray( users_info.body<String>().toString())
        var total_referred_amount = 0
        for (i in 0 until users.length()){
            val user = JSONObject(users[i].toString()).getInt("UserId")
            if (referred_users.contains(user.toLong())){
                total_referred_amount +=  JSONObject(users[i].toString()).getInt("Wallet")
            }
        }

        val user_url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/UserInfo?UserPhone=eq.$userId&select=*"
        val user_info = getData(user_url)
        val  user = JSONArray( user_info.body<String>().toString())
        val referral_earned = (total_referred_amount - redeemed_referred_amt)
        val wallet = JSONObject(user[0].toString()).getInt("Wallet") + referral_earned
        val total_bal = JSONObject(user[0].toString()).getInt("Total_Bal") + referral_earned
        updateWallet(userId , wallet , total_bal )
        updateReferral(userId , referral_code , total_referred_amount)

        return Referral(referred_users , referral_earned , total_referred_amount)
    }

    suspend fun getData( url:String):HttpResponse{


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

        return response
    }

    suspend fun updateReferral(userId : Long , referral_code :Int , referral_amt :Int) {

        withContext(Dispatchers.IO) {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                    })
                }
            }
            val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Referral?UserId=eq.$userId"


            val userInfo = ReferralFields(userId, referral_code, referral_amt)
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


    }}


