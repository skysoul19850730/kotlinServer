package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Submission(
    val id: Int,
    val questionId: Int,
    val questionTitle: String,
    val userId: Int,
    val userName: String,
    val userAnswer: String,
    val score: Int? = null,
    val feedback: String? = null,
    val submittedAt: Long,
    val gradedAt: Long? = null
)

@Serializable
data class SubmitAnswerRequest(
    val questionId: Int,
    val userAnswer: String
)

@Serializable
data class GradeSubmissionRequest(
    val score: Int,
    val feedback: String? = null
)
