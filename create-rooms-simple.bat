@echo off
echo Creating Simple Room Test
echo =========================

REM Set classpath
set CLASSPATH=lib\json-20240303.jar;build\classes

echo Testing simple room creation...

java -cp "%CLASSPATH%" -Djava.class.path="%CLASSPATH%" ^
-Dfile.encoding=UTF-8 ^
ui.EnterpriseHotelManagementGUI

pause