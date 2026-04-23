package com.example.database.dao

import com.example.database.tables.ReasoningQuestions
import com.example.models.ReasoningQuestion
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.transactions.transaction

class ReasoningQuestionDao {
    
    fun insert(userId: Int, title: String, contentText: String?, contentImagePath: String?,
               answerText: String, answerImagePath: String?, difficulty: Int, clientId: Int?): Int {
        return transaction {
            val now = System.currentTimeMillis()
            ReasoningQuestions.insert {
                it[ReasoningQuestions.userId] = userId
                it[ReasoningQuestions.title] = title
                it[ReasoningQuestions.contentText] = contentText
                it[ReasoningQuestions.contentImagePath] = contentImagePath
                it[ReasoningQuestions.answerText] = answerText
                it[ReasoningQuestions.answerImagePath] = answerImagePath
                it[ReasoningQuestions.difficulty] = difficulty
                it[ReasoningQuestions.createdAt] = now
                it[ReasoningQuestions.updatedAt] = now
                it[ReasoningQuestions.clientId] = clientId
            } get ReasoningQuestions.id
        }
    }
    
    fun update(id: Int, userId: Int, title: String?, contentText: String?, contentImagePath: String?,
               answerText: String?, answerImagePath: String?, difficulty: Int?) {
        transaction {
            ReasoningQuestions.update({ (ReasoningQuestions.id eq id) and (ReasoningQuestions.userId eq userId) }) {
                title?.let { value -> it[ReasoningQuestions.title] = value }
                if (contentText != null) it[ReasoningQuestions.contentText] = contentText
                if (contentImagePath != null) it[ReasoningQuestions.contentImagePath] = contentImagePath
                answerText?.let { value -> it[ReasoningQuestions.answerText] = value }
                if (answerImagePath != null) it[ReasoningQuestions.answerImagePath] = answerImagePath
                difficulty?.let { value -> it[ReasoningQuestions.difficulty] = value }
                it[ReasoningQuestions.updatedAt] = System.currentTimeMillis()
            }
        }
    }
    
    fun delete(id: Int, userId: Int) {
        transaction {
            ReasoningQuestions.deleteWhere { (ReasoningQuestions.id eq id) and (ReasoningQuestions.userId eq userId) }
        }
    }
    
    fun findById(id: Int, userId: Int): ReasoningQuestion? {
        return transaction {
            ReasoningQuestions.select { (ReasoningQuestions.id eq id) and (ReasoningQuestions.userId eq userId) }
                .map { toReasoningQuestion(it) }
                .singleOrNull()
        }
    }
    
    fun findByUserId(userId: Int): List<ReasoningQuestion> {
        return transaction {
            ReasoningQuestions.select { ReasoningQuestions.userId eq userId }
                .orderBy(ReasoningQuestions.createdAt to SortOrder.DESC)
                .map { toReasoningQuestion(it) }
        }
    }
    
    fun findByUserIdAndUpdatedAfter(userId: Int, timestamp: Long): List<ReasoningQuestion> {
        return transaction {
            ReasoningQuestions.select { 
                (ReasoningQuestions.userId eq userId) and (ReasoningQuestions.updatedAt greater timestamp) 
            }
                .orderBy(ReasoningQuestions.updatedAt to SortOrder.DESC)
                .map { toReasoningQuestion(it) }
        }
    }
    
    fun findByClientId(userId: Int, clientId: Int): ReasoningQuestion? {
        return transaction {
            ReasoningQuestions.select { 
                (ReasoningQuestions.userId eq userId) and (ReasoningQuestions.clientId eq clientId) 
            }
                .map { toReasoningQuestion(it) }
                .singleOrNull()
        }
    }
    
    private fun toReasoningQuestion(row: ResultRow): ReasoningQuestion {
        return ReasoningQuestion(
            id = row[ReasoningQuestions.id],
            userId = row[ReasoningQuestions.userId],
            title = row[ReasoningQuestions.title],
            contentText = row[ReasoningQuestions.contentText],
            contentImagePath = row[ReasoningQuestions.contentImagePath],
            answerText = row[ReasoningQuestions.answerText],
            answerImagePath = row[ReasoningQuestions.answerImagePath],
            difficulty = row[ReasoningQuestions.difficulty],
            createdAt = row[ReasoningQuestions.createdAt],
            updatedAt = row[ReasoningQuestions.updatedAt],
            clientId = row[ReasoningQuestions.clientId]
        )
    }
}