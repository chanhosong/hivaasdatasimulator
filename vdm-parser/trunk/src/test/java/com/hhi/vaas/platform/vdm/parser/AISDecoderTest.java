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
 * hsbae			2015-03-24			AIS Decoder Test
 */
package com.hhi.vaas.platform.vdm.parser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hhi.vaas.platform.vdm.parser.ais.AISDecoder;
import com.hhi.vaas.platform.vdm.parser.nmea.NMEA0183Parser;

public class AISDecoderTest {

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
	public void testAISMessageType1() {
		String nmeaData = "!AIVDM,1,1,,B,13P;>lh02@aCWopDUGL`?VU20`E:,0*05"; // type 1
		//String nmeaData = "!AIVDM,1,1,,A,1Ch6tkg000NBs10USArVOGsd08oR,0*77";
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		
		String str = parser.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List<String> subFields= null; // new ArrayList();
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(16, subFields.size());
		
		assertEquals(1, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("235065043", decoder.getUserId());
		
		assertEquals("0",subFields.get(3));
		assertEquals("0",subFields.get(4));
		assertEquals("14.4",subFields.get(5));
		assertEquals("1",subFields.get(6));
		assertEquals("130.11583",subFields.get(7));
		assertEquals("35.97288",subFields.get(8));
		assertEquals("211.0",subFields.get(9));
		assertEquals("210",subFields.get(10));
		assertEquals("33",subFields.get(11));
		assertEquals("0",subFields.get(12));
		assertEquals("0",subFields.get(13));
		assertEquals("0",subFields.get(14));
		assertEquals("165194",subFields.get(15));
		
	}
	
	@Test
	public void testAISMessageType2() {
		// do not have sample message

	}
	
	@Test
	public void testAISMessageType3() {
		
		String nmeaData = "!AIVDM,1,1,,B,35Mk33gOkSG?bLtK?;B2dRO`00`A,0*30"; // Message Type 3
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		
		String str = parser.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List<String> subFields= null; // new ArrayList();
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(16, subFields.size());
		
		assertEquals(3, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("366789390", decoder.getUserId());
		
		assertEquals("15",subFields.get(3));
		assertEquals("2577",subFields.get(15));
	}
	
	@Test
	public void testAISMessageType4() {
		
		String nmeaData = "!AIVDM,1,1,,A,403OwpiuIKl:Ro=sbvK=CG700<3b,0*5E"; // Message Type 4
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		
		String str = parser.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List<String> subFields= null; // new ArrayList();
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(17, subFields.size());
		
		assertEquals(4, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("003669987", decoder.getUserId());
		
		assertEquals("2006",subFields.get(3));
		assertEquals("49386",subFields.get(16));
		
		
	}
	
	@Test
	public void testAISMessageType5() {
		String nmeaData =  "!AIVDM,2,1,9,A,55Mf@6P00001MUS;7GQL4hh61L4hh6222222220t41H,0*49";
		String nmeaData2 =  "!AIVDM,2,2,9,A,==40HtI4i@E531H1QDTVH51DSCS0,2*16";
		
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		NMEA0183Parser parser2 = new NMEA0183Parser(nmeaData2);
		
		String str = parser.getString(4) + parser2.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List<String> subFields = new ArrayList<String>();
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
			
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(15, subFields.size());
		
		assertEquals(5, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("366710810", decoder.getUserId());
		
		assertEquals("WYX2158",subFields.get(5));
		assertEquals("WALLA WALLA",subFields.get(6));
		assertEquals("32:88:13:13",subFields.get(8));
		assertEquals("SEATTLE FERRY TERMNL",subFields.get(12));
		
	}
	
	@Test
	public void testAISMessageType6() {
		String nmeaData = "!AIVDM,1,1,4,B,6>jR0600V:C0>da4P106P00,2*02";
		
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		
		String str = parser.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List<String> subFields = new ArrayList<String>();
		
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
			
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(8, subFields.size());
		
		assertEquals(6, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("992509976", decoder.getUserId());
		
		assertEquals("0", subFields.get(3));
		assertEquals("2500912", subFields.get(4));
		assertEquals("0", subFields.get(5));
		assertEquals("0", subFields.get(6));
		
	}
	

	@Test
	public void testAISMessageType7() {
		// do not have sample data
		
	}
	
	@Test
	public void testAISMessageType8() {
		String nmeaData = "!AIVDM,1,1,1,B,8>h8nkP0Glr=<hFI0D6??wvlFR06EuOwgwl?wnSwe7wvlOw?sAwwnSGmwvh0,0*17";
		
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		
		String str = parser.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List<String> subFields = new ArrayList<String>();
		
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(5, subFields.size());
		
		assertEquals(8, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("990000846", decoder.getUserId());
		
		assertEquals("0", subFields.get(3));

		
		
	}
	
	@Test
	public void testAISMessageType9() {
		String nmeaData = "!AIVDM,1,1,,B,900048wwTcJb0mpF16IobRP2086Q,0*48";
		
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		
		String str = parser.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List<String> subFields = new ArrayList<String>();
		
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(18, subFields.size());
		
		assertEquals(9, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("000001059", decoder.getUserId());
		
		
		
		assertEquals("4094", subFields.get(3));
		assertEquals("299", subFields.get(4));
		assertEquals("0", subFields.get(5));
		assertEquals("-74.70817", subFields.get(6));
		assertEquals("38.47783", subFields.get(7));
		assertEquals("196.2", subFields.get(8));
		assertEquals("10", subFields.get(9));
		assertEquals("0", subFields.get(10));
		assertEquals("0", subFields.get(11));
		assertEquals("1", subFields.get(12));
		assertEquals("0", subFields.get(13));
		assertEquals("0", subFields.get(14));
		assertEquals("0", subFields.get(15));
		assertEquals("0", subFields.get(16));
		assertEquals("33185", subFields.get(17));
		
	}
	
	
	@Test
	public void testAISMessageType10() {
		String nmeaData = "!AIVDM,1,1,,A,:5D2Lp1Ghfe0,0*4E";
		
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		
		String str = parser.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List<String> subFields = new ArrayList<String>();
		
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(6, subFields.size());
		
		assertEquals(10, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("356556000", decoder.getUserId());
		
		assertEquals("0", subFields.get(3));
		assertEquals("368098000", subFields.get(4));
		assertEquals("0", subFields.get(5));
	}
	
	@Test
	public void testAISMessageType11() {
		String nmeaData = "!AIVDM,1,1,,B,;8u:8CAuiT7Bm2CIM=fsDJ100000,0*51"; // Message Type 4
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		
		String str = parser.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List<String> subFields= null; // new ArrayList();
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(17, subFields.size());
		
		assertEquals(11, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("601000013", decoder.getUserId());
		
		assertEquals("2012",subFields.get(3));
		assertEquals("6",subFields.get(4));
		assertEquals("8",subFields.get(5));
		assertEquals("7",subFields.get(6));
		assertEquals("18",subFields.get(7));
		assertEquals("53",subFields.get(8));
		assertEquals("32.19953",subFields.get(10));
		assertEquals("-29.83748",subFields.get(11));
		assertEquals("1",subFields.get(12));
		assertEquals("0",subFields.get(13));
		assertEquals("0",subFields.get(14));
		assertEquals("0",subFields.get(15));
		assertEquals("0",subFields.get(16));
		
	}
	
	@Test
	public void testAISMessageType12() {
		String nmeaData =  "!AIVDM,1,1,,A,<03Owph00002QG51D85BP1<5BDQP,0*7D";
		
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		
		String str = parser.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List <String>subFields = null; 
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(8, subFields.size());
		
		assertEquals(12, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("003669987", decoder.getUserId());
		
		assertEquals("!WEATHER ALERT!",subFields.get(7));
	}
	

	@Test
	public void testAISMessageType13() {
		// do not have sample message
	}
	
	
	@Test
	public void testAISMessageType14() {
		String nmeaData =  "!AIVDM,1,1,,B,>>M4fWA<59B1@E=@,0*17";
		
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		
		String str = parser.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List <String>subFields = null; 
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(5, subFields.size());
		
		assertEquals(14, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("970010269", decoder.getUserId());
		assertEquals("0",subFields.get(3));
		assertEquals("SART TEST",subFields.get(4));
	}
		
	@Test
	public void testAISMessageType15() {
		String nmeaData =  "!AIVDM,1,1,,A,?03OwpiGPmD0000,2*07";
		
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		
		String str = parser.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List <String>subFields = null; 
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(11, subFields.size());
		
		assertEquals(15, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("003669987", decoder.getUserId());
		
		assertEquals("0",subFields.get(3));
		assertEquals("367056192",subFields.get(4));
		assertEquals("0",subFields.get(5));
		assertEquals("0",subFields.get(6));
		assertEquals("0",subFields.get(7));
		assertEquals("0",subFields.get(8));
		assertEquals("-1",subFields.get(9));
		assertEquals("-1",subFields.get(10));
	}
	
	
	
	@Test
	public void testAISMessageType18() {
		String nmeaData =  "!AIVDM,1,1,,A,B52IRsP005=abWRnlQP03w`UkP06,0*2A";
		
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		
		String str = parser.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List <String>subFields = null; 
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(21, subFields.size());
		
		assertEquals(18, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("338060014", decoder.getUserId());
		
		assertEquals("0",subFields.get(3));
		assertEquals("-155.84371",subFields.get(6));
		assertEquals("19.96889",subFields.get(7));
		
		assertEquals("393222",subFields.get(20));
		
	}
	
	@Test
	public void testAISMessageType19() 
	{

		String nmeaData =  "!AIVDM,1,1,,A,B52IRsP005=abWRnlQP03w`UkP06,0*2A";
		
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		
		String str = parser.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List <String>subFields = null;
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(21, subFields.size());
		
		assertEquals(18, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("338060014", decoder.getUserId());
		
		assertEquals("0",subFields.get(3));
		assertEquals("-155.84371",subFields.get(6));
		assertEquals("19.96889",subFields.get(7));
		
		assertEquals("393222",subFields.get(20));
		
	}
	
	@Test
	public void testAISMessageType21() 
	{
		
		String nmeaData = "!AIVDO,2,1,5,B,E1c2;q@b44ah4ah0h:2ab@70VRpU<Bgpm4:gP50HH`Th`QF5,0*7B";
		String nmeaData2 = "!AIVDO,2,2,5,B,1CQ1A83PCAH0,0*60";
		
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		NMEA0183Parser parser2 = new NMEA0183Parser(nmeaData2);
		
		String str = parser.getString(4) + parser2.getString(4);
		
		AISDecoder decoder = new AISDecoder();
		List<String> subFields = new ArrayList<String>();
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
			
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(18, subFields.size());
		
		assertEquals(21, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("112233445", decoder.getUserId());
		
		assertEquals("1",subFields.get(3));
		assertEquals("THIS IS A TEST NAME1",subFields.get(4));
		assertEquals("0",subFields.get(5));
		assertEquals("145.18100",subFields.get(6));
		assertEquals("-38.22017",subFields.get(7));
		assertEquals("5:3:3:5",subFields.get(8));
		assertEquals("1",subFields.get(9));
		assertEquals("9",subFields.get(10));
		assertEquals("1",subFields.get(11));
		assertEquals("10",subFields.get(12));
		assertEquals("0",subFields.get(13));
		assertEquals("0",subFields.get(14));
		assertEquals("1",subFields.get(15));
		assertEquals("0",subFields.get(16));
		assertEquals("EXTENDED NAME",subFields.get(17));
		assertEquals("EXTENDED NAME",subFields.get(17));
		
		
		
		
		
	}
	
	
	@Test
	public void testAISMessageType24_A() 
	{
		
		String nmeaData = "!AIVDM,1,1,,A,H52IRsP518Tj0l59D0000000000,2*45";
		
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		AISDecoder decoder = new AISDecoder();
		
		
		String str = parser.getString(4);
		
		List <String>subFields = null;
		
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(5, subFields.size());
		
		assertEquals(24, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("338060014", decoder.getUserId());
		
		assertEquals("0",subFields.get(3));
		assertEquals("APRIL MARU@@@@@@@@@@",subFields.get(4));
	}
	
	@Test
	public void testAISMessageType24_B() 
	{
	
		
		String nmeaData = "!AIVDM,1,1,,A,H52IRsTU000000000000000@5120,0*76";
		
		NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
		AISDecoder decoder = new AISDecoder();
		
		String str = parser.getString(4);
		
		List <String>subFields = null;
		
		try {
			subFields = decoder.decode(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.toString());
		}
		
		System.out.println("Message ID = " + decoder.getMessageId());
		System.out.println("Repeat Indication = " + decoder.getRepeatIndicator());
		System.out.println("Field count = " + decoder.getFieldCount());
		
		for (int i=0; i<subFields.size(); i++) {
			System.out.println("[" + i + "]= " + subFields.get(i) );
		}
		
		assertNotNull("result must not be null", subFields);
		
		assertEquals(10, subFields.size());
		
		assertEquals(24, decoder.getMessageId());
		assertEquals(0, decoder.getRepeatIndicator());
		assertEquals("338060014", decoder.getUserId());
		
		assertEquals("1",subFields.get(3));
		assertEquals("37",subFields.get(4));
		assertEquals("@@@@@@@",subFields.get(5));
		assertEquals("@@@@@@@",subFields.get(6));
		assertEquals("2:5:1:2",subFields.get(7));
		assertEquals("0",subFields.get(8));
		assertEquals("0",subFields.get(9));
		
	}
	
	
}
//end of AISDecoderTest.java