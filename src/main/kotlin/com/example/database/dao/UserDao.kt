package com.example.database.dao

import com.example.database.tables.Users
import com.example.models.User
import com.example.utils.PasswordUtil
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object UserDao {
    
    fun createUser(username: String, email: String, password: String): Boolean {
        return try {
            transaction {
                Users.insert {
                    it[Users.username] = username
                    it[Users.email] = email
                    it[Users.passwordHash] = PasswordUtil.hashPassword(password)
                    it[Users.createdAt] = System.currentTimeMillis()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun authenticateUser(username: String, password: String): User? {
        return transaction {
            Users.select { Users.username eq username }
                .mapNotNull { row ->
                    val storedHash = row[Users.passwordHash]
                    if (PasswordUtil.verifyPassword(password, storedHash)) {
                        User(
                            id = row[Users.id],
                            username = row[Users.username],
                            email = row[Users.email]
                        )
                    } else null
                }
                .singleOrNull()
        }
    }
    
    fun getUserById(userId: Int): User? {
        return transaction {
            Users.select { Users.id eq userId }
                .mapNotNull { row ->
                    User(
                        id = row[Users.id],
                        username = row[Users.username],
                        email = row[Users.email]
                    )
                }
                .singleOrNull()
        }
    }
    
    fun getUserByUsername(username: String): User? {
        return transaction {
            Users.select { Users.username eq username }
                .mapNotNull { row ->
                    User(
                        id = row[Users.id],
                        username = row[Users.username],
                        email = row[Users.email]
                    )
                }
                .singleOrNull()
        }
    }
}
