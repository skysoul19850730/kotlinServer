package com.example.controllers

import com.example.models.*
import com.example.services.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

object AuthController {
    
    /**
     * User registration
     */
    suspend fun register(call: ApplicationCall) {
        val request = call.receive<RegisterRequest>()
        
        AuthService.register(request)
            .onSuccess { user ->
                call.respond(successResponse("Registration successful", user))
            }
            .onFailure { exception ->
                call.respond(errorResponse<User>(exception.message ?: "Registration failed"))
            }
    }
    
    /**
     * User login
     */
    suspend fun login(call: ApplicationCall) {
        val request = call.receive<LoginRequest>()
        
        AuthService.login(request)
            .onSuccess { user ->
                // Set session
                call.sessions.set(UserSession(user.id, user.username))
                call.respond(successResponse("Login successful", user))
            }
            .onFailure { exception ->
                call.respond(errorResponse<User>(exception.message ?: "Login failed"))
            }
    }
    
    /**
     * Logout
     */
    suspend fun logout(call: ApplicationCall) {
        call.sessions.clear<UserSession>()
        call.respond(successResponse<Unit>("Logout successful"))
    }
    
    /**
     * Get current user info
     */
    suspend fun getCurrentUser(call: ApplicationCall) {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, errorResponse<User>("Not logged in"))
            return
        }
        
        val user = AuthService.getUserById(session.userId)
        if (user != null) {
            call.respond(successResponse("Success", user))
        } else {
            call.respond(HttpStatusCode.NotFound, errorResponse<User>("User not found"))
        }
    }
}
