package `in`.oncash.oncash.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.oncash.oncash.Component.sortingComponent
import `in`.oncash.oncash.DataType.OfferList
import `in`.oncash.oncash.Repository.Offer_FIrebase
import `in`.oncash.oncash.Repository.offer_AirtableDatabase
import kotlinx.coroutines.launch

class offer_viewmodel : ViewModel() {
    private val offerList : MutableLiveData<OfferList> = MutableLiveData()

    fun getOfferList() : MutableLiveData<OfferList>{
        viewModelScope.launch{
            offerList.postValue( sortingComponent().sortOfferList(Offer_FIrebase().getData() ))
            offer_AirtableDatabase().getData()
        }
        return offerList
    }


}