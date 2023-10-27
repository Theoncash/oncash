package `in`.oncash.oncash.RoomDb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_offers")
data class CompletedOfferEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Long,
    val offerId: Int,
    val claimed: Boolean
)