package com.example.quozo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.quozo.R
import com.example.quozo.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class DataStoreRepository(
    private val context: Context
) {

    val AVATAR_ID = intPreferencesKey("avatarId")
    val USER_NAME = stringPreferencesKey("userName")

    val userFlow: Flow<User> = context.dataStore.data.map { preferences ->
        User(
            avatarId = preferences[AVATAR_ID] ?: R.drawable.avatar_male,
            name = preferences[USER_NAME] ?: "DataStoreUser"
        )
    }

    suspend fun updateAvatar(avatarId: Int){
        context.dataStore.edit { preferences ->
            preferences[AVATAR_ID] = avatarId
        }
    }

    suspend fun updateUserName(name: String){
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }
}