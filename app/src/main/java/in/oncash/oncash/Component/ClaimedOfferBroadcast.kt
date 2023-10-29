package `in`.oncash.oncash.Component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log


class ClaimedOfferBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val connectivityManager =
                context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            Log.i("SMSDATA", "internet")

            if (networkInfo != null && networkInfo!!.isConnected) {
                val serviceIntent = Intent(
                    context,
                    service::class.java
                )
                context!!.startService(serviceIntent)


            } else {
                // The device does not have internet connectivity
                // You can perform actions here when internet connectivity is lost.
            }
        }
    }
}
