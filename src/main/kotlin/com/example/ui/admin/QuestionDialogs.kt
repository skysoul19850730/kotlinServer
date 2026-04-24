package com.example.ui.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.models.ReasoningQuestion
import com.example.utils.ImageUtil
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * Open a file chooser dialog to select an image
 */
fun pickImageFile(): File? {
    val chooser = JFileChooser()
    chooser.fileFilter = FileNameExtensionFilter(
        "Image Files", "jpg", "jpeg", "png", "gif", "bmp", "webp"
    )
    chooser.dialogTitle = "Select Image"
    val result = chooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) chooser.selectedFile else null
}

/**
 * Load image bitmap from file
 */
fun loadImageBitmap(file: File): ImageBitmap? {
    return try {
        val bufferedImage = ImageIO.read(file)
        bufferedImage?.toComposeImageBitmap()
    } catch (e: Exception) {
        null
    }
}

/**
 * Load image bitmap from relative path
 */
fun loadImageBitmapFromPath(relativePath: String?): ImageBitmap? {
    if (relativePath.isNullOrBlank()) return null
    val file = ImageUtil.getImageFile(relativePath)
    if (!file.exists()) return null
    return loadImageBitmap(file)
}

@Composable
fun ImagePickerField(
    label: String,
    selectedFile: File?,
    existingImagePath: String? = null,
    onFileSelected: (File?) -> Unit
) {
    val imageBitmap = remember(selectedFile, existingImagePath) {
        if (selectedFile != null) {
            loadImageBitmap(selectedFile)
        } else {
            loadImageBitmapFromPath(existingImagePath)
        }
    }
    
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Image preview
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = label,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 150.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // File name display
                if (selectedFile != null) {
                    Text(
                        text = selectedFile.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                } else if (existingImagePath != null) {
                    Text(
                        text = "Current: ${existingImagePath.substringAfterLast("/")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                // Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val file = pickImageFile()
                            if (file != null) {
                                onFileSelected(file)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (imageBitmap != null) "Change Image" else "Select Image")
                    }
                    
                    if (selectedFile != null || existingImagePath != null) {
                        OutlinedButton(
                            onClick = { onFileSelected(null) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remove")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, answer: String, difficulty: Int, contentImage: File?, answerImage: File?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(1) }
    var expanded by remember { mutableStateOf(false) }
    var contentImageFile by remember { mutableStateOf<File?>(null) }
    var answerImageFile by remember { mutableStateOf<File?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Question") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
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
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )
                
                // Question image picker
                ImagePickerField(
                    label = "Question Image (optional)",
                    selectedFile = contentImageFile,
                    onFileSelected = { contentImageFile = it }
                )
                
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Answer") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )
                
                // Answer image picker
                ImagePickerField(
                    label = "Answer Image (optional)",
                    selectedFile = answerImageFile,
                    onFileSelected = { answerImageFile = it }
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
                            onClick = { difficulty = 1; expanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Medium") },
                            onClick = { difficulty = 2; expanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Hard") },
                            onClick = { difficulty = 3; expanded = false }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank() && answer.isNotBlank()) {
                        onConfirm(title, content, answer, difficulty, contentImageFile, answerImageFile)
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
    onConfirm: (title: String, content: String, answer: String, difficulty: Int, contentImage: File?, answerImage: File?) -> Unit
) {
    var title by remember { mutableStateOf(question.title) }
    var content by remember { mutableStateOf(question.contentText ?: "") }
    var answer by remember { mutableStateOf(question.answerText) }
    var difficulty by remember { mutableStateOf(question.difficulty) }
    var expanded by remember { mutableStateOf(false) }
    var contentImageFile by remember { mutableStateOf<File?>(null) }
    var answerImageFile by remember { mutableStateOf<File?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Question") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
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
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )
                
                // Question image picker (show existing image)
                ImagePickerField(
                    label = "Question Image (optional)",
                    selectedFile = contentImageFile,
                    existingImagePath = question.contentImagePath,
                    onFileSelected = { contentImageFile = it }
                )
                
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Answer") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )
                
                // Answer image picker (show existing image)
                ImagePickerField(
                    label = "Answer Image (optional)",
                    selectedFile = answerImageFile,
                    existingImagePath = question.answerImagePath,
                    onFileSelected = { answerImageFile = it }
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
                            onClick = { difficulty = 1; expanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Medium") },
                            onClick = { difficulty = 2; expanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Hard") },
                            onClick = { difficulty = 3; expanded = false }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank() && answer.isNotBlank()) {
                        onConfirm(title, content, answer, difficulty, contentImageFile, answerImageFile)
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
