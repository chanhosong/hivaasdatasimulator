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
 * hsbae			2015. 4. 29.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hhi.vaas.platform.mappingtool.mapdatamodel.MapDataHandler;
import com.hhi.vaas.platform.mappingtool.mapdatamodel.MapDataItem;
import com.hhi.vaas.platform.mappingtool.mapdatamodel.MapDataXMLModel;

public class MapDataHandlerTest {
	
	private static final Logger LOGGER = Logger.getLogger(MapDataHandlerTest.class);
	
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
		
		InputStream is = MapDataHandlerTest.class.getResourceAsStream(TestResources.TEST_MAPPING_VDR);
		
		MapDataXMLModel mapXml = null;
		
		try {
			mapXml = MapDataHandler.load(is);
		} catch (Exception e) {
			fail("mapping.xml load fail");
		}
		
		List<MapDataItem> mapList = mapXml.getMapDataList();
		int itemCount = mapList.size();
		
		assertNotEquals(0, itemCount);
		
		for (int i = 0; i < itemCount; i++) {
			LOGGER.debug(mapList.get(i));
		}
		
		
		
	}

	@Test
	public void testSave() {
		// load
		InputStream is = MapDataHandlerTest.class.getResourceAsStream(TestResources.TEST_MAPPING_VDR);
		
		MapDataXMLModel mapXml = null;
		
		try {
			mapXml = MapDataHandler.load(is);
		} catch (Exception e) {
			fail("mapping.xml load fail");
		}
		
		// save
		
		try {
			MapDataHandler.save(mapXml, "d:\\test.xml", "VDR");
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
//end of MapDataHandlerTest.java