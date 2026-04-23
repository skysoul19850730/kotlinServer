@echo off
echo ========================================
echo Kotlin Server Builder
echo ========================================
echo.

echo [1/2] Building JAR file...
call gradlew.bat fatJar

if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo [2/2] JAR file created successfully!
echo Location: build\libs\kotlin-server.jar
echo.
echo You can now run the server with:
echo     java -jar build\libs\kotlin-server.jar
echo.
echo Or double-click ''run-server.bat''
echo.
pause
