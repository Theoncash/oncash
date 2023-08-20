package `in`.oncash.oncash.DataType
@kotlinx.serialization.Serializable
data class PerformUpsert(
    val fieldsToMergeOn: List<String>
)