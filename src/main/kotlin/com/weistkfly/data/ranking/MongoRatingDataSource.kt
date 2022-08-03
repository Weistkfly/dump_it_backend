package com.weistkfly.data.ranking

import com.weistkfly.data.professor.Professor
import com.weistkfly.data.user.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.insertOne
import org.litote.kmongo.setValue

class MongoRatingDataSource(
    db: CoroutineDatabase
): RatingDataSource {

    private val ratings = db.getCollection<Rating>()

    override suspend fun getAllRatings(): List<Rating> {
        return ratings.find().toList()
    }

    override suspend fun insertRating(rating: Rating): Boolean {
        return ratings.insertOne(rating).wasAcknowledged()
    }

    override suspend fun getRatingsByProfessor(professorId: String): List<Rating?> {
        return ratings.find(Rating::professorId eq professorId).toList()
    }

    override suspend fun getRatingsByUser(userId: String): List<Rating?> {
        return ratings.find(Rating::userId eq userId).toList()
    }

    override suspend fun getRating(ratingId: String): Rating? {
        return ratings.findOneById(ratingId)
    }

    override suspend fun editRating(ratingId: String, rating: Rating): Boolean {
        return ratings.replaceOneById(ratingId, rating)
            .wasAcknowledged()
    }

    override suspend fun likeRating(ratingId: String): Boolean {
        val rating = ratings.findOneById(ratingId)

        return ratings.updateOneById(ratingId, setValue(Rating::likeCount, rating!!.likeCount + 1))
            .wasAcknowledged()
    }

    override suspend fun deleteRating(ratingId: String): Boolean {
        return ratings.deleteOneById(ratingId).wasAcknowledged()
    }

}