package com.weistkfly.data.ranking

interface RatingDataSource {

    suspend fun getAllRatings(): List<Rating>

    suspend fun insertRating(rating: Rating): Boolean

    suspend fun getRatingsByProfessor(professorId: String): List<Rating?>

    suspend fun getRatingsByUser(userId: String): List<Rating?>

    suspend fun getRating(ratingId: String): Rating?

    suspend fun editRating(ratingId: String, rating: Rating): Boolean

    suspend fun likeRating(ratingId: String, wasLiked: Boolean): Boolean

    suspend fun deleteRating(ratingId: String): Boolean

}