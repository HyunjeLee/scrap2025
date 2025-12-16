package com.scrap2025.scrap2025.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tinkManager: TinkManager
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    val accessToken: Flow<String?> = context.authDataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]?.let { encrypted ->
            if (encrypted.isNotEmpty()) tinkManager.decrypt(encrypted).takeIf { it.isNotEmpty() } else null
        }
    }

    val refreshToken: Flow<String?> = context.authDataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]?.let { encrypted ->
            if (encrypted.isNotEmpty()) tinkManager.decrypt(encrypted).takeIf { it.isNotEmpty() } else null
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        val encryptedAccess = tinkManager.encrypt(accessToken)
        val encryptedRefresh = tinkManager.encrypt(refreshToken)

        context.authDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = encryptedAccess
            preferences[REFRESH_TOKEN_KEY] = encryptedRefresh
        }
    }

    suspend fun clearTokens() {
        context.authDataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }
}
