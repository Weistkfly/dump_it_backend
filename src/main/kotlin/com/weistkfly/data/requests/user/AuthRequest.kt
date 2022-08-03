package com.weistkfly.data.requests.user

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
    val name: String,
    val lastName: String,
    val school: String
)
