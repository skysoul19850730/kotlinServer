package com.example.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.models.ReasoningQuestion

@Composable
fun QuestionsManagementScreen(
    viewModel: QuestionManagementViewModel
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var questionToDelete by remember { mutableStateOf<ReasoningQuestion?>(null) }
    
    // Load questions on first composition
    LaunchedEffect(Unit) {
        viewModel.loadQuestions()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question Management",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Button(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Question")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Error message
        state.error?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.clearError() }) {
                        Icon(Icons.Default.Close, "Dismiss")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Loading indicator
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.questions.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Quiz,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "No questions yet",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Click ''Add Question'' to create your first question",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Questions list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.questions) { question ->
                    QuestionCard(
                        question = question,
                        onEdit = {
                            viewModel.selectQuestion(question)
                            showEditDialog = true
                        },
                        onDelete = {
                            questionToDelete = question
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }
    
    // Add Dialog
    if (showAddDialog) {
        AddQuestionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, content, answer, difficulty ->
                viewModel.createQuestion(
                    title = title,
                    contentText = content,
                    answerText = answer,
                    difficulty = difficulty,
                    onSuccess = { showAddDialog = false }
                )
            }
        )
    }
    
    // Edit Dialog
    if (showEditDialog && state.selectedQuestion != null) {
        EditQuestionDialog(
            question = state.selectedQuestion!!,
            onDismiss = {
                showEditDialog = false
                viewModel.selectQuestion(null)
            },
            onConfirm = { title, content, answer, difficulty ->
                viewModel.updateQuestion(
                    questionId = state.selectedQuestion!!.id,
                    userId = state.selectedQuestion!!.userId,
                    title = title,
                    contentText = content,
                    answerText = answer,
                    difficulty = difficulty,
                    onSuccess = {
                        showEditDialog = false
                        viewModel.selectQuestion(null)
                    }
                )
            }
        )
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog && questionToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                questionToDelete = null
            },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete Question?") },
            text = {
                Text("Are you sure you want to delete \"${questionToDelete!!.title}\"? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteQuestion(
                            questionId = questionToDelete!!.id,
                            userId = questionToDelete!!.userId,
                            onSuccess = {
                                showDeleteDialog = false
                                questionToDelete = null
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        questionToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun QuestionCard(
    question: ReasoningQuestion,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = question.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Difficulty: ${getDifficultyText(question.difficulty)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = getDifficultyColor(question.difficulty)
                    )
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Content: ${question.contentText?.take(100) ?: "No content"}${if ((question.contentText?.length ?: 0) > 100) "..." else ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Answer: ${question.answerText.take(50)}${if (question.answerText.length > 50) "..." else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getDifficultyText(difficulty: Int): String {
    return when (difficulty) {
        1 -> "Easy"
        2 -> "Medium"
        3 -> "Hard"
        else -> "Unknown"
    }
}

@Composable
fun getDifficultyColor(difficulty: Int): androidx.compose.ui.graphics.Color {
    return when (difficulty) {
        1 -> MaterialTheme.colorScheme.primary
        2 -> MaterialTheme.colorScheme.tertiary
        3 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }
}
