package `in`.oncash.oncash.Component

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.DataType.walletDatatype
import `in`.oncash.oncash.R
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import `in`.oncash.oncash.RoomDb.userDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseDatabaseService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start listening to Firebase Realtime Database changes
        startFirebaseDatabaseListener()
        return START_STICKY
    }

    private fun startFirebaseDatabaseListener() {
        // Set up your Firebase Realtime Database listener here
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Referral")
        Log.i("FirebaseDataLog" , "working")
        CoroutineScope(Dispatchers.IO).launch {
            val roomDb = Room.databaseBuilder(
                applicationContext,
                userDb::class.java,
                "User"
            ).fallbackToDestructiveMigration() // Add this line for destructive migration
                .build()
            val number = roomDb.userQuery().getUserNumber()

           val code =  UserInfo_Airtable_Repo().getReferralCodee(number)
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (snapshot in dataSnapshot.children) {
                        if (snapshot.key!!.toInt() == code) {
                            val channelName = "My Notification Channel"
                            val channelId = "my_notification_channel"

                            val notificationManager = NotificationManagerCompat.from(this@FirebaseDatabaseService!!)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val channel = NotificationChannel(
                                    channelId,
                                    channelName,
                                    NotificationManager.IMPORTANCE_DEFAULT
                                )
                                notificationManager.createNotificationChannel(channel)
                            }
                            val notificationId = 1 // Unique ID for the notification

                            val notificationBuilder =
                                NotificationCompat.Builder(this@FirebaseDatabaseService!!, channelId)
                                    .setSmallIcon(R.drawable.oncash)
                                    .setContentTitle("Installation Completed!!")
                                    .setContentText("Good job! You just half way to earn  with OnCash. ")
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setAutoCancel(true) // Removes the notification when tapped


                            if (ActivityCompat.checkSelfPermission(
                                    this@FirebaseDatabaseService!!,
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
                            }
                            notificationManager.notify(notificationId, notificationBuilder.build())

                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
        }
    }
}
