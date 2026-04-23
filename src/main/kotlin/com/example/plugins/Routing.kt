package com.example.plugins

import com.example.routes.authRoutes
import com.example.routes.diaryRoutes
import com.example.routes.reasoningQuestionRoutes
import com.example.routes.fileUploadRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Root path - API info
        get("/") {
            call.respond(mapOf(
                "name" to "Kotlin User Authentication & Diary API",
                "version" to "2.0.0",
                "endpoints" to mapOf(
                    "Authentication" to listOf(
                        "POST /api/auth/register - User registration",
                        "POST /api/auth/login - User login",
                        "POST /api/auth/logout - Logout",
                        "GET /api/auth/me - Get current user info"
                    ),
                    "Diary" to listOf(
                        "POST /api/diaries - Create diary",
                        "GET /api/diaries - Get all diaries",
                        "GET /api/diaries/{id} - Get single diary",
                        "PUT /api/diaries/{id} - Update diary",
                        "DELETE /api/diaries/{id} - Delete diary"
                    ),
                    "ReasoningQuestions" to listOf(
                        "POST /api/reasoning-questions - Create question",
                        "GET /api/reasoning-questions - Get all questions",
                        "POST /api/reasoning-questions/sync - Sync questions",
                        "GET /api/reasoning-questions/{id} - Get single question",
                        "PUT /api/reasoning-questions/{id} - Update question",
                        "DELETE /api/reasoning-questions/{id} - Delete question"
                    ),
                    "FileUpload" to listOf(
                        "POST /api/upload - Upload file (multipart/form-data)",
                        "GET /api/upload/{filename} - Download file"
                    )
                ),
                "frontend" to "http://localhost:8081"
            ))
        }
        
        // API routes group
        route("/api") {
            // Auth routes
            authRoutes()
            
            // Diary routes
            diaryRoutes()
            
            // Reasoning Question routes
            reasoningQuestionRoutes()
            
            // File upload routes
            fileUploadRoutes()
            
            // Health check
            get("/health") {
                call.respond(mapOf(
                    "status" to "ok",
                    "timestamp" to System.currentTimeMillis()
                ))
            }
        }
    }
}