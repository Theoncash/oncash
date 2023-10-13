package `in`.oncash.oncash.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.oncash.oncash.Component.get_UserInfo_UseCase
import `in`.oncash.oncash.Component.withdraw_usecase
import `in`.oncash.oncash.DataType.walletDatatype
import `in`.oncash.oncash.DataType.withdrawalTransaction
import `in`.oncash.oncash.DataType.withdrawalsuccess
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import kotlinx.coroutines.launch

class wallet_viewModel:ViewModel() {
    private val withdrawalRequest :MutableLiveData<withdrawalsuccess> = MutableLiveData()
    private val wallet : MutableLiveData<walletDatatype> = MutableLiveData()
    private val withdrawalTransaction : MutableLiveData<ArrayList<withdrawalTransaction>> = MutableLiveData()
    fun withdrawRequest(userNumber: Long, requestAmount: Int, walletBalance: Int )  {
        viewModelScope.launch {
            withdrawalRequest.postValue( withdraw_usecase().withdrawRequest(userNumber, requestAmount, walletBalance ))
        }
    }

    fun withdrawalTransaction(userNumber :Long){
        viewModelScope.launch {
          withdrawalTransaction.value =   get_UserInfo_UseCase().getuserWithdrwalHistory(userNumber )

        }

    }
    fun getWithdrawalTransaction():MutableLiveData<ArrayList<withdrawalTransaction>>{
        return withdrawalTransaction
    }
    fun getWithdrawalRequest(): MutableLiveData<withdrawalsuccess>{
        return withdrawalRequest
    }

    fun getWallet(userNumber: Long ) {
        viewModelScope.launch {
            wallet.value = UserInfo_Airtable_Repo().getWallet(
                userNumber
            )
        }
    }
    fun getWalletPrice():MutableLiveData<walletDatatype>{
        return wallet
    }



}