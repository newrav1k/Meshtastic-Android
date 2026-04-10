package org.meshtastic.core.network.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import org.koin.core.annotation.Single
import org.meshtastic.core.model.AccessToken
import org.meshtastic.core.model.AuthorityResponse
import org.meshtastic.core.model.AuthorizationRequest
import org.meshtastic.core.repository.AuthRepository
import org.meshtastic.core.repository.AuthSession
import org.meshtastic.core.repository.AuthSessionRepository

@Single(binds = [AuthRepository::class])
class AuthRepositoryImpl(
    private val client: HttpClient,
    private val sessionRepository: AuthSessionRepository,
) : AuthRepository {

    companion object {
        private const val DEFAULT_SERVER_ADDRESS = "http://192.168.1.66:8081"
    }

    override suspend fun login(username: String, password: String): AuthSession {
        val token = client.post("$DEFAULT_SERVER_ADDRESS/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(AuthorizationRequest(username, password))
        }.body<AccessToken>()

        val authority = client.get("$DEFAULT_SERVER_ADDRESS/api/me") {
            header(HttpHeaders.Authorization, "Bearer ${token.accessToken}")
        }.body<AuthorityResponse>().authority

        sessionRepository.saveSession(
            token = token.accessToken,
            expiresAt = token.expiresAt,
            authority = authority,
        )

        return AuthSession(
            accessToken = token.accessToken,
            expiresAt = token.expiresAt,
            authority = authority
        )
    }

}