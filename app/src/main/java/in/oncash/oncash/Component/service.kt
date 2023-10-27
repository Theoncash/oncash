package `in`.oncash.oncash.Component

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import `in`.oncash.oncash.DataType.Offer
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.DataType.walletDatatype
import `in`.oncash.oncash.R
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import `in`.oncash.oncash.RoomDb.CompletedOfferEntity
import `in`.oncash.oncash.RoomDb.OfferDb
import `in`.oncash.oncash.RoomDb.OfferEntity
import `in`.oncash.oncash.RoomDb.offerClaimed
import `in`.oncash.oncash.RoomDb.userDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class service : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create a background thread or use coroutines to perform database operations

        val context = applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            val offerDb = Room.databaseBuilder(
                applicationContext,
                OfferDb::class.java,
                "User"
            ).fallbackToDestructiveMigration() // Add this line for destructive migration
                .build()
            CoroutineScope(Dispatchers.IO).launch {
                val roomDb = Room.databaseBuilder(
                    applicationContext,
                    offerClaimed::class.java,
                    "User"
                ).fallbackToDestructiveMigration() // Add this line for destructive migration
                    .build()
                val claimedOffer = roomDb.completedOfferDao().getCompletedOffersByUser()
                for (offer in claimedOffer) {
                    Log.i("SMSDATA", "internet->$offer")
                    val offerData = offerDb.offerDao().getOfferById(offer.offerId)

                    val min = getTimeSpent(offerData!!.appName!!, context)
                    if (min >= 7) {
                        val completedOffer = CompletedOfferEntity(
                            0,
                            offer.userId,
                            offer!!.offerId!!.toInt(),
                            false
                        )




                        UserInfo_Airtable_Repo().removeOneCap(offer!!.offerId!!.toInt())
                        UserInfo_Airtable_Repo().addOfferClaimed(
                            offer!!.userId,
                            offer!!.offerId!!.toInt()
                        )
                        showNotification(context, offerData )

                        roomDb.completedOfferDao().removeCompletedOffer(offer.offerId)
                    }

                }
            }
        }
        return Service.START_NOT_STICKY

        // Process the database result




    // Return a suitable service behavior, e.g., START_NOT_STICKY, START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
    fun showNotification(context: Context, offer: OfferEntity) {
        // Define the channel ID as a constant
        val channelId = "my_notification_channel"
        val notificationId = 3133231// Use a unique ID for the notification

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "My Notification Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        Log.i("SMSDATA" , "working")

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.oncash)
            .setContentTitle("OnCash vibes, climb the skies! High-fives!")
            .setContentText("Good job! You just earned ${offer.appPrice} with OnCash. Keep it up and watch your earnings grow.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Removes the notification when tapped

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(notificationId , notificationBuilder.build())
    }
    fun getTimeSpent(targetPackageName : String , context: Context):Int{
        val targetPackageName = targetPackageName // Replace with your app's package name
        val usageStatsManager =  context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_MONTH, -1) // Subtract 1 month from the current time
        val startTime = calendar.timeInMillis
        var timeSpentInMin = 0
        val stats: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            currentTime
        )

        for (usageStats in stats) {
            if (usageStats.packageName == targetPackageName) {
                val timeInMilis  = usageStats.totalTimeInForeground
                timeSpentInMin = (timeInMilis / 60000).toInt()
                // Convert timeSpentInMillis to hours, minutes, seconds, etc. as needed.
                break
            }
        }
        Log.i("SMSDATA" , timeSpentInMin.toString())

        return timeSpentInMin
    }



}

