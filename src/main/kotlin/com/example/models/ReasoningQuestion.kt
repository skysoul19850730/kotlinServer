package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ReasoningQuestion(
    val id: Int,
    val userId: Int,
    val title: String,
    val contentText: String?,
    val contentImagePath: String?,
    val answerText: String,
    val answerImagePath: String?,
    val difficulty: Int = 1,
    val createdAt: Long,
    val updatedAt: Long,
    val clientId: Int? = null
)

@Serializable
data class CreateReasoningQuestionRequest(
    val title: String,
    val contentText: String?,
    val contentImagePath: String?,
    val answerText: String,
    val answerImagePath: String?,
    val difficulty: Int = 1,
    val clientId: Int? = null
)

@Serializable
data class UpdateReasoningQuestionRequest(
    val title: String?,
    val contentText: String?,
    val contentImagePath: String?,
    val answerText: String?,
    val answerImagePath: String?,
    val difficulty: Int?
)

@Serializable
data class SyncReasoningQuestionsRequest(
    val questions: List<CreateReasoningQuestionRequest>,
    val lastSyncTime: Long = 0
)

@Serializable
data class SyncReasoningQuestionsResponse(
    val serverQuestions: List<ReasoningQuestion>,
    val syncTime: Long = System.currentTimeMillis()
)
