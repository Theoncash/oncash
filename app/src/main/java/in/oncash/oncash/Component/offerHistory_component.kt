package `in`.oncash.oncash.Component

import android.util.Log
import `in`.oncash.oncash.DataType.SerializedDataType.Blacklist.Blacklist
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.OfferHistoryRecord
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo

class offerHistory_component {

    suspend  fun getOfferHIstory(userId : Long) : ArrayList<Fields>{
           val list =  UserInfo_Airtable_Repo().getOfferHistory()
        Log.i("isBeign" , list.toString())

        val userOfferList : ArrayList<Fields>  = ArrayList()
           for (record in list){
               if (record.UserId == userId ){
                   userOfferList.add(record)
               }
           }
        return  userOfferList

      }

    suspend fun updateAirtable( user : userData , offerId: Int , offerPrice:String  , status:String ){
            UserInfo_Airtable_Repo().updateOfferHistory( user , offerId , offerPrice  , status)
     }

}