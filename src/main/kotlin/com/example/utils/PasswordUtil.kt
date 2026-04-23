package com.example.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordUtil {
    
    /**
     * º”√‹√‹¬Î
     */
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
    
    /**
     * —È÷§√‹¬Î
     */
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return try {
            BCrypt.checkpw(password, hashedPassword)
        } catch (e: Exception) {
            false
        }
    }
}
