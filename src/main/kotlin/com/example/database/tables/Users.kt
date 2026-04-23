package com.example.database.tables

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 60)
    val createdAt = long("created_at")
    
    override val primaryKey = PrimaryKey(id)
}
