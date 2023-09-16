package `in`.oncash.oncash.Component

import `in`.oncash.oncash.DataType.UserData1
import `in`.oncash.oncash.DataType.withdrawalTransaction
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class get_UserInfo_UseCase {


    suspend fun loginManager(userNumber: Long): UserData1 = withContext(Dispatchers.Default)
    {
        val userData: UserData1 = isUserRegistered(userNumber)

        if (!userData.isUserRegistered) {
            return@withContext UserData1(true, registerUser(userNumber))
        }
        return@withContext userData
    }

    suspend fun isUserRegistered(userNumber: Long): UserData1 = withContext(Dispatchers.Default) {

        val users: JSONArray? = UserInfo_Airtable_Repo().getUserInfo().value
        var isUserRegistered: Boolean = false
        var userRecordId: String = ""
        if(users != null){

            for (i in 0 until users!!.length()) {

                userRecordId = JSONObject(users[i]!!.toString()).getString("id").toString()

                val user = JSONObject(users[i]!!.toString()).getString("fields")
                val phone = JSONObject(user).getString("UserPhone")
                if (phone.toLong() == userNumber) {
                    isUserRegistered = true
                    return@withContext UserData1(isUserRegistered, userRecordId)

                }
            }
        }

        return@withContext UserData1(isUserRegistered, userRecordId)
    }

    private suspend fun registerUser(userNumber: Long): String = withContext(Dispatchers.Default) {

        return@withContext UserInfo_Airtable_Repo().createUser(userNumber, 0 , 0)
    }


    suspend fun getuserWithdrwalHistory(userNumber: Long): ArrayList<withdrawalTransaction> = withContext(Dispatchers.Default) {
        var withdrawalTransaction: JSONArray? = null
        val list : ArrayList<withdrawalTransaction> = ArrayList()
        try {
            withdrawalTransaction  =
                UserInfo_Airtable_Repo().getWithdrawTransaction().value!!
        }catch (e:NullPointerException){

        }
        lateinit var createdTime: String
        try{
        for (i in 0 until withdrawalTransaction!!.length()) {
            createdTime = JSONObject(withdrawalTransaction[i]!!.toString()).getString("createdTime").toString()
            val user = JSONObject(withdrawalTransaction[i]!!.toString()).getString("fields")
            val requestedAmount = JSONObject(user).getString("RequestedAmount")
            val phone = JSONObject(user).getString("UserNumber")
            val status = JSONObject(user).getString("Status")
            if (phone.toLong() == userNumber) {
               val date : String = createdTime.split("T")[0]

                list.add( withdrawalTransaction(date , requestedAmount ,  status ))
            }
        }}catch(e:Exception){

        }

        return@withContext list
    }

}