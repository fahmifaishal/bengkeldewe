package com.negarfahmifaishal.bengkeldewe.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthPreferences(private val context: Context) {

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val EMAIL = stringPreferencesKey("email")
        private val NAME = stringPreferencesKey("name")
        private val PROFILE_PHOTO_URL = stringPreferencesKey("profile_photo_url")
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    val emailFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[EMAIL] ?: ""
    }

    val nameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[NAME] ?: ""
    }

    val profilePhotoUrlFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PROFILE_PHOTO_URL] ?: ""
    }

    suspend fun saveSession(email: String, name: String, photoUrl: String = "") {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[EMAIL] = email
            preferences[NAME] = name
            preferences[PROFILE_PHOTO_URL] = photoUrl
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences[EMAIL] = ""
            preferences[NAME] = ""
            preferences[PROFILE_PHOTO_URL] = ""
        }
    }
}
