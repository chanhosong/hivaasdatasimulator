@echo off

set "AGENT_SERVICE=VDIPAgent"
set "AGENT_MASTER_SERVICE=VDIPAgentMaster"

sc stop %AGENT_MASTER_SERVICE%
sc stop %AGENT_SERVICE%

prunsrv.exe //DS//%AGENT_MASTER_SERVICE%
prunsrv.exe //DS//%AGENT_SERVICE%




