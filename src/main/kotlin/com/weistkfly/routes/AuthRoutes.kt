package com.weistkfly.routes

import com.weistkfly.data.mail.MailImpl
import com.weistkfly.data.requests.user.AuthRequest
import com.weistkfly.data.requests.user.NewIconRequest
import com.weistkfly.data.requests.user.ResetPasswordRequest
import com.weistkfly.data.requests.user.SignInRequest
import com.weistkfly.data.responses.AuthResponse
import com.weistkfly.data.responses.UserResponse
import com.weistkfly.data.user.User
import com.weistkfly.data.user.UserDataSource
import com.weistkfly.security.hashing.HashingService
import com.weistkfly.security.hashing.SaltedHash
import com.weistkfly.security.token.TokenClaim
import com.weistkfly.security.token.TokenConfig
import com.weistkfly.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {
    post("sign_up") {
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something went wrong during sign up")
            return@post
        }

//        val areFieldsBlanks = request.username.isBlank() || request.password.isBlank()
//        val isPasswordTooShort = request.password.length < 8
//        if (areFieldsBlanks || isPasswordTooShort){
//            call.respond(HttpStatusCode.Conflict)
//            return@post
//        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            email = request.email,
            password = saltedHash.hash,
            name = request.name,
            lastName = request.lastName,
            school = request.school,
            salt = saltedHash.salt,
            givenLikes = 0,
            ratingsMade = 0,
            wasMadeOn = System.currentTimeMillis(),
            iconId = request.iconId
        )

        val wasAcknowledged = userDataSource.insertUser(user)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        call.respond(HttpStatusCode.OK, "Sign up successful")
    }
}

fun Route.signIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("sign_in") {
        val request = call.receiveOrNull<SignInRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Something went wrong on sign in")
            return@post
        }

        val user = userDataSource.getUserByUserName(request.email)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.getSecretInfo(
    userDataSource: UserDataSource
) {
    authenticate {
        get("user_info") {

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asString()

            val user = userDataSource.getUserByUserId(userId)
            if (user == null) {
                call.respond(HttpStatusCode.Conflict, "Token is not working")
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                UserResponse(
                    email = user.email,
                    name = user.name,
                    lastName = user.lastName,
                    school = user.school,
                    givenLikes = user.givenLikes,
                    ratingsMade = user.ratingsMade,
                    wasMadeOn = user.wasMadeOn,
                    iconId = user.iconId
                )
            )
        }
    }
}

fun Route.changeUserIcon(
    userDataSource: UserDataSource
) {
    authenticate {
        patch("new_user_icon") {
            val request = call.receiveOrNull<NewIconRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest, "Something went wrong changing the profile icon")
                return@patch
            }

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asString()

            val wasIconUpdated = userDataSource.changeUserIcon(userId, request.newIconId)
            if (!wasIconUpdated) {
                call.respond(HttpStatusCode.Conflict, "The Icon wasn't updated")
                return@patch
            }

            call.respond(
                HttpStatusCode.OK,
                message = "Icon updated"
            )
        }
    }
}

fun Route.tryEmail(
    mail: MailImpl,
    userDataSource: UserDataSource,
    hashingService: HashingService
) {
    post("reset_password") {
        val request = call.receiveOrNull<ResetPasswordRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "There's something wrong with the email request")
            return@post
        }
        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asString()

        val user = userDataSource.getUserByUserId(userId) ?: return@post
        val newPassword = "123456789"
        val saltedHash = hashingService.generateSaltedHash(newPassword)
        val wasPasswordUpdated = userDataSource.updateUser(user, saltedHash.hash, saltedHash.salt)
        val wasMailSent = mail.sendResetPasswordMail(user.name, request.email, newPassword)
        if (wasMailSent && wasPasswordUpdated) {
            call.respond(HttpStatusCode.OK, "Password reset successful")
            return@post
        }
        call.respond(HttpStatusCode.BadRequest, "Something went from when sending the email")
    }
}