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

/**
 * <pre>
 * Data Receiver interface.
 * </pre>
 * @author BongJin Kwon
 *
 */
public interface DataReceiver {
	
	public static final String TYPE_UDP = "udp";
	public static final String TYPE_NAMED_PIPE = "namedPipe";
	public static final String TYPE_ZMQ = "zmq";
	
	public void start();
}
//end of DataReceiver.java