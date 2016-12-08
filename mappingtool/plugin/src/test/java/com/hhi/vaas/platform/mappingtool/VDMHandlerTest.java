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
 * hsbae			2015. 5. 4.		First Draft.
 */

package com.hhi.vaas.platform.mappingtool;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hhi.vaas.platform.vdm.handler.VesselDataModel;

/**
 * 
 * @author hsbae
 *
 */
public class VDMHandlerTest {

	private static VesselDataModel vdm;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//vdm = VDMLoader.load(VDMHandlerTest.class.getResourceAsStream("/VCD_for_VDR_ver0.3.vcd"));
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
	public void printVDMPathTest() {
		// print vdmpaht as tree 
		//VDMNode vdmNode = vdm.getVDMNode("VDR", "");
		//vdmNode.getChildNodes();
		
	}

}
//end of VDMHandlerTest.java