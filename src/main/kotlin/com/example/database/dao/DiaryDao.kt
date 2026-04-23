package com.example.database.dao

import com.example.database.tables.Diaries
import com.example.models.Diary
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object DiaryDao {
    
    // Create diary
    fun createDiary(userId: Int, title: String, content: String): Diary? {
        return try {
            transaction {
                val now = System.currentTimeMillis()
                val id = Diaries.insert {
                    it[Diaries.userId] = userId
                    it[Diaries.title] = title
                    it[Diaries.content] = content
                    it[Diaries.createdAt] = now
                    it[Diaries.updatedAt] = now
                } get Diaries.id
                
                Diary(
                    id = id,
                    userId = userId,
                    title = title,
                    content = content,
                    createdAt = now,
                    updatedAt = now
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    // Get all diaries by user ID
    fun getDiariesByUserId(userId: Int): List<Diary> {
        return transaction {
            Diaries.select { Diaries.userId eq userId }
                .orderBy(Diaries.createdAt to SortOrder.DESC)
                .map { rowToDiary(it) }
        }
    }
    
    // Get diary by ID
    fun getDiaryById(diaryId: Int): Diary? {
        return transaction {
            Diaries.select { Diaries.id eq diaryId }
                .mapNotNull { rowToDiary(it) }
                .singleOrNull()
        }
    }
    
    // Update diary
    fun updateDiary(diaryId: Int, userId: Int, title: String?, content: String?): Boolean {
        return try {
            transaction {
                val updated = Diaries.update({ 
                    (Diaries.id eq diaryId) and (Diaries.userId eq userId) 
                }) {
                    if (title != null) it[Diaries.title] = title
                    if (content != null) it[Diaries.content] = content
                    it[Diaries.updatedAt] = System.currentTimeMillis()
                }
                updated > 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    // Delete diary
    fun deleteDiary(diaryId: Int, userId: Int): Boolean {
        return try {
            transaction {
                val deleted = Diaries.deleteWhere { 
                    (id eq diaryId) and (Diaries.userId eq userId) 
                }
                deleted > 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    // Check if diary is owned by user
    fun isDiaryOwnedByUser(diaryId: Int, userId: Int): Boolean {
        return transaction {
            Diaries.select { 
                (Diaries.id eq diaryId) and (Diaries.userId eq userId) 
            }.count() > 0
        }
    }
    
    private fun rowToDiary(row: ResultRow) = Diary(
        id = row[Diaries.id],
        userId = row[Diaries.userId],
        title = row[Diaries.title],
        content = row[Diaries.content],
        createdAt = row[Diaries.createdAt],
        updatedAt = row[Diaries.updatedAt]
    )
}
