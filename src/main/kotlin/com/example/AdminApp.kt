package com.example

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.example.ui.admin.*
import com.example.ui.theme.AdminTheme

fun main() = application {
    val serverManager = ServerManager()
    val questionViewModel = QuestionManagementViewModel()
    val userViewModel = UserManagementViewModel()
    val submissionViewModel = SubmissionManagementViewModel()
    val windowState = rememberWindowState(width = 1200.dp, height = 800.dp)
    
    Window(
        onCloseRequest = {
            serverManager.stopServer()
            exitApplication()
        },
        state = windowState,
        title = "Kotlin Server - Admin Panel"
    ) {
        AdminTheme {
            AdminApp(serverManager, questionViewModel, userViewModel, submissionViewModel)
        }
    }
}
