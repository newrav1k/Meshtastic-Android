package org.meshtastic.core.repository

import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single
import org.meshtastic.core.model.Authority

@Single(binds = [AuthSessionRepository::class])
class AuthSessionRepositoryImpl(
    private val authPrefs: AuthPrefs,
) : AuthSessionRepository {

    override suspend fun saveSession(
        token: String,
        expiresAt: String,
        authority: Authority,
    ) {
        authPrefs.setAccessToken(token)
        authPrefs.setExpiresAt(expiresAt)
        authPrefs.setAuthority(authority)
    }

    override suspend fun getSession(): AuthSession? {
        val token = authPrefs.accessToken.first()
        val expiresAt = authPrefs.expiresAt.first()
        val authority = authPrefs.authority.first()

        if (token.isNullOrBlank() || expiresAt.isNullOrBlank() || authority == null) {
            return null
        }

        return AuthSession(
            accessToken = token,
            expiresAt = expiresAt,
            authority = authority,
        )
    }

    override suspend fun clear() {
        authPrefs.setAccessToken(null)
        authPrefs.setExpiresAt(null)
        authPrefs.setAuthority(null)
    }

}