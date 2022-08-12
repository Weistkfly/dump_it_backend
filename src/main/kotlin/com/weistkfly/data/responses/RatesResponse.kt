package com.weistkfly.data.responses

import com.weistkfly.data.ranking.Rating
import kotlinx.serialization.Serializable

@Serializable
data class RatesResponse(
    val rates: List<Rating?>
)
