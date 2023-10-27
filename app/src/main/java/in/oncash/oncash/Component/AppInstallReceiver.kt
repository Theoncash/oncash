import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import `in`.oncash.oncash.DataType.Offer
import `in`.oncash.oncash.R

class AppInstallReceiver (appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {

        Log.i("pwe" , "Periodic work executed!")

        val appName = inputData.getString("appName")
        val name = inputData.getString("name")
        if(isAppInstalled(applicationContext , appName !!))
        {
            showNotification(applicationContext , name!!)
            return Result.success()

        }
        return Result.retry()

    }
}


fun isAppInstalled(context: Context , appName: String): Boolean {
    // get list of all the apps installed
    // get list of all the apps installed
    val pm: PackageManager = context.packageManager
    try {
        val packageInfo = pm.getInstalledApplications(0)

        for(app in packageInfo){
            if(app.packageName == appName){
                return true
            }
        }

    } catch (e: PackageManager.NameNotFoundException) {
        // The app is not installed.
        Toast.makeText(context , "packageInfo.applicationInfo.packageName" , Toast.LENGTH_LONG).show()

        return false
    }
    return false
}

fun showNotification(context: Context, name: String) {
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
        .setContentTitle("$name Installation Completed ")
        .setContentText("Good job! Hurry up. You are just few steps away to with OnCash.")
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


