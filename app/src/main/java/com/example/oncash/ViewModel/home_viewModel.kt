package com.example.oncash.ViewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oncash.Component.get_UserInfo_UseCase
import com.example.oncash.Repository.Offer_FIrebase
import com.example.oncash.Repository.offer_AirtableDatabase
import com.example.oncash.DataType.Offer
import com.example.oncash.Repository.UserInfo_Airtable_Repo
import io.ktor.http.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
public class home_viewModel() : ViewModel() {

    private val offerList : MutableLiveData<ArrayList<Offer>> = MutableLiveData()
    private val wallet : MutableLiveData<Int> = MutableLiveData(0)

    fun getOfferList() : MutableLiveData<ArrayList<Offer>>{
        viewModelScope.launch{
            offerList.postValue( Offer_FIrebase().getData() )
            offer_AirtableDatabase().getData()

        }
        return offerList
    }
    fun getWallet() :MutableLiveData<Int>{
        viewModelScope.launch {
           wallet.value = UserInfo_Airtable_Repo().getWallet("recW4kvQ98bYzYNpJ")
        }
        return wallet
    }

}