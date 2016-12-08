package com.hhi.vaas.platform.vdm.parser;


import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.PortableInterceptor.SUCCESSFUL;

import com.hhi.vaas.platform.vdm.parser.filetest.DataParserFactoryFileTest;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

public class JSONConverterTest_KV {
	
	private static DataConverter converter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//InputStream vdmStream = DataParserFactoryFileTest.class.getResourceAsStream(TestResources.TEST_VCD_LCS_FILE);
		//converter = DataConverterFactory.create(TestResources.TEST_MAP_LCS_FILE, vdmStream);
		InputStream vdmStream = DataParserFactoryFileTest.class.getResourceAsStream("/VCD_for_ACONIS_LC_VDR_ver0.7.vcd");
		converter = DataConverterFactory.create("/Mapping_for_HHI_ACONIS_v0.7_150727.xml", vdmStream);
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

	
	/**
	 * Json kv converter
	 * @throws IOException
	 */
	@Test
	public void testKVConvert() throws IOException {
		
		//String orginStr = "{\"STS01.VAL\":\"20151231\", \"STS02.VAL\":\"155011\", \"STS04.VAL\":\"F\"}";
		//String orginStr = "{\"GB001@LO\":0.000000,\"GB002\":0.000000,\"GB002@HH\":0.000000,\"GB002@HI\":0.000000,\"GB002@LL\":0.000000,\"GB002@LO\":0.000000,\"GB003\":0.000000,\"GB003@HH\":0.000000,\"GB003@HI\":0.000000,\"GB003@LL\":0.000000,\"GB003@LO\":1.000000,\"GB003_HI\":0.000000,\"GB004\":0.000000,\"GB004@HH\":0.000000,\"GB004@HI\":0.000000,\"GB004@LL\":0.000000,\"GB004@LO\":0.000000,\"GB005\":0.000000,\"GB005@HH\":0.000000,\"GB005@HI\":0.000000,\"GB005@LL\":0.000000,\"GB005@LO\":0.000000,\"GB006\":1.500000,\"GB006@HH\":1.000000,\"GB006@HI\":1.000000,\"GB006@LL\":0.000000,\"GB006@LO\":0.000000,\"GB007\":1.500000,\"GB007@HH\":1.000000,\"GB007@HI\":1.000000,\"GB007@LL\":0.000000,\"GB007@LO\":0.000000,\"GB008\":5.000000,\"GB008@HH\":1.000000,\"GB008@HI\":1.000000,\"GB008@LL\":0.000000,\"GB008@LO\":0.000000,\"GB009\":15.000000,\"GB009@HH\":1.000000,\"GB009@HI\":1.000000,\"GB009@LL\":0.000000,\"GB009@LO\":0.000000,\"GB010\":3.500000,\"GB010@HH\":1.000000,\"GB010@HI\":1.000000,\"GB010@LL\":0.000000,\"GB010@LO\":0.000000,\"GB011\":1.000000,\"GB011@OF\":0.000000,\"GB011@ON\":1.000000,\"GB012\":1.000000,\"GB012@OF\":0.000000,\"GB012@ON\":1.000000,\"GB018\":1.000000,\"GB018@OF\":0.000000,\"GB018@ON\":1.000000,\"GB019\":1.000000,\"GB019@OF\":0.000000,\"GB019@ON\":1.000000,\"GB020\":1.000000,\"GB020@OF\":0.000000,\"GB020@ON\":1.000000,\"GB021\":1.000000,\"GB021@OF\":0.000000,\"GB021@ON\":1.000000,\"GB025\":1.000000,\"GB025@OF\":0.000000,\"GB025@ON\":1.000000,\"GB026\":1.000000,\"GB026@OF\":0.000000,\"GB026@ON\":1.000000,\"GB027\":1.000000,\"GB027@OF\":0.000000,\"GB027@ON\":1.000000,\"GB028\":1.000000,\"GB028@OF\":0.000000,\"GB028@ON\":1.000000,\"GB029\":1.000000,\"GB029@OF\":0.000000,\"GB029@ON\":1.000000,\"GB030\":0.000000,\"GB030@HH\":0.000000,\"GB030@HI\":0.000000,\"GB030@LL\":0.000000,\"GB030@LO\":0.000000,\"GB031\":0.000000,\"GB031@HH\":0.000000,\"GB031@HI\":0.000000,\"GB031@LL\":0.000000,\"GB031@LO\":0.000000,\"GB032\":0.000000,\"GB032@HH\":0.000000,\"GB032@HI\":0.000000,\"GB032@LL\":0.000000,\"GB032@LO\":0.000000,\"GB033\":0.000000,\"GB033@HH\":0.000000,\"GB033@HI\":0.000000,\"GB033@LL\":0.000000,\"GB033@LO\":0.000000}";
		String orginStr = "{\"MS014@ALM.name\":\"MS014\",\"MS014@ALM.desc\":\"M/E NO.2 CYL EXHGAS OUT TEMP\",\"MS014@ALM.signalType\":\"Analog\",\"MS014@ALM.alarmType\":\"ALARM\",\"MS014@ALM.alarmLevel\":\"HH\",\"MS014@ALM.status\":\"NOR\",\"MS014@ALM.alarmTime\":1438150249,\"MS014@ALM.ackTime\":1438150256,\"MS014@ALM.normalTime\":1438150275,\"MS014@ALM.instVal\":\"475.00\",\"MS014@ALM.name\":\"MS014\",\"MS014@ALM.desc\":\"M/E NO.2 CYL EXH GAS OUT TEMP\",\"MS014@ALM.signalType\":\"Analog\",\"MS014@ALM.alarmType\":\"ALARM\",\"MS014@ALM.alarmLevel\":\"HIGH\",\"MS014@ALM.status\":\"NOR\",\"MS014@ALM.alarmTime\":1438150234,\"MS014@ALM.ackTime\":1438150256,\"MS014@ALM.normalTime\":1438150275,\"MS014@ALM.instVal\":\"455.00\"}";
		
		String json = null;
		
		try{
			json = converter.convert(orginStr);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("testKVConvert result: " + json);
		
		assertNotNull("result must not be null.", json);

		//Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);

		//assertEquals("20151231", JsonPath.read(document, "$.[0].value"));
		
	}

}
	
