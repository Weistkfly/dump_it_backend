package com.weistkfly

import com.weistkfly.data.mail.MailImpl
import com.weistkfly.data.professor.MongoProfessorDataSource
import com.weistkfly.data.ranking.MongoRatingDataSource
import com.weistkfly.data.user.MongoUserDataSource
import io.ktor.server.application.*
import com.weistkfly.plugins.*
import com.weistkfly.security.hashing.SHA256HashingService
import com.weistkfly.security.token.JwtTokenService
import com.weistkfly.security.token.TokenConfig
import io.ktor.http.*
import io.ktor.server.plugins.cors.routing.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(CORS) {
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Head)
        allowNonSimpleContentTypes = true
        allowCredentials = true
        allowSameOrigin = true
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        anyHost()
    }
    val mongoPw = System.getenv("MONGO_PW")
    val dbName = "ktor-auth"
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://weistkfly:$mongoPw@cluster0.dcmlz.mongodb.net/$dbName?retryWrites=true&w=majority"
    ).coroutine
        .getDatabase(dbName)

    val userDataSource = MongoUserDataSource(db)
    val ratingDataSource = MongoRatingDataSource(db)
    val professorDataSource = MongoProfessorDataSource(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()
    val mail = MailImpl()

    configureRouting(
        userDataSource = userDataSource,
        hashingService = hashingService,
        tokenService = tokenService,
        tokenConfig = tokenConfig,
        mail = mail,
        professorDataSource = professorDataSource,
        ratingDataSource = ratingDataSource
    )
    configureSecurity(tokenConfig)
    configureMonitoring()
    configureSerialization()
}
