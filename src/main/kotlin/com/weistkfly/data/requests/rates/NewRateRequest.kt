package com.weistkfly.data.requests.rates

data class NewRateRequest(
    val tag: List<String>,
    val subject: String,
    val userGrade: Int,
    val modality: String,
    val subjectCredits: Int,
    val Rate: Double,
    val difficulty: Double,
    val likeCount: Int,
    val userId: String,
    val professorId: String
)
