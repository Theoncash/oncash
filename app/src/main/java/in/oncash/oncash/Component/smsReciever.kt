package `in`.oncash.oncash.Component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log

class smsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            Log.d("SMSReceiver", "Received SMS")

            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as? Array<ByteArray>
                if (pdus != null) {
                    for (pdu in pdus) {
                        val smsMessage = SmsMessage.createFromPdu(pdu)
                        val sender = smsMessage.displayOriginatingAddress
                        val message = smsMessage.displayMessageBody

                        // Handle the received SMS message here
                        Log.d("SMSReceiver", "Sender: $sender")
                        Log.d("SMSReceiver", "Message: $message")
                    }
                }
            }
        }
    }
}

//    override fun onReceive(context: Context, intent: Intent) {
//        if (intent.action != null && intent.action == "android.provider.Telephony.SMS_RECEIVED") {
//            val bundle = intent.extras
//            if (bundle != null) {
//                val pdus = bundle["pdus"] as Array<Any>?
//                if (pdus != null) {
//                    for (pdu in pdus) {
//                        val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
//                        val sender = smsMessage.displayOriginatingAddress
//                        val message = smsMessage.displayMessageBody
//
//                        // Do something with the SMS data, e.g., display it in a Toast
//                       Log.i("DataFromSMS", "SMS from $sender: $message")
//                        GlobalScope.launch {
//                            val offers =  Offer_FIrebase().getData()
//                            for (offer in offers){
//                                val key = offer.regSMS!!
//                                if (message.contains(key)) {
//                                    GlobalScope.launch {
//                                        UserInfo_Airtable_Repo().removeOneCap(offer.OfferId!!.toInt())
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

