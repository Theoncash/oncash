package com.example.oncash.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oncash.Component.offerHistory_component
import com.example.oncash.DataType.SerializedDataType.OfferHistory.OfferHistoryRecord
import kotlinx.coroutines.launch

class offer_history_viewModel: ViewModel() {
   private val offerhistoryList : MutableLiveData<ArrayList<OfferHistoryRecord>> = MutableLiveData()
    fun getOffersHistory(userId:String){
        viewModelScope.launch {
            offerhistoryList.postValue(offerHistory_component().getOfferHIstory(userId = userId))
        }
    }

    fun getOfferHistoryList():MutableLiveData<ArrayList<OfferHistoryRecord>>{
        return offerhistoryList
    }
}