package com.weistkfly.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class ProfessorResponse(
    val fullName: String,
    val reviewCount: Int,
    val avgRating: Double,
    val school: String,
    val avgDifficulty: Double,
    val excellent: Int,
    val veryGood: Int,
    val good: Int,
    val notGood: Int,
    val bad: Int
)
