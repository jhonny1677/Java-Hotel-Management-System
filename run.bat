@echo off
echo Starting Enterprise Hotel Management System...
echo ===========================================

REM Set classpath
set CLASSPATH=lib\json-20240303.jar;lib\junit-platform-console-standalone-1.10.2.jar;build\classes

echo Starting application with FAANG-level features...
echo.

REM Run the application
java -cp "%CLASSPATH%" ui.EnterpriseHotelManagementGUI

echo.
echo Application closed.
pause