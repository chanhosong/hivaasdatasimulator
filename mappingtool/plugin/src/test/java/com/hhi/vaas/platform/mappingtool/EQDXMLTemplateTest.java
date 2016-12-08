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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hhi.vaas.platform.mappingtool.eqdmodel.EQDXMLHandler;
import com.hhi.vaas.platform.mappingtool.eqdmodel.EQDXMLModel;
import com.hhi.vaas.platform.mappingtool.eqdmodel.EquipmentDataMappingTemplate;



public final class EQDXMLTemplateTest {
	public static final String EquipmentDataXMLtest = "/Equipment_for_HHI_ACONIS_v0.2_150826.xml";
	public static final String EquipmentDataXMLtest_save = "D:\\Equipment_for_HHI_ACONIS_v0.2_150826.xml";
	

	
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
		
		EquipmentDataMappingTemplate templateInfo = eqdXmlModel.getTemplateInfo();
		
		System.out.println(templateInfo);
	}

	/* temporary remove because of resource access error.
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