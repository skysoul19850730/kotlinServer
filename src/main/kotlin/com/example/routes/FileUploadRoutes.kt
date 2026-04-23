package com.example.routes

import com.example.models.errorResponse
import com.example.models.successResponse
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*

fun Route.fileUploadRoutes() {
    route("/upload") {
        post {
            try {
                val multipart = call.receiveMultipart()
                var fileName: String? = null
                
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            val originalFileName = part.originalFileName ?: "unknown"
                            val fileExtension = originalFileName.substringAfterLast('.', "")
                            
                            // Generate unique filename
                            val uniqueFileName = "${UUID.randomUUID()}.$fileExtension"
                            
                            // Create uploads directory if not exists
                            val uploadDir = File("./uploads")
                            if (!uploadDir.exists()) {
                                uploadDir.mkdirs()
                            }
                            
                            // Save file
                            val file = File(uploadDir, uniqueFileName)
                            part.streamProvider().use { input ->
                                file.outputStream().buffered().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            
                            fileName = uniqueFileName
                        }
                        else -> {}
                    }
                    part.dispose()
                }
                
                if (fileName != null) {
                    // Return server file path
                    val serverPath = "/uploads/$fileName"
                    call.respond(successResponse("File uploaded successfully", mapOf("filePath" to serverPath)))
                } else {
                    call.respond(HttpStatusCode.BadRequest, errorResponse<Unit>("No file uploaded"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, errorResponse<Unit>("Upload failed: ${e.message}"))
            }
        }
        
        // Serve uploaded files
        get("/{filename}") {
            val filename = call.parameters["filename"]
            if (filename == null) {
                call.respond(HttpStatusCode.BadRequest, "Filename required")
                return@get
            }
            
            val file = File("./uploads/$filename")
            if (file.exists()) {
                call.respondFile(file)
            } else {
                call.respond(HttpStatusCode.NotFound, "File not found")
            }
        }
    }
}