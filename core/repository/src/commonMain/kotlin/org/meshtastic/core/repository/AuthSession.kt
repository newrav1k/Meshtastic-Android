package org.meshtastic.core.repository

import org.meshtastic.core.model.Authority

data class AuthSession(

    val accessToken: String,

    val expiresAt: String,

    val authority: Authority,

)