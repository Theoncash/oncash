package `in`.oncash.oncash.RoomDb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_offers")
data class CompletedOfferEntity(
    @PrimaryKey( ) val offerId: Int,
    val userId: Long,
    val claimed: Boolean
)