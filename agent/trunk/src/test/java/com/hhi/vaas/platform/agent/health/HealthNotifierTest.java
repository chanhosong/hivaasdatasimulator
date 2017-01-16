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
 * Bongjin Kwon	2015. 5. 11.		First Draft.
 */
package com.hhi.vaas.platform.agent.health;

import com.hhi.vaas.platform.agent.enlist.MockMQSender;
import com.hhi.vaas.platform.agent.model.AgentStatus;
import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.agent.update.UpdateHandler;
import com.hhi.vaas.platform.agent.vcd.VCDHandler;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.parser.DataConverter;
import com.hhi.vaas.platform.vdm.parser.DataConverterFactory;
import org.apache.http.localserver.LocalServerTestBase;
import org.apache.http.localserver.LocalTestServer;
import org.junit.*;

import java.io.InputStream;
import java.net.InetSocketAddress;

import static org.junit.Assert.*;

public class HealthNotifierTest extends LocalServerTestBase{
	
	private PropertyService props = new PropertyService("");
	private RabbitMQSender sender = new MockMQSender(props);
	
	private HealthNotiRequestHandler requestHandler = new HealthNotiRequestHandler();
	private HealthNotifier hn;
	private String agentId = "junitTestAgent";
	private String managerHealthUri;

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
		String healthUri = props.getProperty("vaas.manager.heath.uri");
		this.localServer = new LocalTestServer(null, null);
        localServer.register(healthUri, requestHandler);
          
        startServer();
          
        
        /*
         * set health uri.
         */
        InetSocketAddress serviceAddress = this.localServer.getServiceAddress();
          
        managerHealthUri = "http://localhost:" + serviceAddress.getPort() + props.getProperty("vaas.manager.heath.uri");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNotifyHealth() {
		
		AgentStatus status = new AgentStatus(true);
		Activator activator = new Activator(sender, status);
		
		try{
			InputStream mInputStream = props.getResourceStreamFromKey("mapping.xml.path");
			InputStream vInputStream = VCDHandler.getVCDInputStream(props);
			DataConverter dataConverter = DataConverterFactory.create(mInputStream, vInputStream);
			
			hn = new HealthNotifier(activator, props, new UpdateHandler(dataConverter));
			
			/*
			 * test notifyHealth
			 */
			hn.notifyHealth(managerHealthUri, agentId);
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}

}
//end of HealthNotifierTest.java