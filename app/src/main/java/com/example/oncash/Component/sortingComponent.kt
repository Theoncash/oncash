package com.example.oncash.Component

import androidx.lifecycle.MutableLiveData
import com.example.oncash.DataType.Offer
import com.example.oncash.DataType.OfferList
import com.example.oncash.Repository.offer_AirtableDatabase

class sortingComponent {

    fun sortOfferList(offerList :ArrayList<Offer>) : OfferList {
        val weeklyOffersList : ArrayList<Offer> =ArrayList()
        val monthlyOffersList : ArrayList<Offer> =ArrayList()

        for (offer in offerList){
            if (offer.Type == "weekly"){
                weeklyOffersList.add(offer)
            }else{
                monthlyOffersList.add(offer)
            }
        }

        return OfferList(weeklyOffersList , monthlyOffersList)

    }
}