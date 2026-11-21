@echo off
cd /d "%~dp0"
echo Starting MAKLU CUT...
.venv\Scripts\python.exe -m studio.server
pause
