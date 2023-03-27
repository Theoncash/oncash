package com.example.oncash.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oncash.Component.sortingComponent
import com.example.oncash.DataType.OfferList
import com.example.oncash.Repository.Offer_FIrebase
import com.example.oncash.Repository.UserInfo_Airtable_Repo
import com.example.oncash.Repository.offer_AirtableDatabase
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