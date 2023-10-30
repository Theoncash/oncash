package `in`.oncash.oncash.RoomDb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_checker")
data class NotificationChecker(
    @PrimaryKey val offerId: Int,
    val isNotificationSent: Boolean
)
