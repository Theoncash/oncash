package `in`.oncash.oncash.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.oncash.oncash.Component.offerHistory_component
import `in`.oncash.oncash.Repository.Info_FirebaseRepo
import `in`.oncash.oncash.DataType.Instruction
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import kotlinx.coroutines.launch

class info_viewModel : ViewModel() {

    val InstructionsList : MutableLiveData<ArrayList<Instruction>> = MutableLiveData()

    fun getInstrutionList(offerId: String) : MutableLiveData<ArrayList<Instruction>>{
        viewModelScope.launch{
            InstructionsList.value = Info_FirebaseRepo().getInstructionList(offerId)
        }

        return InstructionsList
    }

    fun updateOfferHistory(user : userData , offerId: String , offerPrice:String , offerName : String , status:String){
        viewModelScope.launch {
            offerHistory_component().updateAirtable(user , offerId , offerPrice , offerName , status)
        }
    }
    fun addBlacklist(user : userData , offerId: String ){
        viewModelScope.launch {
            UserInfo_Airtable_Repo().addBlacklist(user , offerId)
        }
    }
    fun getBlacklist(userId : String , offerId: String ) : MutableLiveData<Boolean>{
        val Blacklisted : MutableLiveData<Boolean> = MutableLiveData(false)
        viewModelScope.launch {
          var Blacklist =   UserInfo_Airtable_Repo().getBlacklist()
            for (offer in Blacklist){
                if (offer.fields.UserId == userId && offer.fields.OfferId == offerId){
                    Blacklisted.postValue(true)
                }
            }
        }
        return Blacklisted
    }
    fun isCompleted (userId: String  , offerName : String):MutableLiveData<Boolean>{
       var isCompleted = MutableLiveData<Boolean>(false)
        viewModelScope.launch {
           val offerHistory =  offerHistory_component().getOfferHIstory( userId )
            for (offer in offerHistory ){
               if( offer.fields.OfferName.contains( offerName ) && offer.fields.Status == "Completed")
                  isCompleted.postValue(  true )
            }
        }
        return isCompleted
    }

    fun isOfferBeign (userId: String  , offerName : String):Boolean{
        var isCompleted = MutableLiveData<Boolean>(false)
        viewModelScope.launch {
            val offerHistory =  offerHistory_component().getOfferHIstory( userId )
            for (offer in offerHistory ){
                if( offer.fields.OfferName.contains( offerName ) && offer.fields.Status == "Being Reviewed")
                    isCompleted.postValue(  true )
            }
        }
        return isCompleted.value!!
    }
}