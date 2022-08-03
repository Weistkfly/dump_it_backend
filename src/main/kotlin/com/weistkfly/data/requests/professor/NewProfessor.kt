package com.weistkfly.data.requests.professor

import kotlinx.serialization.Serializable

@Serializable
data class NewProfessor(
    val name: String,
    val lastName: String,
    val school: String
)
