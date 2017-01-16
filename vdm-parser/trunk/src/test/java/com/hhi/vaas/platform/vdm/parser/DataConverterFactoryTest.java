package com.hhi.vaas.platform.vdm.parser;

import org.junit.*;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


public class DataConverterFactoryTest {
	
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
	public void testCreate() {
		try{
			
			InputStream vInputStream = DataConverterFactoryTest.class.getResourceAsStream(TestResources.TEST_VCD_LCS_FILE);
			DataConverter converter = DataConverterFactory.create(TestResources.TEST_MAP_VDR_FILE, vInputStream);
			
			assertNotNull("converter must not be null.", converter);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testCreateByStream() {
		try{
			InputStream vInputStream = DataConverterFactoryTest.class.getResourceAsStream(TestResources.TEST_VCD_LCS_FILE);
			InputStream mInputStream = DataConverterFactory.class.getResourceAsStream(TestResources.TEST_MAP_VDR_FILE);
			DataConverter converter = DataConverterFactory.create(mInputStream, vInputStream);
			
			assertNotNull("converter must not be null.", converter);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}

}
