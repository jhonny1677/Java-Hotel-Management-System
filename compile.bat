@echo off
echo Compiling Enterprise Hotel Management System...
echo =============================================

REM Create directories if they don't exist
if not exist "build" mkdir build
if not exist "build\classes" mkdir build\classes

REM Set classpath
set CLASSPATH=lib\json-20240303.jar;lib\junit-platform-console-standalone-1.10.2.jar;src\main;build\classes

echo Compiling Java sources...

REM Compile all Java files
javac -cp "%CLASSPATH%" -d build\classes ^
    src\main\model\*.java ^
    src\main\auth\*.java ^
    src\main\repository\*.java ^
    src\main\repository\impl\*.java ^
    src\main\cache\*.java ^
    src\main\services\*.java ^
    src\main\payment\*.java ^
    src\main\pricing\*.java ^
    src\main\analytics\*.java ^
    src\main\concurrency\*.java ^
    src\main\patterns\observer\*.java ^
    src\main\patterns\factory\*.java ^
    src\main\patterns\command\*.java ^
    src\main\config\*.java ^
    src\main\ui\*.java ^
    src\main\persistence\*.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo ✓ Compilation successful!
echo.
echo To run the application:
echo   run.bat
echo.
pause