package `in`.oncash.oncash.Repository

import android.content.Context
import `in`.oncash.oncash.Component.jsonConversion
import `in`.oncash.oncash.DataType.FieldsX
import `in`.oncash.oncash.DataType.withdrawalTransaction
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import `in`.oncash.oncash.RoomDb.WithdrawalRequestEntity
import `in`.oncash.oncash.RoomDb.withdrawRequestDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

class RoomRepo {

    suspend fun getTransaction(context: Context): ArrayList<WithdrawalRequestEntity> {
     return   withContext(Dispatchers.IO) {
         var withdrawRequestsJson: JSONArray;
               try {
                   withdrawRequestsJson   =
                       UserInfo_Airtable_Repo().getAllWithdrawTransaction()!!
               }catch (e:NullPointerException){
                   withdrawRequestsJson   =
                       UserInfo_Airtable_Repo().getAllWithdrawTransaction()!!
               }
                val withdrawRequests: ArrayList<FieldsX> =
                    jsonConversion().convertJSON(withdrawRequestsJson)
                var withdrawRequestRoom: ArrayList<WithdrawalRequestEntity> = ArrayList()
                val database = withdrawRequestDB.getDatabase(context)

                for (request in withdrawRequests) {
                    // Get an instance of the Room database
                    val withdrawalRequest = WithdrawalRequestEntity(
                        UserNumber = request.UserNumber,
                        WalletBalance = request.WalletBalance,
                        Status = "Success",
                        isDisplayed = false
                    )
                    database.withdrawalRequestDao().insertWithdrawalRequest(
                        withdrawalRequest
                    )

                }
                withdrawRequestRoom = database.withdrawalRequestDao().getAllWithdrawalRequests().value as ArrayList<WithdrawalRequestEntity>

                return@withContext withdrawRequestRoom
        }
    }

    suspend fun updateWithdrawRequest(request : WithdrawalRequestEntity , context: Context) {
        val database = withdrawRequestDB.getDatabase(context)

        withContext(Dispatchers.IO) {

            database.withdrawalRequestDao().updateWithdrawalRequest(
                request
            )
        }
    }
}