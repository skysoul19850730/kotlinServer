package com.example.ui.admin

import com.example.database.dao.UserDao
import com.example.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.database.tables.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

data class UserManagementState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedUser: User? = null
)

class UserManagementViewModel {
    private val scope = CoroutineScope(Dispatchers.IO)
    
    private val _state = MutableStateFlow(UserManagementState())
    val state: StateFlow<UserManagementState> = _state.asStateFlow()
    
    fun loadUsers() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val users = getAllUsers()
                _state.value = _state.value.copy(
                    users = users,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load users"
                )
            }
        }
    }
    
    fun deleteUser(userId: Int, onSuccess: () -> Unit) {
        scope.launch {
            try {
                transaction {
                    Users.deleteWhere { Users.id eq userId }
                }
                loadUsers()
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to delete user: ${e.message}"
                )
            }
        }
    }
    
    fun selectUser(user: User?) {
        _state.value = _state.value.copy(selectedUser = user)
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
    
    private fun getAllUsers(): List<User> {
        return transaction {
            Users.selectAll()
                .map { row ->
                    User(
                        id = row[Users.id],
                        username = row[Users.username],
                        email = row[Users.email]
                    )
                }
        }
    }
}
