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




        CoroutineScope(Dispatchers.IO).launch {
            val smsBody = intent!!.getStringExtra("regSMS")
            Log.i("SMSDATA", "${smsBody}")

                val claimedDb = Room.databaseBuilder(
                    applicationContext,
                    offerClaimed::class.java,
                    "User"
                ).fallbackToDestructiveMigration() // Add this line for destructive migration
                    .build()
            Log.i("SMSDATA", "DB1")

                val userDb = Room.databaseBuilder(
                    applicationContext,
                    userDb::class.java,
                    "User2"
                ).fallbackToDestructiveMigration() // Add this line for destructive migration
                    .build()
            Log.i("SMSDATA", "DB2")


                val roomDb = Room.databaseBuilder(
                    applicationContext,
                    OfferDb::class.java,
                    "User1"
                ).fallbackToDestructiveMigration() // Add this line for destructive migration
                    .build()

                val user = userDb.userQuery().getUserNumber()
            Log.i("SMSDATA", "user->$user")

            val offers = roomDb.offerDao().getAllOffers()
            Log.i("SMSDATA", "offers->$offers")

            for (offer in offers) {
                    Log.i("SMSDATA", "DB->${offer.regSms}")

                    if (smsBody!!.contains(offer.regSms)) {

                        Log.i("SMSDATA", "DB->${offer.regSms}")

                        claimedDb.completedOfferDao().insert(
                            CompletedOfferEntity(
                                offer.offerId,
                                user,
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



