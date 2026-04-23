package com.example.routes

import com.example.controllers.AuthController
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.authRoutes() {
    route("/auth") {
        // POST /api/auth/register
        post("/register") {
            AuthController.register(call)
        }
        
        // POST /api/auth/login
        post("/login") {
            AuthController.login(call)
        }
        
        // POST /api/auth/logout
        post("/logout") {
            AuthController.logout(call)
        }
        
        // GET /api/auth/me
        get("/me") {
            AuthController.getCurrentUser(call)
        }
    }
}
