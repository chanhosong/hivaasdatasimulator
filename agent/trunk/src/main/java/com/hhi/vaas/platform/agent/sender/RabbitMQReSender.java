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
 * Bongjin Kwon	2015. 4. 28.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.sender;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.agent.model.BackupMessage;

/**
 * reconnect mq & resend backup message
 * 
 * @author BongJin Kwon
 *
 */
public class RabbitMQReSender implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQReSender.class);

	private RabbitMQSender sender;
	private BigQueueHandler backupHandler;
	
	private boolean isRun;
	
	public RabbitMQReSender(RabbitMQSender sender, BigQueueHandler backupHandler) {
		this.sender = sender;
		this.backupHandler = backupHandler;
		
		
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		isRun = true;
		
		LOGGER.info("start.");
		
		
		while(this.sender.isOpen() == false){
			try{
				Thread.sleep(500);
				
				//create new channel
				this.sender.reConnect();
				
				if(this.sender.isOpen()){
					LOGGER.info("VaasQueueClient is connected.");
					
					reSendMessage();
				}
				
			}catch(Exception e){
				LOGGER.error(e.toString(), e);
			}
		}
		
		
		isRun = false;
		LOGGER.info("stoped.");
	}

	public boolean isRun() {
		return isRun;
	}
	
	/**
	 * resend backup message.
	 * 
	 * @param connection
	 */
	private void reSendMessage(){
		
		int cnt = 0;
		int check = 0;
		while(true){
			List<BackupMessage> msgs = this.backupHandler.getMessages();
			
			if(msgs.size() == 0){
				LOGGER.info("all backup data is resended. count: {}", cnt);
				isRun = false;
				
				
				if(check < 3){
					check++;
					continue;
				}
				
				break;
			}
			
			for (BackupMessage backupMessage : msgs) {
				
				sender.doSendMessage(backupMessage.getRoutingKey(), backupMessage.getBody());
				cnt++;
				LOGGER.debug("resend : {}", backupMessage.getBody());
			}
			
		}
		
	}
	
	

}
//end of RabbitMQReSender.java