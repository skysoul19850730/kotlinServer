package com.example.database.tables

import org.jetbrains.exposed.sql.Table

object Submissions : Table("submissions") {
    val id = integer("id").autoIncrement()
    val questionId = integer("question_id").references(ReasoningQuestions.id)
    val userId = integer("user_id").references(Users.id)
    val userAnswer = text("user_answer")
    val score = integer("score").nullable()
    val feedback = text("feedback").nullable()
    val submittedAt = long("submitted_at")
    val gradedAt = long("graded_at").nullable()
    
    override val primaryKey = PrimaryKey(id)
}
