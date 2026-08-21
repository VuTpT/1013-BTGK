@echo off
REM ============================================================================
REM  MiniShop Automation - ban rut gon v2 (mot project Maven duy nhat)
REM
REM  Cach dung:
REM      ChayTest.bat            -> chay unit + web (Selenium) + Playwright, an trinh duyet
REM      ChayTest.bat hien       -> chay va HIEN trinh duyet (dung khi quay video)
REM      ChayTest.bat mobile     -> chay them tang Appium (can Emulator + appium server)
REM
REM  Yeu cau: da cai JDK 17 va Maven, da them Maven vao PATH (kiem tra bang: mvn -v)
REM ============================================================================
setlocal
cd /d "%~dp0"

set OPTS=
if /i "%~1"=="hien"   set OPTS=-Dheadless=false
if /i "%~1"=="mobile" set OPTS=-Dmobile.tests=true

echo.
echo === Dang chay: mvn clean test %OPTS%
echo.
call mvn clean test %OPTS%
if errorlevel 1 goto :error

echo.
echo === TAT CA TEST DEU PASS ===
echo   - Bao cao do phu ma lenh : target\site\jacoco\index.html
echo   - Du lieu cho Allure     : target\allure-results  (xem bang: allure serve target\allure-results)
echo.
pause
exit /b 0

:error
echo.
echo === CO TEST BI FAIL - doc log ben tren, hoac mo target\surefire-reports ===
pause
exit /b 1
