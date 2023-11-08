package `in`.oncash.oncash.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
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

    suspend fun getTransaction(context: Context): MutableLiveData<ArrayList<WithdrawalRequestEntity>> = withContext(Dispatchers.IO) {
         var withdrawRequestsJson: JSONArray;
               try {
                   withdrawRequestsJson   =
                       UserInfo_Airtable_Repo().getAllWithdrawTransaction()!!


                   val withdrawRequests: ArrayList<WithdrawalRequestEntity> =
                       jsonConversion().convertJSON(withdrawRequestsJson)
                   var withdrawRequestRoom: ArrayList<WithdrawalRequestEntity> = ArrayList()
                   val database = withdrawRequestDB.getDatabase(context)

                   for (request in withdrawRequests) {

                       // Get an instance of the Room database
                       database.withdrawalRequestDao().insertWithdrawalRequest(
                           request
                       )

                   }
                   val d =   database.withdrawalRequestDao().getAllWithdrawalRequests().value
                   if(d != null){
                       withdrawRequestRoom = d as  ArrayList<WithdrawalRequestEntity>
                   }
                   return@withContext MutableLiveData( withdrawRequestRoom )
               }catch (e:NullPointerException){
                   withdrawRequestsJson   =
                       UserInfo_Airtable_Repo().getAllWithdrawTransaction()!!
                val data : ArrayList<WithdrawalRequestEntity> = ArrayList<WithdrawalRequestEntity>(
                )
                   data.add(

                       WithdrawalRequestEntity(
                           0,
                       UserNumber = 2132131123,
                       WalletBalance = 0,
                       Status = "Success",
                       isDisplayed = true
                   ))
                   return@withContext  MutableLiveData(data)
               }

        }


    suspend fun updateWithdrawRequest(request : WithdrawalRequestEntity , context: Context) {
        val database = withdrawRequestDB.getDatabase(context)
        withContext(Dispatchers.IO) {
            database.withdrawalRequestDao().updateWithdrawalRequest(request)
        }
    }
}