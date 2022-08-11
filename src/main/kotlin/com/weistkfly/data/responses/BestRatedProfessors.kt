package com.weistkfly.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class BestRatedProfessors(
    val fullName: String,
    val reviewCount: Int,
    val avgRating: Double
)
