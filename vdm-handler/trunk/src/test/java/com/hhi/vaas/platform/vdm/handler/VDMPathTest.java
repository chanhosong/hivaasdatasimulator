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
 * Bongjin Kwon	2015. 6. 23.		First Draft.
 */
package com.hhi.vaas.platform.vdm.handler;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VDMPathTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(VDMPathTest.class);
	
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
	public void testDepth_Path() {
		
		VDMPath vdmPath = new VDMPath("ACONIS", "MainEngine/CommonSensor.RPM.Response");
		
		LOGGER.debug("depth: {}", vdmPath.getDepth());
		for (int i = 0; i < vdmPath.getDepth(); i++) {
			LOGGER.debug("depth index: {}, {}", (i+1), vdmPath.getPath(i+1));
		}
		System.out.println(vdmPath.getPath(5));
	}

}
//end of VDMPathTest.java