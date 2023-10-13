package `in`.oncash.oncash.Component

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle incoming FCM messages and display notifications
        val notification = remoteMessage.notification
        if (notification != null) {
            // Create and display a notification
        }
    }
}
