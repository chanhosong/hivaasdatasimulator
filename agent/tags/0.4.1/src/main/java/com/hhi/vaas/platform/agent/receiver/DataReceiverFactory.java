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
 * Bongjin Kwon	2015. 4. 13.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMessageClient;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.parser.DataConverter;

/**
 * @author BongJin Kwon
 *
 */
public abstract class DataReceiverFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataReceiverFactory.class);

	/**
	 * 
	 */
	public DataReceiverFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public static DataReceiver create(String recvType, DataConverter dataConverter, RabbitMQSender sender, PropertyService props){
		
		DataReceiver dataReceiver = null;
		
		if (DataReceiver.TYPE_UDP.equals(recvType)) {
			dataReceiver = new UDPServer(dataConverter, sender, props);
		
		} else if (DataReceiver.TYPE_ZMQ.equals(recvType)) {
			dataReceiver = new ZeroMQReceiver(dataConverter, sender, props);
		}
		
		if(dataReceiver == null){
			throw new IllegalArgumentException("Not supported receiver type : "+recvType);
		}
		
		LOGGER.info("DataReceiver is {}.", dataReceiver.getClass().getName());
		
		return dataReceiver;
	}

}
//end of DataReceiverFactory.java