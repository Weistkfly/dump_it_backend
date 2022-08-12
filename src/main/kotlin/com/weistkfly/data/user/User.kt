package com.weistkfly.data.user

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId


data class User(
    val email: String,
    val password: String,
    val name: String,
    val lastName: String,
    val school: String,
    val salt: String,
    val givenLikes: Int,
    var ratingsMade: Int,
    val wasMadeOn: Long,
    val iconId: Int,
    @BsonId val id: String = ObjectId().toString()
)
