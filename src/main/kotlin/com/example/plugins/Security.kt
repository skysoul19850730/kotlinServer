package com.example.plugins

import com.example.models.UserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

fun Application.configureSecurity() {
    // CORS configuration
    install(CORS) {
        allowHost("localhost:8081")
        allowHost("127.0.0.1:8081")
        allowCredentials = true
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
    }
    
    // Session configuration
    install(Sessions) {
        cookie<UserSession>("USER_SESSION") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600 * 24 // 24 hours
            cookie.extensions["SameSite"] = "Lax"
        }
    }
    
    // Exception handling
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(
                text = "500: ${cause.message}",
                status = HttpStatusCode.InternalServerError
            )
            cause.printStackTrace()
        }
    }
}
