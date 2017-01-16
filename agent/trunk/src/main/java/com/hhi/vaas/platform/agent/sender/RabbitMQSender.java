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
package com.hhi.vaas.platform.agent.sender;

import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasExchangeMessageClient;
import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMQException;
import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMQMessage;
import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMessageClient;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * <pre>
 * VaasMessageClient Proxy for transmission processing to the middleware
 * </pre>
 * @author BongJin Kwon
 *
 */
public class RabbitMQSender {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQSender.class);
	
	private PropertyService props;
	private VaasMessageClient queueClient;
	private BigQueueHandler backupHandler;
	private RabbitMQReSender reSender;
	
	private String exchange;
	private String routingKey;
	private String errRoutingKey;

	public RabbitMQSender(PropertyService props) {
		this.props = props;
		
		this.queueClient = createVaasMessageClient(props);
		this.backupHandler = new BigQueueHandler(props);
		this.reSender = new RabbitMQReSender(this, backupHandler);
		
		// excxhange
		exchange = props.getProperty("vaas.mq.topic.exchange.name");
		
		// normal message routingKey
        boolean enlisting = props.getProperty("vaas.agent.enlist.enable", Boolean.class, false);
        if(enlisting) {
        	routingKey = props.getProperty("vaas.agent.id");
        } else {
        	routingKey = props.getProperty("vaas.test.agent.id", String.class, "testagent2");
        }
		
        LOGGER.debug("routingKey is {}", routingKey);
        
        // error message routingKey
		errRoutingKey = props.getProperty("vaas.mq.topic.name.exception");
	}
	
	public void setQueueClient(VaasMessageClient queueClient) {
		this.queueClient = queueClient;
	}
	
	public VaasMessageClient createVaasMessageClient(PropertyService props){
		VaasMessageClient client = new VaasExchangeMessageClient(props);
		client.setAutomaticRecoveryEnabled(false);
		
		return client;
	}


	/**
	 * prepare sender
	 */
	public void prepare(){
		
		if(this.queueClient.isOpen()){
			LOGGER.info("aleady prepared.");
			return;
		}
		
		try {
			
			this.queueClient.declare(exchange);
		} catch (IOException e) {
			throw new VaasMQException(e);
		}
		LOGGER.info("prepared.");
		
		if (backupHandler.queueSize() > 0) {
			runReSender();
		}
	}
	
	/**
	 * send message(body) to middleware mq
	 * 
	 * @param exchange
	 * @param routingKey
	 * @param body
	 */
	public void sendMessage(String routingKey, String body){

		
		if(this.queueClient.isOpen() && reSender.isRun() == false){
			
			try{
				doSendMessage(routingKey, body);
				LOGGER.debug("send : {}", body);
	    	}catch(Exception e){
	    		LOGGER.error(e.toString(), e);

	    		backupMessage(routingKey, body);
	    		runReSender();
	    	}
			
		}else{
			backupMessage(routingKey, body);
			
			if(reSender.isRun() == false){
				
	    		runReSender();
	    	}
		}
		
	}
	
	protected synchronized void doSendMessage(String routingKey, String body){
		//create queue message instance
        VaasMQMessage message = new VaasMQMessage(exchange, routingKey, body);

        //send message to queue
        this.queueClient.basicPublish(message);
	}
	
	public boolean isOpen(){
		return this.queueClient.isOpen();
	}
	
	public void reConnect()throws IOException{

		if(this.queueClient.isOpen()){
			close();
		}
		
		this.queueClient = createVaasMessageClient(props);
		
		prepare();
	}
	

	/**
	 * get routingKey
	 * @return
	 */
	public String getRoutingKey() {
		return routingKey;
	}

	/**
	 * get errRoutingKey
	 * @return
	 */
	public String getErrRoutingKey() {
		return errRoutingKey;
	}
	
	/**
	 * close VaasMessageClient
	 */
	public void close(){
		
		if (queueClient != null) {
			queueClient.close();
		}
	}
	
	protected void backupMessage(String routingKey, String body){
		this.backupHandler.backupMessage(routingKey, body);
	}
	
	private void runReSender(){
		
		if(reSender.isRun() == false){
		
			new Thread(reSender).start();
			LOGGER.debug("ReSender start.");
		}
	}
	

}
//end of RabbitMQSender.java