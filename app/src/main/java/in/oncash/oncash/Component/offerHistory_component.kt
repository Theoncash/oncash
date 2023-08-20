package `in`.oncash.oncash.Component

import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.OfferHistoryRecord
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo

class offerHistory_component {

    suspend  fun getOfferHIstory(userId : String) : ArrayList<OfferHistoryRecord>{
           val list =  UserInfo_Airtable_Repo().getOfferHistory()
           val userOfferList : ArrayList<OfferHistoryRecord>  = ArrayList()
           for (record in list){
               if (record.fields.UserId== userId ){
                   userOfferList.add(record)
               }
           }
        return  userOfferList

      }

    suspend fun updateAirtable( user : userData , offerId: String , offerPrice:String , offerName : String){
            UserInfo_Airtable_Repo().updateOfferHistory( user , offerId , offerPrice , offerName)
     }

}