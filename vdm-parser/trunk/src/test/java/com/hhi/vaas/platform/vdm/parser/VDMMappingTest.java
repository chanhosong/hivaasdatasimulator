package com.hhi.vaas.platform.vdm.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hhi.vaas.platform.vdm.handler.VDMLoader;
import com.hhi.vaas.platform.vdm.handler.VesselDataModel;

public class VDMMappingTest {
	
	VesselDataModel vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream(TestResources.TEST_VCD_VDR_FILE));

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
	
	public static String getMappingXMLPath() throws URISyntaxException{
		return VDMMappingTest.class.getResource(TestResources.TEST_MAP_VDR_FILE).getFile();
	}

	@Test
	public void testInit() throws Exception {
		VDMMapping vdmMapping = new VDMMapping();
		
		try{
			vdmMapping.init( VDMMappingTest.getMappingXMLPath(), vdm );
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
		
		String key1 = "GLL_field:02";
		String key2 = "GLL_field:03";
		
		/*
		 * NMEA KEY를 잘 가져오는지 test
		 */
		assertEquals(key1, vdmMapping.getNMEAKey("GLL", 1));
		assertEquals(key2, vdmMapping.getNMEAKey("GLL", 2));
		
		
		/*  
		 * NMEA VDMPath를 잘 가져오는지 test
		 */
		assertEquals("Navigational/Measurement/PositioningDevice/GPSSensor1.Position.latitude.dir", vdmMapping.getVDMPathList(key1).get(0));
		assertEquals("Navigational/Measurement/PositioningDevice/GPSSensor1.Position.longitude.deg", vdmMapping.getVDMPathList(key2).get(0));

	}
	
	@Test
	public void testInitByStream() throws Exception {
		VDMMapping vdmMapping = new VDMMapping();
		
		try{
			vdmMapping.init( VDMMappingTest.class.getResourceAsStream(TestResources.TEST_MAP_VDR_FILE), vdm);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
		
		String key1 = "DTM_field:01";
		String key2 = "DTM_field:08";
		
		/*
		 * NMEA KEY를 잘 가져오는지 test
		 */
		assertEquals(key1, vdmMapping.getNMEAKey("DTM", 0));
		assertEquals(key2, vdmMapping.getNMEAKey("DTM", 7));
		
		
		/*
		 * NMEA VDMPath를 잘 가져오는지 test
		 */
		assertEquals("Navigational/Measurement/PositioningDevice/GPSSensor1.Datum.localdatum", vdmMapping.getVDMPathList(key1).get(0));
		assertEquals("Navigational/Measurement/PositioningDevice/GPSSensor1.Datum.refdatum", vdmMapping.getVDMPathList(key2).get(0));
	}

}
