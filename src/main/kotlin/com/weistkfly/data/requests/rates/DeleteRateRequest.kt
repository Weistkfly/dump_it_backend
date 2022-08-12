package com.weistkfly.data.requests.rates

import kotlinx.serialization.Serializable

@Serializable
data class DeleteRateRequest(
    val rateId: String
)
