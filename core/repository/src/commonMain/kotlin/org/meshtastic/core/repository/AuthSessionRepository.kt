package org.meshtastic.core.repository

import org.meshtastic.core.model.Authority

interface AuthSessionRepository {

    suspend fun saveSession(
        token: String,
        expiresAt: String,
        authority: Authority,
    )

    suspend fun getSession(): AuthSession?

    suspend fun clear()

}