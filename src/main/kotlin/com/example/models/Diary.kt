package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Diary(
    val id: Int,
    val userId: Int,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class CreateDiaryRequest(
    val title: String,
    val content: String
)

@Serializable
data class UpdateDiaryRequest(
    val title: String?,
    val content: String?
)
