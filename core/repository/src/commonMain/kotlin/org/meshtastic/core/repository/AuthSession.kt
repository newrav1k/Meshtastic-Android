package org.meshtastic.core.repository

data class AuthSession(

    val accessToken: String,

    val expiresAt: String,

    val authority: String,

)