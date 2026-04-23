package com.example.routes

import com.example.controllers.DiaryController
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.diaryRoutes() {
    route("/diaries") {
        // POST /api/diaries - 创建日记
        post {
            DiaryController.createDiary(call)
        }
        
        // GET /api/diaries - 获取所有日记
        get {
            DiaryController.getAllDiaries(call)
        }
        
        // GET /api/diaries/{id} - 获取单个日记
        get("/{id}") {
            DiaryController.getDiary(call)
        }
        
        // PUT /api/diaries/{id} - 更新日记
        put("/{id}") {
            DiaryController.updateDiary(call)
        }
        
        // DELETE /api/diaries/{id} - 删除日记
        delete("/{id}") {
            DiaryController.deleteDiary(call)
        }
    }
}
