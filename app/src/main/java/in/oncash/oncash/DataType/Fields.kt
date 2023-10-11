package `in`.oncash.oncash.DataType
import kotlinx.serialization.Serializable

@Serializable
data class Fields(
    val UserPhone: Long,
    val Wallet: Int,
    val Total_Bal :Int

)