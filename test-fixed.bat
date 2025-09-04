@echo off
echo Testing FIXED Room Initialization
echo =================================

REM Set classpath
set CLASSPATH=lib\json-20240303.jar;build\classes

echo Recompiling with room initialization fix...
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\config\ApplicationContext.java
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\repository\impl\InMemoryRoomRepository.java

echo.
echo Testing room creation - should now show 100 rooms...
echo.

java -cp "%CLASSPATH%" testing.DebugTestRunner

echo.
echo If you see "✓ Successfully created 100 hotel rooms" above, 
echo then the main application will work!
echo.
pause