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
 * Bongjin Kwon	2015. 5. 8.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.agent.model.AgentStatus;
import com.hhi.vaas.platform.agent.model.Authentication;
import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;

/**
 * agent activator
 * 
 * @author BongJin Kwon
 *
 */
public class Activator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

	private RabbitMQSender sender;
	private AgentStatus status;
	
	
	public Activator(RabbitMQSender sender, AgentStatus status) {
		this.sender = sender;
		this.status = status;
	}
	
	public void activate(Authentication auth, PropertyService props){
		
		props.put("vaas.mq.username", auth.getUsername());
		props.put("vaas.mq.passwd", auth.getPassword());
		
		sender.close();
		
		sender.setQueueClient(sender.createVaasMessageClient(props));
		sender.prepare();
		
		status.setAuthentication(auth);
		
		LOGGER.info("activated.");
	}

}
//end of Activator.java