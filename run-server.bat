@echo off
title Kotlin Server
echo ========================================
echo Starting Kotlin Server...
echo ========================================
echo.

if not exist "build\libs\kotlin-server.jar" (
    echo ERROR: JAR file not found!
    echo Please run ''build-and-run.bat'' first to build the project.
    pause
    exit /b 1
)

echo Server starting on http://localhost:8080
echo.
echo Press Ctrl+C to stop the server
echo ========================================
echo.

java -jar build\libs\kotlin-server.jar

pause
