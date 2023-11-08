package `in`.oncash.oncash.Component

import `in`.oncash.oncash.DataType.Offer
import `in`.oncash.oncash.DataType.OfferList

class sortingComponent {

    fun sortOfferList(offerList :ArrayList<Offer>) : OfferList {
        val weeklyOffersList : ArrayList<Offer> =ArrayList()
        val monthlyOffersList : ArrayList<Offer> =ArrayList()

        for (offer in offerList){
            if (offer.Price!!.toInt() <= 20){
                weeklyOffersList.add(offer)
            }
        }

        for (offer in offerList){
            if (offer.Type == "weekly" && offer.Price!!.toInt() > 20){
                weeklyOffersList.add(offer)
            }else{
                monthlyOffersList.add(offer)
            }
        }

        return OfferList(weeklyOffersList , monthlyOffersList)

    }
}