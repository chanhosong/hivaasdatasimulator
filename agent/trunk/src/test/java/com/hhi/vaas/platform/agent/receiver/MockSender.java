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
 * Bongjin Kwon	2015. 5. 6.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;

/**
 * @author BongJin Kwon
 *
 */
public class MockSender extends RabbitMQSender {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MockSender.class);
	
	private int backupMsgCount;
	private int sendMsgCount;
	/**
	 * @param props
	 */
	public MockSender(PropertyService props) {
		super(props);
	}


	@Override
	public void prepare() {
		if(backupMsgCount > 10){
			//reconnect after 10 backup message.
			super.prepare();
		}
	}
	


	@Override
	public void sendMessage(String routingKey, String body) {
		sendMsgCount++;
		super.sendMessage(routingKey, body);
	}


	@Override
	protected void backupMessage(String routingKey, String body) {
		backupMsgCount++;
		super.backupMessage(routingKey, body);
		LOGGER.debug("{} backup message.", backupMsgCount);
	}


	public int getBackupMsgCount() {
		return backupMsgCount;
	}

	public int getSendMsgCount() {
		return sendMsgCount;
	}
	

}
//end of MockSender.java