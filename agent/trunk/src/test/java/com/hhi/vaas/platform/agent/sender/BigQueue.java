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
 * Bongjin Kwon	2015. 4. 30.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.sender;

import com.leansoft.bigqueue.BigArrayImpl;
import com.leansoft.bigqueue.BigQueueImpl;
import com.leansoft.bigqueue.IBigQueue;

import java.io.IOException;

/**
 * @author BongJin Kwon
 *
 */
public class BigQueue {

	/**
	 * 
	 */
	public BigQueue() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) throws IOException {
		
		// create a new big queue
		IBigQueue bigQueue = null;
		try {
		    bigQueue = new BigQueueImpl("d:/bigqueue/tutorial", "demo", BigArrayImpl.MINIMUM_DATA_PAGE_SIZE);
		
			// enqueue some items
		    int length = 100;
			for(int i = 0; i < length; i++) {
			  String item = String.valueOf(i);
			  bigQueue.enqueue(item.getBytes());
			  
			  System.out.println("enq : " + item);
			}
			System.out.println("");
			
			// get current size of the queue
			long size = bigQueue.size();
			System.out.println("queue size: " + size);
			
			// peek the front of the queue
			String item = new String(bigQueue.peek());
			System.out.println("queue peek: " + item);
			
			
			// dequeue all remaining items
			while(true) {
			  byte[] data = bigQueue.dequeue();
			  
			  if (data == null) break;
			  System.out.println("deq : " + new String(data));
			}
			
			System.out.println("bigQueue.isEmpty() : "+ bigQueue.isEmpty());
			
		} finally {
			if(bigQueue != null){
				bigQueue.close();
			}
		}
		
		
	}

}
//end of BigQueueHandler.java