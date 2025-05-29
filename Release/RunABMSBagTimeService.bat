@echo off
REM === CONFIGURATION ===

REM Full path to your Java installation
set JAVA_HOME=C:\Java\jdk-17.0.2

REM Path to your JAR file
set JAR_PATH=C:\MyApp\myapp.jar

REM Optional Java options
set JAVA_OPTS=-Xms512m -Xmx1024m

REM === EXECUTION ===

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: Java not found at %JAVA_HOME%\bin\java.exe
    pause
    exit /b 1
)

echo Running application with %JAVA_HOME%\bin\java.exe

"%JAVA_HOME%\bin\java.exe" %JAVA_OPTS% -jar "%JAR_PATH%"

pause
