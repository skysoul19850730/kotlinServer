package com.example.ui.admin

import com.example.database.dao.SubmissionDao
import com.example.models.Submission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SubmissionManagementState(
    val submissions: List<Submission> = emptyList(),
    val ungradedOnly: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedSubmission: Submission? = null
)

class SubmissionManagementViewModel {
    private val dao = SubmissionDao()
    private val scope = CoroutineScope(Dispatchers.IO)
    
    private val _state = MutableStateFlow(SubmissionManagementState())
    val state: StateFlow<SubmissionManagementState> = _state.asStateFlow()
    
    fun loadSubmissions() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val submissions = if (_state.value.ungradedOnly) {
                    dao.findUngraded()
                } else {
                    dao.findAll()
                }
                _state.value = _state.value.copy(
                    submissions = submissions,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load submissions"
                )
            }
        }
    }
    
    fun toggleFilter() {
        _state.value = _state.value.copy(
            ungradedOnly = !_state.value.ungradedOnly
        )
        loadSubmissions()
    }
    
    fun gradeSubmission(
        submissionId: Int,
        score: Int,
        feedback: String?,
        onSuccess: () -> Unit
    ) {
        scope.launch {
            try {
                dao.grade(submissionId, score, feedback)
                loadSubmissions()
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to grade submission: ${e.message}"
                )
            }
        }
    }
    
    fun selectSubmission(submission: Submission?) {
        _state.value = _state.value.copy(selectedSubmission = submission)
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
