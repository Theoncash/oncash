package `in`.oncash.oncash.DataType
import kotlinx.serialization.Serializable

@Serializable
data class FieldsX(
    val UserNumber: Long,
    val WalletBalance: Int,
    val Status: String
)