package `in`.oncash.oncash.DataType.SerializedDataType

import kotlinx.serialization.Serializable

@Serializable
data class Referred (val UserId :Long , val Referred_code :Int)