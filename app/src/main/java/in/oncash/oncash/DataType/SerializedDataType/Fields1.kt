package `in`.oncash.oncash.DataType.SerializedDataType
import kotlinx.serialization.Serializable

@Serializable
data class Fields1(
    val UserPhone: Long,
    val Wallet: Int,
    val Total_Bal :Int,
    val Name:String?
)