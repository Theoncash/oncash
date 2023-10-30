package `in`.oncash.oncash.RoomDb

import android.content.Context
import androidx.room.*


@Dao
interface NotificationCheckerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(notificationChecker: NotificationChecker)

    @Query("SELECT * FROM notification_checker WHERE offerId = :id")
    suspend fun getNotificationCheckerById(id: Int): NotificationChecker?
}

@Database(entities = [NotificationChecker::class], version = 1, exportSchema = false)
abstract class notification_checker : RoomDatabase() {
    abstract fun notificationCheckerDao(): NotificationCheckerDao
}


