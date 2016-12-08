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
 * Bongjin Kwon	2015. 6. 9.		First Draft.
 */
package com.hhi.vaas.platform.agent;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hhi.vaas.platform.middleware.common.util.PropertyService;

public class AgentMainTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateAgentId() {
		PropertyService props = new PropertyService("");
		
		try {
			String agentId = AgentMain.createAgentId("testId", props);
			
			
			assertEquals(agentId, AgentMain.createAgentId("testId", props));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
	}

}
//end of AgentMainTest.java