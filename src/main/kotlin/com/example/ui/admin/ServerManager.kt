package com.example.ui.admin

import com.example.database.DatabaseFactory
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ServerState(
    val isRunning: Boolean = false,
    val port: Int = 8080,
    val message: String = "Server is stopped"
)

class ServerManager {
    private var server: NettyApplicationEngine? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    
    private val _serverState = MutableStateFlow(ServerState())
    val serverState: StateFlow<ServerState> = _serverState.asStateFlow()
    
    fun startServer() {
        if (_serverState.value.isRunning) {
            _serverState.value = _serverState.value.copy(
                message = "Server is already running"
            )
            return
        }
        
        scope.launch {
            try {
                // Initialize database
                DatabaseFactory.init()
                
                // Import server configuration
                val module: Application.() -> Unit = {
                    configureSerialization()
                    configureSecurity()
                    configureRouting()
                }
                
                // Start server
                server = embeddedServer(
                    factory = Netty,
                    port = 8080,
                    host = "0.0.0.0",
                    module = module
                )
                
                server?.start(wait = false)
                
                _serverState.value = ServerState(
                    isRunning = true,
                    port = 8080,
                    message = "Server is running on http://localhost:8080"
                )
            } catch (e: Exception) {
                _serverState.value = ServerState(
                    isRunning = false,
                    message = "Failed to start server: ${e.message}"
                )
                e.printStackTrace()
            }
        }
    }
    
    fun stopServer() {
        scope.launch {
            try {
                server?.stop(1000, 2000)
                server = null
                
                _serverState.value = ServerState(
                    isRunning = false,
                    message = "Server stopped"
                )
            } catch (e: Exception) {
                _serverState.value = _serverState.value.copy(
                    message = "Failed to stop server: ${e.message}"
                )
            }
        }
    }
}
