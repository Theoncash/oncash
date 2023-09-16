package `in`.oncash.oncash.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import `in`.oncash.oncash.DataType.Offer
import kotlinx.coroutines.tasks.await

import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.withContext

class Offer_FIrebase  {

    suspend fun getData() : ArrayList<Offer>  = withContext(Dispatchers.IO){
       // FirebaseApp.initializeApp(context)
        val data : DatabaseReference = FirebaseDatabase.getInstance().getReference("Offers")
        val offerList : MutableLiveData<ArrayList<Offer>> = MutableLiveData()
        val response = ArrayList<Offer>()
        try {
            data.get().await().children.map { snapShot ->

                val name = snapShot.child("Name").getValue(String::class.java)
                val description = snapShot.child("Description").getValue(String::class.java)
                val image = snapShot.child("Image").getValue(String::class.java)
                val noOfSteps = snapShot.child("noOfSteps").getValue(Int::class.java)
                val appName = snapShot.child("appName").getValue(String::class.java)
                val regSMS = snapShot.child("regSMS").getValue(String::class.java)
                val price = snapShot.child("Price").getValue(String::class.java)
                val offerId = snapShot.child("OfferId").getValue(String::class.java)
                val link = snapShot.child("Link").getValue(String::class.java)
                val subid = snapShot.child("subid").getValue(String::class.java)
                val payout = snapShot.child("payout").getValue(String::class.java)
                val type = snapShot.child("Type").getValue(String::class.java)
                val videoId = snapShot.child("VideoId").getValue(String::class.java)



                // Create an instance of the Offer data class
                val offer = Offer(
                    Name = name,
                    Description = description,
                    Image = image,
                    noOfSteps = noOfSteps ?: 0,
                    appName = appName,
                    regSMS = regSMS,
                    Price = price,
                    OfferId = offerId,
                    Link = link,
                    subid = subid,
                    payout = payout,
                    Type = type,
                    VideoId = videoId,
                )

                Log.i("fbData", offer.noOfSteps!!.toString())


                response.add( offer )
            }
        } catch (exception: Exception) {

        }
        Log.i("fbdata", response.size.toString())

        return@withContext response

    }
    fun parseJsonToOffer(jsonData: String): Offer? {
        val gson = Gson()
        return try {
            gson.fromJson(jsonData, Offer::class.java)
        } catch (e: Exception) {
            null
        }
    }


}