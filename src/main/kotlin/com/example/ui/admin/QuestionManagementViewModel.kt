package com.example.ui.admin

import com.example.database.dao.ReasoningQuestionDao
import com.example.models.ReasoningQuestion
import com.example.utils.ImageUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

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
    
    fun loadQuestions() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val questions = dao.findByUserId(1)
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
        userId: Int = 1,
        title: String,
        contentText: String,
        answerText: String,
        difficulty: Int,
        contentImageFile: File? = null,
        answerImageFile: File? = null,
        onSuccess: () -> Unit
    ) {
        scope.launch {
            try {
                // Save images if provided
                val contentImagePath = contentImageFile?.let {
                    if (ImageUtil.isValidImage(it)) {
                        ImageUtil.saveImage(it, "question")
                    } else null
                }
                
                val answerImagePath = answerImageFile?.let {
                    if (ImageUtil.isValidImage(it)) {
                        ImageUtil.saveImage(it, "answer")
                    } else null
                }
                
                dao.insert(
                    userId = userId,
                    title = title,
                    contentText = contentText,
                    contentImagePath = contentImagePath,
                    answerText = answerText,
                    answerImagePath = answerImagePath,
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
        contentImageFile: File? = null,
        answerImageFile: File? = null,
        onSuccess: () -> Unit
    ) {
        scope.launch {
            try {
                // Save new images if provided
                val contentImagePath = contentImageFile?.let {
                    if (ImageUtil.isValidImage(it)) {
                        // Delete old image
                        val oldQuestion = dao.findById(questionId, userId)
                        ImageUtil.deleteImage(oldQuestion?.contentImagePath)
                        ImageUtil.saveImage(it, "question")
                    } else null
                }
                
                val answerImagePath = answerImageFile?.let {
                    if (ImageUtil.isValidImage(it)) {
                        val oldQuestion = dao.findById(questionId, userId)
                        ImageUtil.deleteImage(oldQuestion?.answerImagePath)
                        ImageUtil.saveImage(it, "answer")
                    } else null
                }
                
                dao.update(
                    id = questionId,
                    userId = userId,
                    title = title,
                    contentText = contentText,
                    contentImagePath = contentImagePath,
                    answerText = answerText,
                    answerImagePath = answerImagePath,
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
                // Delete related images
                val question = dao.findById(questionId, userId)
                ImageUtil.deleteImage(question?.contentImagePath)
                ImageUtil.deleteImage(question?.answerImagePath)
                
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
}
