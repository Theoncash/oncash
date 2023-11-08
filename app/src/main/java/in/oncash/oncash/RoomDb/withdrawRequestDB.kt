package `in`.oncash.oncash.RoomDb


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "withdrawal_request_table")
data class WithdrawalRequestEntity(
    @PrimaryKey
    val UserNumber: Long,
    val WalletBalance: Int,
    val Status: String,
    val isDisplayed: Boolean
)

@Dao
interface WithdrawalRequestDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWithdrawalRequest(request: WithdrawalRequestEntity)

    @Update
    suspend fun updateWithdrawalRequest(request: WithdrawalRequestEntity)

    @Query("SELECT * FROM withdrawal_request_table")
    fun getAllWithdrawalRequests(): LiveData<List<WithdrawalRequestEntity>>
}

@Database(entities = [WithdrawalRequestEntity::class], version = 1, exportSchema = false)
abstract class withdrawRequestDB : RoomDatabase() {
    abstract fun withdrawalRequestDao(): WithdrawalRequestDao

    companion object {
        @Volatile
        private var INSTANCE: withdrawRequestDB? = null

        fun getDatabase(context: Context): withdrawRequestDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    withdrawRequestDB::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
