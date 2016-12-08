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
 * hsbae			2015. 4. 15.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool;

import static org.junit.Assert.fail;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.hhi.vaas.platform.mappingtool.eqdmodel.EQDExcelException;
import com.hhi.vaas.platform.mappingtool.eqdmodel.EQDExcelLoader;
import com.hhi.vaas.platform.mappingtool.eqdmodel.EquipmentDataModel;

public class EQDExcelLoaderTest {
	public static final String EquipmentDataExcelTest = "/VDR_JRC.xlsx";
	
	private static final Logger LOGGER = Logger.getLogger(EQDExcelLoaderTest.class);
	
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

	//@Test
	public void testLoad() {
		EQDExcelLoader excelLoader = new EQDExcelLoader();
		
		InputStream is = EQDExcelLoaderTest.class.getResourceAsStream(EquipmentDataExcelTest);
		
		try {
			EquipmentDataModel eqdModel = excelLoader.load(is); //, "JRC_VDR", "NMEA");
			is.close();
			
			//assertEquals("NMEA", eqdModel.child.get(0).getName());
			
			
		} catch (EQDExcelException ex) {
			LOGGER.debug(ex.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Err : Excel load error");
		}
	}

}
//end of EQDExcelLoaderTest.java