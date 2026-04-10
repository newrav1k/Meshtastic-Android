package org.meshtastic.core.repository

interface AuthSessionRepository {

    suspend fun saveSession(
        token: String,
        expiresAt: String,
        authority: String,
    )

    suspend fun getSession(): AuthSession?

    suspend fun clear()

}