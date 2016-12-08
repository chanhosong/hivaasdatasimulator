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

import java.io.IOException;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMQException;
import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMQMessage;
import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMessageClient;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.parser.DataConverter;

/**
 * @author BongJin Kwon
 *
 */
public abstract class AbstractDataReceiver implements DataReceiver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataReceiver.class);

	private DataConverter dataConverter;
	private RabbitMQSender sender;
	protected PropertyService props;
	
	boolean isMQConnected;
	/**
	 * 
	 */
	public AbstractDataReceiver(DataConverter dataConverter, RabbitMQSender sender, PropertyService props) {
		this.dataConverter = dataConverter;
		this.props = props;
		
		this.sender = sender;
	}

	/**
	 * @param propertyService the propertyService to set
	 */
	public void setPropertyService(PropertyService props) {
		this.props = props;
	}
	
	

	@Override
	public void start() {
		LOGGER.debug("context.properties : [{}]", props.getAllProperties());
		
		int agentMode = props.getProperty("vaas.agent.mode", Integer.class);
        
		try {
			preStart();
			LOGGER.info("waiting message...");
			
			if(agentMode > 2) {
				this.sender.prepare();
			}
	        
	        
	        String routingKey;
	        
	        long start = System.currentTimeMillis();
	        int i = 0;
	        
	        while (true) {
	        	try {
	        		routingKey = this.sender.getRoutingKey();
		        	
		            String recvStr = receiveData();
		            LOGGER.debug("Received Message : [{}]", recvStr);
		            
		            String converted = null;
		            if(agentMode > 1) {
			            
			            try {
			            	// json 으로 변환.
			            	converted = dataConverter.convert(recvStr);
			            } catch (Exception e) {
			            	LOGGER.warn(e.toString());
			            	converted = recvStr;
			            	routingKey = this.sender.getErrRoutingKey();
			            }
			            
		            }
		            
		            if(agentMode > 2) {
		            	this.sender.sendMessage(routingKey, converted);
		            }
		            i++;
		            
	        	} catch (VaasMQException e){
	        		
	        	} catch (Exception e) {
	        		
	        		LOGGER.error(e.toString(), e);
	        		
	        		if (e instanceof SocketException && e.getMessage().startsWith("Socket closed")) {
						break;
					}
	        	}
	        	
	        	if(LOGGER.isDebugEnabled()){
	        		long elapsedTime = System.currentTimeMillis() - start;
		        	LOGGER.debug("sended count: "+ i +", elapsedTime: "+ elapsedTime);
	        	}
	        	
	        } //end of while
	        
	        
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
		} finally {
			this.stopAll();
		}
		
	}
	
	
	/**
	 * stop Receiver.
	 */
	protected void stopAll() {
		stop();
		
		if (this.sender != null) {
			this.sender.close();
		}
		
		LOGGER.info("Receiver stopped.!!");
	}
	
	/**
	 * pre process before starting.
	 */
	protected abstract void preStart();
	
	/**
	 * data receive function
	 * 
	 * @return
	 * @throws IOException
	 */
	protected abstract String receiveData();
	
	/**
	 * This is an explicit "destructor". It can be called to ensure the corresponding Receiver has been disposed of.
	 */
	protected abstract void stop();

}
//end of NamedPiple.java