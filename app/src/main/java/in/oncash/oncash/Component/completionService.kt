package `in`.oncash.oncash.Component

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.room.Room
import `in`.oncash.oncash.DataType.SerializedDataType.OfferClaimed
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.RoomDb.CompletedOfferEntity
import `in`.oncash.oncash.RoomDb.OfferDb
import `in`.oncash.oncash.RoomDb.offerClaimed
import `in`.oncash.oncash.RoomDb.userDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class completionService(): Service()  {
    val offerData : CompletedOfferEntity? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            // Create a background thread or use coroutines to perform database operations

//
//        val serviceOfferDbIntent = Intent(
//            context,
//            completionService::class.java
//        )
//        serviceOfferDbIntent.putExtra("userId", completedOffer.userId)
//        serviceOfferDbIntent.putExtra("offerId", completedOffer.offerId)
//        serviceOfferDbIntent.putExtra("claimed", completedOffer.claimed)
//
//        context.startService(serviceOfferDbIntent)


            val smsBody = intent!!.getStringExtra("regSMS")
            CoroutineScope(Dispatchers.IO).launch {

                val claimedDb = Room.databaseBuilder(
                    applicationContext,
                    offerClaimed::class.java,
                    "User"
                ).fallbackToDestructiveMigration() // Add this line for destructive migration
                    .build()

                val userDb = Room.databaseBuilder(
                    applicationContext,
                    userDb::class.java,
                    "User"
                ).fallbackToDestructiveMigration() // Add this line for destructive migration
                    .build()


                val roomDb = Room.databaseBuilder(
                    applicationContext,
                    OfferDb::class.java,
                    "User"
                ).fallbackToDestructiveMigration() // Add this line for destructive migration
                    .build()

                val user = userDb.userQuery().getUserNumber()
                val offers = roomDb.offerDao().getAllOffers()
                for (offer in offers) {
                    if (smsBody!!.contains(offer.regSms)) {
                        claimedDb.completedOfferDao().insert(
                            CompletedOfferEntity(
                            offer.id,
                            user,
                            offer.offerId,
                            false
                        )
                        )
                    }
                }

            }
            return Service.START_STICKY

            // Process the database result
        }

        override fun onBind(intent: Intent?): IBinder? {
            TODO("Not yet implemented")
        }

        // Return a suitable service behavior, e.g., START_NOT_STICKY, START_STICKY
    }



