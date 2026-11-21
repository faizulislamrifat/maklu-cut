@echo off
title MAKLU CUT Server
cd /d "%~dp0"

echo ========================================================
echo                 Starting MAKLU CUT...
echo ========================================================
echo.

:: 1. Detect Python
set "PY="
if exist ".venv\Scripts\python.exe" set "PY=%~dp0.venv\Scripts\python.exe"
if not defined PY if exist "venv\Scripts\python.exe" set "PY=%~dp0venv\Scripts\python.exe"
if not defined PY (
    where python >nul 2>&1
    if not errorlevel 1 set "PY=python"
)
if not defined PY (
    where py >nul 2>&1
    if not errorlevel 1 set "PY=py"
)

if not defined PY (
    echo [ERROR] Python was not found on this computer!
    echo Please install Python from https://www.python.org/downloads/
    echo Remember to check "Add Python to PATH" during installation.
    echo.
    pause
    exit /b 1
)

:: 2. Auto-install dependencies if needed
if not exist ".venv" (
    if not exist "venv" (
        echo [INFO] First-time setup: installing dependencies...
        "%PY%" -m pip install -r requirements.txt
    )
)

:: 3. Open browser
start "" explorer "http://127.0.0.1:8765"

:: 4. Run Server
echo MAKLU CUT server is running at http://127.0.0.1:8765
echo.
echo NOTE: Keep this window open while using MAKLU CUT!
echo.

"%PY%" -m studio.server

echo.
echo ========================================================
echo [Notice] MAKLU CUT server stopped.
echo ========================================================
pause
