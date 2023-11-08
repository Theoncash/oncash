package `in`.oncash.oncash.Component

import `in`.oncash.oncash.DataType.FieldsX
import `in`.oncash.oncash.DataType.withdrawalTransaction
import org.json.JSONArray
import org.json.JSONObject

class jsonConversion {

    suspend fun convertJSON(json: JSONArray) :ArrayList<FieldsX> {
        var list :ArrayList<FieldsX> = ArrayList()

        for (i in 0 until json!!.length()) {
            val user = JSONObject(json[i]!!.toString())
            val requestedAmount = user.getInt("WalletBalance")
            val status = user.getString("Status")
            val userId = user.getString("UserNumber")
            list.add( FieldsX(userId.toLong() ,  requestedAmount.toInt(), status))

        }
        return list
    }
}