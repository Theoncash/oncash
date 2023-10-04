package `in`.oncash.oncash.DataType.SerializedDataType

import kotlinx.serialization.Serializable

@Serializable
data class OfferInfo(val OfferId :Int , val OfferName :String?, val Cap :Int)