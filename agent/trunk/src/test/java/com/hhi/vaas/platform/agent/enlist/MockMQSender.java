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
 * Bongjin Kwon	2015. 5. 11.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.enlist;

import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMessageClient;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;

/**
 * @author BongJin Kwon
 *
 */
public class MockMQSender extends RabbitMQSender {

	/**
	 * @param props
	 */
	public MockMQSender(PropertyService props) {
		super(props);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setQueueClient(VaasMessageClient queueClient) {
		
	}

	@Override
	public void prepare() {
		
	}

	@Override
	public void close() {
		
	}
	

}
//end of MockMQSender.java