package org.meshtastic.core.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import org.meshtastic.core.model.Authority
import org.meshtastic.core.repository.AuthPrefs

@Single(binds = [AuthPrefs::class])
class AuthPrefsImpl(
    private val dataStore: DataStore<Preferences>,
) : AuthPrefs {

    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("auth.access_token")
        val EXPIRES_AT = stringPreferencesKey("auth.expires_at")
        val AUTHORITY = stringPreferencesKey("auth.authority")
    }

    override val accessToken: Flow<String?> =
        dataStore.data.map { it[ACCESS_TOKEN] }

    override val expiresAt: Flow<String?> =
        dataStore.data.map { it[EXPIRES_AT] }

    override val authority: Flow<Authority?> =
        dataStore.data.map { prefs ->
            prefs[AUTHORITY]?.let { raw ->
                runCatching { Authority.valueOf(raw) }.getOrNull()
            }
        }

    override suspend fun setAccessToken(token: String?) {
        dataStore.edit {
            if (token == null) it.remove(ACCESS_TOKEN) else it[ACCESS_TOKEN] = token
        }
    }

    override suspend fun setExpiresAt(value: String?) {
        dataStore.edit {
            if (value == null) it.remove(EXPIRES_AT) else it[EXPIRES_AT] = value
        }
    }

    override suspend fun setAuthority(value: Authority?) {
        dataStore.edit {
            if (value == null) it.remove(AUTHORITY) else it[AUTHORITY] = value.name
        }
    }

}