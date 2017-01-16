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
 * Bongjin Kwon	2015. 6. 1.		First Draft.
 */
package com.hhi.vaas.platform.vdm.handler.validation;

import com.hhi.vaas.platform.vdm.handler.VDMLoader;
import com.hhi.vaas.platform.vdm.handler.VDMNode;
import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.Assert.*;

public class DataValidatorTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataValidatorTest.class);
	private static final String TEST_VCD_FILE = "/VCD_for_ACONIS_LC_VDR_ver0.6.vcd";

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
	public void testConvertType() {
		
		VesselDataModel vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream(TEST_VCD_FILE));
		DataValidator validator = vdm.getValidator();
		
		
		try {
			/*
			 * Test Valid Case
			 */
			assertEquals(true, validator.convertType("Boolean", "true"));
			assertEquals(true, validator.convertType("Boolean", "TRUE"));
			assertEquals(false, validator.convertType("Boolean", "False"));
			
			assertEquals(11, validator.convertType("INT", "11"));
			assertEquals(0, validator.convertType("INT", "0"));
			
			assertEquals(11L, validator.convertType("LONG", "11"));
			assertEquals(3333333333L, validator.convertType("LONG", "3333333333"));
			
			assertEquals(30.0f, validator.convertType("FLOAT", "30.0"));
			assertEquals(45.234f, validator.convertType("FLOAT", "45.234"));
			assertEquals(11557.00000099f, validator.convertType("FLOAT", "11557.00000099"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
			fail(e.toString());
		}
		
		
		
		/*
		 * Test Invalid Case
		 */
		try {
			validator.convertType("BOOLEAN", "true11");
			fail("Valid BOOLEAN.");
		} catch (Exception e) {
		}
		
		try {
			validator.convertType("INT", "11.0");
			fail("Valid INT.");
		} catch (NumberFormatException e) {
		} catch (Exception e) {
			fail(e.toString());
		}
		
		try {
			validator.convertType("LONG", "11.0");
			fail("Valid LONG.");
		} catch (NumberFormatException e) {
		} catch (Exception e) {
			fail(e.toString());
		}
		
		try {
			validator.convertType("FLOAT", "true");
			
			fail("Valid FLOAT.");
		} catch (NumberFormatException e) {
		} catch (Exception e) {
			fail(e.toString());
		}
		
		try {
			validator.convertType("UNType", "true");
			
			fail("Valid Type.");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail(e.toString());
		}
		
	}
	
	@Test
	public void testIsMinMaxTarget(){
		VesselDataModel vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream(TEST_VCD_FILE));
		DataValidator validator = vdm.getValidator();
		
		try {
			String sysName = "ACONIS";
			//String prePath = "Cylinders/Cylinder1.ExhGasOutTemp";
			String prePath = "MainEngine/CommonSensor.RPM.Response";
			
			VDMNode vdmNode = vdm.getVDMNode(sysName, prePath + ".val");
			LOGGER.debug("vdmNode : {}", vdmNode);
			
			assertTrue(validator.isMinMaxValidationTarget(vdmNode));
			
			/*
			 * check cache.
			 */
			Map<String, Boolean> rangeCfgMap = validator.getRangeCfgMap();
			
			assertNotNull(rangeCfgMap.get(sysName + "_" + prePath + ".val"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
		
	}
	
	@Test
	public void testValidateMinMax(){
		
		VesselDataModel vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream(TEST_VCD_FILE));
		DataValidator validator = vdm.getValidator();
		
		String vdmpath = "MainEngine/CommonSensor.RPM.Response.val";
		String value = "11.57";
		String bType = "FLOAT";
		String result = null;
		try {
			result = validator.validateMinMax(vdmpath, value, bType);
			
			assertEquals(DataValidator.VALID_RESULT_DOUBT, result);
			
			
			validator.saveMinMax("MainEngine/CommonSensor.RPM.Response.rangeCfg.min", "10.0");
			validator.saveMinMax("MainEngine/CommonSensor.RPM.Response.rangeCfg.max", "12.0");
			
			result = validator.validateMinMax(vdmpath, value, bType);
			
			assertEquals(DataValidator.VALID_RESULT_TRUE, result);
			
			
			value = "9.80";
			result = validator.validateMinMax(vdmpath, value, bType);
			
			assertEquals(DataValidator.VALID_RESULT_FALSE, result);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
		
		
		value = "13";
		bType = "INT";
		try {
			validator.saveMinMax("MainEngine/CommonSensor.RPM.Response.rangeCfg.min", "5");
			validator.saveMinMax("MainEngine/CommonSensor.RPM.Response.rangeCfg.max", "15");
			
			result = validator.validateMinMax(vdmpath, value, bType);
			
			assertEquals(DataValidator.VALID_RESULT_TRUE, result);
			
		} catch (Exception e) {
			fail(e.toString());
		}
		
		
		value = "100";
		bType = "LONG";
		try {
			validator.saveMinMax("MainEngine/CommonSensor.RPM.Response.rangeCfg.min", "90");
			validator.saveMinMax("MainEngine/CommonSensor.RPM.Response.rangeCfg.max", "120");
			
			result = validator.validateMinMax(vdmpath, value, bType);
			
			assertEquals(DataValidator.VALID_RESULT_TRUE, result);
			
		} catch (Exception e) {
			fail(e.toString());
		}
		
		value = "100";
		bType = "BOOLEAN";
		try {
			
			result = validator.validateMinMax(vdmpath, value, bType);
			
			assertEquals(DataValidator.VALID_RESULT_DOUBT, result);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}

}
//end of ValidatorTest.java