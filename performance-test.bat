@echo off
echo Running Enterprise Performance Tests...
echo =====================================

REM Set classpath
set CLASSPATH=lib\json-20240303.jar;lib\junit-platform-console-standalone-1.10.2.jar;build\classes

echo Starting comprehensive performance testing suite...
echo This will test:
echo - Concurrent booking system under load
echo - Dynamic pricing algorithm performance  
echo - Cache efficiency and hit rates
echo - Analytics report generation speed
echo - Payment gateway resilience
echo.

REM Run performance tests
java -cp "%CLASSPATH%" -Xmx2g testing.PerformanceTestRunner

echo.
echo Performance testing completed.
pause