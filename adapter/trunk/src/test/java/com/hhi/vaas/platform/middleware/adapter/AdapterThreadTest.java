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
 * Sang-cheon Park	2015. 4. 7.		First Draft.
 */
package com.hhi.vaas.platform.middleware.adapter;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hhi.vaas.platform.middleware.common.mongodb.VaasMongoManager;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.middleware.metadata.MetadataService;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * <pre>
 * JUnit test class for @AdapterThread
 * </pre>
 * @author Sang-cheon Park
 * @version 1.0
 */
public class AdapterThreadTest {

	private static DB db;
	private static DBCollection collection;

	/**
	 * <pre>
	 * Execute once before test
	 * </pre>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyService propertyService = new PropertyService();
		
		db = VaasMongoManager.getDB(propertyService);
		collection = db.getCollection(propertyService.getProperty("vaas.mongo.metadata.collection.name"));

		InputStream is = AdapterManagerTest.class.getResourceAsStream("/metadata.json");
		collection.insert((List<DBObject>)JSON.parse(IOUtils.toString(is)));
	}

	/**
	 * <pre>
	 * Execute once after test
	 * </pre>
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		/**
		 * drop collection in used
		 */
		collection.drop();
	}

	/**
	 * JUnit test case for {@link com.hhi.vaas.platform.middleware.adapter.AdapterThread#initialize()} and
	 * {@link com.hhi.vaas.platform.middleware.adapter.AdapterThread#run()} methods.
	 */
	@Test
	public void unicastAdapterTest() {
		/**
		 * 1. initialize
		 */
    	MetadataService metadataService = new MetadataService(new PropertyService());
		AdapterThread adapter = null;
		
		/**
		 * 2. Test(UDP Listener start)
		 */
		try {
			adapter = new AdapterThread(UUID.randomUUID().toString(), AdapterProcsssType.NMEA.toString(), AdapterThread.DEFAULT_PORT, AdapterManagerTest.convertStreamToString("/Mapping.xml"), metadataService);
			
			// start a udp listener thread
			adapter.start();
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener start failed.");
		}
		
		/**
		 * 3. Verify
		 */
		try {
			assertTrue("Running port is 5001.", AdapterThread.DEFAULT_PORT == adapter.getPort());
			
			String message = "$GPGGA,091646.33,3444.0530,N,12622.6404,E,2,05,02,+0045,M,+019,M,06,0738*4B";
			DatagramSocket socket = new DatagramSocket();
			InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
			DatagramPacket outPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, serverAddress, AdapterThread.DEFAULT_PORT);
			socket.send(outPacket);
			
			Thread.sleep(1000);
			
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP datagram send failed.");
		}
		
		try {
			AdapterEventMessage message = new AdapterEventMessage(AdapterThread.DEFAULT_PORT, AdapterStatusType.STOP);
			adapter.handleEvent(message);

			Thread.sleep(1000);
			
			assertTrue("message.isHandled() is true.", message.isHandled());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("UDP Listener stop failed.");
		}
	}

	/**
	 * JUnit test case for {@link com.hhi.vaas.platform.middleware.adapter.AdapterThread#initialize()} and
	 * {@link com.hhi.vaas.platform.middleware.adapter.AdapterThread#run()} methods.
	 */
	@Test
	public void multicastAdapterTest() {
		/**
		 * 1. initialize
		 */
    	MetadataService metadataService = new MetadataService(new PropertyService());
		AdapterThread adapter = null;
		InetAddress local = null;
		
		try {
			local = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		/**
		 * 2. Test(UDP Listener start)
		 */
		try {
			adapter = new AdapterThread(UUID.randomUUID().toString(), AdapterProcsssType.NMEA.toString(), local.getHostAddress(), "228.5.6.7", AdapterThread.DEFAULT_PORT, AdapterManagerTest.convertStreamToString("/Mapping.xml"), metadataService);
			
			// start a udp listener thread
			adapter.start();
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener start failed.");
		}
		
		/**
		 * 3. Verify
		 */
		try {
			assertTrue("Running port is 5001.", AdapterThread.DEFAULT_PORT == adapter.getPort());
			
			String message = "$GPGGA,091646.33,3444.0530,N,12622.6404,E,2,05,02,+0045,M,+019,M,06,0738*4B";
			InetAddress serverAddress = InetAddress.getByName("228.5.6.7");
			DatagramPacket outPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, serverAddress, AdapterThread.DEFAULT_PORT);
			
			MulticastSocket socket = new MulticastSocket();
			socket.setInterface(local);
			socket.send(outPacket);
			
			Thread.sleep(1000);
			
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP datagram send failed.");
		}
		
		try {
			AdapterEventMessage message = new AdapterEventMessage(AdapterThread.DEFAULT_PORT, AdapterStatusType.STOP);
			adapter.handleEvent(message);

			Thread.sleep(1000);
			
			assertTrue("message.isHandled() is true.", message.isHandled());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("UDP Listener stop failed.");
		}
	}

	/**
	 * JUnit test case for {@link com.hhi.vaas.platform.middleware.adapter.AdapterThread#initialize()} and
	 * {@link com.hhi.vaas.platform.middleware.adapter.AdapterThread#run()} methods.
	 */
	@Test
	public void broadcastAdapterTest() {
		/**
		 * 1. initialize
		 */
    	MetadataService metadataService = new MetadataService(new PropertyService());
		AdapterThread adapter = null;
		
		/**
		 * 2. Test(UDP Listener start)
		 */
		try {
			adapter = new AdapterThread(UUID.randomUUID().toString(), AdapterProcsssType.NMEA.toString(), AdapterThread.DEFAULT_PORT, AdapterManagerTest.convertStreamToString("/Mapping.xml"), metadataService);
			
			// start a udp listener thread
			adapter.start();
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener start failed.");
		}
		
		/**
		 * 3. Verify
		 */
		try {
			assertTrue("Running port is 5001.", AdapterThread.DEFAULT_PORT == adapter.getPort());
			
			String message = "$GPGGA,091646.33,3444.0530,N,12622.6404,E,2,05,02,+0045,M,+019,M,06,0738*4B";
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			InetAddress broadcastAddr = InetAddress.getByName("255.255.255.255");
			DatagramPacket outPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, broadcastAddr, AdapterThread.DEFAULT_PORT);
			socket.send(outPacket);
			
			Thread.sleep(1000);
			
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP datagram send failed.");
		}
		
		try {
			AdapterEventMessage message = new AdapterEventMessage(AdapterThread.DEFAULT_PORT, AdapterStatusType.STOP);
			adapter.handleEvent(message);

			Thread.sleep(1000);
			
			assertTrue("message.isHandled() is true.", message.isHandled());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("UDP Listener stop failed.");
		}
	}

	/**
	 * JUnit test case for {@link com.hhi.vaas.platform.middleware.adapter.AdapterThread#initialize()} and
	 * {@link com.hhi.vaas.platform.middleware.adapter.AdapterThread#run()} methods.
	 */
	@Test
	public void binDataTest() {
		/**
		 * 1. initialize
		 */
    	MetadataService metadataService = new MetadataService(new PropertyService());
		AdapterThread adapter = null;
		
		/**
		 * 2. Test(UDP Listener start)
		 */
		try {
			adapter = new AdapterThread(UUID.randomUUID().toString(), AdapterProcsssType.RADAR_IMAGE.toString(), AdapterThread.DEFAULT_PORT, null, metadataService);
			
			// start a udp listener thread
			adapter.start();
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener start failed.");
		}
		
		/**
		 * 3. Verify
		 */
		try {
			assertTrue("Running port is 5001.", AdapterThread.DEFAULT_PORT == adapter.getPort());
			
			byte[] message = IOUtils.toByteArray(AdapterManagerTest.class.getResourceAsStream("/logo.png"));
			DatagramSocket socket = new DatagramSocket();
			InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
			DatagramPacket outPacket = new DatagramPacket(message, message.length, serverAddress, AdapterThread.DEFAULT_PORT);
			socket.send(outPacket);
			
			Thread.sleep(1000);
			
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP datagram send failed.");
		}
		
		try {
			AdapterEventMessage message = new AdapterEventMessage(AdapterThread.DEFAULT_PORT, AdapterStatusType.STOP);
			adapter.handleEvent(message);

			Thread.sleep(1000);
			
			DBCollection collection = metadataService.getDb().getCollection((String) metadataService.selectOne("vaas_mongo_collection_binary_data"));
			DBCursor cursor = collection.find(new BasicDBObject("type", AdapterProcsssType.RADAR_IMAGE.toString()));
			
			assertEquals("cursor.count() is 1.", 1, cursor.count());
			assertTrue("message.isHandled() is true.", message.isHandled());
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener stop failed.");
		}
	}
}
//end of AdapterThreadTest.java