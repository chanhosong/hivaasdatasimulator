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
 * Bongjin Kwon	2015. 5. 8.		First Draft.
 */
package com.hhi.vaas.platform.agent.enlist;

import com.hhi.vaas.platform.agent.health.Activator;
import com.hhi.vaas.platform.agent.model.AgentStatus;
import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import org.apache.http.localserver.LocalServerTestBase;
import org.apache.http.localserver.LocalTestServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.*;

public class EnlistHandlerTest extends LocalServerTestBase{
	
	private PropertyService props = new PropertyService("");
	private RabbitMQSender sender = new MockMQSender(props);
	
	private EnlistRequestHandler requestHandler = new EnlistRequestHandler();
	private EnlistHandler eh;
	private String agentId = "junitTestAgent";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		/*
		 * start http test server (on http://localhost & random port)
		 */
		String enlistUri = props.getProperty("vaas.manager.enlist.uri");
		this.localServer = new LocalTestServer(null, null);
        localServer.register(enlistUri, requestHandler);
          
        startServer();
          
        
        /*
         * set test properties
         */
        InetSocketAddress serviceAddress = this.localServer.getServiceAddress();
          
        props.put("vaas.manager.host", "http://localhost:" + serviceAddress.getPort());
        props.put("vaas.agent.id", agentId);
          
          
	}

	/*
	 * will be execute in LocalServerTestBase
	@After
	public void tearDown() throws Exception {
	}
	*/

	@Test
	public void testProcess() {
		
		/*
         * create EnlistHandler instance
         */
        AgentStatus status = new AgentStatus(false);
  		Activator activator = new Activator(sender, status);
  		
  		eh = new EnlistHandler(activator, props);
		
  		/*
  		 * test process().
  		 */
		try {
			/*
			 * test simple enlist
			 */
			requestHandler.setResponseJson("{\"result\":\"ok\"}");
			eh.process();
			
			assertTrue(status.isActivated() == false);
			assertEquals(agentId, requestHandler.getAgentId());// validate parameter.
			assertNotNull(requestHandler.getMappingXML());
			
			/*
			 * test activation response
			 */
			requestHandler.setResponseJson("{\"result\":\"ok\", \"activation\":{\"username\":\"user1\",\"passwd\":\"pass1\"}}");
			eh.process();
			
			assertTrue(status.isActivated() == true);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
	}

	@Test
	public void testLoadMappingXml() {
		
		/*
         * create EnlistHandler instance
         */
        AgentStatus status = new AgentStatus(false);
  		Activator activator = new Activator(sender, status);
  		
  		eh = new EnlistHandler(activator, props);
		

		/*
		 * test for EnlistHandler.loadMappingXml()
		 */
		try {
			String mappingXML = eh.loadMappingXml();
			
			/*
			 * check xml.
			 */
			assertTrue(mappingXML.startsWith("<?xml"));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
	}

}
//end of EnlistHandlerTest.java