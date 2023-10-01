package `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory

@kotlinx.serialization.Serializable
data class Fields( val UserId : Long , val OfferId : Int , val Status:String , val Payout :Int )