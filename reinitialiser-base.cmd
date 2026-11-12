@echo off
rem Remet la base de donnees a zero (donnees de demo rechargees au prochain lancement)
cd /d "%~dp0"
docker compose down -v
docker compose up -d
echo.
echo Base remise a zero. Lance maintenant lancer.cmd
pause
