package `in`.oncash.oncash.Component

import android.content.Context
import android.net.Uri
import androidx.work.Worker
import androidx.work.WorkerParameters
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class checkRegistered(context: Context, workerParams: WorkerParameters) : Worker( context , workerParams  ) {
    val context :Context = context
     fun isRegistered(context: Context, regSMS :String) : Boolean{
        val inboxSms = ArrayList<String>()
        val uri = Uri.parse("content://sms/inbox")
        val cursor = context.contentResolver.query(uri, null, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val bodyIndex = cursor.getColumnIndex("body")
            do {
                val smsBody = cursor.getString(bodyIndex)
                inboxSms.add(smsBody)
            } while (cursor.moveToNext())
            cursor.close()
        }
        // Assuming you have already retrieved SMS messages and stored them in the 'inboxSms' list
        var messageFound = false

        for (smsBody in inboxSms) {

            if (smsBody.lowercase() .contains(regSMS.toString().lowercase(), ignoreCase = true)) {
                // The message contains the search string
                messageFound = true
                break  // Exit the loop once a matching message is found
            }
        }

        return messageFound
    }


    override  fun doWork(): Result {
        try {
            val inputData = getInputData()
            val appName = inputData.getString("appId")
            val regSMS = inputData.getString("regSms")

            if (appName != null && regSMS != null) {
                val isReg = isRegistered(applicationContext, regSMS)
                if (isReg) {
                    // Perform your task here, e.g., remove something from an Airtable repository
                    GlobalScope.launch {
                        UserInfo_Airtable_Repo().removeOneCap(appName.toInt())
                    }
                }
            } else {
                // Handle missing input data
                return Result.failure()
            }

            return Result.success()
        } catch (e: Exception) {
            // Handle any exceptions that may occur
            return Result.failure()
        }
    }

}