package com.weistkfly.data.requests.rates

import kotlinx.serialization.Serializable

@Serializable
data class NewRateRequest(
    val tag: List<String>,
    val subject: String,
    val userGrade: Int,
    val modality: String,
    val subjectCredits: Int,
    val rate: Int,
    val difficulty: Int,
    val professorId: String,
    val wouldTakeAgain: Boolean
)
