package `in`.oncash.oncash.Component

import `in`.oncash.oncash.DataType.FieldsX
import `in`.oncash.oncash.DataType.withdrawalTransaction
import `in`.oncash.oncash.RoomDb.WithdrawalRequestEntity
import org.json.JSONArray
import org.json.JSONObject

class jsonConversion {

    suspend fun convertJSON(json: JSONArray) :ArrayList<WithdrawalRequestEntity> {
        var list :ArrayList<WithdrawalRequestEntity> = ArrayList()

        for (i in 0 until json!!.length()) {
            val user = JSONObject(json[i]!!.toString())
            val requestedAmount = user.getInt("WalletBalance")
            val status = user.getString("Status")
            val userId = user.getString("UserNumber")
            val id = user.getString("id")
            list.add( WithdrawalRequestEntity(id.toInt() , userId.toLong() ,  requestedAmount.toInt(), status , false) )

        }
        return list
    }
}