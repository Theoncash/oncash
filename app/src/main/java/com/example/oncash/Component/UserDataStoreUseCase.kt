package com.example.oncash.Component

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.first

class UserDataStoreUseCase {

    suspend fun retrieveUser(dataStore: DataStore<Preferences>) : Boolean?{
        return  dataStore.data.first()[booleanPreferencesKey("isUserLogin")]

    }
    suspend fun storeUser(dataStore: DataStore<Preferences>, bool :Boolean, userNumber: Long) {
        dataStore.edit {
            it[booleanPreferencesKey("isUserLogin")] = bool
            it[longPreferencesKey("userNumber")] = userNumber
        }
        Log.i("userData" , retrieveUser(dataStore).toString())  }

}