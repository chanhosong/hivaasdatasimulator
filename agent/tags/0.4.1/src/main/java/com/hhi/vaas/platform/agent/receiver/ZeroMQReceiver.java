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
package com.hhi.vaas.platform.agent.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.parser.DataConverter;

/**
 * <pre>
 * ZeroMQ-based data receiver
 * 
 * - http://zeromq.org/
 * - https://github.com/zeromq/jeromq
 * 
 * </pre>
 * @author BongJin Kwon
 *
 */
public class ZeroMQReceiver extends AbstractDataReceiver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZeroMQReceiver.class);

	private ZMQ.Context context;
	private ZMQ.Socket receiver;
	private String bindAddr;
	
	/**
	 * @param dataConverter
	 * @param queueClient
	 * @param props
	 */
	public ZeroMQReceiver(DataConverter dataConverter, RabbitMQSender sender, PropertyService props) {
		super(dataConverter, sender, props);
		
		context = ZMQ.context(1);
		bindAddr = props.getProperty("vaas.agent.zmq.bind.addr", String.class, "tcp://*:5558");
	}
	

	public String getBindAddr() {
		return bindAddr;
	}


	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.agent.receiver.AbstractDataReceiver#preStart()
	 */
	@Override
	protected void preStart() {
		
		//  Prepare socket
        receiver = context.socket(ZMQ.PULL);
        
        LOGGER.info("listening {}", bindAddr);
        receiver.bind(bindAddr);

	}

	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.agent.receiver.AbstractDataReceiver#receiveData()
	 */
	@Override
	protected String receiveData() {
		
		return new String(receiver.recv(0)).trim();
	}

	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.agent.receiver.AbstractDataReceiver#stop()
	 */
	@Override
	protected void stop() {
		receiver.close();
        context.term();

	}

}
//end of JeroMQReceiver.java