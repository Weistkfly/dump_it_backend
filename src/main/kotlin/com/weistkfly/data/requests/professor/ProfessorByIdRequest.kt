package com.weistkfly.data.requests.professor

import kotlinx.serialization.Serializable

@Serializable
data class ProfessorByIdRequest(
    val professorId: String
)
