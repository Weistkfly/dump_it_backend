package com.weistkfly.data.requests.user

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val email: String
)
