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
 * Hyo-jeong Lee	2015. 3. 16.		First Draft.
 */
package com.hhi.vaas.platform.agent.test;

import java.util.Map;

import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasExchangeMessageClient;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

/**
 * <pre>
 * 
 * </pre>
 * @author Hyo-jeong Lee
 */
public class TopicConsumer {

	private static PropertyService propertyService = new PropertyService("");
	
	public static final String EXCHANGE_NAME = "exchange_vdip";
	public static final String ROUTING_KEY1 = "testagent";
	public static final String ROUTING_KEY2 = "direct2";
	public static final String QUEUE_NAME = "test_queue";

	public static void main(String[] argv) throws Exception {

		/** Rabbitmq connect(host, port, user name, password, exchange, exchange type)
		    exchange type -
				fanout : All, 
				direct : rounting-key matching
				topic : rounting-key pattern matching
		 */
		VaasExchangeMessageClient client = new VaasExchangeMessageClient(propertyService);
		Channel channel = client.getChannel();
			
		//bind exchange to queue
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY1);
		
		QueueingConsumer consumer = new QueueingConsumer(channel);
		
		boolean autoAck = false;// for redelivering when consuming failed.
		channel.basicConsume(QUEUE_NAME, autoAck, consumer);

		while (true) {
			Delivery delivery = consumer.nextDelivery();

			Map<String, Object> header = delivery.getProperties().getHeaders();
			//System.out.println("[header] equipmentKey : " + header.get("equipmentKey") + " / equipmentName : " + header.get("equipmentName"));

			System.out.println(" [receive] message : '" + new String(delivery.getBody()) + "'");

			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false); //acknowledge received messages

			//System.out.println(" [receive] End");

		}
	}
	
}
