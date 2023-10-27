package `in`.oncash.oncash.DataType.SerializedDataType

import kotlinx.serialization.Serializable

@Serializable
data class OfferClaimed(val OfferId :Int , val UserId :Long, val Claimed :Boolean )