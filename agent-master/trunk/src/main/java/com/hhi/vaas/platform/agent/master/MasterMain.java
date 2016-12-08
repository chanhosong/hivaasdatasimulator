/* Copyright (C) 2015~ Hyundai Heavy Industries. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Hyundai Heavy Industries
 * You shall not disclose such Confidential Information and shall use it only 
 * in accordance with the terms of the license agreement
 * you entered into with Hyundai Heavy Industries.
 *
 * Revision History
 * Author			Date				Description
 * ---------------	----------------	------------
 * Bongjin Kwon	2015. 6. 30.		First Draft.
 */
package com.hhi.vaas.platform.agent.master;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.middleware.common.util.PropertyService;


public class MasterMain {

	private static final Logger LOGGER = LoggerFactory.getLogger(MasterMain.class);
	
	/**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';

    /**
     * The system separator character.
     */
    private static final char SYSTEM_SEPARATOR = File.separatorChar;
    
	private static boolean stop = false;

	public static void main(String[] args) {
		/*
		if(args.length == 0){
			System.out.println("com.hhi.vaas.platform.agent.master.MasterMain [start|stop]");
			return;
		}
		*/
		
		if (args.length == 0 || "start".equals(args[0])) {
            start(args);
        } else if ("stop".equals(args[0])) {
            stop(args);
        }
		
		System.out.println("master finished!!");
	}
	
	public static void start(String[] args) {
		
		PropertyService props = new PropertyService("");
		
		
		String serviceName = props.getProperty("vaas.agent.service.name", String.class, "VDIPAgent");
		long checkInterval = props.getProperty("vaas.agent.pc.check.interval", Long.class, 5000L);
		
		String downPath = null;
		if(MasterMain.isSystemWindows()){
			downPath = "C:/Temp" + props.getProperty("vaas.agent.down.path", String.class, "/agentUpdate");
		} else {
			downPath = "/tmp" + props.getProperty("vaas.agent.down.path", String.class, "/agentUpdate");
		}
		long fileCheckInterval = props.getProperty("vaas.agent.file.check.interval", Long.class, 5000L);
		
		String updateTargetPath = System.getProperty("user.dir");
		UpdateHandler updateHandler = new UpdateHandler(downPath, updateTargetPath, serviceName);
		
		/*
		 * start process checker
		 */
		new Thread(new AgentProcessChecker(serviceName, checkInterval, updateHandler)).start();
		
		/*
		 * start update watcher
		 */
		try {
			
			UpdateFileWatcher watcher = new UpdateFileWatcher(downPath, fileCheckInterval);
			watcher.startWatch(updateHandler);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
	}
	
	public static void stop(String[] args) {
		stop = true;
	}
	
	protected static boolean isStarted(){
		return stop == false;
	}
	
	public static boolean isSystemWindows() {
        return SYSTEM_SEPARATOR == WINDOWS_SEPARATOR;
    }

}
//end of MasterMain.java