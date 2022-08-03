package com.weistkfly.data.user

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    val email: String,
    val password: String,
    val name: String,
    val lastName: String,
    val school: String,
    val salt: String,
//    val givenLikes: Int,
//    val ratingsMade: Int,
    @BsonId val id: String = ObjectId().toString()
)
