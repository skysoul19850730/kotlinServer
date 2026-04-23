package com.example.services

import com.example.database.dao.DiaryDao
import com.example.models.Diary
import com.example.models.CreateDiaryRequest
import com.example.models.UpdateDiaryRequest

object DiaryService {
    
    /**
     * Create diary
     */
    fun createDiary(userId: Int, request: CreateDiaryRequest): Result<Diary> {
        if (request.title.isBlank()) {
            return Result.failure(Exception("Title cannot be empty"))
        }
        
        if (request.content.isBlank()) {
            return Result.failure(Exception("Content cannot be empty"))
        }
        
        val diary = DiaryDao.createDiary(userId, request.title, request.content)
            ?: return Result.failure(Exception("Failed to create diary"))
        
        return Result.success(diary)
    }
    
    /**
     * Get all user diaries
     */
    fun getUserDiaries(userId: Int): List<Diary> {
        return DiaryDao.getDiariesByUserId(userId)
    }
    
    /**
     * Get single diary
     */
    fun getDiary(diaryId: Int, userId: Int): Result<Diary> {
        val diary = DiaryDao.getDiaryById(diaryId)
            ?: return Result.failure(Exception("Diary not found"))
        
        if (diary.userId != userId) {
            return Result.failure(Exception("Access denied"))
        }
        
        return Result.success(diary)
    }
    
    /**
     * Update diary
     */
    fun updateDiary(diaryId: Int, userId: Int, request: UpdateDiaryRequest): Result<Diary> {
        // Check permission
        if (!DiaryDao.isDiaryOwnedByUser(diaryId, userId)) {
            return Result.failure(Exception("Access denied"))
        }
        
        // Update
        val success = DiaryDao.updateDiary(diaryId, userId, request.title, request.content)
        if (!success) {
            return Result.failure(Exception("Update failed"))
        }
        
        // Return updated diary
        val diary = DiaryDao.getDiaryById(diaryId)
            ?: return Result.failure(Exception("Failed to get updated diary"))
        
        return Result.success(diary)
    }
    
    /**
     * Delete diary
     */
    fun deleteDiary(diaryId: Int, userId: Int): Result<Unit> {
        val success = DiaryDao.deleteDiary(diaryId, userId)
        if (!success) {
            return Result.failure(Exception("Delete failed or access denied"))
        }
        
        return Result.success(Unit)
    }
}
