package `in`.oncash.oncash.Repository


import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import `in`.oncash.oncash.DataType.*
import `in`.oncash.oncash.DataType.SerializedDataType.Blacklist.Blacklist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import `in`.oncash.oncash.DataType.SerializedDataType.Blacklist.BlackList_Fields
import `in`.oncash.oncash.DataType.SerializedDataType.Fields1
import `in`.oncash.oncash.DataType.SerializedDataType.OfferClaimed
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.OfferHistoryRecord
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.ReferralsDataType
import `in`.oncash.oncash.DataType.SerializedDataType.OfferInfo
import `in`.oncash.oncash.DataType.SerializedDataType.OfferTime
import `in`.oncash.oncash.DataType.SerializedDataType.Referral
import `in`.oncash.oncash.DataType.SerializedDataType.ReferralFields
import `in`.oncash.oncash.DataType.SerializedDataType.Referred
import `in`.oncash.oncash.DataType.SerializedDataType.Version
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
import java.sql.Date
import java.sql.Ref
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.properties.Delegates
import kotlin.random.Random

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
        Log.i("userrepository" ,  userNumber.toString())


        val url =  "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/UserInfo?UserPhone=eq.$userNumber&select=*"
        val response = client.get(url) {
            headers {
                append("apikey", apiKey)
                append("Authorization", "Bearer $apiKey")
            }
        }

        var current_bal : Int  = 0
         var  total_bal :Int = 0
        try {
            Log.i("userrepository" ,response.body<String>())

            if(response.status.value == 200 ){
                val fields = JSONArray(response.body<String>())
                current_bal = JSONObject(fields[0].toString()).getString("Wallet").toInt()
                total_bal = JSONObject(fields[0].toString()).getString("Total_Bal").toInt()
            }

        }catch ( e:Exception){

        }
        Log.i("userrepository"  , "cb" + current_bal.toString())

        return@withContext walletDatatype(current_bal ,total_bal )
    }



    @SuppressLint("SimpleDateFormat")
    suspend fun getOfferTime(offerId: Int) = withContext(Dispatchers.IO) {

        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }


        val url =  "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/OfferInfo?select=OfferId=eq.$offerId&select=*"
        val response = client.get(url) {
            headers {
                append("apikey", apiKey)
                append("Authorization", "Bearer $apiKey")
            }
        }

        var date : String  = ""
        var  time :String = ""
        var pt : java.util.Date?  = null
        var praseddate : java.util.Date? = null
        try {
            Log.i("userrepository" ,response.body<String>())

            if(response.status.value == 200 ){
                val fields = JSONArray(response.body<String>())
                time = JSONObject(fields[0].toString()).getString("OfferLaunch")
                date = JSONObject(fields[0].toString()).getString("OfferLauchDate")
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val timeFormat = SimpleDateFormat("HH:mm:ss")
                  pt = timeFormat.parse(time)
                  praseddate = dateFormat.parse(date)
            }

        }catch ( e:Exception){

        }
        return@withContext OfferTime(praseddate!!,pt!!)
    }



    suspend fun getWithdrawTransaction(userNumber : Long): JSONArray? = withContext(Dispatchers.IO) {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
        val url ="https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/WithdrawRequest?UserNumber=eq.$userNumber&select=*"
        var data : JSONArray? = null;
        try {
            val response = client.get(url) {
                headers {
                    append("apikey", apiKey)
                    append("Authorization", "Bearer $apiKey")
                }
            }
            Log.i("withdrawt" , response.body<String>().toString())


           data =  JSONArray(response.body<String>())
        } catch (e: Exception) {
        }
        return@withContext data

    }

    suspend fun createUser(number: Long, wallet: Int , total_bal :Int , referral_code: Int , name:String?): Boolean =
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

            val userInfo = Fields1(number, wallet , total_bal , name)
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
                   addReffered(number, referral_code)
                   addRefferal(number )

                   return@withContext true

               }


            } catch (e: Exception) {
                Log.i("airtable", e.toString())
            }

            return@withContext false

        }

    suspend fun addReffered(userId:Long , referral_code: Int) = withContext(Dispatchers.IO) {

        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Referred"

        val userInfo = Referred(userId,   referral_code )
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
            Log.i("refferral" , "r" + response.body<String>().toString())

            if( response.status.value == 201 ){
                return@withContext true
            }


        } catch (e: Exception) {
            Log.i("airtable", e.toString())
        }

        return@withContext false


    }

    suspend fun removeOneCap(offerId:Int) = withContext(Dispatchers.IO) {

        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/OfferInfo"
        val offer_info = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/OfferInfo?OfferId=eq.$offerId"
        val offerInfo = getData(offer_info)
        val offer =  JSONObject (JSONArray(offerInfo.body<String>())[0].toString())
        val offer_name = offer.getString("OfferName")!!
        val cap = offer.getInt("Cap")
        val dayLeft = offer.getInt("DayLeft")
        val userInfo = OfferInfo(offerId ,    offer_name , (cap - 1) , dayLeft)
        try {
            val response = client.post {
                url(url)
                headers {
                    append("apikey", apiKey)
                    append("Prefer", "resolution=merge-duplicates")
                    append("Authorization", "Bearer $apiKey")
                }
                contentType(ContentType.Application.Json)
                setBody(userInfo)

            }
            Log.i("refferral" , "r" + response.body<String>().toString())

            if( response.status.value == 201 ){
                return@withContext true
            }


        } catch (e: Exception) {
            Log.i("airtable", e.toString())
        }

        return@withContext false
    }
    suspend fun addOfferClaimed(userId:Long  , offerId: Int ) { withContext(Dispatchers.IO)
    {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/ClaimOffer"

        val userInfo = OfferClaimed(offerId, userId, false)
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
            Log.i("SMSDATA", response.body<String>().toString())
            if (response.status.value == 201) {

            }else{

            }


        } catch (e: Exception) {
            Log.i("airtable", e.toString())
        }


    }
    }
    suspend fun addRefferal(userId:Long  ) { withContext(Dispatchers.IO)
    {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Referral"

        val referral_code = Random.nextInt(111111, 999999)
        val userInfo = ReferralFields(userId, referral_code, 0)
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
            Log.i("referral", response.body<String>().toString())
            if (response.status.value == 201) {

            }else{

            }


        } catch (e: Exception) {
            Log.i("airtable", e.toString())
        }


    }
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

            val getname = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/UserInfo?UserPhone=eq.$number&select=Name"
            val name_response = getData(getname)
            Log.i("airtabledata", name_response.body<String>().toString())

            val name = JSONObject( JSONArray(name_response.body<String>())[0].toString() ).getString("Name")
            val userInfo = Fields(number, wallet , total_bal  , name)
            var status by Delegates.notNull<String>()
            try {
                val response = client.post {
                    url(url)
                    headers {
                        append("apikey", apiKey)
                        append("Prefer", "resolution=merge-duplicates")
                        append("Authorization", "Bearer $apiKey")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(userInfo)
                }
                status = response.status.value.toString()
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
            val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/WithdrawRequest"
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
                responseStatus = response.status.value .toString()
                if(response.status.value == 201){

                }

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






    suspend fun updateOfferHistory(userData: userData ,offerId: Int , offerPrice:String , status :String) = withContext(Dispatchers.IO) {


        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/OffersHistory"

        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        prettyPrint = true
                    }
                )
            }
        }

        val offerHistory = `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields(
            userData.userNumber,
            offerId,
            status,
            offerPrice.toInt()
        )


        try {
            val response = client.post {
                url(url)
                headers {
                    append("apikey", apiKey)
                    append("Authorization", "Bearer $apiKey")
                }
                contentType(ContentType.Application.Json)
                setBody(offerHistory)
            }
            if (response.status.value == 201) {
                return@withContext true
            } else {
                Log.i("airtable", response.body<String>().toString())

            }


        } catch (e: Exception) {
            Log.i("airtable", e.toString())
        }
    }




        suspend fun updateCompletedOffer(     number :Long , total_bal: Int  , wallet: Int,     userData: userData ,offerId: Int , offerPrice:String  , status :String) = withContext(Dispatchers.IO){

            val history = getOfferHistory(number)
            var completed = false
            for(offer in history){
                if(offer.OfferId == offerId && offer.UserId == userData.userNumber && offer.Status.contains("Completed"))
                {
                    completed = true
                    break;
                }
            }
            if(!completed){

                val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/OffersHistory?UserId=eq.$number&OfferId=eq.$offerId"
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

                val offerHistory =  `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields(userData.userNumber , offerId ,  status , offerPrice.toInt() )

                Log.i("offerhistory" , "userid"+userData.userNumber)
                Log.i("offerhistory" , getOfferHistory(number).toString())

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
                val total_wallet =  getWallet(number)

                val wallet = total_wallet.currentBal + offerPrice.toInt()
                val total = total_wallet.totalBa + offerPrice.toInt()
                Log.i("wallett" , total.toString())
                updateWallet(number!!.toLong() , wallet , total)

                Log.i("offerhistory" , status.status.value.toString())
            }


        }





    suspend fun getOfferHistory(userId:Long) : ArrayList<`in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields> = withContext(Dispatchers.IO){

        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/OffersHistory?UserId=eq.$userId&select=*"

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
        var list :ArrayList<`in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields> = ArrayList()
Log.i("blacklistt" , response.toString())
       try {
           var json = JSONArray( response.body<String>().toString())
           Log.i("blacklistt" , response.body<String>().toString())

           for(i in 0 until json.length()){
               var offerHistory = JSONObject(json[i].toString())
               Log.i("blacklistt" , offerHistory.getLong("UserId").toString())

               list.add(`in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields(offerHistory.getLong("UserId") , offerHistory.getInt("OfferId")  , offerHistory.getString("Status") , offerHistory.getInt("Payout")))
           }
       }catch(e:Exception){

       }

           return@withContext list
    }

    suspend fun getUserOfferHistory( userId:Long) : ArrayList<`in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields> = withContext(Dispatchers.IO){

        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/OffersHistory?UserId=eq.$userId&select=*"

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
        var list :ArrayList<`in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields> = ArrayList()
        Log.i("blacklistt" , response.toString())
        try {
            var json = JSONArray( response.body<String>().toString())
            Log.i("blacklistt" , response.body<String>().toString())

            for(i in 0 until json.length()){
                var offerHistory = JSONObject(json[i].toString())
                Log.i("blacklistt" , offerHistory.getLong("UserId").toString())

                list.add(`in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields(offerHistory.getLong("UserId") , offerHistory.getInt("OfferId")  , offerHistory.getString("Status") , offerHistory.getInt("Payout")))
            }
        }catch(e:Exception){

        }

        return@withContext list
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

    suspend fun getLeaderBoard() : MutableLiveData<ArrayList<Fields1>>{
        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/UserInfo?Total_Bal=gt.0&order=Total_Bal.desc&select=*"
        val list = MutableLiveData<ArrayList<Fields1>>()
        val response = getData(url)
        val users :ArrayList<Fields1> = ArrayList()
        var json = JSONArray( response.body<String>().toString())
        for(i in 0 until json.length()){
            var user = JSONObject(json[i].toString())
            val number = user.getLong("UserPhone")
            val wallet = user.getInt("Wallet")
            val total_bal = user.getInt("Total_Bal")
            val name :String? = user.getString("Name")
            users.add(Fields1(number.toLong() , wallet , total_bal , name))
    }
        Log.i("leaderboardd" , response.body<String>().toString())
        Log.i("leaderboardd" , users.toString())

        list.postValue(users)

        return list

}

    suspend fun getVersion() : Version = withContext(Dispatchers.IO){


        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/LatestVersion?select=*"

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

            val json = JSONArray( response.body<String>().toString())
                val version_json = JSONObject(json[0].toString())
                 val version = Version(version_json.getDouble("id") , version_json.getString("Link") )

            return@withContext version
        }



    suspend fun getIsWeb(offerId: Int) : Boolean = withContext(Dispatchers.IO){


        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/OfferInfo?OfferId=eq.$offerId&select=*"

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
        var isWeb = false
        val json = JSONArray( response.body<String>().toString())
        if (json.toString() != "[]" || json.length() > 0) {
            isWeb = JSONObject(json[0].toString()).getBoolean("isWeb")
        }

        return@withContext isWeb
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

    suspend fun getReferralCode(userId:Long) : MutableLiveData<Int> = withContext(Dispatchers.IO){
        val url =
            "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Referral?UserId=eq.$userId&select=*"
        val response = getData(url)
        var code = 0
        var json = JSONArray(response.body<String>().toString())
        if (json.toString() != "[]" || json.length() > 0) {
            code = JSONObject(json[0].toString()).getInt("Referral_code")
        }
        val referral_code = MutableLiveData<Int>()
        referral_code.postValue(code)
        return@withContext  referral_code
    }

    suspend fun isCompleted(userId:Long , offerId: Int) : String= withContext(Dispatchers.IO){
        val url =
            "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/OffersHistory?UserId=eq.$userId&OfferId=eq.$offerId&select=Status"
        val response = getData(url)
        var code = ""
        var json = JSONArray(response.body<String>().toString())
        if (json.toString() != "[]" || json.length() > 0) {
            code = JSONObject(json[0].toString()).getString("Status")
        }

        return@withContext code
    }

    suspend fun isOfferStarted(userId:Long , offerId: Int) : Boolean= withContext(Dispatchers.IO){
        val url =
            "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/OffersHistory?UserId=eq.$userId&OfferId=eq.$offerId&select=Status"
        val response = getData(url)
        var code = ""
        var isOfferStarted = false
        var json = JSONArray(response.body<String>().toString())
        if (json.toString() != "[]" || json.length() > 0) {
            code = JSONObject(json[0].toString()).getString("Status")
            isOfferStarted = true
        }
        Log.i("blacklisttt" ,"userStarted" +  isOfferStarted.toString())


        return@withContext isOfferStarted
    }

    suspend fun OfferUserHistory(userId: Long) : ArrayList<`in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields> = withContext(Dispatchers.IO){

        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/OffersHistory?UserId=eq.$userId&select=*"

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
        var list :ArrayList<`in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields> = ArrayList()
        Log.i("blacklistt" , response.toString())
        try {
            var json = JSONArray( response.body<String>().toString())
            Log.i("blacklistt" , response.body<String>().toString())

            for(i in 0 until json.length()){
                var offerHistory = JSONObject(json[i].toString())
                Log.i("blacklistt" , offerHistory.getLong("UserId").toString())

                list.add(`in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields(offerHistory.getLong("UserId") , offerHistory.getInt("OfferId")  , offerHistory.getString("Status") , offerHistory.getInt("Payout")))
            }
        }catch(e:Exception){

        }

        return@withContext list
    }


    suspend fun getReferralCodee(userId:Long) : Int= withContext(Dispatchers.IO){
        val url =
            "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Referral?UserId=eq.$userId&select=*"
        val response = getData(url)
        var code = 0
        var json = JSONArray(response.body<String>().toString())
        if (json.toString() != "[]" || json.length() > 0) {
            code = JSONObject(json[0].toString()).getInt("Referral_code")
        }
        return@withContext code
    }
     suspend fun getOfferInfo(offerId:Int) : OfferInfo= withContext(Dispatchers.IO){
        Log.i("fbDataa" , offerId.toString() )

        val url =
            "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/OfferInfo?OfferId=eq.$offerId&select=*"
        val response = getData(url)
        var cap = 0
        var dayLeft = 0
        var name = ""
        var json = JSONArray(response.body<String>().toString())
        if (json.toString() != "[]" || json.length() > 0) {
            cap = JSONObject(json[0].toString()).getInt("Cap")
            dayLeft = JSONObject(json[0].toString()).getInt("DayLeft")
            name = JSONObject(json[0].toString()).getString("OfferName")
        }
        Log.i("fbDataa" ,  cap.toString())
        val referral_code = OfferInfo(offerId , name, cap , dayLeft)
        return@withContext referral_code
    }
    suspend fun getRefferals(userId :Long):MutableLiveData<ReferralsDataType> = withContext(Dispatchers.IO){
        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Referral?UserId=eq.$userId&select=*"
        val response = getData(url)
        var json = JSONArray( response.body<String>().toString())
        if(json.toString() != "[]" || json.length() > 0) {
            val user_referral_info = JSONObject(json[0].toString())

            var user_referral_code = user_referral_info.getInt("Referral_code")
            var user_referral_amount = user_referral_info.getInt("Referral_amount")
            Log.i("userdataa" , user_referral_code.toString())


            val referred_url =
                "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/Referred?Referred_code=eq.${user_referral_code.toString()}&select=UserId"
            val referrals = getData(referred_url)
            val users = JSONArray(referrals.body<String>())
            Log.i("userdataa" , users.toString())
            val referred_users = ArrayList<Long>()

            for (i in 0 until users.length()) {
                val user = JSONObject(users[i].toString()).getLong("UserId")
                Log.i("userdataa" , JSONObject(users[0].toString()).getLong("UserId").toString())
                referred_users.add(user.toLong())
            }

            val final_referred_users = ArrayList<Fields>()

            for (user in referred_users) {
                val referral_url =
                    "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/UserInfo?UserPhone=eq.$user&select=*"
                val users_info = getData(referral_url)
                Log.i("userdataa" , users_info.body<String>(). toString())

                val amaount_json = JSONArray(users_info.body<String>().toString())
                val amount = JSONObject(amaount_json[0].toString()).getInt("Total_Bal")
                val name = JSONObject(amaount_json[0].toString()).getString("Name")
                final_referred_users.add(Fields(user, 0, amount.toInt() ,name ))
            }

            Log.i("userdataa" , referred_users.toString())

            val referral: Referral =
                getReferralAmt(referred_users, user_referral_amount, userId, user_referral_code)
            Log.i("userdataa" , referral.toString())

            val mutableLiveData = MutableLiveData<ReferralsDataType>()
            mutableLiveData.postValue(
                ReferralsDataType(
                    final_referred_users,
                    referral.Referral_amt,
                    referral.Total_Referral_amt
                )
            )
            return@withContext mutableLiveData
        }
        val mutableLiveData = MutableLiveData<ReferralsDataType>()

        mutableLiveData.postValue(
            ReferralsDataType(
                null,
                0,
                0
            )
        )
        return@withContext mutableLiveData
        }

    suspend fun getReferralAmt(referred_users :ArrayList<Long> ,redeemed_referred_amt :Int , userId: Long , referral_code: Int):Referral= withContext(Dispatchers.IO){

        val url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/UserInfo?select=*"
        val users_info = getData(url)
        val  users = JSONArray( users_info.body<String>().toString())
        var total_referred_amount = 0
        Log.i("userdataa" ,"reffered_usres" +  referred_users.toString())

        for (i in 0 until users.length()){
            val user = JSONObject(users[i].toString()).getLong("UserPhone")
            if (referred_users.contains(user.toLong())){
                Log.i("userdataa" , user.toString())

                total_referred_amount += ( JSONObject(users[i].toString()).getInt("Wallet") * 20 ) / 100
            }
        }

        val user_url = "https://vamlpwgxmtqpxnykzarp.supabase.co/rest/v1/UserInfo?UserPhone=eq.$userId&select=*"
        val user_info = getData(user_url)
        val  user = JSONArray( user_info.body<String>().toString())
        Log.i("userdataa" , user.toString())

        val referral_earned = (total_referred_amount - redeemed_referred_amt)
        val wallet = JSONObject(user[0].toString()).getInt("Wallet") + referral_earned
        val total_bal = JSONObject(user[0].toString()).getInt("Total_Bal") + referral_earned
        Log.i("userdataa" , referral_earned.toString())

        updateWallet(userId , wallet , total_bal )
        updateReferral(userId , referral_code , total_referred_amount)

        return@withContext Referral(referred_users , referral_earned , total_referred_amount)
    }

    suspend fun getData( url:String):HttpResponse= withContext(Dispatchers.IO){


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

        return@withContext response
    }

    suspend fun updateReferral(userId : Long , referral_code :Int , referral_amt :Int) = withContext(Dispatchers.IO){


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
                val response = client.post {
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


    }


