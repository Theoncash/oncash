package `in`.oncash.oncash.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.oncash.oncash.Component.offerHistory_component
import `in`.oncash.oncash.DataType.SerializedDataType.Blacklist.Blacklist
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.OfferHistoryRecord
import kotlinx.coroutines.launch

class offer_history_viewModel: ViewModel() {
   private val offerhistoryList : MutableLiveData<ArrayList<Fields>> = MutableLiveData()
    fun getOffersHistory(userId:Long){
        viewModelScope.launch {
            offerhistoryList.postValue(offerHistory_component().getOfferHIstory(userId = userId))
        }
    }

    fun getOfferHistoryList():MutableLiveData<ArrayList<Fields>>{
        return offerhistoryList
    }
}