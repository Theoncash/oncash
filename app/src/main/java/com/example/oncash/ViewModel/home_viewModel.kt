package com.example.oncash.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oncash.Component.UserDataStoreUseCase
import com.example.oncash.Component.get_UserInfo_UseCase
import com.example.oncash.Component.offerHistory_component
import com.example.oncash.Component.sortingComponent
import com.example.oncash.DataType.*
import com.example.oncash.DataType.SerializedDataType.OfferHistory.OfferHistoryRecord
import com.example.oncash.Repository.Offer_FIrebase
import com.example.oncash.Repository.UserInfo_Airtable_Repo
import com.example.oncash.Repository.offer_AirtableDatabase
import com.example.oncash.RoomDb.userDb
import io.ktor.http.*
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
class home_viewModel : ViewModel() {

    private val wallet : MutableLiveData<walletDatatype> = MutableLiveData()
    private val userData : MutableLiveData<userData> = MutableLiveData()

    //wallet
    private val withdrawalTransaction : MutableLiveData<ArrayList<withdrawalTransaction>> = MutableLiveData()



    // wallet
    fun withdrawalTransaction(userNumber :Long){
        viewModelScope.launch {
            withdrawalTransaction.value =   get_UserInfo_UseCase().getuserWithdrwalHistory(userNumber )

        }

    }

    fun getWithdrawalTransaction():MutableLiveData<ArrayList<withdrawalTransaction>>{
        return withdrawalTransaction
    }

    // weekly offer viewmodel
    private val offerList : MutableLiveData<OfferList> = MutableLiveData()

    fun getOfferList() : MutableLiveData<OfferList> {
        viewModelScope.launch {
            offerList.postValue(sortingComponent().sortOfferList(Offer_FIrebase().getData()))
            offer_AirtableDatabase().getData()
        }
        return offerList
    }
//offer history viewmodel
private val offerhistoryList : MutableLiveData<ArrayList<OfferHistoryRecord>> = MutableLiveData()
    fun getOffersHistory(userId:String){
        viewModelScope.launch {
            offerhistoryList.postValue(offerHistory_component().getOfferHIstory(userId = userId))
        }
    }

    fun getOfferHistoryList():MutableLiveData<ArrayList<OfferHistoryRecord>>{
        return offerhistoryList
    }

//home viewmodel

    fun getWallet(userRecordId :String) {
        viewModelScope.launch {
               Log.i("recordID" , userRecordId)
                wallet.value = UserInfo_Airtable_Repo().getWallet(
                        userRecordId
                    )
                }
    }

    fun setUserData(user : userData){
        userData.postValue( user )
    }

    fun getUserData(context:Context) {
        viewModelScope.launch {
          userData.postValue( userData(UserDataStoreUseCase().retrieveUserRecordId(context) ,  UserDataStoreUseCase().retrieveUserNumber(context)) )
        }
    }

    fun getuserData():MutableLiveData<userData>{
        return userData
    }

    fun getWalletPrice():MutableLiveData<walletDatatype>{
        return wallet
    }

}