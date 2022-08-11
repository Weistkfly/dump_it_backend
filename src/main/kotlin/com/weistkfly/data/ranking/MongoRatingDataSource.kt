package com.weistkfly.data.ranking

import com.weistkfly.data.professor.Professor
import com.weistkfly.data.user.User
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoRatingDataSource(
    db: CoroutineDatabase
): RatingDataSource {

    private val ratings = db.getCollection<Rating>()
    private val users = db.getCollection<User>()

    override suspend fun getAllRatings(): List<Rating> {
        return ratings.find().descendingSort(Rating::wasMadeOn).toList()
    }

    override suspend fun insertRating(rating: Rating): Boolean {
        return ratings.insertOne(rating).wasAcknowledged()
    }

    override suspend fun getRatingsByProfessor(professorId: String): List<Rating?> {
        return ratings.find(Rating::professorId eq professorId)
            .descendingSort(Rating::wasMadeOn)
            .toList()
    }

    override suspend fun getRatingsByUser(userId: String): List<Rating?> {
        return ratings.find(Rating::userId eq userId)
            .descendingSort(Rating::wasMadeOn)
            .toList()
    }

    override suspend fun getRating(ratingId: String): Rating? {
        return ratings.findOneById(ratingId)
    }

    override suspend fun editRating(ratingId: String, rating: Rating): Boolean {
        return ratings.replaceOneById(ratingId, rating)
            .wasAcknowledged()
    }

    override suspend fun likeRating(ratingId: String, wasLiked: Boolean): Boolean {
        val rating = ratings.findOneById(ratingId)

        if (!wasLiked && rating!!.likeCount > 0){
            return ratings.updateOneById(ratingId, setValue(Rating::likeCount, rating.likeCount - 1))
                .wasAcknowledged()
        } else if (wasLiked)
        return ratings.updateOneById(ratingId, setValue(Rating::likeCount, rating!!.likeCount + 1))
            .wasAcknowledged()

        return false
    }

    override suspend fun deleteRating(ratingId: String): Boolean {
        return ratings.deleteOneById(ratingId).wasAcknowledged()
    }

}