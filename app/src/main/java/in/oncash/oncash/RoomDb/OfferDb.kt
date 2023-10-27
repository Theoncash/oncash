package `in`.oncash.oncash.RoomDb

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Database
import androidx.room.RoomDatabase

// Define the entity for your table
@Entity(tableName = "offers")
data class OfferEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val regSms: String,
    val offerId: Int,
    val appName: String,
    val appPrice : Int
)

// Create a DAO (Data Access Object) to define database operations
@Dao
interface OfferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(offer: OfferEntity)

    @Query("SELECT * FROM offers WHERE offerId = :offerId")
    suspend fun getOfferById(offerId: Int): OfferEntity?

    @Query("SELECT * FROM offers")
    suspend fun getAllOffers(): List<OfferEntity>
}

// Define your Room database
@Database(entities = [OfferEntity::class], version = 1)
abstract class OfferDb : RoomDatabase() {
    abstract fun offerDao(): OfferDao
}
