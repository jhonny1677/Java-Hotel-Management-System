@echo off
echo Quick Test - Enterprise Hotel Management System
echo ==============================================

REM Set classpath
set CLASSPATH=lib\json-20240303.jar;build\classes

echo Testing room initialization and basic functionality...
echo.

REM Run quick test
java -cp "%CLASSPATH%" testing.SimpleTestRunner

echo.
echo If you see rooms being created above, the system is working!
echo Now you can run the main application:
echo   run.bat
echo.
pause