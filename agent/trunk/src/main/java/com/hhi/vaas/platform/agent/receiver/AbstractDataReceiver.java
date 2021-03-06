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

import com.hhi.vaas.platform.agent.model.AgentStatus;
import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.parser.DataConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQException;

import java.io.IOException;

/**
 * <pre>
 * common logic of DataReceiver.
 * </pre>
 * @author BongJin Kwon
 *
 */
public abstract class AbstractDataReceiver implements DataReceiver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataReceiver.class);

	private DataConverter dataConverter;
	private RabbitMQSender sender;
	protected PropertyService props;
	private AgentStatus status;
	
	boolean isMQConnected;
	private static boolean stoped;
	/**
	 * 
	 */
	public AbstractDataReceiver(DataConverter dataConverter, RabbitMQSender sender, PropertyService props, AgentStatus status) {
		this.dataConverter = dataConverter;
		this.props = props;
		
		this.sender = sender;
		this.status = status;
	}

	/**
	 * @param propertyService the propertyService to set
	 */
	public void setPropertyService(PropertyService props) {
		this.props = props;
	}
	
	

	@Override
	public void start() {
		stoped = false;
		LOGGER.debug("context.properties : [{}]", props.getAllProperties());
		
		int agentMode = props.getProperty("vaas.agent.mode", Integer.class);
        
		try {
			preStart();
			LOGGER.info("waiting message...");
			
			if (agentMode > 2) {
				if (status.isActivated()){
					this.sender.prepare();
				}
			}
	        
	        
	        String routingKey;
	        long cnt = 0;
	        long start = System.currentTimeMillis();
	        while (isStoped() == false) {
	        	try {
	        		routingKey = this.sender.getRoutingKey();
		        	
		            String recvStr = receiveData();
		            LOGGER.debug("Received : {}", recvStr);
		            
		            
		            if (status.isActivated() == false){
		            	LOGGER.warn("Agent is not activated. message is discarded.");
		            	continue;
		            }
		            
		            String converted = null;
		            if (agentMode > 1) {
			            
			            try {
			            	// json 으로 변환.
			            	converted = dataConverter.convert(recvStr);
			            } catch (Exception e) {
			            	if(LOGGER.isDebugEnabled()){
			            		LOGGER.error(e.getMessage(), e);
			            	} else {
			            		LOGGER.error(e.toString());
			            	}
			            	converted = recvStr;
			            	routingKey = this.sender.getErrRoutingKey();
			            }
			            
		            }
		            
		            if (agentMode > 2) {
		            	this.sender.sendMessage(routingKey, converted);
		            }
		            
	        	} catch (ZMQException e){
	        		
	        		if(e.getErrorCode() == 156384765){
	        			// Errno 156384765 : Context was terminated
	        			throw e;
	        		}
	        		LOGGER.error(e.toString(), e);
	        		
	        	} catch (Exception e) {
	        		LOGGER.error(e.toString(), e);
	        	} finally {
	        		cnt++;
	        		
	        		if(cnt % 1000 == 0){
	        			long elapsedTime = System.currentTimeMillis() - start;
	        			LOGGER.info("elapsedTime: {}", elapsedTime);
	        			start = System.currentTimeMillis();
	        		}
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
		stoped = true;
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
	
	public static boolean isStoped() {
		return stoped;
	}
	
	public static void stopAgent(){
		stoped = true;
		System.exit(0);
	}

}
//end of NamedPiple.java