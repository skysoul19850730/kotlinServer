package com.example.routes

import com.example.controllers.ReasoningQuestionController
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.reasoningQuestionRoutes() {
    route("/reasoning-questions") {
        // POST /api/reasoning-questions - 创建题目
        post {
            ReasoningQuestionController.createQuestion(call)
        }
        
        // GET /api/reasoning-questions - 获取所有题目
        get {
            ReasoningQuestionController.getAllQuestions(call)
        }
        
        // POST /api/reasoning-questions/sync - 同步题目
        post("/sync") {
            ReasoningQuestionController.syncQuestions(call)
        }
        
        // GET /api/reasoning-questions/{id} - 获取单个题目
        get("/{id}") {
            ReasoningQuestionController.getQuestion(call)
        }
        
        // PUT /api/reasoning-questions/{id} - 更新题目
        put("/{id}") {
            ReasoningQuestionController.updateQuestion(call)
        }
        
        // DELETE /api/reasoning-questions/{id} - 删除题目
        delete("/{id}") {
            ReasoningQuestionController.deleteQuestion(call)
        }
    }
}