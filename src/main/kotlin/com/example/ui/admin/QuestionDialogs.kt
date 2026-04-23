package com.example.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.models.ReasoningQuestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, answer: String, difficulty: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(1) }
    var expanded by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Question") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Question Content") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
                
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Answer") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
                
                // Difficulty dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = getDifficultyText(difficulty),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Difficulty") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Easy") },
                            onClick = {
                                difficulty = 1
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Medium") },
                            onClick = {
                                difficulty = 2
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hard") },
                            onClick = {
                                difficulty = 3
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank() && answer.isNotBlank()) {
                        onConfirm(title, content, answer, difficulty)
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank() && answer.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditQuestionDialog(
    question: ReasoningQuestion,
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, answer: String, difficulty: Int) -> Unit
) {
    var title by remember { mutableStateOf(question.title) }
    var content by remember { mutableStateOf(question.contentText ?: "") }
    var answer by remember { mutableStateOf(question.answerText) }
    var difficulty by remember { mutableStateOf(question.difficulty) }
    var expanded by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Question") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Question Content") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
                
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Answer") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
                
                // Difficulty dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = getDifficultyText(difficulty),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Difficulty") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Easy") },
                            onClick = {
                                difficulty = 1
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Medium") },
                            onClick = {
                                difficulty = 2
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hard") },
                            onClick = {
                                difficulty = 3
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank() && answer.isNotBlank()) {
                        onConfirm(title, content, answer, difficulty)
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank() && answer.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
