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
import com.example.models.Submission
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionsManagementScreen(
    viewModel: SubmissionManagementViewModel
) {
    val state by viewModel.state.collectAsState()
    var showGradeDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    
    // Load submissions on first composition
    LaunchedEffect(Unit) {
        viewModel.loadSubmissions()
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
            Column {
                Text(
                    text = "Submission Management",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = if (state.ungradedOnly) 
                        "${state.submissions.size} ungraded submissions" 
                    else 
                        "${state.submissions.size} total submissions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row {
                FilterChip(
                    selected = state.ungradedOnly,
                    onClick = { viewModel.toggleFilter() },
                    label = { Text(if (state.ungradedOnly) "Ungraded Only" else "All") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (state.ungradedOnly) Icons.Default.FilterList else Icons.Default.List,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { viewModel.loadSubmissions() }) {
                    Icon(Icons.Default.Refresh, "Refresh")
                }
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
        } else if (state.submissions.isEmpty()) {
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
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (state.ungradedOnly) "No ungraded submissions" else "No submissions yet",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = if (state.ungradedOnly) 
                            "All submissions have been graded" 
                        else 
                            "Submissions will appear here when students submit answers",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Submissions list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.submissions) { submission ->
                    SubmissionCard(
                        submission = submission,
                        onGrade = {
                            viewModel.selectSubmission(submission)
                            showGradeDialog = true
                        },
                        onViewDetails = {
                            viewModel.selectSubmission(submission)
                            showDetailsDialog = true
                        }
                    )
                }
            }
        }
    }
    
    // Grade Dialog
    if (showGradeDialog && state.selectedSubmission != null) {
        GradeSubmissionDialog(
            submission = state.selectedSubmission!!,
            onDismiss = {
                showGradeDialog = false
                viewModel.selectSubmission(null)
            },
            onConfirm = { score, feedback ->
                viewModel.gradeSubmission(
                    submissionId = state.selectedSubmission!!.id,
                    score = score,
                    feedback = feedback,
                    onSuccess = {
                        showGradeDialog = false
                        viewModel.selectSubmission(null)
                    }
                )
            }
        )
    }
    
    // Details Dialog
    if (showDetailsDialog && state.selectedSubmission != null) {
        SubmissionDetailsDialog(
            submission = state.selectedSubmission!!,
            onDismiss = {
                showDetailsDialog = false
                viewModel.selectSubmission(null)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionCard(
    submission: Submission,
    onGrade: () -> Unit,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (submission.score == null) 
                MaterialTheme.colorScheme.surfaceVariant 
            else 
                MaterialTheme.colorScheme.surface
        )
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
                        text = submission.questionTitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Student: ${submission.userName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Submitted: ${formatTimestamp(submission.submittedAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row {
                    if (submission.score == null) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error
                        ) {
                            Text("Ungraded")
                        }
                    } else {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Text("Score: ${submission.score}")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Answer: ${submission.userAnswer.take(100)}${if (submission.userAnswer.length > 100) "..." else ""}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Details")
                }
                
                if (submission.score == null) {
                    Button(
                        onClick = onGrade,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Grade")
                    }
                } else {
                    OutlinedButton(
                        onClick = onGrade,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Grade")
                    }
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
