package com.example.oncash.Repository

import androidx.lifecycle.MutableLiveData
import com.example.oncash.Word.Offer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Offer_FIrebase  {
  suspend fun getData() : MutableLiveData<ArrayList<Offer>>  = withContext(Dispatchers.IO){




    }
}