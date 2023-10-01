package `in`.oncash.oncash.Component

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import `in`.oncash.oncash.R
import `in`.oncash.oncash.View.Home

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle the incoming FCM message and display a notification.
        val notificationData = remoteMessage.data

        val notificationTitle = notificationData["title"]
        val notificationBody = notificationData["body"]

        // Create and display the notification
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "your_channel_id"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel Name", NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.oncash)
            .setContentTitle(notificationTitle)
            .setContentText(notificationBody)
            .setAutoCancel(true)

        val resultIntent = Intent(this, Home::class.java) // Replace with the desired activity

        val resultPendingIntent = PendingIntent.getActivity(
            this,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        notificationBuilder.setContentIntent(resultPendingIntent)

        val notificationId = 0 // You can use a unique ID for each notification
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        // Get updated registration token and update it in your server.
    }



}