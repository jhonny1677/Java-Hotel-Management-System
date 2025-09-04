@echo off
echo Testing Enhanced User Features
echo ================================

REM Set classpath
set CLASSPATH=lib\json-20240303.jar;build\classes

echo.
echo Recompiling enhanced GUI with all user features...
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\classes src\main\ui\EnterpriseHotelManagementGUI.java

if %ERRORLEVEL% NEQ 0 (
    echo COMPILATION FAILED! Check for errors above.
    pause
    exit /b 1
)

echo.
echo SUCCESS: Enhanced GUI compiled successfully!
echo.
echo Starting application with ALL USER FEATURES:
echo - Payment system integration
echo - Booking cancellation (async)
echo - User profile management
echo - Room preferences and favorites
echo - Notifications and alerts
echo - Loyalty program features
echo.
echo Login as GUEST user: guest@hotel.com / password123
echo.
echo Expected tabs for GUEST users:
echo 1. Bookings (with cancellation, check-in/out)
echo 2. Rooms (browse rooms)
echo 3. User Profile (payment methods, preferences, favorites)
echo 4. Notifications (alerts, loyalty program status)
echo 5. Dynamic Pricing
echo.
echo Additional tabs for ADMIN/STAFF:
echo 6. Analytics
echo 7. Payments
echo 8. System
echo 9. User Management (Admin only)
echo.

java -cp "%CLASSPATH%" ui.EnterpriseHotelManagementGUI

echo.
echo Application closed.
pause