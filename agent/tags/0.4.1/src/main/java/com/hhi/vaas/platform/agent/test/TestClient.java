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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Agent test client.
 * @author BongJin Kwon
 *
 */
public class TestClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestClient.class);

	private int msgCount = 100;
	private long timeSleep = 100;
	private long recvCount;
	private boolean logSending;
	
	public TestClient() {
	}
	
	public TestClient(int msgCount, long timeSleep, boolean logSending) {
		this.msgCount = msgCount;
		this.timeSleep = timeSleep;
		this.logSending = logSending;
	}
	
	public int getMsgCount() {
		return msgCount;
	}
	
	public void received(){
		recvCount++;
	}
	
	public long getRecvCount(){
		return recvCount;
	}

	public void sendFile(InputStream is, TestSender sender) {
		
		LOGGER.info("sending {} msg.", msgCount);
		
    	BufferedReader br = null;
    	int i = 0;
    	try {
    		br = new BufferedReader(new InputStreamReader(is));
    		
    		String line = null;
    		long start = System.currentTimeMillis();
    		while ((line = br.readLine()) != null) {
    			
    			sender.send(line);
    			
    			i++;
    			if (logSending) {
    				LOGGER.info(i+" send: "+ line);
    			}
    			
    			if(i == msgCount){
    				break;
    			}
    			
    			Thread.sleep(timeSleep);
    		}
    		long elapsedTime = System.currentTimeMillis() - start;
    		LOGGER.info("sended count: " + i + ", elapsedTime: "+ elapsedTime);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		
    		if (br != null) {
    			try {
    				br.close();
    			} catch (IOException e) {}
    		}
    		sender.close();
    		LOGGER.info("sender closed.");
    	}
    	
    }
	
	public static void main(String[] args) {
		
		int msgCount = 100;
		int timeSleep = 100;
		
		if(args.length > 0){
			msgCount = Integer.parseInt(args[0]);
		}
		
		if(args.length > 1){
			timeSleep = Integer.parseInt(args[1]);
		}
		
		TestClient client = new TestClient(msgCount, timeSleep, true);
		TestZeroMQSender sender = new TestZeroMQSender("tcp://127.0.0.1:5558");
		client.sendFile(TestClient.class.getResourceAsStream("/test_data.txt"), sender);
	}

}
//end of TestClient.java