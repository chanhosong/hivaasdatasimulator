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

import com.hhi.vaas.platform.middleware.common.mongodb.VaasMongoManager;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.middleware.metadata.KeyValueDomain;
import com.hhi.vaas.platform.middleware.metadata.MetadataQueryDomain;
import com.hhi.vaas.platform.middleware.metadata.MetadataService;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;

import static org.junit.Assert.fail;

/**
 * <pre>
 * JUnit test class for @AdapterManager
 * </pre>
 * @author Sang-cheon Park
 * @version 1.0
 */
public class AdapterManagerTest {

	private static DB db;
	private static DBCollection collection;
	private static MetadataService metadataService;

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
		metadataService = new MetadataService(propertyService);
		
		db = VaasMongoManager.getDB(propertyService);
		collection = db.getCollection(propertyService.getProperty("vaas.mongo.metadata.collection.name"));

		InputStream is = AdapterManagerTest.class.getResourceAsStream("/metadata.json");
		collection.insert((List<DBObject>)JSON.parse(convertStreamToString(is)));
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
	 * <pre>
	 * get json string from file
	 * </pre>
	 * @param is
	 * @return
	 */
	@SuppressWarnings("resource")
	public static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

	/**
	 * JUnit test case for {@link com.hhi.vaas.platform.middleware.adapter.AdapterManager#init()} and
	 * {@link com.hhi.vaas.platform.middleware.adapter.AdapterManager#stopAll()} methods.
	 */
	@Test
	public void unicastAdapterTest() {
		/**
		 * 1. initialize
		 */
    	PropertyService propertyService = new PropertyService();
		AdapterManager manager = AdapterManager.getInstance(propertyService);
		String agentId = "123456789";
		
		/**
		 * 2. Test(UDP Listener start)
		 */
		try {
			manager.createAdapter(agentId);
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener start failed.");
		}
		
		/**
		 * 3. Verify
		 */
		try {
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

		/**
		 * 4. Test(UDP Listener pause/resume)
		 */
		try {
			manager.pause(AdapterThread.DEFAULT_PORT);
			manager.resume(AdapterThread.DEFAULT_PORT);
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener pause/resume failed.");
		}

		/**
		 * 5. Test(UDP Listener update)
		 */
		try {
			manager.updateAdapter(AdapterThread.DEFAULT_PORT, agentId);
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener update failed.");
		}
		
		/**
		 * 6. Test(UDP Listener stop)
		 */
		try {
			// stop a udp listener thread
			manager.stopAll();
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener stop failed.");
		}
	}

	/**
	 * JUnit test case for {@link com.hhi.vaas.platform.middleware.adapter.AdapterManager#init()} and
	 * {@link com.hhi.vaas.platform.middleware.adapter.AdapterManager#stopAll()} methods.
	 */
	@Test
	public void multicastAdapterTest() {
		/**
		 * 1. initialize
		 */
    	PropertyService propertyService = new PropertyService();
		AdapterManager manager = AdapterManager.getInstance(propertyService);
		InetAddress local = null;
		
		try {
			local = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		Map<String, Object> agentInfo = null;
		String agentId = "987654321";
		
		/**
		 * 2. Test(UDP Listener start)
		 */
		try {
			manager.createAdapter(agentId);
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener start failed.");
		}
		
		/**
		 * 3. Verify
		 */
		try {
			String message = "$GPGGA,091646.33,3444.0530,N,12622.6404,E,2,05,02,+0045,M,+019,M,06,0738*4B";
			MulticastSocket socket = new MulticastSocket();
			InetAddress serverAddress = InetAddress.getByName("228.5.6.7");
			DatagramPacket outPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, serverAddress, AdapterThread.DEFAULT_PORT);
			socket.setInterface(local);
			socket.send(outPacket);
			
			Thread.sleep(1000);
			
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP datagram send failed.");
		}

		/**
		 * 4. Test(UDP Listener pause/resume)
		 */
		try {
			manager.pause(AdapterThread.DEFAULT_PORT);
			manager.resume(AdapterThread.DEFAULT_PORT);
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener pause/resume failed.");
		}

		/**
		 * 5. Test(UDP Listener update)
		 */
		try {
			MetadataQueryDomain query = new MetadataQueryDomain();
			query.addQuery(new KeyValueDomain("value.agentType", "adapter"));
			query.addQuery(new KeyValueDomain("value.agentId", agentId));
			
			DBObject agent = null;
			
			agent = (DBObject) metadataService.selectOne("agent_list", query);
			
			if (agent == null) {
	    		throw new Exception("Could not find the document.");
			}

			agentInfo = new HashMap<String, Object>();
			agentInfo.put("agentType", (String) agent.get("agentType"));
			agentInfo.put("agentId", agentId);
			agentInfo.put("equipmentId", (String) agent.get("equipmentId"));
			agentInfo.put("processType", (String) agent.get("processType"));
			agentInfo.put("interfaceIp", (String) agent.get("interfaceIp"));
			agentInfo.put("multicastIp", (String) agent.get("multicastIp"));
			agentInfo.put("port", (Integer) agent.get("port"));
			agentInfo.put("mappingXml", (String) agent.get("mappingXml"));
			agentInfo.put("regDt", Calendar.getInstance().getTime());
						
			agentInfo.put("oldPort", (Integer) agent.get("port"));
			agentInfo.put("port", ((Integer) agent.get("port")) + 1);
			agentInfo.put("status", AdapterStatusType.PAUSE.toString());
			manager.updateAdapter(agentInfo);
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener update failed.");
		}
		
		/**
		 * 6. Test(UDP Listener updateAll)
		 */
		try {
			// update adapters
			manager.updateAll();
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener stop failed.");
		}
		
		/**
		 * 7. Test(UDP Listener stop)
		 */
		try {
			// stop a udp listener thread
			manager.stopAll();
		} catch (Exception e) {
			e.printStackTrace();
			fail("UDP Listener stop failed.");
		}
	}

	/**
	 * <pre>
	 * get string from file
	 * </pre>
	 * @param is
	 * @return
	 */
	@SuppressWarnings("resource")
	public static String convertStreamToString(String path) {
		InputStream is = null;
		try {
			is = AdapterManager.class.getResourceAsStream(path);
		    Scanner s = new Scanner(is).useDelimiter("\\A");
		    return s.hasNext() ? s.next() : "";
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			}
		}
	}
	//end of convertStreamToString()
}
//end of AdapterManagerTest.java