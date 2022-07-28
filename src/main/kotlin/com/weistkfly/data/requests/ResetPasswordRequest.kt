package com.weistkfly.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val username: String,
    val email: String
)
