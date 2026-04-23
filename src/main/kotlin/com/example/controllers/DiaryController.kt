package com.example.controllers

import com.example.models.*
import com.example.services.DiaryService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

object DiaryController {
    
    /**
     * Create diary
     */
    suspend fun createDiary(call: ApplicationCall) {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, errorResponse<Diary>("Not logged in"))
            return
        }
        
        val request = call.receive<CreateDiaryRequest>()
        
        DiaryService.createDiary(session.userId, request)
            .onSuccess { diary ->
                call.respond(HttpStatusCode.Created, successResponse("Created successfully", diary))
            }
            .onFailure { exception ->
                call.respond(errorResponse<Diary>(exception.message ?: "Creation failed"))
            }
    }
    
    /**
     * Get all diaries of current user
     */
    suspend fun getAllDiaries(call: ApplicationCall) {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, errorResponse<List<Diary>>("Not logged in"))
            return
        }
        
        val diaries = DiaryService.getUserDiaries(session.userId)
        call.respond(successResponse("Success", diaries))
    }
    
    /**
     * Get single diary
     */
    suspend fun getDiary(call: ApplicationCall) {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, errorResponse<Diary>("Not logged in"))
            return
        }
        
        val diaryId = call.parameters["id"]?.toIntOrNull()
        if (diaryId == null) {
            call.respond(HttpStatusCode.BadRequest, errorResponse<Diary>("Invalid diary ID"))
            return
        }
        
        DiaryService.getDiary(diaryId, session.userId)
            .onSuccess { diary ->
                call.respond(successResponse("Success", diary))
            }
            .onFailure { exception ->
                call.respond(HttpStatusCode.NotFound, errorResponse<Diary>(exception.message ?: "Diary not found"))
            }
    }
    
    /**
     * Update diary
     */
    suspend fun updateDiary(call: ApplicationCall) {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, errorResponse<Diary>("Not logged in"))
            return
        }
        
        val diaryId = call.parameters["id"]?.toIntOrNull()
        if (diaryId == null) {
            call.respond(HttpStatusCode.BadRequest, errorResponse<Diary>("Invalid diary ID"))
            return
        }
        
        val request = call.receive<UpdateDiaryRequest>()
        
        DiaryService.updateDiary(diaryId, session.userId, request)
            .onSuccess { diary ->
                call.respond(successResponse("Updated successfully", diary))
            }
            .onFailure { exception ->
                call.respond(errorResponse<Diary>(exception.message ?: "Update failed"))
            }
    }
    
    /**
     * Delete diary
     */
    suspend fun deleteDiary(call: ApplicationCall) {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, errorResponse<Unit>("Not logged in"))
            return
        }
        
        val diaryId = call.parameters["id"]?.toIntOrNull()
        if (diaryId == null) {
            call.respond(HttpStatusCode.BadRequest, errorResponse<Unit>("Invalid diary ID"))
            return
        }
        
        DiaryService.deleteDiary(diaryId, session.userId)
            .onSuccess {
                call.respond(successResponse<Unit>("Deleted successfully"))
            }
            .onFailure { exception ->
                call.respond(errorResponse<Unit>(exception.message ?: "Delete failed"))
            }
    }
}
