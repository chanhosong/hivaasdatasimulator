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
 * Bongjin Kwon	2015. 4. 22.		First Draft.
 */
package com.hhi.vaas.platform.agent.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

import com.hhi.vaas.platform.middleware.common.util.PropertyService;

public class ZeromqReceiver {


	public static void main(String[] args) {
		System.out.println("========================");
		System.out.println("  starting "+ZeromqReceiver.class.getSimpleName()+".");
		System.out.println("========================");

		PropertyService props = new PropertyService("");
		
		ZMQ.Context context = ZMQ.context(1);
		String bindAddr = props.getProperty("vaas.agent.zmq.bind.addr", String.class, "tcp://*:5588");

		//  Prepare socket
		ZMQ.Socket receiver = context.socket(ZMQ.PULL);
        
		System.out.println("listening "+ bindAddr);
        receiver.bind(bindAddr);
        
        double msgCount = 60000;
        
        long start = 0;
        for (int i = 0; i < msgCount; i++) {
        	String recevedData = new String(receiver.recv(0)).trim();
        	
        	if(i == 0){
        		start = System.currentTimeMillis();
        	}
        	
        	if(i%1000 == 0){
        		System.out.println("receved "+ i);
        	}
		}
        
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("msgCount : " + msgCount + ", elapsedTime : "+ elapsedTime + ", receved " + (msgCount/(elapsedTime/1000.0)) + " msg/sec .");
        
        receiver.close();
        context.term();
        System.out.println("finished!!");
	}

}
//end of ZeroMQPull.java