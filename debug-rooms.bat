@echo off
echo DEBUG: Testing Room Repository
echo ==============================

REM Set classpath
set CLASSPATH=lib\json-20240303.jar;build\classes

echo Running debug test to check room creation...
echo.

REM Compile debug test
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\testing\DebugTestRunner.java

REM Run debug test
java -cp "%CLASSPATH%" testing.DebugTestRunner

echo.
echo Check the output above to see if rooms are being created and stored.
pause