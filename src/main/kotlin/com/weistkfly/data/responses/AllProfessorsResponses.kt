package com.weistkfly.data.responses

import com.weistkfly.data.professor.Professor
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class AllProfessorsResponses(
    val professors: List<Professor?>
)
