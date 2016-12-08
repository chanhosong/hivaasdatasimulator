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
 * Bongjin Kwon	2015. 7. 7.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BongJin Kwon
 *
 */
public class AgentProcessChecker implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AgentProcessChecker.class);

	private String serviceName;
	private long checkInterval;
	private UpdateHandler updateHandler;
	
	public AgentProcessChecker(String serviceName, long checkInterval, UpdateHandler updateHandler) {
		
		this.serviceName = serviceName;
		this.checkInterval = checkInterval;
		this.updateHandler = updateHandler;
	}

	@Override
	public void run() {
		
		
		while (MasterMain.isStarted()) {
			
			try {
				Thread.sleep(checkInterval);
			} catch (InterruptedException e) {
				//ignore.
			}
			
			String status = CommandUtil.checkAgentStatus(serviceName);
			
			if (CommandUtil.STOPPED.equals(status) && updateHandler.isUpdating() == false && MasterMain.isStarted()) {
				CommandUtil.startAgent(serviceName);
			}
			
		}
		
		LOGGER.info("stopped!!");
		
	}
	
	
	

}
//end of AgentWindowsProcessChecker.java