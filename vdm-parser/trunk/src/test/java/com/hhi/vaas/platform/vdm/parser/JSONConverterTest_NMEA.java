package com.hhi.vaas.platform.vdm.parser;


import com.hhi.vaas.platform.vdm.parser.filetest.DataParserFactoryFileTest;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.junit.*;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class JSONConverterTest_NMEA {
	
	
	private static DataConverter converter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		InputStream vdmStream = DataParserFactoryFileTest.class.getResourceAsStream(TestResources.TEST_VCD_ACONIS_LC_VDR);
		converter = DataConverterFactory.create(TestResources.TEST_MAP_VDR_FILE, vdmStream);
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
	public void testNMEAConvert() throws IOException {
		
		String orginStr = "$GPZDA,091648.00,05,12,2014,00,00*65";
		
		String json = null;
		
		long preTime = System.currentTimeMillis();
		try{
			json = converter.convert(orginStr);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("testNMEAConvert $GPZDA result: " + json);
		
		assertNotNull("result must not be null.", json);

		Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);

		/*
		 * 6번째 json format (vdmpath, value, etc) 확인.
		 */
		assertTrue( (long)JsonPath.read(document, "$.[5].receivedTime") >= preTime );
		assertEquals("Navigational/Measurement/PositioningDevice/GPSSensor1.Time.Difference.minute", JsonPath.read(document, "$.[5].vdmpath"));
		assertEquals("ZDA_field:06", JsonPath.read(document, "$.[5].key"));
		assertEquals(0, JsonPath.read(document, "$.[5].value"));
		assertNotNull(JsonPath.read(document, "$.[5].valid"));
		
		
	}
	
	
	
	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testAIS_NMEAConvert() throws IOException {
		
		
		//String orginStr = "!AIVDM,1,1,,B,13P;>lh02@aCWopDUGL`?VU20`E:,0*05";
		String orginStr = "!AIVDM,1,1,,A,1815@5@01j08?a0E6POb:H8R8L0>,0*7F";
		String json = null;
		
		try{
			json = converter.convert(orginStr);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("testNMEAConvert !AIVDM result: " + json);
		
		assertNotNull("result must not be null.", json);

		Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);

		//assertEquals("VDMPath:GPS/GPS1.Time.localzoneMin", JsonPath.read(document, "$.[5].vdmpath"));
		//assertEquals("00", JsonPath.read(document, "$.[5].GPZDA_06"));
	}
	
	
	
	@Test
	public void testNMEABypass() throws IOException {
		
		String orginStr = "$VRDOR,E,A,OT,,000,000,C,,005_Pilot_Door_Status(P)*5A";
		
		System.out.println("nmea data : " + orginStr);
		String json = null;
		
		try{
			json = converter.convert(orginStr);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Bypass Test Result result: " + json);
		
		assertNotNull("result must not be null.", json);
	}
	
	@Test
	public void testNMEAUndefined() throws IOException {
		
		String orginStr = "$PBAE,THIS, IS, UNDEFINED, NMEA, SENTENCE, FOR, TEST*37";
		
		System.out.println("nmea data : " + orginStr);
		String json = null;
		
		try{
			json = converter.convert(orginStr);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Undefined Test Result result: " + json);
		
		assertNotNull("result must not be null.", json);
	}
	
	
	@Test
	public void testNMEAConvertDummy() throws IOException {
		
		String orginStr = "$WIMWV,325.8,R,13.8,M,A*16";
		
		System.out.println("nmea data : " + orginStr);
		String json = null;
		
		try{
			json = converter.convert(orginStr);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("testNMEAConvertDummy result: " + json);
		
		assertNotNull("result must not be null.", json);
	}
	


}
