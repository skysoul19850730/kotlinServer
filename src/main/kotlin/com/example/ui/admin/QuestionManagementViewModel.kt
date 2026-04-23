package com.example.ui.admin

import com.example.database.dao.ReasoningQuestionDao
import com.example.models.ReasoningQuestion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuestionManagementState(
    val questions: List<ReasoningQuestion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedQuestion: ReasoningQuestion? = null
)

class QuestionManagementViewModel {
    private val dao = ReasoningQuestionDao()
    private val scope = CoroutineScope(Dispatchers.IO)
    
    private val _state = MutableStateFlow(QuestionManagementState())
    val state: StateFlow<QuestionManagementState> = _state.asStateFlow()
    
    // Load all questions for admin (userId = 0 means all)
    fun loadQuestions() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                // Get all questions from all users
                val questions = getAllQuestionsFromDB()
                _state.value = _state.value.copy(
                    questions = questions,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load questions"
                )
            }
        }
    }
    
    fun createQuestion(
        userId: Int = 1,  // Default admin user
        title: String,
        contentText: String,
        answerText: String,
        difficulty: Int,
        onSuccess: () -> Unit
    ) {
        scope.launch {
            try {
                dao.insert(
                    userId = userId,
                    title = title,
                    contentText = contentText,
                    contentImagePath = null,
                    answerText = answerText,
                    answerImagePath = null,
                    difficulty = difficulty,
                    clientId = null
                )
                loadQuestions()
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to create question: ${e.message}"
                )
            }
        }
    }
    
    fun updateQuestion(
        questionId: Int,
        userId: Int,
        title: String,
        contentText: String,
        answerText: String,
        difficulty: Int,
        onSuccess: () -> Unit
    ) {
        scope.launch {
            try {
                dao.update(
                    id = questionId,
                    userId = userId,
                    title = title,
                    contentText = contentText,
                    contentImagePath = null,
                    answerText = answerText,
                    answerImagePath = null,
                    difficulty = difficulty
                )
                loadQuestions()
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to update question: ${e.message}"
                )
            }
        }
    }
    
    fun deleteQuestion(questionId: Int, userId: Int, onSuccess: () -> Unit) {
        scope.launch {
            try {
                dao.delete(questionId, userId)
                loadQuestions()
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to delete question: ${e.message}"
                )
            }
        }
    }
    
    fun selectQuestion(question: ReasoningQuestion?) {
        _state.value = _state.value.copy(selectedQuestion = question)
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
    
    // Helper function to get all questions
    private fun getAllQuestionsFromDB(): List<ReasoningQuestion> {
        // Since we want admin to see all questions, we need to modify this
        // For now, we''ll get questions from userId 1 (admin)
        return dao.findByUserId(1)
    }
}
