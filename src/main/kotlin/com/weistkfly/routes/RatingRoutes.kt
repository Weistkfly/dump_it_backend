package com.weistkfly.routes

import com.weistkfly.data.professor.ProfessorDataSource
import com.weistkfly.data.ranking.Rating
import com.weistkfly.data.ranking.RatingDataSource
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
    post("rateProfessor") {
        val request = call.receiveOrNull<NewRateRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something wrong with the request")
            return@post
        }

        val rating = Rating(
            tag = request.tag,
            subject = request.subject,
            userGrade = request.userGrade,
            modality = request.modality,
            subjectCredits = request.subjectCredits,
            Rate = request.Rate,
            difficulty = request.difficulty,
            likeCount = request.likeCount,
            userId = request.userId,
            professorId = request.professorId
        )
        val wasInserted = rateDataSource.insertRating(rating)
        if (!wasInserted) {
            call.respond(HttpStatusCode.Conflict, "Error inserting rate")
            return@post
        }

        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asString()

        val user = userDataSource.getUserByUserId(userId)

        var getProfessor = professorDataSource.getProfessorById(request.professorId)
        if (getProfessor == null) {
            call.respond(HttpStatusCode.Conflict, "Professor was not found")
            return@post
        }
        getProfessor.reviewCount += 1
        when (request.Rate) {
            1.0 -> {
                getProfessor.bad += 1
            }

            2.0 -> {
                getProfessor.notGood += 1
            }

            3.0 -> {
                getProfessor.good += 1
            }

            4.0 -> {
                getProfessor.veryGood += 1
            }

            5.0 -> {
                getProfessor.excellent += 1
            }
        }
        getProfessor.avgRating =
            ((getProfessor.bad + getProfessor.notGood + getProfessor.good + getProfessor.veryGood + getProfessor.excellent) / 5).toDouble()
        val professor = professorDataSource.updateProfessor(request.professorId, getProfessor)
        if (!professor){
            call.respond(HttpStatusCode.Conflict, "Couldn't update professor")
            return@post
        }



    }
}