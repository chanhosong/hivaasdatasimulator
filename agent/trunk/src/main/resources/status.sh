#!/bin/bash

PID=`ps -ef | grep java | grep "agent.AgentMain" | awk '{print $2}'`

#Do not modify below never
if [[ -z ${PID} ]];then
    echo "HiVaas Agent is STOPPED."
else
    echo "HiVaas Agent is RUNNING."
fi
