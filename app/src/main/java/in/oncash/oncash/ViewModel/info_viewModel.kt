package `in`.oncash.oncash.ViewModel

import android.util.Log
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
    val ClosingInstructionsList : MutableLiveData<ArrayList<Instruction>> = MutableLiveData()
    val OfferQueries : MutableLiveData<ArrayList<Instruction>> = MutableLiveData()
    val iscompleted = MutableLiveData<Boolean>(false)
    val Blacklisted : MutableLiveData<Boolean> = MutableLiveData(false)
    val isWebData : MutableLiveData<Boolean> = MutableLiveData()

   fun getInstructionListData (): MutableLiveData<ArrayList<Instruction>>{
       return InstructionsList
   }
    fun getInstrutionList(offerId: String) {
        viewModelScope.launch{
            InstructionsList.value = Info_FirebaseRepo().getInstructionList(offerId)
        }

    }
    fun getClosingInstrutionList(offerId: String) : MutableLiveData<ArrayList<Instruction>>{
        viewModelScope.launch{
            ClosingInstructionsList.value = Info_FirebaseRepo().getClosingnstructionList(offerId)
        }

        return ClosingInstructionsList
    }

    fun getOfferQueries(offerId: String) : MutableLiveData<ArrayList<Instruction>>{
        viewModelScope.launch{
            OfferQueries.value = Info_FirebaseRepo().getOfferQueries(offerId)
        }

        return OfferQueries
    }

    fun updateOfferHistory(user : userData , offerId: Int , offerPrice:String ,  status:String){
        viewModelScope.launch {
            offerHistory_component().updateAirtable(user , offerId , offerPrice , status)
        }
    }
    fun addBlacklist(user : userData , offerId: Int ){
        viewModelScope.launch {
            UserInfo_Airtable_Repo().addBlacklist(user , offerId)
        }
    }

    fun getBlackListData():MutableLiveData<Boolean>{
        return Blacklisted
    }
    fun getBlacklist(userId : Long , offerId: Int ){
        viewModelScope.launch {
          var Blacklist =   UserInfo_Airtable_Repo().getBlacklist()
            for (offer in Blacklist){
                if (offer.UserId == userId && offer.OfferId == offerId){
                    Blacklisted.postValue(true)
                }
            }
        }
    }
    fun getIsCompleted():MutableLiveData<Boolean>{
        return iscompleted;
    }
    fun isCompleted (userId: Long  , offerId: Int ){
        viewModelScope.launch {
            Log.i("closingInstructions" ," offerHistory.toString()")

            val offerHistory =  offerHistory_component().getOfferHIstory( userId )
            Log.i("closingInstructions" , offerHistory.toString())

            for (offer in offerHistory ){
                Log.i("closingInstructions" , "offerId" +  offerId + "Status" + offer.Status)

                if( offer.OfferId == offerId && offer.Status == "Completed"){
                    iscompleted.postValue(  true )
               }
            }
        }
    }
 private   val isCompleted :MutableLiveData<Boolean> = MutableLiveData()

    private   val isOfferCompleted :MutableLiveData<Boolean> = MutableLiveData()

    fun isOfferBeign (userId: Long  , offerId : Int){
        viewModelScope.launch {


            var isOffer= UserInfo_Airtable_Repo().isOfferStarted(userId , offerId)
                            Log.i("blacklisttt" ,"userStarted" +  isOffer.toString())
            isOfferCompleted.postValue(isOffer)
        }
    }

    fun getisOfferCompleted(): MutableLiveData<Boolean>{
        return isOfferCompleted
    }

    fun getIsWebData(): MutableLiveData<Boolean>{
        return isWebData
    }
    fun getIsWeb (  offerId : Int){
        viewModelScope.launch {


            var isWeb= UserInfo_Airtable_Repo().getIsWeb( offerId)
            Log.i("blacklisttt" ,"userStarted" +  isWeb.toString())
            isWebData.postValue(isWeb)
        }
    }
}