package `in`.oncash.oncash.Component

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.room.Room
import `in`.oncash.oncash.DataType.Offer
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.DataType.walletDatatype
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import `in`.oncash.oncash.RoomDb.userDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class service : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create a background thread or use coroutines to perform database operations
        CoroutineScope(Dispatchers.IO).launch {
            val roomDb = Room.databaseBuilder(
                applicationContext,
                userDb::class.java,
                "User"
            ).fallbackToDestructiveMigration() // Add this line for destructive migration
                .build()
            val number = roomDb.userQuery().getUserNumber()



        }
        return Service.START_NOT_STICKY

        // Process the database result
        }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    // Return a suitable service behavior, e.g., START_NOT_STICKY, START_STICKY
    }


