package com.weistkfly.data.requests.user

import kotlinx.serialization.Serializable


@Serializable
data class NewIconRequest(
    val newIconId: Int
)
