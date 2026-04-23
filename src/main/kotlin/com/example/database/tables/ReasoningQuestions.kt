package com.example.database.tables

import org.jetbrains.exposed.sql.Table

object ReasoningQuestions : Table("reasoning_questions") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val title = varchar("title", 500)
    val contentText = text("content_text").nullable()
    val contentImagePath = varchar("content_image_path", 500).nullable()
    val answerText = text("answer_text")
    val answerImagePath = varchar("answer_image_path", 500).nullable()
    val difficulty = integer("difficulty").default(1)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
    val clientId = integer("client_id").nullable()
    
    override val primaryKey = PrimaryKey(id)
}
