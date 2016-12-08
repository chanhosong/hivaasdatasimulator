#!/bin/sh
ps -ef | grep java | grep -v grep | grep "com.hhi.vaas.platform.agent.master.MasterMain" | awk {'print "kill -9 " $2'} | sh -x
