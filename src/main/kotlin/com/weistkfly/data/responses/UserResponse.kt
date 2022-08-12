package com.weistkfly.data.responses

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class UserResponse(
    val email: String,
    val name: String,
    val lastName: String,
    val school: String,
    val givenLikes: Int,
    val ratingsMade: Int,
    val wasMadeOn: Long,
    val iconId: Int,
    @BsonId val id: String = ObjectId().toString()
)
