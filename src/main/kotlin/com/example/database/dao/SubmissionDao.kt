package com.example.database.dao

import com.example.database.tables.Submissions
import com.example.database.tables.ReasoningQuestions
import com.example.database.tables.Users
import com.example.models.Submission
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class SubmissionDao {
    
    fun insert(questionId: Int, userId: Int, userAnswer: String): Int {
        return transaction {
            val now = System.currentTimeMillis()
            Submissions.insert {
                it[Submissions.questionId] = questionId
                it[Submissions.userId] = userId
                it[Submissions.userAnswer] = userAnswer
                it[Submissions.submittedAt] = now
            } get Submissions.id
        }
    }
    
    fun findAll(): List<Submission> {
        return transaction {
            (Submissions innerJoin Users innerJoin ReasoningQuestions)
                .selectAll()
                .orderBy(Submissions.submittedAt to SortOrder.DESC)
                .map { toSubmission(it) }
        }
    }
    
    fun findUngraded(): List<Submission> {
        return transaction {
            (Submissions innerJoin Users innerJoin ReasoningQuestions)
                .select { Submissions.score.isNull() }
                .orderBy(Submissions.submittedAt to SortOrder.ASC)
                .map { toSubmission(it) }
        }
    }
    
    fun findById(id: Int): Submission? {
        return transaction {
            (Submissions innerJoin Users innerJoin ReasoningQuestions)
                .select { Submissions.id eq id }
                .map { toSubmission(it) }
                .singleOrNull()
        }
    }
    
    fun grade(id: Int, score: Int, feedback: String?) {
        transaction {
            Submissions.update({ Submissions.id eq id }) {
                it[Submissions.score] = score
                it[Submissions.feedback] = feedback
                it[Submissions.gradedAt] = System.currentTimeMillis()
            }
        }
    }
    
    private fun toSubmission(row: ResultRow): Submission {
        return Submission(
            id = row[Submissions.id],
            questionId = row[Submissions.questionId],
            questionTitle = row[ReasoningQuestions.title],
            userId = row[Submissions.userId],
            userName = row[Users.username],
            userAnswer = row[Submissions.userAnswer],
            score = row[Submissions.score],
            feedback = row[Submissions.feedback],
            submittedAt = row[Submissions.submittedAt],
            gradedAt = row[Submissions.gradedAt]
        )
    }
}
