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
 * Bongjin Kwon	2015. 3. 24.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.agent.model.AgentStatus;
import com.hhi.vaas.platform.agent.receiver.DataReceiver;
import com.hhi.vaas.platform.agent.receiver.DataReceiverFactory;
import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.parser.DataConverter;
import com.hhi.vaas.platform.vdm.parser.DataConverterFactory;

/**
 * <pre>
 * agent execution class
 * </pre>
 * 
 * @author BongJin Kwon
 * 
 */
public class AgentMain {
	
	private static final Logger logger = LoggerFactory.getLogger(AgentMain.class);

	/**
	 * start agent.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		
		System.out.println("========================");
		System.out.println("  starting VDIP Agent.");
		System.out.println("========================");

		PropertyService props = new PropertyService("");
		props.put("vaas.agent.id", "testagent");
		
		AgentStatus status = new AgentStatus();
		
		try {
			DataConverter dataConverter = DataConverterFactory.create(props.getResourceStreamFromKey("mapping.xml.path"));
		
			/*
			 * 1. Enlisting
			 */
			//TODO EnlistHandler.process(status, props);
			
	
			/*
			 * 2. Health noti
			 */
			//TODO new Thread(new HealthNotifier(status, props, new UpdateHandler(jsonConverter))).start();
	
			
			/*
			 * 3. transfer to middleware
			 */
			RabbitMQSender sender = new RabbitMQSender(props);
			String recvType = props.getProperty("vaas.agent.type");
			
			DataReceiver dataReceiver = DataReceiverFactory.create(recvType, dataConverter, sender, props);
			dataReceiver.start();
		
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}

	}
	
	

}
// end of AgentMain.java