package `in`.oncash.oncash.Component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import `in`.oncash.oncash.DataType.SerializedDataType.Blacklist.BlackList_Fields
import `in`.oncash.oncash.DataType.SerializedDataType.test
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.Repository.Offer_FIrebase
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class checkRegistration : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            val pdus = intent.extras?.get("pdus") as Array<*>
            for (pdu in pdus) {
                val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                val messageBody = smsMessage.messageBody
                // Check the message content and take appropriate action




                GlobalScope.launch {
                    val offers =  Offer_FIrebase().getData()
                    for (offer in offers){
                        val key = offer.regSMS!!
                        Log.i("SMSDATA" , key)

                        if (messageBody.contains(key)) {
                            GlobalScope.launch {
                                Log.i("SMSDATA" , "sENDING REQUEST"+key)

                                UserInfo_Airtable_Repo().removeOneCap(offer.OfferId!!.toInt())
                            }
                        }
                    }
                }


            }
        }
        }
        }





