package `in`.oncash.oncash.Component

import android.util.Log
import `in`.oncash.oncash.DataType.withdrawalsuccess
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class withdraw_usecase {
    private lateinit var Withdrawstatus :String


    suspend fun isWithdrawRequested(
        phone: Long,
        RequestedAmount: Int,
        WalletBalance: Int,
        userRecordId: String
    ):String  = withContext(Dispatchers.Default){
            lateinit var status :String
            val updateWallet = WalletBalance - RequestedAmount
            val walletstatus = UserInfo_Airtable_Repo().updateWallet(phone, updateWallet , 0)
            Log.i("withdraw"  , walletstatus)
            if (walletstatus.contains("200"))
            {
                status = walletstatus
                Log.i("withdraw"  , walletstatus)

            }

        return@withContext status
    }
    suspend fun withdrawRequest (userNumber: Long, requestAmount: Int, walletBalance: Int , userRecordId :String ):withdrawalsuccess {
       val withdrawalTransaction :withdrawalsuccess  =   UserInfo_Airtable_Repo().withdrawRequest(
            userNumber,
            requestAmount.toInt(),
            walletBalance,
        )
        val status = withdrawalTransaction.response
        if (status .contains("200"))
        {
           val walletStatus =  isWithdrawRequested( userNumber, requestAmount ,walletBalance, userRecordId)
            if (walletStatus.contains("200"))
            {
                return withdrawalTransaction
            }
        }
        return withdrawalTransaction

    }


}