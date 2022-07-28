package com.weistkfly.data.user

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    val email: String,
    val username: String,
    val password: String,
    val name: String,
    val lastName: String,
    val phoneNumber: String,
    val sex: String,
    val birthDate: String,
    val country: String,
    val state: String,
    val address: String,
    val salt: String,
    @BsonId val id: String = ObjectId().toString()
)
