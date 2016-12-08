@echo off

set AGENT_SERVICE=VDIPAgent
set AGENT_MASTER_SERVICE=VDIPAgentMaster

set PR_DESCRIPTION="HHI VDIP Agent"
set PR_INSTALL=%cd%\prunsrv.exe

REM Service log configuration
REM set PR_LOGPREFIX=%AGENT_SERVICE%
REM set PR_LOGPATH=c:\logs
REM set PR_STDOUTPUT=c:\logs\stdout.txt
REM set PR_STDERROR=c:\logs\stderr.txt
REM set PR_LOGLEVEL=Error
 
REM Path to java installation
REM set PR_JVM=C:\Program Files\Java\jre7\bin\server\jvm.dll
set PR_JVM=auto
set PR_CLASSPATH=./conf;lib\*
 
REM Startup configuration
set PR_STARTUP=auto
set PR_STARTMODE=jvm
set PR_STARTCLASS=com.hhi.vaas.platform.agent.AgentMain
set PR_STARTMETHOD=start
 
REM Shutdown configuration
set PR_STOPMODE=jvm
set PR_STOPCLASS=com.hhi.vaas.platform.agent.AgentMain
set PR_STOPMETHOD=stop
 
REM JVM configuration
set PR_JVMMS=256
set PR_JVMMX=256
REM set PR_JVMSS=4000
REM set PR_JVMOPTIONS=-Duser.language=DE;-Duser.region=de
 
REM ---- Install service
prunsrv.exe //IS//%AGENT_SERVICE%



REM ---------------------- Install Agent Master Service ---------------

set PR_CLASSPATH=./conf;vdip-agent-master-1.0.0-jar-with-dependencies.jar
set PR_STARTCLASS=com.hhi.vaas.platform.agent.master.MasterMain
set PR_STOPCLASS=com.hhi.vaas.platform.agent.master.MasterMain

REM ---- Install service
prunsrv.exe //IS//%AGENT_MASTER_SERVICE%
sc failure %AGENT_MASTER_SERVICE% reset= 30 actions= restart/15000/restart/15000/restart/15000
sc start %AGENT_MASTER_SERVICE%

