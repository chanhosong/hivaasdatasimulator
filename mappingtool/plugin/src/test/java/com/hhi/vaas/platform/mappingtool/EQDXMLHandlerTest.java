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
 * hsbae			2015. 4. 13.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool;



public final class EQDXMLHandlerTest {
	public static final String EquipmentDataXMLtest = "/devicemap_draft_v0.5.xml";
	public static final String EquipmentDataXMLtest_save = "D:\\test.xml";
	
	private EQDXMLHandlerTest() {
		
	}
	
	/* temporary remove because of resource access error.
	
	private EQDXMLModel eqdModel;
	
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
	public void testLoad() {
		
		EQDXMLModel eqdXmlModel = EQDXMLHandler.load(EQDXMLHandlerTest.class.getResourceAsStream(EquipmentDataXMLtest));
		
		EquipmentDataModel root = eqdXmlModel.parse(); 
		
		LOGGER.debug("documentId : " + eqdXmlModel.getDocumentId());
		LOGGER.debug("documentVersion : " + eqdXmlModel.getDocumentVersion());
		LOGGER.debug("deviceName : " + eqdXmlModel.getDeviceName());
		LOGGER.debug("protocolName : " + eqdXmlModel.getProtocolName());
		LOGGER.debug("vendorName : " + eqdXmlModel.getVendorName());
		
		assertEquals("VDR_Device_Map", eqdXmlModel.getDocumentId());
		assertEquals("NMEA", eqdXmlModel.getProtocolName());

	}

	//@Test
	public void testSave() {
		
		boolean result = false;
		
		try {
			EQDXMLModel eqdXmlModel = 
				EQDXMLHandler.load(EQDXMLHandlerTest.class.getResourceAsStream(EquipmentDataXMLtest));
			
			EquipmentDataModel edmRoot = eqdXmlModel.parse();
			
			OutputStream os = new FileOutputStream(EquipmentDataXMLtest_save);
			
			result = EQDXMLHandler.save(os, eqdXmlModel.getEqdHeader(), edmRoot);
			
			assertEquals(true, result);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("error");
			
			
		} catch (Exception e2) {
			e2.printStackTrace();
			fail("error");
		}
	}
	
	*/
}
//end of EQDXMLHandlerTest.java