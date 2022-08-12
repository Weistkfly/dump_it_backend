package com.weistkfly.data.requests.rates

import com.weistkfly.data.ranking.Rating
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRateRequest(
    val rate: Rating
)
