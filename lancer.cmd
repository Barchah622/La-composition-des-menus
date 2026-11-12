@echo off
rem Lance l'application La composition des menus (double-clic)
chcp 65001 >nul
cd /d "%~dp0"
set MVN=mvn
where mvn >nul 2>nul || set MVN=%USERPROFILE%\tools\apache-maven-3.9.9\bin\mvn.cmd
call "%MVN%" -q compile exec:java -Dexec.args="%*"
pause
