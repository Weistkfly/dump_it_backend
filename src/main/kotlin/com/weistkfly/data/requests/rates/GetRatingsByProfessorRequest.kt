package com.weistkfly.data.requests.rates

import kotlinx.serialization.Serializable

@Serializable
data class GetRatingsByProfessorRequest(
    val professorId: String
)
