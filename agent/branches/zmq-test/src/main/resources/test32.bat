@echo off


echo ========================
echo   starting ZeromqSender (32bit).
echo ========================

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

set PATH=%PATH%;%AGENT_HOME%\lib_zmq_win32

echo %PATH%

"%VDIP_JAVA%" -Djava.library.path=%AGENT_HOME%\lib_zmq_win32 -classpath ./conf;lib\*;lib_zmq_jar\jzmq-3.1.0.jar com.hhi.vaas.platform.agent.test.ZeromqSender
goto end

:exit
exit /b 1

:end
exit /b 0