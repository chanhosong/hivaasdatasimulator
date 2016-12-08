@echo off


rem set "JAVA_HOME=C:\Program Files\Java\jdk1.7.0_45"
set "AGENT_HOME=%cd%"

echo AGENT_HOME: %AGENT_HOME%

if not "%JAVA_HOME%" == "" goto gotJava
goto noJavaHome


:gotJava
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
set "VDIP_JAVA=%JAVA_HOME%\bin\java"
goto runJava

:noJavaHome
set "VDIP_JAVA=java"
goto runJava


:runJava
echo VDIP_JAVA: %VDIP_JAVA%
"%VDIP_JAVA%" -version
echo ----------------------------------------------------------

set "JAVA_OPTS=%JAVA_OPTS% -Xms256m -Xmx256m"
set "JAVA_OPTS=%JAVA_OPTS% -Dcom.sun.management.jmxremote.port=3333"
set "JAVA_OPTS=%JAVA_OPTS% -Dcom.sun.management.jmxremote.ssl=false"
set "JAVA_OPTS=%JAVA_OPTS% -Dcom.sun.management.jmxremote.authenticate=false"

"%VDIP_JAVA%" -classpath ./conf;lib\* %JAVA_OPTS% com.hhi.vaas.platform.agent.AgentMain
goto end

:exit
exit /b 1

:end
exit /b 0