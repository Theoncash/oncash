package `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory

@kotlinx.serialization.Serializable
data class Fields( val UserId : String , val OfferId : String , val Status:String , val Payout :String , val OfferName : String)