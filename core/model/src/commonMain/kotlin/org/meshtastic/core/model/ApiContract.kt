package org.meshtastic.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class Authority {
    ROLE_DRIVER,
    ROLE_DISPATCHER
}

@Serializable
data class AuthorizationRequest(
    val username: String,
    val password: String
)

@Serializable
data class AccessToken(
    val accessToken: String,
    val expiresAt: String
)

@Serializable
data class AuthorityResponse(
    val authority: String
)