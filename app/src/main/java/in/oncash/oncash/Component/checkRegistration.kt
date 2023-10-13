package `in`.oncash.oncash.Component

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import `in`.oncash.oncash.DataType.Offer
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.DataType.walletDatatype
import `in`.oncash.oncash.R
import `in`.oncash.oncash.Repository.Offer_FIrebase
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class checkRegistration : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        val c = context
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            val pdus = intent.extras?.get("pdus") as Array<*>
            for (pdu in pdus) {
                val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                val messageBody = smsMessage.messageBody
                // Check the message content and take appropriate action


//                GlobalScope.launch {
//
//                    userData(UserDataStoreUseCase().retrieveUserNumber(context))
//                    val serviceIntent = Intent(
//                        context,
//                        service::class.java
//                    )
//                    context.startService(serviceIntent)
//                }

                GlobalScope.launch {
                    val offers =  Offer_FIrebase().getData()
                    for (offer in offers){
                        val key = offer.regSMS!!
                        Log.i("SMSDATA" , key)

                        if (messageBody.contains(key)) {
                            UserInfo_Airtable_Repo().removeOneCap(offer.OfferId!!.toInt())
                            showNotification(context , offer)




                    }
                }


            }
        }
        }
        }}



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


