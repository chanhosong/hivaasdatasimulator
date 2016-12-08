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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMQMessage;
import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMessageClient;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Method;

/**
 * @author BongJin Kwon
 *
 */
public class MockVaasMessageClient implements VaasMessageClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MockVaasMessageClient.class);

	private boolean isOpen = true;
	private boolean isClosed;
	private boolean failed;
	
	private int sendMsgCount;
	
	public MockVaasMessageClient() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMessageClient#getFactory()
	 */
	@Override
	public ConnectionFactory getFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMessageClient#getChannel()
	 */
	@Override
	public Channel getChannel() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMessageClient#declare(java.lang.String)
	 
	@Override
	public void declare(String name) throws IOException {
		LOGGER.debug("declare.");
		isOpen = true;
		isClosed = false;

	}*/

	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMessageClient#declare(java.lang.String, java.lang.String)
	 
	@Override
	public void declare(String exchangeName, String exchangeType)
			throws IOException {

	}*/

	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMessageClient#basicPublish(com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMQMessage)
	 */
	@Override
	public void basicPublish(VaasMQMessage message) {
		
		sendMsgCount++;
		if(failed == false && sendMsgCount == 6){
			failed = true;
			sendMsgCount--;

			close();
			throw new RuntimeException("send fail test.");
		}
		

	}

	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMessageClient#isOpen()
	 */
	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return isOpen;
	}

	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMessageClient#close()
	 */
	@Override
	public void close() {
		isClosed = true;
		isOpen = false;
		LOGGER.debug("closed.");
	}

	public int getSendMsgCount() {
		return sendMsgCount;
	}

	@Override
	public Method declare(String name) throws IOException {
		LOGGER.debug("declare.");
		isOpen = true;
		isClosed = false;
		
		return null;
	}

	@Override
	public Method declare(String exchangeName, String exchangeType)
			throws IOException {
		LOGGER.debug("declare.");
		isOpen = true;
		isClosed = false;
		
		return null;
	}

	@Override
	public void setNetworkRecoveryInterval(int networkRecoveryInterval) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConnectionTimeout(int connectionTimeout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAutomaticRecoveryEnabled(boolean automaticRecovery) {
		// TODO Auto-generated method stub
		
	}
	

}
//end of MockVaasMessageClient.java