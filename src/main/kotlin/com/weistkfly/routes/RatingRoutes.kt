package com.weistkfly.routes

import com.weistkfly.data.professor.ProfessorDataSource
import com.weistkfly.data.ranking.Rating
import com.weistkfly.data.ranking.RatingDataSource
import com.weistkfly.data.requests.rates.LikeRateRequest
import com.weistkfly.data.requests.rates.NewRateRequest
import com.weistkfly.data.user.UserDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.rate(
    professorDataSource: ProfessorDataSource,
    userDataSource: UserDataSource,
    rateDataSource: RatingDataSource
) {
    authenticate {
        post("rateProfessor") {
            val request = call.receiveOrNull<NewRateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest, "Something wrong with the request")
                return@post
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asString()

            val rating = Rating(
                tag = request.tag,
                subject = request.subject,
                userGrade = request.userGrade,
                modality = request.modality,
                subjectCredits = request.subjectCredits,
                Rate = request.rate,
                difficulty = request.difficulty,
                likeCount = 0,
                userId = userId,
                professorId = request.professorId,
                wasMadeOn = System.currentTimeMillis()
            )

            val wasInserted = rateDataSource.insertRating(rating)
            if (!wasInserted) {
                call.respond(HttpStatusCode.Conflict, "Error inserting rate")
                return@post
            }

            val wasRatesMadeIncremented = userDataSource.incrementRatesCount(userId)
            if (!wasRatesMadeIncremented){
                call.respond(HttpStatusCode.Conflict, "Error incrementing user's madeRates count")
                return@post
            }

            val professor = professorDataSource.updateProfessorRate(request.professorId, request.rate, request.difficulty)
            if (!professor) {
                call.respond(HttpStatusCode.Conflict, "Couldn't update professor")
                return@post
            }
            call.respond(HttpStatusCode.OK, rating)
        }
    }
}

fun Route.likeRate(
    rateDataSource: RatingDataSource
) {
    patch("likeRate") {
        val request = call.receiveOrNull<LikeRateRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something wrong with the request")
            return@patch
        }

        val rate = rateDataSource.likeRating(request.rateId, request.wasLiked)

        if (!rate) {
            call.respond(HttpStatusCode.Conflict, "Couldn't update like count on rate")
            return@patch
        }

        call.respond(HttpStatusCode.OK, "rate liked")
    }
}