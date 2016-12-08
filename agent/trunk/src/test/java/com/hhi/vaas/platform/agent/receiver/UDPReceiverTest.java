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
 * Bongjin Kwon	2015. 6. 18.		First Draft.
 */
package com.hhi.vaas.platform.agent.receiver;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.agent.model.AgentStatus;
import com.hhi.vaas.platform.agent.vcd.VCDHandler;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.parser.DataConverter;
import com.hhi.vaas.platform.vdm.parser.DataConverterFactory;

public class UDPReceiverTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UDPReceiverTest.class);
	
	private PropertyService props;
	private UDPReceiver dataReceiver;
	private DataConverter dataConverter;
	private MockSender sender;
	private AgentStatus status;
	private boolean serverStarted = false;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		props = new PropertyService("");
		
		InputStream mInputStream = props.getResourceStreamFromKey("mapping.xml.path");
		InputStream vInputStream = VCDHandler.getVCDInputStream(props);
		dataConverter = DataConverterFactory.create(mInputStream, vInputStream);
		
		sender = new MockSender(props);
		MockVaasMessageClient mqClient = new MockVaasMessageClient();
		sender.setQueueClient(mqClient);
		status = new AgentStatus(true);
		serverStarted = false;
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUDPUnicast() throws Exception {
		
		
		try {
			/*
			 * start udp receiver 
			 */
			dataReceiver = (UDPReceiver)DataReceiverFactory.create("udp", dataConverter, sender, props, status);
			
			Thread recever = new Thread(){
				@Override
				public void run() {
					
					try {
						dataReceiver.start();
					} catch (Exception e) {
						LOGGER.info("stopped receving message: {}", e.toString());
					} 
				}
			};
			
			recever.start();
			
			Thread.sleep(1000);
			
			/*
			 * send message.
			 */
			int port = props.getProperty("vaas.agent.listen.port", Integer.class);
			String message = "$GPGGA,091646.33,3444.0530,N,12622.6404,E,2,05,02,+0045,M,+019,M,06,0738*4B";
			DatagramSocket socket = new DatagramSocket();
			InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
			DatagramPacket outPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, serverAddress, port);
			socket.send(outPacket);
			
			Thread.sleep(1000);
			
			assertTrue(sender.getSendMsgCount() > 0);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dataReceiver.stopAll();
		}
		
	}
	
	
	@Test
	public void testUDPMulticast() throws Exception {
		
		String interfaceIp = "127.0.0.1";
		String multicastIp = "228.5.6.7";
		props.put("vaas.udp.if.ip", interfaceIp);
		props.put("vaas.udp.mc.ip", multicastIp);
		
		
		try {
			/*
			 * start udp receiver 
			 */
			dataReceiver = (UDPReceiver)DataReceiverFactory.create("udp", dataConverter, sender, props, status);
			
			Thread recever = new Thread(){
				@Override
				public void run() {
					
					try {
						dataReceiver.start();
					} catch (Exception e) {
						LOGGER.info("stopped receving message: {}", e.toString());
					} 
				}
			};
			
			recever.start();
			
			Thread.sleep(1000);
			
			/*
			 * send message.
			 */
			InetAddress local = InetAddress.getByName(interfaceIp);
			int port = props.getProperty("vaas.agent.listen.port", Integer.class);
			String message = "$GPGGA,091646.33,3444.0530,N,12622.6404,E,2,05,02,+0045,M,+019,M,06,0738*4B";
			InetAddress serverAddress = InetAddress.getByName(multicastIp);
			DatagramPacket outPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, serverAddress, port);
			
			MulticastSocket socket = new MulticastSocket();
			socket.setInterface(local);
			socket.send(outPacket);
			
			
			Thread.sleep(1000);
			
			assertTrue(sender.getSendMsgCount() > 0);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			LOGGER.debug("finally stopAll.");
			dataReceiver.stopAll();
		}
		
	}

}
//end of UDPReceiverTest.java