package com.weistkfly.data.ranking

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Rating(
    val tag: List<String>,
    val subject: String,
    val userGrade: Int,
    val modality: String,
    val subjectCredits: Int,
    val Rate: Int,
    val difficulty: Int,
    val likeCount: Int,
    val userId: String,
    val professorId: String,
    val wasMadeOn: Long,
    val wouldTakeAgain: Boolean,
    @BsonId
    val id: String = ObjectId().toString()
)
