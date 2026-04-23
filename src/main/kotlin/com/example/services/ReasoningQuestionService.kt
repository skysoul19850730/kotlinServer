package com.example.services

import com.example.database.dao.ReasoningQuestionDao
import com.example.models.*

object ReasoningQuestionService {
    
    private val dao = ReasoningQuestionDao()
    
    /**
     * Create reasoning question
     */
    fun createQuestion(userId: Int, request: CreateReasoningQuestionRequest): Result<ReasoningQuestion> {
        if (request.title.isBlank()) {
            return Result.failure(Exception("Title cannot be empty"))
        }
        
        if (request.answerText.isBlank()) {
            return Result.failure(Exception("Answer cannot be empty"))
        }
        
        val id = dao.insert(
            userId = userId,
            title = request.title,
            contentText = request.contentText,
            contentImagePath = request.contentImagePath,
            answerText = request.answerText,
            answerImagePath = request.answerImagePath,
            difficulty = request.difficulty,
            clientId = request.clientId
        )
        
        val question = dao.findById(id, userId)
            ?: return Result.failure(Exception("Failed to create question"))
        
        return Result.success(question)
    }
    
    /**
     * Get all user questions
     */
    fun getAllQuestions(userId: Int): List<ReasoningQuestion> {
        return dao.findByUserId(userId)
    }
    
    /**
     * Get single question
     */
    fun getQuestion(questionId: Int, userId: Int): Result<ReasoningQuestion> {
        val question = dao.findById(questionId, userId)
            ?: return Result.failure(Exception("Question not found"))
        
        return Result.success(question)
    }
    
    /**
     * Update question
     */
    fun updateQuestion(questionId: Int, userId: Int, request: UpdateReasoningQuestionRequest): Result<ReasoningQuestion> {
        // Check if question exists and belongs to user
        dao.findById(questionId, userId)
            ?: return Result.failure(Exception("Question not found or access denied"))
        
        // Update
        dao.update(
            id = questionId,
            userId = userId,
            title = request.title,
            contentText = request.contentText,
            contentImagePath = request.contentImagePath,
            answerText = request.answerText,
            answerImagePath = request.answerImagePath,
            difficulty = request.difficulty
        )
        
        // Return updated question
        val question = dao.findById(questionId, userId)
            ?: return Result.failure(Exception("Failed to get updated question"))
        
        return Result.success(question)
    }
    
    /**
     * Delete question
     */
    fun deleteQuestion(questionId: Int, userId: Int): Result<Unit> {
        dao.delete(questionId, userId)
        return Result.success(Unit)
    }
    
    /**
     * Sync questions
     */
    fun syncQuestions(userId: Int, request: SyncReasoningQuestionsRequest): Result<SyncReasoningQuestionsResponse> {
        try {
            // Process client questions
            request.questions.forEach { clientQuestion ->
                val existingQuestion = clientQuestion.clientId?.let { 
                    dao.findByClientId(userId, it) 
                }
                
                if (existingQuestion == null) {
                    // New question from client, insert it
                    dao.insert(
                        userId = userId,
                        title = clientQuestion.title,
                        contentText = clientQuestion.contentText,
                        contentImagePath = clientQuestion.contentImagePath,
                        answerText = clientQuestion.answerText,
                        answerImagePath = clientQuestion.answerImagePath,
                        difficulty = clientQuestion.difficulty,
                        clientId = clientQuestion.clientId
                    )
                }
                // If exists, keep server version (server is source of truth)
            }
            
            // Get server questions updated after lastSyncTime
            val serverQuestions = if (request.lastSyncTime > 0) {
                dao.findByUserIdAndUpdatedAfter(userId, request.lastSyncTime)
            } else {
                dao.findByUserId(userId)
            }
            
            val response = SyncReasoningQuestionsResponse(
                serverQuestions = serverQuestions,
                syncTime = System.currentTimeMillis()
            )
            
            return Result.success(response)
        } catch (e: Exception) {
            return Result.failure(Exception("Sync failed: ${e.message}"))
        }
    }
}