package `in`.oncash.oncash.RoomDb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CompletedOfferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(completedOffer: CompletedOfferEntity)

    @Query("SELECT * FROM completed_offers")
    suspend fun getCompletedOffersByUser(): List<CompletedOfferEntity>

    @Query("DELETE FROM completed_offers WHERE offerId = :offerId")
    suspend fun removeCompletedOffer(offerId: Int)
}

