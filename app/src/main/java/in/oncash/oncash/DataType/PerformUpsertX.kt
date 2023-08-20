package `in`.oncash.oncash.DataType
@kotlinx.serialization.Serializable
data class PerformUpsertX(
    val fieldsToMergeOn: List<String>
)