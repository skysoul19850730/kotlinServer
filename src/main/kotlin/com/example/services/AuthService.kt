package com.example.services

import com.example.database.dao.UserDao
import com.example.models.User
import com.example.models.RegisterRequest
import com.example.models.LoginRequest

object AuthService {
    
    /**
     * User registration
     */
    fun register(request: RegisterRequest): Result<User> {
        // Validate input
        if (request.username.isBlank() || request.email.isBlank() || request.password.isBlank()) {
            return Result.failure(Exception("All fields are required"))
        }
        
        if (request.password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }
        
        // Create user
        val success = UserDao.createUser(request.username, request.email, request.password)
        if (!success) {
            return Result.failure(Exception("Username or email already exists"))
        }
        
        // Get created user
        val user = UserDao.getUserByUsername(request.username)
            ?: return Result.failure(Exception("Failed to create user"))
        
        return Result.success(user)
    }
    
    /**
     * User login
     */
    fun login(request: LoginRequest): Result<User> {
        val user = UserDao.authenticateUser(request.username, request.password)
            ?: return Result.failure(Exception("Invalid username or password"))
        
        return Result.success(user)
    }
    
    /**
     * Get user by ID
     */
    fun getUserById(userId: Int): User? {
        return UserDao.getUserById(userId)
    }
}
