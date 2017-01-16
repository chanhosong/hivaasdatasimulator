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

import com.hhi.vaas.platform.agent.model.AgentStatus;
import com.hhi.vaas.platform.agent.test.TestClient;
import com.hhi.vaas.platform.agent.test.TestSender;
import com.hhi.vaas.platform.agent.test.TestZeroMQSender;
import com.hhi.vaas.platform.agent.vcd.VCDHandler;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.parser.DataConverter;
import com.hhi.vaas.platform.vdm.parser.DataConverterFactory;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
		
		int msgCount = 30;
		final TestClient client = new TestClient(msgCount, 200, false);
		
		
		PropertyService props = new PropertyService("");
		
		InputStream mInputStream = props.getResourceStreamFromKey("mapping.xml.path");
		InputStream vInputStream = VCDHandler.getVCDInputStream(props);
		DataConverter dataConverter = DataConverterFactory.create(mInputStream, vInputStream);
		
		MockSender sender = new MockSender(props);
		MockVaasMessageClient mqClient = new MockVaasMessageClient();
		sender.setQueueClient(mqClient);
		AgentStatus status = new AgentStatus(true);
		
		final ZeroMQReceiver dataReceiver = (ZeroMQReceiver)DataReceiverFactory.create("zmq", dataConverter, sender, props, status);
		
		Thread recever = null;
		try{
			/*
			 * start receiver thread.
			 */
			recever = new Thread(){

				@Override
				public void run() {

					try {
						dataReceiver.start();
					} catch(Exception e) {
						LOGGER.info("stopped receving message: {}", e.toString());
					}
					
				}
				
			};
			recever.start();
			
			
			TestSender zmqSender = new TestZeroMQSender(dataReceiver.getBindAddr());
			client.sendFile(ZeroMQReceiverTest.class.getResourceAsStream("/test_data.txt"), zmqSender);
			
			System.out.println("receved count: " + sender.getSendMsgCount());
			
			/*
			 * backup message count must be greater than 0
			 */
			assertTrue(sender.getBackupMsgCount() > 0);
			
			/*
			 * equals sender msg and mqClient msg.
			 */
			//assertEquals( sender.getSendMsgCount(), mqClient.getSendMsgCount() );
			
			
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