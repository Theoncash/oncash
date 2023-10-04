package `in`.oncash.oncash.Component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import `in`.oncash.oncash.Repository.Offer_FIrebase
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pdus = intent.extras?.get("pdus") as Array<*>
        for (pdu in pdus) {
            val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
            val messageBody = smsMessage.messageBody
            // Check the message content and take appropriate action

            GlobalScope.launch {
               val offers =  Offer_FIrebase().getData()
                for (offer in offers){
                    val key = offer.regSMS!!
                    if (messageBody.contains(key)) {
                        GlobalScope.launch {
                            UserInfo_Airtable_Repo().removeOneCap(offer.OfferId!!.toInt())
                        }
                    }
                }
            }


        }
    }
}
