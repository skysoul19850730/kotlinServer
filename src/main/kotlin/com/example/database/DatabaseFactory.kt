package com.example.database

import com.example.database.tables.Users
import com.example.database.tables.Diaries
import com.example.database.tables.ReasoningQuestions
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
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
            SchemaUtils.create(Users, Diaries, ReasoningQuestions)
        }
        
        println("Database initialized successfully")
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
