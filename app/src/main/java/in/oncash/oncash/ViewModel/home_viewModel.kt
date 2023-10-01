package `in`.oncash.oncash.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.oncash.oncash.Component.UserDataStoreUseCase
import `in`.oncash.oncash.Component.get_UserInfo_UseCase
import `in`.oncash.oncash.Component.offerHistory_component
import `in`.oncash.oncash.Component.sortingComponent
import `in`.oncash.oncash.DataType.*
import `in`.oncash.oncash.DataType.SerializedDataType.Blacklist.Blacklist
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.OfferHistoryRecord
import `in`.oncash.oncash.Repository.Offer_FIrebase
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import `in`.oncash.oncash.Repository.offer_AirtableDatabase
import io.ktor.http.*
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
class home_viewModel : ViewModel() {

    private val wallet : MutableLiveData<walletDatatype> = MutableLiveData()
    private val userData : MutableLiveData<userData> = MutableLiveData()
     val completedOffers : MutableLiveData<Int> = MutableLiveData(0)
     val totalOffers : MutableLiveData<Int> = MutableLiveData(0)
     val userNumber : MutableLiveData<Long> = MutableLiveData()
    private val withdrawalTransaction : MutableLiveData<ArrayList<withdrawalTransaction>> = MutableLiveData()



    // wallet
    fun withdrawalTransaction(userNumber :Long){
        viewModelScope.launch {
            withdrawalTransaction.value =   get_UserInfo_UseCase().getuserWithdrwalHistory(userNumber )

        }

    }


    

    fun setProgressBar(completedOffer : Int, totalOffer: Int){
        completedOffers.value = completedOffer
        totalOffers.value = totalOffer
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
    fun getOffer():ArrayList<Offer>{
        return offerList.value!!.weeklyOffersList
    }

//offer history viewmodel
private val offerhistoryList : MutableLiveData<ArrayList<Fields>> = MutableLiveData()
    fun getOffersHistory(userId:Long){
        viewModelScope.launch {
            offerhistoryList.postValue(offerHistory_component().getOfferHIstory(userId = userId))
        }
    }

    fun getOfferHistoryList():MutableLiveData<ArrayList<Fields>>{
        return offerhistoryList
    }

//home viewmodel

    fun getWallet(userNumber: Long ) {
        viewModelScope.launch {
                wallet.value = UserInfo_Airtable_Repo().getWallet(
                    userNumber
                    )
                }
    }

    fun setUserData(user : userData){
        userData.postValue( user )
    }

    fun getUserData(context:Context) {
        viewModelScope.launch {
          userData.postValue( userData(UserDataStoreUseCase().retrieveUserNumber(context)) )
        }
    }

    fun getuserData():MutableLiveData<userData>{
        return userData
    }

    fun getWalletPrice():MutableLiveData<walletDatatype>{
        return wallet
    }

}