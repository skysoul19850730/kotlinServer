package com.example.controllers

import com.example.models.*
import com.example.services.ReasoningQuestionService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

object ReasoningQuestionController {
    
    /**
     * Create reasoning question
     */
    suspend fun createQuestion(call: ApplicationCall) {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, errorResponse<ReasoningQuestion>("Not logged in"))
            return
        }
        
        val request = call.receive<CreateReasoningQuestionRequest>()
        
        ReasoningQuestionService.createQuestion(session.userId, request)
            .onSuccess { question ->
                call.respond(HttpStatusCode.Created, successResponse("Question created successfully", question))
            }
            .onFailure { exception ->
                call.respond(errorResponse<ReasoningQuestion>(exception.message ?: "Creation failed"))
            }
    }
    
    /**
     * Get all questions of current user
     */
    suspend fun getAllQuestions(call: ApplicationCall) {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, errorResponse<List<ReasoningQuestion>>("Not logged in"))
            return
        }
        
        val questions = ReasoningQuestionService.getAllQuestions(session.userId)
        call.respond(successResponse("Success", questions))
    }
    
    /**
     * Get single question
     */
    suspend fun getQuestion(call: ApplicationCall) {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, errorResponse<ReasoningQuestion>("Not logged in"))
            return
        }
        
        val questionId = call.parameters["id"]?.toIntOrNull()
        if (questionId == null) {
            call.respond(HttpStatusCode.BadRequest, errorResponse<ReasoningQuestion>("Invalid question ID"))
            return
        }
        
        ReasoningQuestionService.getQuestion(questionId, session.userId)
            .onSuccess { question ->
                call.respond(successResponse("Success", question))
            }
            .onFailure { exception ->
                call.respond(HttpStatusCode.NotFound, errorResponse<ReasoningQuestion>(exception.message ?: "Question not found"))
            }
    }
    
    /**
     * Update question
     */
    suspend fun updateQuestion(call: ApplicationCall) {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, errorResponse<ReasoningQuestion>("Not logged in"))
            return
        }
        
        val questionId = call.parameters["id"]?.toIntOrNull()
        if (questionId == null) {
            call.respond(HttpStatusCode.BadRequest, errorResponse<ReasoningQuestion>("Invalid question ID"))
            return
        }
        
        val request = call.receive<UpdateReasoningQuestionRequest>()
        
        ReasoningQuestionService.updateQuestion(questionId, session.userId, request)
            .onSuccess { question ->
                call.respond(successResponse("Question updated successfully", question))
            }
            .onFailure { exception ->
                call.respond(errorResponse<ReasoningQuestion>(exception.message ?: "Update failed"))
            }
    }
    
    /**
     * Delete question
     */
    suspend fun deleteQuestion(call: ApplicationCall) {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, errorResponse<Unit>("Not logged in"))
            return
        }
        
        val questionId = call.parameters["id"]?.toIntOrNull()
        if (questionId == null) {
            call.respond(HttpStatusCode.BadRequest, errorResponse<Unit>("Invalid question ID"))
            return
        }
        
        ReasoningQuestionService.deleteQuestion(questionId, session.userId)
            .onSuccess {
                call.respond(successResponse<Unit>("Question deleted successfully"))
            }
            .onFailure { exception ->
                call.respond(errorResponse<Unit>(exception.message ?: "Delete failed"))
            }
    }
    
    /**
     * Sync questions
     */
    suspend fun syncQuestions(call: ApplicationCall) {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, errorResponse<SyncReasoningQuestionsResponse>("Not logged in"))
            return
        }
        
        val request = call.receive<SyncReasoningQuestionsRequest>()
        
        ReasoningQuestionService.syncQuestions(session.userId, request)
            .onSuccess { response ->
                call.respond(successResponse("Sync completed successfully", response))
            }
            .onFailure { exception ->
                call.respond(errorResponse<SyncReasoningQuestionsResponse>(exception.message ?: "Sync failed"))
            }
    }
}