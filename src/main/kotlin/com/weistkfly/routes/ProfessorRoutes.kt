package com.weistkfly.routes

import com.weistkfly.data.professor.Professor
import com.weistkfly.data.professor.ProfessorDataSource
import com.weistkfly.data.ranking.RatingDataSource
import com.weistkfly.data.requests.professor.NewProfessor
import com.weistkfly.data.requests.professor.ProfessorRequest
import com.weistkfly.data.responses.AllProfessorsResponses
import com.weistkfly.data.responses.ProfessorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.professors(
    professorDataSource: ProfessorDataSource,
){
    post("register_professor"){
        val request = call.receiveOrNull<NewProfessor>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something wrong with professor info")
            return@post
        }

        val professor = Professor(
            fullName = "${request.name} ${request.lastName}",
            reviewCount = 0,
            avgRating = 0.0,
            school = request.school,
            avgDifficulty = 0.0,
            excellent = 0,
            veryGood = 0,
            good = 0,
            notGood = 0,
            bad = 0
        )

        val wasInserted = professorDataSource.addProfessor(professor)
        if (!wasInserted){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        call.respond(HttpStatusCode.OK, "Professor was added successfully")
    }
}

fun Route.getProfessor(
    professorDataSource: ProfessorDataSource
){
    get("professor"){
        val request = call.receiveOrNull<ProfessorRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something went wrong getting the professor")
            return@get
        }

        val professors = professorDataSource.getProfessorsByName(request.fullName)
        if (professors.isEmpty()){
            call.respond(HttpStatusCode.Conflict, "Professor not found")
            return@get
        }

        call.respond(
            status = HttpStatusCode.OK,
            message = AllProfessorsResponses(
                professors = professors
            )
        )
    }
}

fun Route.getProfessors(
    professorDataSource: ProfessorDataSource
){
    get("professors"){
        val professors = professorDataSource.getAllProfessors()

        if (professors.isEmpty()){
            call.respond("There are no professors yet")
            return@get
        }

        call.respond(
            status = HttpStatusCode.OK,
            message = AllProfessorsResponses(
                professors = professors
            )
        )
    }
}
