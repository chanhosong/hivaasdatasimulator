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
 * Bongjin Kwon	2015. 4. 20.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.test;

import org.zeromq.ZMQ;

/**
 * @author BongJin Kwon
 *
 */
public class TestZeroMQSender implements TestSender {

	private ZMQ.Context context = ZMQ.context(1);
	private ZMQ.Socket sender;
	
	public TestZeroMQSender(String pullAddr) {
		sender = context.socket(ZMQ.PUSH);
	    sender.connect(pullAddr);
	}

	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.agent.test.TestSender#send(java.lang.String)
	 */
	@Override
	public void send(String message) {
		sender.send(message);

	}

	@Override
	public void close() {
		if(sender != null){
			sender.close();
		}
		context.term();
	}
	
	

}
//end of JeroMQTestSender.java