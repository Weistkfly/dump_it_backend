package com.weistkfly.plugins

import com.weistkfly.*
import com.weistkfly.data.mail.MailImpl
import com.weistkfly.data.professor.ProfessorDataSource
import com.weistkfly.data.ranking.RatingDataSource
import com.weistkfly.data.user.UserDataSource
import com.weistkfly.routes.*
import com.weistkfly.security.hashing.HashingService
import com.weistkfly.security.token.TokenConfig
import com.weistkfly.security.token.TokenService
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    professorDataSource: ProfessorDataSource,
    ratingDataSource: RatingDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    mail: MailImpl
) {

    routing {
        signIn(userDataSource, hashingService, tokenService, tokenConfig)
        signUp(hashingService, userDataSource)
        authenticate()
        getSecretInfo(userDataSource)
        tryEmail(mail, userDataSource, hashingService)
        professors(professorDataSource)
        getProfessor(professorDataSource)
        getProfessors(professorDataSource)
    }
}
