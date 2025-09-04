@echo off
echo Setting up Enterprise Demo Data...
echo =================================

REM Set classpath
set CLASSPATH=lib\json-20240303.jar;lib\junit-platform-console-standalone-1.10.2.jar;build\classes

echo Initializing production-ready demo data...
echo This will create:
echo - 100 hotel rooms across 6 floors
echo - 6 months of realistic booking history
echo - Seasonal booking patterns
echo - Comprehensive payment records
echo - Revenue analytics data
echo.

REM Run demo data generator
java -cp "%CLASSPATH%" config.DatabaseInitializer

echo.
echo Demo data setup completed.
echo You can now run the main application with rich, realistic data!
echo.
pause