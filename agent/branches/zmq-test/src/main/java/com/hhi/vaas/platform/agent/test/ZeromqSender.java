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
import org.zeromq.ZMQ;

/**
 * Agent test client.
 * @author BongJin Kwon
 *
 */
public class ZeromqSender {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZeromqSender.class);

	private int msgCount = 100;
	private long timeSleep = 100;
	private long recvCount;
	private boolean logSending;
	
	/**
	 * 
	 * @param msgCount -1 이면 파일의 모든 데이타를 전송
	 * @param timeSleep
	 * @param logSending
	 */
	public ZeromqSender(int msgCount, long timeSleep, boolean logSending) {
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

	public void sendFile(InputStream is, ZMQ.Socket sender) {
		
		
		LOGGER.info("sending {} msg.", msgCount);
		
    	BufferedReader br = null;
    	int i = 0;
    	try {
    		// for zeromq connect.
    		Thread.sleep(1000);
    		
    		br = new BufferedReader(new InputStreamReader(is));
    		
    		String line = null;
    		long start = System.currentTimeMillis();
    		while ((line = br.readLine()) != null) {
    			
    			sender.send(line);
    			
    			i++;
    			if (logSending && i%1000 == 0) {
    				LOGGER.info(i+" send.");
    			}
    			
    			if(msgCount > -1 && i == msgCount){
    				break;
    			}
    			
    			Thread.sleep(timeSleep);
    		}
    		long elapsedTime = System.currentTimeMillis() - start;
    		LOGGER.info("sended count: " + i + ", elapsedTime: "+ elapsedTime + ", sended " + (msgCount/(elapsedTime/1000.0)) + " msg/sec .");
    		
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
		
		ZeromqSender client = new ZeromqSender(-1, 0, true);
		
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket sender = context.socket(ZMQ.PUSH);
	    sender.connect("tcp://127.0.0.1:5588");
	    sender.setSendTimeOut(0);
	    
		client.sendFile(ZeromqSender.class.getResourceAsStream("/ship_no_2619.txt"), sender);
		
		context.term();// queue 에 데이타가 있으면 term 안됨.
		System.out.println("finished!!");
	}

}
//end of TestClient.java