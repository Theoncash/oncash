package `in`.oncash.oncash.DataType

import kotlinx.serialization.Serializable

@Serializable
data class Offers
    (
    val offerId: Int,
    val regSms: String,
    val appName: String,
    val appPrice : Int
            ){
}