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

import com.hhi.vaas.platform.agent.model.BackupMessage;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.leansoft.bigqueue.BigArrayImpl;
import com.leansoft.bigqueue.BigQueueImpl;
import com.leansoft.bigqueue.IBigQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BongJin Kwon
 *
 */
public class BigQueueHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BigQueueHandler.class);
	
	private static final String MSG_DELIMETER = ";";

	private IBigQueue bigQueue;
	
	private int reSendCount;
	
	private long maxQueueSize;
	
	
	public BigQueueHandler(PropertyService props) {
		
		this.reSendCount = props.getProperty("vaas.mq.clent.resend.count", Integer.class, 10);
		this.maxQueueSize = props.getProperty("vaas.mq.clent.queue.max", Integer.class, 200000);
		
		try {
			String queueDir = props.getProperty("vaas.bigqueue.dir", String.class, "backup_queue");
			String queueName = props.getProperty("vaas.bigqueue.name", String.class, "message");
			
			bigQueue = new BigQueueImpl(queueDir, queueName, BigArrayImpl.MINIMUM_DATA_PAGE_SIZE);
		} catch (IOException e) {
			LOGGER.warn("Backup queue was not created. {}", e.toString());
		}
	}
	
	/**
	 * backup message.
	 * 
	 * @param routingKey
	 * @param body
	 */
	public void backupMessage(String routingKey, String body){
		
		try {
			
			if(bigQueue.size() > maxQueueSize){
				LOGGER.warn("The max queue size has been exceeded. queue size : {}, The message is discarded.", bigQueue.size());
				return;
			}
			
			bigQueue.enqueue((routingKey + MSG_DELIMETER + body).getBytes());
			
			LOGGER.debug("backup message.");
			
		} catch (IOException e) {
			LOGGER.warn("backup fail. {}", e.toString());
		}
		
	}
	
	public List<BackupMessage> getMessages(){
		
		List<BackupMessage> messages = new ArrayList<BackupMessage>();
		
		try {
			for (int i = 0; i < reSendCount; i++) {
				byte[] data = bigQueue.dequeue();
				if (data == null) break;
				
				String strData = new String(data);
				BackupMessage msg = new BackupMessage();
				int pos = strData.indexOf(MSG_DELIMETER);
				
				msg.setRoutingKey(strData.substring(0, pos));
				msg.setBody(strData.substring(pos+1));
				
				messages.add(msg);
				
			}
		} catch (IOException e) {
			LOGGER.warn("dequeue fail. {}", e.toString());
		}
		
		return messages;
		
	}
	
	public long queueSize(){
		return bigQueue.size();
	}
	

}
//end of BigQueueHandler.java