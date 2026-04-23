package com.example.database.tables

import org.jetbrains.exposed.sql.Table

object Diaries : Table("diaries") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val title = varchar("title", 200)
    val content = text("content")
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
    
    override val primaryKey = PrimaryKey(id)
}
