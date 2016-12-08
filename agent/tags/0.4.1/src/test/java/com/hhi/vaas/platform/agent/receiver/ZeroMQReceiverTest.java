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
package com.hhi.vaas.platform.agent.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.agent.test.TestClient;
import com.hhi.vaas.platform.agent.test.TestSender;
import com.hhi.vaas.platform.agent.test.TestZeroMQSender;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.parser.DataConverter;
import com.hhi.vaas.platform.vdm.parser.DataConverterFactory;

/**
 * @author BongJin Kwon
 *
 */
public class ZeroMQReceiverTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZeroMQReceiverTest.class);
			
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.hhi.vaas.platform.agent.receiver.ZeroMQReceiver#preStart()}.
	 */
	@Test
	public void testReceiveData() throws Exception {
		
		final TestClient client = new TestClient();
		
		
		PropertyService props = new PropertyService("");
		DataConverter dataConverter = DataConverterFactory.create(props.getResourceStreamFromKey("mapping.xml.path"));
		
		RabbitMQSender sender = new RabbitMQSender(props);
		final ZeroMQReceiver dataReceiver = new ZeroMQReceiver(dataConverter, sender, props);
		
		try{
			/*
			 * start receiver thread.
			 */
			new Thread(){

				@Override
				public void run() {

					try{
						dataReceiver.preStart();
						
						while(true){
							String recvData = dataReceiver.receiveData();
							
							/*
							 * recvData must be not null.
							 */
							assertNotNull(recvData);
							client.received();
						}
					}catch(Exception e){
						LOGGER.info("stopped receving message: {}", e.toString());
					}
					
				}
				
			}.start();
			
			
			TestSender zmqSender = new TestZeroMQSender(dataReceiver.getBindAddr());
			client.sendFile(ZeroMQReceiverTest.class.getResourceAsStream("/test_data.txt"), zmqSender);
			
			System.out.println("receved count: " + client.getRecvCount());
			
			/*
			 * send msg count must be 100.
			 */
			assertEquals(100, client.getMsgCount());
			
			/*
			 * recv msg count must be > 10.
			 */
			assertTrue( client.getRecvCount() > 10 );
			
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}finally{
			if(dataReceiver != null){
				dataReceiver.stopAll();
			}
		}
		
		
	}

}
//end of JeroMQReceiverTest.java