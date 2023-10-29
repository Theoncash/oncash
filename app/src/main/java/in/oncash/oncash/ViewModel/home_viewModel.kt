package `in`.oncash.oncash.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.oncash.oncash.Component.UserDataStoreUseCase
import `in`.oncash.oncash.Component.get_UserInfo_UseCase
import `in`.oncash.oncash.Component.sortingComponent
import `in`.oncash.oncash.DataType.*
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields
import `in`.oncash.oncash.DataType.SerializedDataType.Version
import `in`.oncash.oncash.Repository.Offer_FIrebase
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import `in`.oncash.oncash.RoomDb.OfferDb
import `in`.oncash.oncash.RoomDb.OfferEntity
import kotlinx.coroutines.*

class home_viewModel : ViewModel() {

    private val wallet : MutableLiveData<walletDatatype> = MutableLiveData()
    private val userData : MutableLiveData<userData> = MutableLiveData()
     val completedOffers : MutableLiveData<Int> = MutableLiveData(0)
     val totalOffers : MutableLiveData<Int> = MutableLiveData(0)
     val userNumber : MutableLiveData<Long> = MutableLiveData()
     val verionInfo : MutableLiveData<Version> = MutableLiveData()
    private val withdrawalTransaction : MutableLiveData<ArrayList<withdrawalTransaction>> = MutableLiveData()
    var checkingCompleted = false
    val isCompleted : MutableLiveData<Boolean> = MutableLiveData()

    val isWeb : MutableLiveData<Boolean> = MutableLiveData()

    // wallet
    fun withdrawalTransaction(userNumber :Long){
        viewModelScope.launch {
            withdrawalTransaction.postValue(   get_UserInfo_UseCase().getuserWithdrwalHistory(userNumber ))
        }

    }


    fun getVersionInfo():MutableLiveData<Version>{
        return verionInfo
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

    fun getOfferListData():  MutableLiveData<OfferList>{
        return offerList
    }
    fun getOfferList() {
        viewModelScope.launch {
            val data = Offer_FIrebase().getData()
//            addOffer_OfferDb(data , db)
            val offerList_data = sortingComponent().sortOfferList(data)
            offerList.postValue( offerList_data )
        }
    }
    fun getOffer(): ArrayList<Offer>? {

        return offerList.value?.weeklyOffersList
    }


  suspend  fun addOffer_OfferDb(offer_list : ArrayList<Offer> , db: OfferDb){
      var count = 0
      withContext(Dispatchers.IO){
          for (offer in offer_list){
              count += 1
              val offer_entity = OfferEntity(
                  offer.OfferId!!.toInt(),

                  offer.regSMS!!,
                  offer.appName!!,
                  offer.Price!!.toInt()
              )
              db.offerDao().insert(offer_entity)
          }
      }

    }

//offer history viewmodel
private val offerhistoryList : MutableLiveData<ArrayList<Fields>> = MutableLiveData()
    fun getOffersHistory(userId:Long){
        viewModelScope.launch {
            offerhistoryList.postValue(UserInfo_Airtable_Repo().OfferUserHistory(userId = userId))
        }
    }

    fun getOfferHistoryList():MutableLiveData<ArrayList<Fields>>{
        return offerhistoryList
    }

//home viewmodel

    fun getWallet(userNumber: Long ) {
        viewModelScope.launch {
                wallet.postValue( UserInfo_Airtable_Repo().getWallet(
                    userNumber
                    ))
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

    fun getVersion():MutableLiveData<Version>{
        viewModelScope.launch {
            verionInfo.postValue( UserInfo_Airtable_Repo().getVersion() )
        }
        return verionInfo
    }

    fun getuserData():MutableLiveData<userData>{
        return userData
    }

    fun getWalletPrice():MutableLiveData<walletDatatype>{
        return wallet
    }

    fun getIsCompleted( offerId : Int  , userId:Long ){
        viewModelScope.launch {
            var bool = UserInfo_Airtable_Repo().isCompleted(userId , offerId) .contains("Completed")
            Log.i("isCompletedOffer" , bool.toString())
            isCompleted.postValue( bool )
        }
    }

    fun getIsWeb( offerId : Int  ){
        viewModelScope.launch {
            isWeb.value = UserInfo_Airtable_Repo().getIsWeb( offerId)
        }
    }

    fun getIsWebData():MutableLiveData<Boolean>{
        return  isWeb
    }
    fun getIsCompletedData():MutableLiveData<Boolean>{
        return isCompleted
    }

}