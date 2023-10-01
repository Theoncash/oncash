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
    ):String  = withContext(Dispatchers.Default){
             var status :String = ""
            val updateWallet = WalletBalance - RequestedAmount
            val user_wallet = UserInfo_Airtable_Repo().getWallet(phone)
            val walletstatus = UserInfo_Airtable_Repo().updateWallet(phone, 0 , user_wallet.totalBa)
            Log.i("withdraw"  , walletstatus)
            if (walletstatus.contains("201"))
            {
                status = walletstatus
                Log.i("withdraw"  , walletstatus)

            }

        return@withContext status
    }
    suspend fun withdrawRequest (userNumber: Long, requestAmount: Int, walletBalance: Int  ):withdrawalsuccess {
       val withdrawalTransaction :withdrawalsuccess  =   UserInfo_Airtable_Repo().withdrawRequest(
            userNumber,
            requestAmount.toInt(),
            walletBalance,
        )
        val status = withdrawalTransaction.response
        if (status .contains("201"))
        {
           val walletStatus =  isWithdrawRequested( userNumber, requestAmount ,walletBalance)
            if (walletStatus.contains("201"))
            {
                return withdrawalTransaction
            }
        }
        return withdrawalTransaction

    }


}