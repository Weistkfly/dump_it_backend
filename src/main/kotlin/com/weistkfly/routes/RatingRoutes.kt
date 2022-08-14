package com.weistkfly.routes

import com.weistkfly.data.professor.ProfessorDataSource
import com.weistkfly.data.ranking.Rating
import com.weistkfly.data.ranking.RatingDataSource
import com.weistkfly.data.requests.rates.*
import com.weistkfly.data.responses.RatesResponse
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
        post("rate_professor") {
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
                wasMadeOn = System.currentTimeMillis(),
                wouldTakeAgain = request.wouldTakeAgain
            )

            val wasInserted = rateDataSource.insertRating(rating)
            if (!wasInserted) {
                call.respond(HttpStatusCode.Conflict, "Error inserting rate")
                return@post
            }

            val wasRatesMadeIncremented = userDataSource.incrementRatesCount(userId)
            if (!wasRatesMadeIncremented) {
                call.respond(HttpStatusCode.Conflict, "Error incrementing user's madeRates count")
                return@post
            }

            val professor =
                professorDataSource.updateProfessorRate(request.professorId, request.rate, request.difficulty)
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
    patch("like_rate") {
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

fun Route.getRatingsByProfessor(
    ratingDataSource: RatingDataSource
) {
    get("rates_by_professor") {
        val request = call.receiveOrNull<GetRatingsByProfessorRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something wrong with the request")
            return@get
        }

        val rates = ratingDataSource.getRatingsByProfessor(request.professorId)

        call.respond(
            status = HttpStatusCode.OK,
            message = RatesResponse(
                rates
            )
        )
    }
}

fun Route.getRatingsByUser(
    ratingDataSource: RatingDataSource
) {
    authenticate {
        get("rates_by_user") {

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asString()

            val rates = ratingDataSource.getRatingsByUser(userId)

            call.respond(
                status = HttpStatusCode.OK,
                message = RatesResponse(
                    rates
                )
            )
        }
    }
}

fun Route.updateRating(
    rateDataSource: RatingDataSource
) {
    put("update_rate") {
        val request = call.receiveOrNull<UpdateRateRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something went wrong with the request")
            return@put
        }

        val wasEdited = rateDataSource.editRating(request.rate.id, request.rate)

        if (!wasEdited) {
            call.respond(HttpStatusCode.BadRequest, "Couldn't update rate")
            return@put
        }

        call.respond(HttpStatusCode.OK, "Updated successfully")
    }
}

fun Route.deleteRate(
    rateDataSource: RatingDataSource
) {
    delete("delete_rate") {
        val request = call.receiveOrNull<DeleteRateRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something wrong with the request")
            return@delete
        }

        val wasDeleted = rateDataSource.deleteRating(request.rateId)

        if (!wasDeleted) {
            call.respond(HttpStatusCode.Conflict, "Couldn't delete rate")
            return@delete
        }

        call.respond(HttpStatusCode.OK, "Deleted successfully")
    }
}
