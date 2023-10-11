package `in`.oncash.oncash.Component

import android.annotation.SuppressLint
import `in`.oncash.oncash.DataType.UserData1
import `in`.oncash.oncash.DataType.withdrawalTransaction
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class get_UserInfo_UseCase {


    suspend fun loginManager(userNumber: Long , referred_code: Int , name:String?): Boolean = withContext(Dispatchers.Default)
    {
        val userData: Boolean = isUserRegistered(userNumber)
        if(userData){
            return@withContext true
        }else{
            registerUser(userNumber , referred_code  ,name)
            return@withContext true

        }
        return@withContext userData
    }

    suspend fun isUserRegistered(userNumber: Long): Boolean = withContext(Dispatchers.Default) {

        val isRegistered: Boolean = UserInfo_Airtable_Repo().isUserRegistered(userNumber)

        return@withContext isRegistered
    }

    private suspend fun registerUser(userNumber: Long , referred_code:Int , name:String?): Boolean = withContext(Dispatchers.Default) {

        return@withContext UserInfo_Airtable_Repo().createUser(userNumber, 0 , 0 , referred_code , name)
    }


    @SuppressLint("SuspiciousIndentation")
    suspend fun getuserWithdrwalHistory(userNumber: Long): ArrayList<withdrawalTransaction> = withContext(Dispatchers.Default) {
        var withdrawalTransaction: JSONArray? = null
        val list : ArrayList<withdrawalTransaction> = ArrayList()
        try {
            withdrawalTransaction  =
                UserInfo_Airtable_Repo().getWithdrawTransaction(userNumber.toString()).value!!
        }catch (e:NullPointerException){

        }
        try{
        for (i in 0 until withdrawalTransaction!!.length()) {
            val user = JSONObject(withdrawalTransaction[i]!!.toString())
            val requestedAmount = user.getInt("WalletBalance")
            val phone =user.getInt("UserNumber")
            val status = user.getString("Status")
                list.add( withdrawalTransaction( requestedAmount.toString() ,  status ))

        }}catch(e:Exception){

        }

        return@withContext list
    }

}