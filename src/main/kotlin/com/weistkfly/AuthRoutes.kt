package com.weistkfly

import com.weistkfly.data.mail.MailImpl
import com.weistkfly.data.requests.AuthRequest
import com.weistkfly.data.requests.ResetPasswordRequest
import com.weistkfly.data.requests.SignInRequest
import com.weistkfly.data.requests.UserRequest
import com.weistkfly.data.responses.AuthResponse
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
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
){
    post("signup"){
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "U fucked up sign up")
            return@post
        }

        val areFieldsBlanks = request.username.isBlank() || request.password.isBlank()
        val isPasswordTooShort = request.password.length < 8
        if (areFieldsBlanks || isPasswordTooShort){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            email = request.email,
            username = request.username,
            password = saltedHash.hash,
            name = request.name,
            lastName = request.lastName,
            phoneNumber = request.phoneNumber,
            sex = request.sex,
            birthDate = request.birthDate,
            state = request.state,
            address = request.address,
            country = request.country,
            salt = saltedHash.salt
        )

        val wasAcknowledged = userDataSource.insertUser(user)
        if(!wasAcknowledged){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
    post("signin"){
        val request = call.receiveOrNull<SignInRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "U fucked up sign in")
            return@post
        }

        val user = userDataSource.getUserByUserName(request.username)
        if (user == null){
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
        if (!isValidPassword){
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

fun Route.authenticate(){
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }

    }
}

fun Route.getSecretInfo(
    userDataSource: UserDataSource
){
    authenticate{
        post("secret") {
            val request = call.receiveOrNull<UserRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest, "U fucked up secret")
                return@post
            }

            val user = userDataSource.getUserByUserName(request.username)
            if (user == null){
                call.respond(HttpStatusCode.Conflict, "Incorrect username")
                return@post
            }
            call.respond(user)
        }
    }
}

fun Route.tryEmail(
    mail: MailImpl,
    userDataSource: UserDataSource,
    hashingService: HashingService
){
    post("resetPassword") {
        val request = call.receiveOrNull<ResetPasswordRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "U fucked up mail")
            return@post
        }
        val user = userDataSource.getUserByUserName(request.username) ?: return@post
        val newPassword = "123456789"
        val saltedHash = hashingService.generateSaltedHash(newPassword)
        val wasPasswordUpdated = userDataSource.updateUser(user, saltedHash.hash, saltedHash.salt)
        val wasMailSent = mail.sendResetPasswordMail(request.username, request.email, newPassword)
        if (wasMailSent && wasPasswordUpdated){
            call.respond(HttpStatusCode.OK, "Password reset successful")
            return@post
        }
        call.respond(HttpStatusCode.BadRequest, "Didn't work")
    }
}