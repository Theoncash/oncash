package `in`.oncash.oncash.Component

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.room.Room
import `in`.oncash.oncash.DataType.Offer
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.DataType.walletDatatype
import `in`.oncash.oncash.R
import `in`.oncash.oncash.Repository.Offer_FIrebase
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import `in`.oncash.oncash.RoomDb.CompletedOfferEntity
import `in`.oncash.oncash.RoomDb.OfferDb
import `in`.oncash.oncash.RoomDb.userDb
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

class checkRegistration : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        val c = context
        var userdata = userData(0 )
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            val pdus = intent.extras?.get("pdus") as Array<*>
            for (pdu in pdus) {
                val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                val messageBody = smsMessage.messageBody
                // Check the message content and take appropriate action


                GlobalScope.launch {
                    val userDb = Room.databaseBuilder(
                        context,
                        userDb::class.java,
                        "User2"
                    ).fallbackToDestructiveMigration() // Add this line for destructive migration
                        .build()
                   val offfer =  userDb.userQuery().getUserNumber()
                    Log.i("SMSDATA", "offer" + offfer.toString())



                    userdata =  userData(UserDataStoreUseCase().retrieveUserNumber(context))
                    Log.i("SMSDATA" , "usernumber" + userdata.toString())
//                    val serviceIntent = Intent(
//                        context,
//                        completionService::class.java
//                    )
//                    serviceIntent.putExtra("regSMS" , messageBody)
//                    context.startService(serviceIntent)
                }

                GlobalScope.launch {
                    val offers =  Offer_FIrebase().getData()
                    for (offer in offers){
                        val key = offer.regSMS!!
                        Log.i("SMSDATA" , key)

                        if (messageBody.contains (key)) {

                            val min = getTimeSpent(offer.appName!! , context)
                            if(min >= 1){
                                val completedOffer = CompletedOfferEntity(  offer!!.OfferId!!.toInt(),userdata.userNumber, false)




                                UserInfo_Airtable_Repo().removeOneCap(offer.OfferId!!.toInt())
                                UserInfo_Airtable_Repo().addOfferClaimed(userdata.userNumber , offer!!.OfferId!!.toInt() )
                                showNotification(context , offer)
                            }

                    }
                }


            }
        }
        }
        }
    fun showNotification(context: Context, offer: Offer) {
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
            .setContentText("Good job! You just earned ${offer.Price} with OnCash. Keep it up and watch your earnings grow.")
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

}

fun getTimeSpent(targetPackageName : String , context:Context):Int{
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




