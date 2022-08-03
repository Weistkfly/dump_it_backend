package com.weistkfly.data.ranking

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Rating(
    val tag: List<String>,
    val subject: String,
    val userGrade: Int,
    val modality: String,
    val subjectCredits: Int,
    val Rate: Double,
    val difficulty: Double,
    val likeCount: Int,
    val userId: String,
    val professorId: String,
    @BsonId
    val id: String = ObjectId().toString()
)
