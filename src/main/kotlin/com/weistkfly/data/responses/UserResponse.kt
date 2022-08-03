package com.weistkfly.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val email: String,
    val name: String,
    val lastName: String,
    val school: String
)
