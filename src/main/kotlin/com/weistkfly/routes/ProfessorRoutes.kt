package com.weistkfly.routes

import com.weistkfly.data.professor.Professor
import com.weistkfly.data.professor.ProfessorDataSource
import com.weistkfly.data.requests.professor.*
import com.weistkfly.data.responses.AllProfessorsResponses
import com.weistkfly.data.responses.ProfessorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.professors(
    professorDataSource: ProfessorDataSource,
) {
    post("register_professor") {
        val request = call.receiveOrNull<NewProfessor>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something wrong with professor info")
            return@post
        }

        val professor = Professor(
            fullName = "${request.name} ${request.lastName}",
            reviewCount = 0,
            avgRating = 0.0,
            school = request.school,
            avgDifficulty = 0.000001,
            excellent = 0,
            veryGood = 0,
            good = 0,
            notGood = 0,
            bad = 0,
            staredTags = emptyList()
        )

        val wasInserted = professorDataSource.addProfessor(professor)
        if (!wasInserted) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        call.respond(HttpStatusCode.OK, "Professor was added successfully")
    }
}

fun Route.getProfessor(
    professorDataSource: ProfessorDataSource
) {
    get("name_professor") {
        val request = call.receiveOrNull<ProfessorRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something went wrong getting the professor")
            return@get
        }

        val professors = professorDataSource.getProfessorsByName(request.fullName)
        if (professors.isEmpty()) {
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
) {
    get("all_professors") {
        val professors = professorDataSource.getAllProfessors()

        if (professors.isEmpty()) {
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

fun Route.getBestRatedProfessors(
    professorDataSource: ProfessorDataSource
) {
    get("best_rated_professor") {
        val professors = professorDataSource.getBestRatedProfessors()

        if (professors.isEmpty()) {
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

fun Route.getProfessorById(
    professorDataSource: ProfessorDataSource
) {
    get("professor_id") {
        val request = call.receiveOrNull<ProfessorByIdRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something went wrong getting the professor")
            return@get
        }

        val prof = professorDataSource.getProfessorById(request.professorId)

        if (prof == null) {
            call.respond(HttpStatusCode.BadRequest, "Couldn't find professor")
            return@get
        }

        call.respond(
            status = HttpStatusCode.OK,
            message = ProfessorResponse(
                fullName = prof.fullName,
                reviewCount = prof.reviewCount,
                avgRating = prof.avgRating,
                school = prof.school,
                avgDifficulty = prof.avgDifficulty,
                excellent = prof.excellent,
                veryGood = prof.veryGood,
                good = prof.good,
                notGood = prof.notGood,
                bad = prof.bad
            )
        )
    }
}

fun Route.getProfessorBySchool(
    professorDataSource: ProfessorDataSource
) {
    get("school_professor") {
        val request = call.receiveOrNull<ProfessorBySchoolRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something went wrong getting the professor")
            return@get
        }

        val professors = professorDataSource.getProfessorsBySchool(request.school)
        if (professors.isEmpty()) {
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

fun Route.updateProfessor(
    professorDataSource: ProfessorDataSource
) {
    put("update_professor") {
        val request = call.receiveOrNull<UpdateProfessorRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something went wrong updating the professor")
            return@put
        }

        val prof = Professor(
            fullName = request.fullName,
            reviewCount = request.reviewCount,
            avgRating = request.avgRating,
            school = request.school,
            avgDifficulty = request.avgDifficulty,
            excellent = request.excellent,
            veryGood = request.veryGood,
            good = request.good,
            notGood = request.notGood,
            bad = request.bad,
            id = request.id,
            staredTags = request.staredTags
        )
        val newProf = professorDataSource.updateProfessor(request.id, prof)

        if (!newProf) {
            call.respond(HttpStatusCode.Conflict, "Couldn't update professor")
            return@put
        }

        call.respond(HttpStatusCode.OK, "Professor updated")
    }
}

