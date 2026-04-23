package com.example.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.models.Submission

@Composable
fun GradeSubmissionDialog(
    submission: Submission,
    onDismiss: () -> Unit,
    onConfirm: (score: Int, feedback: String?) -> Unit
) {
    var score by remember { mutableStateOf(submission.score?.toString() ?: "") }
    var feedback by remember { mutableStateOf(submission.feedback ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Grade Submission") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Student info
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Student: ${submission.userName}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Question: ${submission.questionTitle}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Student answer
                Text(
                    text = "Student\'s Answer:",
                    style = MaterialTheme.typography.titleSmall
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = submission.userAnswer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                
                // Score input
                OutlinedTextField(
                    value = score,
                    onValueChange = { 
                        if (it.isEmpty() || it.toIntOrNull() != null) {
                            score = it
                        }
                    },
                    label = { Text("Score (0-100)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = score.toIntOrNull()?.let { it < 0 || it > 100 } ?: false,
                    supportingText = {
                        if (score.toIntOrNull()?.let { it < 0 || it > 100 } == true) {
                            Text("Score must be between 0 and 100")
                        }
                    }
                )
                
                // Feedback input
                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    label = { Text("Feedback (optional)") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5,
                    placeholder = { Text("Add comments or feedback for the student...") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val scoreValue = score.toIntOrNull()
                    if (scoreValue != null && scoreValue in 0..100) {
                        onConfirm(
                            scoreValue,
                            if (feedback.isBlank()) null else feedback
                        )
                    }
                },
                enabled = score.toIntOrNull()?.let { it in 0..100 } ?: false
            ) {
                Text("Submit Grade")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SubmissionDetailsDialog(
    submission: Submission,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Submission Details") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow("Student", submission.userName)
                Divider()
                DetailRow("Question", submission.questionTitle)
                Divider()
                DetailRow("Submitted At", formatTimestamp(submission.submittedAt))
                
                if (submission.score != null) {
                    Divider()
                    DetailRow("Score", "${submission.score}/100")
                }
                
                if (submission.gradedAt != null) {
                    Divider()
                    DetailRow("Graded At", formatTimestamp(submission.gradedAt))
                }
                
                Divider()
                
                Text(
                    text = "Student\'s Answer:",
                    style = MaterialTheme.typography.titleSmall
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = submission.userAnswer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                
                if (submission.feedback != null) {
                    Text(
                        text = "Feedback:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = submission.feedback,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
