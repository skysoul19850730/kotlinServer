package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

// Convenience functions
fun <T> successResponse(message: String = "Success", data: T? = null) =
    ApiResponse(success = true, message = message, data = data)

fun <T> errorResponse(message: String, data: T? = null) =
    ApiResponse(success = false, message = message, data = data)
