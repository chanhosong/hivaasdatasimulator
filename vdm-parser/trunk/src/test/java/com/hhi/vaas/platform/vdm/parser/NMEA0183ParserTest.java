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
 * hsbae			2015. 4. 6.			First Draft.
 */
package com.hhi.vaas.platform.vdm.parser;

import com.hhi.vaas.platform.vdm.parser.nmea.NMEA0183Parser;
import org.junit.*;

import static org.junit.Assert.fail;

public class NMEA0183ParserTest {

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
	public void testNMEA0183Parser() {
		
		String[] datas = new String[]{
				//"$BNALR,144707.60,000,V,A,c1=MAN;c2=746;c3=1*59",
				//"$VRXDR,G,0.00,P,00,G,0.00,P,01,G,0.00,P,02,G,0.00,P,03*4A",
				//"$GPZDA,145917.00,28,02,2015,-01,00*4B",
				//"$GPDTM,W84,,0.0,N,0.0,E,+0.0,W84*44",
				//"$HEHDT,084.2,T*21",
				//"$ERALR,150052.30,A02.01,V,A,A02.01*5D",
				//"$RATTM,08,17.38,52.19,T,16.9,77.2,T,17.01,84.04,N,,T,,145934.00,M*0C".
				//"$VDVBW,016.98,,A,018.50,-000.28,A,,V,-000.20,A*44",
				//"$AGHTD,V,2.0,R,S,N,15.0,10.0,0.000,0.0,84.0,0.000,0.0,T,A,A,,84.1*17"
				//"$WIMWV,252.0,R,12.8,M,A*1E",
				//"$AGRSA,-32.0,A,0.0,A*5A"
				//"$VMVBW,15.0,,A,,,,,,,*03"
				//"$WIMWV,325.8,R,13.8,M,A*16"
				"$GPVTG,235.4,T,15.0,M,30.0,N,00.0,K*79"
				};
		
		
		for (int i = 0; i < datas.length; i++) {
			
			parse(datas[i]);
		}
		
		
	}
	
	private void parse(String nmeaData){
		try{
			NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
			
			System.out.println("Message ID = " + parser.getSentenceId());
			System.out.println("Field count = " + parser.getItemCount());
			
			for (int i=0; i<parser.getItemCount(); i++) {
				System.out.println("[" + i + "]= " + parser.getString(i));
			}
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
//end of NMEA0183ParserTest.java