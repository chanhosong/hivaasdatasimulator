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
 * Bongjin Kwon	2015. 4. 9.		First Draft.
 */

package com.hhi.vaas.platform.mappingtool;

import org.apache.log4j.Logger;


import com.hhi.vaas.platform.vdm.handler.VesselDataModel;

public class VDMNodeTest {
	
	
	private static final Logger LOGGER = Logger.getLogger(VDMNodeTest.class);

	private static VesselDataModel vdm;
	
	/*	Temporary remove because of the resource access error.
	 * 
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream("/VCD_for_VDR_ver0.3.vcd"));
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
	public void testGetVdmpath() {
		
		 * test VDMNode.getVdmpath.
		 
		try{
			VDMNode vdmNode = vdm.getVDMNode("VDR", "GPS/GPS1.Pos.latitude.deg");
			
			assertNotNull(vdmNode);
			
			LOGGER.debug("vdmpath: {}", vdmNode.getVdmpath());
			
			
			 * check vdmpath
			 
			assertEquals("GPS/GPS1.Pos.latitude.deg", vdmNode.getVdmpath());
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testGetAttribute() {
		
		 * test VDMNode.getAttribute.
		 
		try{
			VDMNode vdmNode = vdm.getVDMNode("VDR", "GPS/GPS1.Pos.latitude.deg");
			
			assertNotNull(vdmNode);
			
			LOGGER.debug("vdmpath: {}", vdmNode.getAttribute("bType"));
			
			
			 * check attrbute
			 
			assertEquals("FLOAT32", vdmNode.getAttribute("bType"));
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}


	@Test
	public void testIsLeaf() {
		
		 * test VDMNode.isLeaf.
		 
		try{
			VDMNode vdmNode = vdm.getVDMNode("VDR", "GPS/GPS1.Pos.latitude.deg");
			
			assertNotNull(vdmNode);
			
			LOGGER.debug("isLeaf: {}", vdmNode.isLeaf());
			
			
			 * check isLeaf
			 
			assertTrue(vdmNode.isLeaf());
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testToString() {
		
		 * test VDMNode.toString.
		 
		try{
			VDMNode vdmNode = vdm.getVDMNode("VDR", "GPS/GPS1.Pos.latitude.deg");
			
			assertNotNull(vdmNode);
			
			LOGGER.debug("toString: {}", vdmNode.toString());
			
			
			 * check toString
			 
			assertEquals("<BDA bType=\"FLOAT32\" name=\"deg\"/>", vdmNode.toString());
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}*/
}
//end of VDMNodeTest.java