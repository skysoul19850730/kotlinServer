package com.example.database

import com.example.database.tables.Users
import com.example.database.tables.Diaries
import com.example.database.tables.ReasoningQuestions
import com.example.database.tables.Submissions
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseFactory {
    fun init() {
        // Create data directory
        val dataDir = File("./data")
        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }
        
        // Connect to database
        Database.connect(
            url = "jdbc:sqlite:./data/users.db",
            driver = "org.sqlite.JDBC"
        )
        
        // Create tables
        transaction {
            SchemaUtils.create(Users, Diaries, ReasoningQuestions, Submissions)
        }
        
        println("Database initialized successfully")
    }
}
