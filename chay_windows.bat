@echo off
cd /d %~dp0
if not exist out mkdir out
dir /s /b src\*.java > out\sources.list
javac -encoding UTF-8 -d out -cp ".;lib\mysql-connector-j-9.6.0.jar" @out\sources.list
if %errorlevel% neq 0 pause & exit /b
java -cp "out;lib\mysql-connector-j-9.6.0.jar" main.Main
pause
