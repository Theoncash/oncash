package com.example.oncash.ViewModel

import android.provider.Settings.Global
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.oncash.Word.Offer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class home_viewModel : ViewModel() {

    val offerList : MutableLiveData<ArrayList<Offer>> = MutableLiveData()
    init {
          GlobalScope.launch{

        }

    }
    @JvmName("getOfferList1")
    fun getOfferList() : MutableLiveData<ArrayList<Offer>>{
        return offerList
    }
}