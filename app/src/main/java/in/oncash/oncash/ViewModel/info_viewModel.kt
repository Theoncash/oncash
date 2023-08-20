package `in`.oncash.oncash.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.oncash.oncash.Component.offerHistory_component
import `in`.oncash.oncash.Repository.Info_FirebaseRepo
import `in`.oncash.oncash.DataType.Instruction
import `in`.oncash.oncash.DataType.userData
import kotlinx.coroutines.launch

class info_viewModel : ViewModel() {

    val InstructionsList : MutableLiveData<ArrayList<Instruction>> = MutableLiveData()

    fun getInstrutionList(offerId: String) : MutableLiveData<ArrayList<Instruction>>{
        viewModelScope.launch{
            InstructionsList.value = Info_FirebaseRepo().getInstructionList(offerId)
        }

        return InstructionsList
    }
    fun updateOfferHistory(user : userData , offerId: String , offerPrice:String , offerName : String){
        viewModelScope.launch {
            offerHistory_component().updateAirtable(user , offerId , offerPrice , offerName)
        }
    }

}