package com.weistkfly.data.requests.rates

import kotlinx.serialization.Serializable

@Serializable
data class LikeRateRequest(
    val wasLiked: Boolean,
    val rateId: String
)
