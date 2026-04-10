package org.meshtastic.core.repository

interface AuthRepository {

    suspend fun login(username: String, password: String): AuthSession

}