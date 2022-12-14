package com.weistkfly.data.professor

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
@Serializable
data class Professor(
    val fullName: String,
    var reviewCount: Int,
    var avgRating: Double,
    val school: String,
    var avgDifficulty: Double,
    var excellent: Int,
    var veryGood: Int,
    var good: Int,
    var notGood: Int,
    var bad: Int,
    val staredTags: List<String>,
    @BsonId
    val id: String = ObjectId().toString()
)
