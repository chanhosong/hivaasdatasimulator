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
 * hsbae			2015. 4. 27.		First Draft.
 */
package com.hhi.vaas.platform.vdm.parser;

import com.hhi.vaas.platform.vdm.handler.VDMLoader;
import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import com.hhi.vaas.platform.vdm.parser.exception.VDMMappingException;
import com.hhi.vaas.platform.vdm.parser.kv.KVParser;
import com.hhi.vaas.platform.vdm.parser.model.DefaultModel;
import org.junit.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.*;

public class KVParserTest {
	
	private static VesselDataModel vdm;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream(TestResources.TEST_VCD_LCS_FILE));
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
	public void testSupports() {
		// 
	}

	public static String getMappingXMLPath() throws URISyntaxException{
		return KVParserTest.class.getResource(TestResources.TEST_MAP_LCS_FILE).getFile();
	}
	
	@Test
	public void testParse() {
		
		List<DefaultModel> model;
		
		String KVData = "{\"STS01.VAL\":\"20151231\", \"STS01.MIN\":\"155011\", \"STS01.MAX\":\"F\"}";
		//String KVData = "{\"STS01.VAL\":\"20151231\", \"STS01.MIN\":\"20000101\",\"STS01.MAX\":\"20191231\"}";
		VDMMapping vdmMapping = new VDMMapping();
		
		try{
			vdmMapping.init( KVParserTest.getMappingXMLPath(), vdm );
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
		
		KVParser parser = new KVParser();
		model = parser.parse(KVData, vdmMapping, vdm);
		System.out.println(KVData + " >> " + model);

		assertNotNull(model);
		//assertEquals(1, model.size());
		
		KVData = "{\"skey1\":\"s1\", \"skey2\":\"s2\",\"skey3\":\"s3\"}";
		
		try{
			model = parser.parse(KVData, vdmMapping, vdm);
			System.out.println(KVData + " >> " + model);
			//fail("must be throwed exception.");
		}catch(Exception e){
			assertTrue(e instanceof VDMMappingException);
		}
		
	}
	
	@Test
	public void testConvertType(){
		//vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream(TestResources.TEST_VCD_LCS_FILE));
		VesselDataModel vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream(TestResources.TEST_VCD_ACONIS_LC_VDR));
		KVParser parser = new KVParser();
		
		Object val = parser.convertType("VDR", "Navigational/Measurement/PositioningDevice/GPSSensor1.COG.val", "207.0", vdm);
		assertTrue(val instanceof Float);
	}

}
//end of KVParserTest.java