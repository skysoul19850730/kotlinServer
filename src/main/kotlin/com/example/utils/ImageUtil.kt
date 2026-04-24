package com.example.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.UUID

object ImageUtil {
    
    // Image storage directory
    private val UPLOAD_DIR = File("./data/uploads")
    private val QUESTION_IMAGE_DIR = File(UPLOAD_DIR, "questions")
    private val ANSWER_IMAGE_DIR = File(UPLOAD_DIR, "answers")
    
    init {
        // Create directories if not exist
        QUESTION_IMAGE_DIR.mkdirs()
        ANSWER_IMAGE_DIR.mkdirs()
    }
    
    /**
     * Copy image file to storage directory
     * @param sourceFile The source image file to copy
     * @param type "question" or "answer"
     * @return The relative path of the stored image
     */
    fun saveImage(sourceFile: File, type: String = "question"): String {
        val targetDir = if (type == "answer") ANSWER_IMAGE_DIR else QUESTION_IMAGE_DIR
        targetDir.mkdirs()
        
        // Generate unique filename
        val extension = sourceFile.extension.lowercase()
        val fileName = "${UUID.randomUUID()}.$extension"
        val targetFile = File(targetDir, fileName)
        
        // Copy file
        Files.copy(
            sourceFile.toPath(),
            targetFile.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
        
        // Return relative path for API access
        return "uploads/${if (type == "answer") "answers" else "questions"}/$fileName"
    }
    
    /**
     * Delete an image file
     * @param relativePath The relative path of the image
     */
    fun deleteImage(relativePath: String?) {
        if (relativePath.isNullOrBlank()) return
        val file = File("./data/$relativePath")
        if (file.exists()) {
            file.delete()
        }
    }
    
    /**
     * Get the absolute file for a relative path
     */
    fun getImageFile(relativePath: String): File {
        return File("./data/$relativePath")
    }
    
    /**
     * Check if a file is a valid image
     */
    fun isValidImage(file: File): Boolean {
        val ext = file.extension.lowercase()
        return ext in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    }
    
    /**
     * Get upload directory path
     */
    fun getUploadDir(): File = UPLOAD_DIR
}
