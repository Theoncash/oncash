package `in`.oncash.oncash.Component

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private val Context.dataStore by preferencesDataStore(
    name = "UserData"
)

class UserDataStoreUseCase {

    suspend fun retrieveUser(context: Context): Boolean ? = withContext(Dispatchers.IO){
        return@withContext context.dataStore.data.first()[booleanPreferencesKey("isUserLogin")]
    }

    suspend fun retrieveUserNumber(context: Context): Long = withContext(Dispatchers.IO){
        return@withContext context.dataStore.data.first()[longPreferencesKey("userNumber")]!!
    }
    suspend fun retrieveEndTime(context: Context): Long = withContext(Dispatchers.IO){
        return@withContext context.dataStore.data.first()[longPreferencesKey("endTime")]!!
    }

    suspend fun retrieveUserRecordId(context: Context): String = withContext(Dispatchers.IO){
        return@withContext context.dataStore.data.first()[stringPreferencesKey("userRecordId")]!!
    }

    suspend fun storeUser(context: Context, bool: Boolean, userNumber: Long ) = withContext(Dispatchers.IO){
      context.dataStore.edit{
          it[longPreferencesKey("userNumber")] = userNumber
          it[booleanPreferencesKey("isUserLogin")] = bool
      }
    }

    suspend fun storeTimer(context: Context, endTime : Long) = withContext(Dispatchers.IO){
        context.dataStore.edit{
            it[ longPreferencesKey("endTime")] = endTime
        }
    }
}
