package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    // Initialize database
    DatabaseFactory.init()
    
    // Start server
    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    // Configure serialization
    configureSerialization()
    
    // Configure security (CORS, Session, Exception handling)
    configureSecurity()
    
    // Configure routing
    configureRouting()
    
    log.info("Application started successfully")
    log.info("API documentation: http://localhost:8080/")
}
