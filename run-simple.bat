@echo off
echo Running Simple Working Version
echo ==============================

REM Set classpath
set CLASSPATH=lib\json-20240303.jar;build\classes

echo Compiling simple version...
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\ui\SimpleHotelGUI.java

echo Starting simple hotel management system...
echo This version uses the original Hotel class that definitely works.
echo.

java -cp "%CLASSPATH%" ui.SimpleHotelGUI

pause