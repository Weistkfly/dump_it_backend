package com.weistkfly.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val username: String
)
