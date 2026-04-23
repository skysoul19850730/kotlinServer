package com.example.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class AdminScreen {
    SERVER_CONTROL,
    USERS,
    QUESTIONS,
    SUBMISSIONS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApp(
    serverManager: ServerManager,
    questionViewModel: QuestionManagementViewModel,
    userViewModel: UserManagementViewModel,
    submissionViewModel: SubmissionManagementViewModel
) {
    var currentScreen by remember { mutableStateOf(AdminScreen.SERVER_CONTROL) }
    val serverState by serverManager.serverState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Server Admin Panel") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == AdminScreen.SERVER_CONTROL,
                    onClick = { currentScreen = AdminScreen.SERVER_CONTROL },
                    icon = { Icon(Icons.Default.Settings, "Server") },
                    label = { Text("Server") }
                )
                NavigationBarItem(
                    selected = currentScreen == AdminScreen.USERS,
                    onClick = { currentScreen = AdminScreen.USERS },
                    icon = { Icon(Icons.Default.Person, "Users") },
                    label = { Text("Users") }
                )
                NavigationBarItem(
                    selected = currentScreen == AdminScreen.QUESTIONS,
                    onClick = { currentScreen = AdminScreen.QUESTIONS },
                    icon = { Icon(Icons.Default.Quiz, "Questions") },
                    label = { Text("Questions") }
                )
                NavigationBarItem(
                    selected = currentScreen == AdminScreen.SUBMISSIONS,
                    onClick = { currentScreen = AdminScreen.SUBMISSIONS },
                    icon = { Icon(Icons.Default.Assessment, "Submissions") },
                    label = { Text("Submissions") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentScreen) {
                AdminScreen.SERVER_CONTROL -> ServerControlScreen(serverManager)
                AdminScreen.USERS -> UsersManagementScreen(userViewModel)
                AdminScreen.QUESTIONS -> QuestionsManagementScreen(questionViewModel)
                AdminScreen.SUBMISSIONS -> SubmissionsManagementScreen(submissionViewModel)
            }
        }
    }
}

@Composable
fun ServerControlScreen(serverManager: ServerManager) {
    val serverState by serverManager.serverState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = if (serverState.isRunning) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = if (serverState.isRunning) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.error
        )
        
        Text(
            text = if (serverState.isRunning) "Server Running" else "Server Stopped",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = serverState.message)
                if (serverState.isRunning) {
                    Text(text = "Port: ${serverState.port}")
                    Text(text = "URL: http://localhost:${serverState.port}")
                }
            }
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { serverManager.startServer() },
                enabled = !serverState.isRunning,
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Server")
            }
            
            Button(
                onClick = { serverManager.stopServer() },
                enabled = serverState.isRunning,
                modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Stop, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stop Server")
            }
        }
    }
}
