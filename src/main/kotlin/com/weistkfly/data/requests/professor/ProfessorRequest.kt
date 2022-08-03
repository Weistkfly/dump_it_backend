package com.weistkfly.data.requests.professor

import kotlinx.serialization.Serializable

@Serializable
data class ProfessorRequest(
    val fullName: String
)
