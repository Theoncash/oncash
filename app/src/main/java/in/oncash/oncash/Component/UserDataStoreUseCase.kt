package `in`.oncash.oncash.Component

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import `in`.oncash.oncash.DataType.Offers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore


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


    private inline fun <reified T> fromJson(json: String): T {
        return Gson().fromJson(json, T::class.java)
    }

    private inline fun <reified T> toJson(obj: T): String {
        return Gson().toJson(obj)
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
