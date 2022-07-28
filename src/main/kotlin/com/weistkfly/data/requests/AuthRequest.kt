package com.weistkfly.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val email: String,
    val username: String,
    val password: String,
    val name: String,
    val lastName: String,
    val phoneNumber: String,
    val sex: String,
    val birthDate: String,
    val country: String,
    val state: String,
    val address: String
)
