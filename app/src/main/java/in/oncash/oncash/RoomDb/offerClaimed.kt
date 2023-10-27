package `in`.oncash.oncash.RoomDb

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Database
import androidx.room.RoomDatabase
import `in`.oncash.oncash.RoomDb.CompletedOfferDao
import `in`.oncash.oncash.RoomDb.CompletedOfferEntity

// Define your Room database
@Database(entities = [CompletedOfferEntity::class], version = 1)
abstract class offerClaimed : RoomDatabase() {
    abstract fun completedOfferDao(): CompletedOfferDao
}

// Define the entity for your table


// Create a DAO (Data Access Object) to define database operations
