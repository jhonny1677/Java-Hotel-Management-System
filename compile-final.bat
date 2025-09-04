@echo off
echo Compiling Enterprise Hotel Management System (Final)...
echo ===================================================

REM Create directories if they don't exist
if not exist "build" mkdir build
if not exist "build\classes" mkdir build\classes

REM Set classpath
set CLASSPATH=lib\json-20240303.jar;lib\junit-platform-console-standalone-1.10.2.jar;src\main;build\classes

echo Compiling core Java sources...

REM Compile in stages to avoid dependency issues
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\model\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\auth\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\repository\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\repository\impl\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\cache\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\services\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\payment\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\pricing\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\analytics\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\concurrency\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\patterns\observer\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\patterns\factory\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\patterns\command\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\config\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\ui\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\persistence\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\demo\*.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\testing\SimpleTestRunner.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo SUCCESS! Enterprise Hotel Management System compiled!
echo ===================================================
echo.
echo To run the application:
echo   run.bat
echo.
echo To test the system:
echo   java -cp "lib\json-20240303.jar;build\classes" testing.SimpleTestRunner
echo.
echo Login credentials:
echo   Admin: admin@hotel.com / password123
echo   Staff: staff@hotel.com / password123
echo   Guest: guest@hotel.com / password123
echo.
pause