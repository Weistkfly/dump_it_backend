package com.weistkfly.data.requests.professor

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfessorRequest(
    val fullName: String,
    var reviewCount: Int,
    var avgRating: Double,
    val school: String,
    var avgDifficulty: Double,
    var excellent: Int,
    var veryGood: Int,
    var good: Int,
    var notGood: Int,
    var bad: Int,
    val staredTags: List<String>,
    val id: String
)
